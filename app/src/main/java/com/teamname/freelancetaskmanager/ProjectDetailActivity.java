package com.teamname.freelancetaskmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamname.freelancetaskmanager.data.entities.Task;
import com.teamname.freelancetaskmanager.ui.adapters.TaskAdapter;
import com.teamname.freelancetaskmanager.viewmodels.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProjectDetailActivity extends AppCompatActivity {

    private long projectId;
    private TaskViewModel taskViewModel;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back); // optionnel
        }
        projectId = getIntent().getLongExtra("project_id", -1);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(null, new TaskAdapter.OnTaskActionListener() {

            @Override
            public void onStatusClick(Task task) {
                task.setStatus(task.getStatus().equals("PENDING") ? "DONE" : "PENDING");
                taskViewModel.update(task);
            }

            @Override
            public void onDeleteClick(Task task) {
                taskViewModel.delete(task);
                Toast.makeText(ProjectDetailActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(Task task) {
                showEditTaskDialog(task);
            }

        });

        recyclerView.setAdapter(taskAdapter);

        taskViewModel.getTasksForProject(projectId).observe(this, tasks -> taskAdapter.setTaskList(tasks));

        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // termine cette activité et revient à l'activité précédente
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // ================= ADD TASK DIALOG =================
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText editTitle = view.findViewById(R.id.editTaskTitle);
        EditText editDeadline = view.findViewById(R.id.editTaskDeadline);

        // Set default deadline = one week from today
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editDeadline.setText(sdf.format(calendar.getTime()));
        final long[] selectedDeadline = new long[]{calendar.getTimeInMillis()};

        // Pick deadline
        editDeadline.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(selectedDeadline[0]);
            new DatePickerDialog(
                    ProjectDetailActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        c.set(year, month, dayOfMonth);
                        selectedDeadline[0] = c.getTimeInMillis();
                        editDeadline.setText(sdf.format(c.getTime()));
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = editTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Task title required", Toast.LENGTH_SHORT).show();
                return;
            }
            Task task = new Task(projectId, title, "", selectedDeadline[0]);
            taskViewModel.insert(task);
            Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ================= EDIT TASK DIALOG =================
    private void showEditTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText editTitle = view.findViewById(R.id.editTaskTitle);
        EditText editDeadline = view.findViewById(R.id.editTaskDeadline);

        editTitle.setText(task.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editDeadline.setText(sdf.format(task.getDeadline()));
        final long[] selectedDeadline = new long[]{task.getDeadline()};

        editDeadline.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(selectedDeadline[0]);
            new DatePickerDialog(
                    ProjectDetailActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        c.set(year, month, dayOfMonth);
                        selectedDeadline[0] = c.getTimeInMillis();
                        editDeadline.setText(sdf.format(c.getTime()));
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = editTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                task.setTitle(title);
                task.setDeadline(selectedDeadline[0]);
                taskViewModel.update(task);
                Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}