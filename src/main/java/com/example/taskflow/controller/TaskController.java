package com.example.taskflow.controller;

import com.example.taskflow.model.Task;
import com.example.taskflow.service.TaskService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ChatClient chatClient;

    public TaskController(TaskService taskService, ChatClient.Builder chatClientBuilder) {
        this.taskService = taskService;
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.findAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {

        Task createdTask = taskService.createTask(task);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task task) {

        return ResponseEntity.ok(
                taskService.updateTask(id, task)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {

        return ResponseEntity.ok(
                taskService.markTaskAsComplete(id)
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<String> getTaskSummary() {

        List<Task> tasks = taskService.findAllTasks();

        String summary = chatClient
                .prompt()
                .user("Give a short plain English summary of what tasks are pending and what tasks are completed: " + tasks)
                .call()
                .content();

        return ResponseEntity.ok(summary);
    }
}