package com.teamname.freelancetaskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamname.freelancetaskmanager.data.entities.Project;
import com.teamname.freelancetaskmanager.ui.adapters.ProjectAdapter;
import com.teamname.freelancetaskmanager.viewmodels.ProjectViewModel;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProjectAdapter adapter;
    private ProjectViewModel projectViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        // Configurer RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialiser ViewModel
        projectViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        // Initialiser l'Adapter
        adapter = new ProjectAdapter(null, project -> {
            // Quand on clique sur un projet
            Toast.makeText(MainActivity.this,
                    "Projet: " + project.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        // Observer les données de Room
        projectViewModel.getAllProjects().observe(this, new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                // Mettre à jour l'adapter avec les données de la BD
                adapter.setProjectList(projects);
            }
        });

        // Configurer le bouton d'ajout
        fab.setOnClickListener(v -> showAddProjectDialog());

        // Ajouter quelques données de test au premier lancement
        addInitialTestData();
    }

    private void addInitialTestData() {
        // Vérifier si la base est vide, ajouter des données test
        new Thread(() -> {
            List<Project> projects = projectViewModel.getAllProjects().getValue();
            if (projects == null || projects.isEmpty()) {
                Calendar calendar = Calendar.getInstance();

                Project project1 = new Project(
                        "Site Web E-commerce",
                        "Boutique Moda",
                        "Création site e-commerce avec paiement en ligne",
                        5000.0,
                        System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
                );

                Project project2 = new Project(
                        "Application Mobile",
                        "Startup Tech",
                        "Développement app iOS et Android",
                        8000.0,
                        System.currentTimeMillis() + 45L * 24 * 60 * 60 * 1000
                );

                projectViewModel.insert(project1);
                projectViewModel.insert(project2);
            }
        }).start();
    }

    private void showAddProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nouveau projet");

        // Inflater le layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_project, null);
        builder.setView(dialogView);

        // Récupérer les champs
        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editClient = dialogView.findViewById(R.id.editClient);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editBudget = dialogView.findViewById(R.id.editBudget);

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String name = editName.getText().toString().trim();
            String client = editClient.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String budgetStr = editBudget.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Le nom est requis", Toast.LENGTH_SHORT).show();
                return;
            }

            double budget = 0.0;
            try {
                budget = budgetStr.isEmpty() ? 0.0 : Double.parseDouble(budgetStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Budget invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            // Date par défaut : 1 mois plus tard
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);
            long deadline = calendar.getTimeInMillis();

            // Créer et insérer le projet
            Project project = new Project(name, client, description, budget, deadline);
            projectViewModel.insert(project);

            Toast.makeText(this, "Projet ajouté !", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", null);
        builder.create().show();
    }
}