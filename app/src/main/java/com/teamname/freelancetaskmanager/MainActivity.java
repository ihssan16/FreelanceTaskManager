package com.teamname.freelancetaskmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamname.freelancetaskmanager.data.entities.Project;
import com.teamname.freelancetaskmanager.ui.adapters.ProjectAdapter;
import com.teamname.freelancetaskmanager.viewmodels.ProjectViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProjectAdapter adapter;
    private ProjectViewModel projectViewModel;
    private List<Project> allProjects;

    private Button filterAll, filterPending, filterDone;
    private Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== Views =====
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        filterAll = findViewById(R.id.filterAll);
        filterPending = findViewById(R.id.filterPending);
        filterDone = findViewById(R.id.filterDone);
        sortSpinner = findViewById(R.id.sortSpinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ===== ViewModel =====
        projectViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        // ===== Adapter =====
        adapter = new ProjectAdapter(new ArrayList<>(), new ProjectAdapter.OnProjectActionListener() {
            @Override
            public void onStatusClick(Project project) {
                String newStatus = project.getStatus().equals("PENDING") ? "DONE" : "PENDING";
                projectViewModel.updateStatus(project.getId(), newStatus);
            }

            @Override
            public void onDeleteClick(Project project) {
                showDeleteConfirmation(project);
            }

            @Override
            public void onEditClick(Project project) {
                showEditProjectDialog(project);
            }

            @Override
            public void onProjectClick(Project project) {
                // Open ProjectDetailActivity with project ID
                Intent intent = new Intent(MainActivity.this, ProjectDetailActivity.class);
                intent.putExtra("project_id", project.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        // ===== Observe DB =====
        projectViewModel.getAllProjects().observe(this, projects -> {
            allProjects = projects;
            updateAdapter();
        });

        // ===== FAB =====
        fab.setOnClickListener(v -> showAddProjectDialog());

        // ===== Filters =====
        filterAll.setOnClickListener(v -> updateAdapter());
        filterPending.setOnClickListener(v -> updateAdapter("PENDING"));
        filterDone.setOnClickListener(v -> updateAdapter("DONE"));

        // ===== Sorting =====
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateAdapter();
            }
        });

        // ===== Test Data =====
        addInitialTestData();
    }

    // ================= UPDATE ADAPTER =================
    private void updateAdapter() {
        updateAdapter(null);
    }

    private void updateAdapter(String filterStatus) {
        if (allProjects == null) return;

        List<Project> displayList = new ArrayList<>();

        // Filter
        for (Project p : allProjects) {
            if (filterStatus == null || p.getStatus().equals(filterStatus)) {
                displayList.add(p);
            }
        }

        // Sort
        int sortPosition = sortSpinner.getSelectedItemPosition();
        switch (sortPosition) {
            case 0: // Deadline
                Collections.sort(displayList, (p1, p2) -> Long.compare(p1.getDeadline(), p2.getDeadline()));
                break;
            case 1: // Budget
                Collections.sort(displayList, (p1, p2) -> Double.compare(p1.getBudget(), p2.getBudget()));
                break;
            case 2: // Status
                Collections.sort(displayList, (p1, p2) -> p1.getStatus().compareTo(p2.getStatus()));
                break;
        }

        adapter.setProjectList(displayList);
    }

    // ================= DELETE =================
    private void showDeleteConfirmation(Project project) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete this project?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    projectViewModel.delete(project);
                    Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ================= EDIT =================
    private void showEditProjectDialog(Project project) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Project");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_project, null);
        builder.setView(view);

        EditText editName = view.findViewById(R.id.editName);
        EditText editClient = view.findViewById(R.id.editClient);
        EditText editDescription = view.findViewById(R.id.editDescription);
        EditText editBudget = view.findViewById(R.id.editBudget);
        EditText editDeadline = view.findViewById(R.id.editDeadline);

        editName.setText(project.getName());
        editClient.setText(project.getClient());
        editDescription.setText(project.getDescription());
        editBudget.setText(String.valueOf(project.getBudget()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editDeadline.setText(sdf.format(new Date(project.getDeadline())));

        // Deadline picker
        editDeadline.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(project.getDeadline());

            new DatePickerDialog(
                    this,
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        project.setDeadline(calendar.getTimeInMillis());
                        editDeadline.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            project.setName(editName.getText().toString().trim());
            project.setClient(editClient.getText().toString().trim());
            project.setDescription(editDescription.getText().toString().trim());

            try {
                project.setBudget(Double.parseDouble(editBudget.getText().toString().trim()));
            } catch (NumberFormatException e) {
                project.setBudget(0);
            }

            projectViewModel.update(project);
            Toast.makeText(this, "Project updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ================= ADD =================
    private void showAddProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Project");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_project, null);
        builder.setView(view);

        EditText editName = view.findViewById(R.id.editName);
        EditText editClient = view.findViewById(R.id.editClient);
        EditText editDescription = view.findViewById(R.id.editDescription);
        EditText editBudget = view.findViewById(R.id.editBudget);
        EditText editDeadline = view.findViewById(R.id.editDeadline);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editDeadline.setText(sdf.format(calendar.getTime()));
        long[] selectedDeadline = new long[]{calendar.getTimeInMillis()};

        editDeadline.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(selectedDeadline[0]);
            new DatePickerDialog(
                    this,
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

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = editName.getText().toString().trim();
            String client = editClient.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String budgetStr = editBudget.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Project name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            double budget;
            try {
                budget = budgetStr.isEmpty() ? 0.0 : Double.parseDouble(budgetStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid budget", Toast.LENGTH_SHORT).show();
                return;
            }

            Project project = new Project(name, client, description, budget, selectedDeadline[0]);
            projectViewModel.insert(project);
            Toast.makeText(this, "Project added!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ================= TEST DATA =================
    private void addInitialTestData() {
        new Thread(() -> {
            List<Project> projects = projectViewModel.getAllProjects().getValue();
            if (projects == null || projects.isEmpty()) {
                Project project1 = new Project(
                        "E-commerce Website",
                        "Moda Store",
                        "E-commerce website with online payment",
                        5000.0,
                        System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
                );
                Project project2 = new Project(
                        "Mobile Application",
                        "Tech Startup",
                        "iOS and Android app development",
                        8000.0,
                        System.currentTimeMillis() + 45L * 24 * 60 * 60 * 1000
                );
                projectViewModel.insert(project1);
                projectViewModel.insert(project2);
            }
        }).start();
    }
}