package com.example.listatarefa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TaskDatabase db;
    private EditText editTextTitle, editTextDescription;
    private ListView listViewTasks;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = TaskDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        listViewTasks = findViewById(R.id.listViewTasks);

        buttonAddTask.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            if (!title.isEmpty() && !description.isEmpty()) {
                executorService.execute(() -> {
                    db.taskDao().insert(new Task(title, description));
                    runOnUiThread(this::loadTasks);  // Atualiza a UI após inserir a tarefa
                });
            }
        });

        loadTasks();
    }

    private void loadTasks() {
        executorService.execute(() -> {
            List<Task> taskList = db.taskDao().getAllTasks();
            runOnUiThread(() -> {
                TaskAdapter adapter = new TaskAdapter(this, taskList);
                listViewTasks.setAdapter(adapter);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
