package app.reminderappbackend.controller;

import app.reminderappbackend.service.ReminderEntity;
import app.reminderappbackend.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reminderapi.controller.RemindersApi;
import reminderapi.model.ReminderDTO;

@RestController
@RequiredArgsConstructor
public class ReminderController implements RemindersApi {

  private final ReminderService reminderService;

  @Override
  public ResponseEntity<ReminderDTO> getReminder(Long id) {
    var entity = reminderService.findById(id);
    var dto = toReminderDTO(entity);

    return ResponseEntity.ok(dto);
  }

  private ReminderDTO toReminderDTO(ReminderEntity entity) {
    return new ReminderDTO(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getDueDate(),
        entity.getPriority(),
        entity.getIsCompleted(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
