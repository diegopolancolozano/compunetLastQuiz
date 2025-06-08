package co.icesi.taskManager.services.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.icesi.taskManager.model.Task;
import co.icesi.taskManager.model.User;
import co.icesi.taskManager.repositories.TaskRepository;
import co.icesi.taskManager.repositories.UserRepository;
import co.icesi.taskManager.services.interfaces.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImp implements TaskService {
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public Task createTask(Task task) {
        // Inicializar lista de usuarios asignados si es null
        if (task.getAssignedUsers() == null) {
            task.setAssignedUsers(new ArrayList<>());
        }
        return taskRepository.save(task);
    }
    
    @Override
    @Transactional
    public Task updateTask(Task task) {
        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            throw new IllegalArgumentException("Task not found");
        }
        
        // Actualizar propiedades preservando las asignaciones de usuarios
        if (task.getName() != null) {
            existingTask.setName(task.getName());
        }
        if (task.getDescription() != null) {
            existingTask.setDescription(task.getDescription());
        }
        if (task.getNotes() != null) {
            existingTask.setNotes(task.getNotes());
        }
        if (task.getPriority() != null) {
            existingTask.setPriority(task.getPriority());
        }
        
        return taskRepository.save(existingTask);
    }
    
    @Override
    @Transactional
    public void deleteTask(long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        
        // Desasociar usuarios antes de eliminar la tarea
        for (User user : task.getAssignedUsers()) {
            user.getTasks().remove(task);
            userRepository.save(user);
        }
        
        taskRepository.deleteById(taskId);
    }
    
    @Override
    @Transactional
    public void assignTask(long taskId, long userId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (taskOpt.isPresent() && userOpt.isPresent()) {
            Task task = taskOpt.get();
            User user = userOpt.get();
            
            // Inicializar listas si son null
            if (user.getTasks() == null) {
                user.setTasks(new ArrayList<>());
            }
            if (task.getAssignedUsers() == null) {
                task.setAssignedUsers(new ArrayList<>());
            }
            
            // Asignar tarea al usuario y usuario a la tarea si no existe la relación
            if (!user.getTasks().contains(task)) {
                user.getTasks().add(task);
            }
            if (!task.getAssignedUsers().contains(user)) {
                task.getAssignedUsers().add(user);
            }
            
            userRepository.save(user);
            taskRepository.save(task);
        } else {
            throw new IllegalArgumentException("Task or User not found");
        }
    }
    
    @Override
    @Transactional
    public void unassignTask(long taskId, long userId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (taskOpt.isPresent() && userOpt.isPresent()) {
            Task task = taskOpt.get();
            User user = userOpt.get();
            
            user.getTasks().remove(task);
            task.getAssignedUsers().remove(user);
            
            userRepository.save(user);
            taskRepository.save(task);
        } else {
            throw new IllegalArgumentException("Task or User not found");
        }
    }
    
    @Override
    public Task getTaskById(long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }
    
    @Override
    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }
}