package co.icesi.taskManager.mappers;

import co.icesi.taskManager.dtos.TaskDto;
import co.icesi.taskManager.model.Task;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T05:17:00-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskDto taskToTaskDto(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDto taskDto = new TaskDto();

        taskDto.setDescription( task.getDescription() );
        taskDto.setId( task.getId() );
        taskDto.setName( task.getName() );
        taskDto.setNotes( task.getNotes() );
        taskDto.setPriority( task.getPriority() );

        return taskDto;
    }

    @Override
    public Task taskDtoToTask(TaskDto task) {
        if ( task == null ) {
            return null;
        }

        Task task1 = new Task();

        task1.setDescription( task.getDescription() );
        task1.setId( task.getId() );
        task1.setName( task.getName() );
        task1.setNotes( task.getNotes() );
        task1.setPriority( task.getPriority() );

        return task1;
    }
}
