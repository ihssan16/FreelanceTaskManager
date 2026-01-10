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
import com.teamname.freelancetaskmanager.AuthManager;
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

    private List<Project> allProjects = new ArrayList<>();

    private Button filterAll, filterPending, filterDone;
    private Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ===== AUTH GUARD =====
        if (!AuthManager.isLoggedIn(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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
        projectViewModel = new ViewModelProvider(this)
                .get(ProjectViewModel.class);

        // ===== Adapter =====
        adapter = new ProjectAdapter(new ArrayList<>(),
                new ProjectAdapter.OnProjectActionListener() {

                    @Override
                    public void onStatusClick(Project project) {
                        String newStatus =
                                project.getStatus().equals("PENDING") ? "DONE" : "PENDING";
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
                        Intent intent = new Intent(MainActivity.this,
                                ProjectDetailActivity.class);
                        intent.putExtra("project_id", project.getId());
                        startActivity(intent);
                    }
                });

        recyclerView.setAdapter(adapter);

        // ===== Observe DB =====
        projectViewModel.getAllProjects().observe(this, projects -> {
            allProjects = projects;
            updateAdapter(null);
        });

        // ===== FAB =====
        fab.setOnClickListener(v -> showAddProjectDialog());

        // ===== Filters =====
        filterAll.setOnClickListener(v -> updateAdapter(null));
        filterPending.setOnClickListener(v -> updateAdapter("PENDING"));
        filterDone.setOnClickListener(v -> updateAdapter("DONE"));

        // ===== Sorting =====
        sortSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {
                        updateAdapter(null);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        updateAdapter(null);
                    }
                });

        // ===== Test Data =====
        addInitialTestData();
    }

    // ================= UPDATE LIST =================
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
        int sort = sortSpinner.getSelectedItemPosition();
        switch (sort) {
            case 0: // Deadline
                Collections.sort(displayList,
                        (a, b) -> Long.compare(a.getDeadline(), b.getDeadline()));
                break;
            case 1: // Budget
                Collections.sort(displayList,
                        (a, b) -> Double.compare(a.getBudget(), b.getBudget()));
                break;
            case 2: // Status
                Collections.sort(displayList,
                        (a, b) -> a.getStatus().compareTo(b.getStatus()));
                break;
        }

        adapter.setProjectList(displayList);
    }

    // ================= DELETE =================
    private void showDeleteConfirmation(Project project) {
        new AlertDialog.Builder(this)
                .setTitle("Delete project")
                .setMessage("Are you sure you want to delete this project?")
                .setPositiveButton("Delete", (d, w) -> {
                    projectViewModel.delete(project);
                    Toast.makeText(this,
                            "Project deleted",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ================= EDIT =================
    private void showEditProjectDialog(Project project) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit project");

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_project, null);
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

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editDeadline.setText(sdf.format(new Date(project.getDeadline())));

        editDeadline.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(project.getDeadline());

            new DatePickerDialog(this,
                    (d, y, m, day) -> {
                        c.set(y, m, day);
                        project.setDeadline(c.getTimeInMillis());
                        editDeadline.setText(sdf.format(c.getTime()));
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        builder.setPositiveButton("Save", (d, w) -> {
            project.setName(editName.getText().toString().trim());
            project.setClient(editClient.getText().toString().trim());
            project.setDescription(editDescription.getText().toString().trim());

            try {
                project.setBudget(
                        Double.parseDouble(editBudget.getText().toString()));
            } catch (Exception e) {
                project.setBudget(0);
            }

            projectViewModel.update(project);
            Toast.makeText(this,
                    "Project updated",
                    Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ================= ADD =================
    private void showAddProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New project");

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_project, null);
        builder.setView(view);

        EditText editName = view.findViewById(R.id.editName);
        EditText editClient = view.findViewById(R.id.editClient);
        EditText editDescription = view.findViewById(R.id.editDescription);
        EditText editBudget = view.findViewById(R.id.editBudget);
        EditText editDeadline = view.findViewById(R.id.editDeadline);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        long[] deadline = {c.getTimeInMillis()};

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editDeadline.setText(sdf.format(c.getTime()));

        editDeadline.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(deadline[0]);

            new DatePickerDialog(this,
                    (d, y, m, day) -> {
                        cal.set(y, m, day);
                        deadline[0] = cal.getTimeInMillis();
                        editDeadline.setText(sdf.format(cal.getTime()));
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        builder.setPositiveButton("Add", (d, w) -> {
            String name = editName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this,
                        "Project name required",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double budget = 0;
            try {
                budget = Double.parseDouble(editBudget.getText().toString());
            } catch (Exception ignored) {}

            Project project = new Project(
                    name,
                    editClient.getText().toString(),
                    editDescription.getText().toString(),
                    budget,
                    deadline[0]
            );

            projectViewModel.insert(project);
            Toast.makeText(this,
                    "Project added",
                    Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ================= LOGOUT =================
    private void logout() {
        AuthManager.logout(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // ================= TEST DATA =================
    private void addInitialTestData() {
        new Thread(() -> {
            List<Project> projects =
                    projectViewModel.getAllProjects().getValue();
            if (projects == null || projects.isEmpty()) {
                projectViewModel.insert(new Project(
                        "E-commerce Website",
                        "Moda Store",
                        "Online shop with payment",
                        5000,
                        System.currentTimeMillis() + 30L * 86400000
                ));
            }
        }).start();
    }
}
