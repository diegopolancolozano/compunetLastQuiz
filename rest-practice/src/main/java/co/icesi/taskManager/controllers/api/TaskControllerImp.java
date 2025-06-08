package co.icesi.taskManager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import co.icesi.taskManager.dtos.TaskDto;
import co.icesi.taskManager.mappers.TaskMapper;
import co.icesi.taskManager.model.Task;
import co.icesi.taskManager.model.User;
import co.icesi.taskManager.repositories.UserRepository;
import co.icesi.taskManager.services.interfaces.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskControllerImp implements TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserRepository userRepository;

    // Obtener el usuario autenticado
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_TASK')")
    public ResponseEntity<?> findAllTask() {
        User user = getAuthenticatedUser();
        List<Task> tasks = user.getTasks();
        List<TaskDto> taskDtos = tasks.stream()
                .map(taskMapper::taskToTaskDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDtos);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_TASK')")
    public ResponseEntity<?> addTask(@RequestBody TaskDto dto) {
        Task task = taskMapper.taskDtoToTask(dto);
        Task createdTask = taskService.createTask(task);

        User user = getAuthenticatedUser();
        taskService.assignTask(createdTask.getId(), user.getId());

        TaskDto createdTaskDto = taskMapper.taskToTaskDto(createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDto);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('UPDATE_TASK')")
    public ResponseEntity<?> updateTask(@RequestBody TaskDto dto) {
        Task task = taskMapper.taskDtoToTask(dto);
        Task updatedTask = taskService.updateTask(task);
        TaskDto updatedTaskDto = taskMapper.taskToTaskDto(updatedTask);
        return ResponseEntity.ok(updatedTaskDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_TASK')")
    public ResponseEntity<?> deleteTask(@PathVariable("id") long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_TASK')")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        TaskDto taskDto = taskMapper.taskToTaskDto(task);
        return ResponseEntity.ok(taskDto);
    }
}