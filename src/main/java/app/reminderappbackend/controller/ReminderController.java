package app.reminderappbackend.controller;

import app.reminderappbackend.service.ReminderEntity;
import app.reminderappbackend.service.ReminderService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reminderapi.controller.RemindersApi;
import reminderapi.model.ReminderDTO;
import reminderapi.model.ReminderForm;

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

  @Override
  public ResponseEntity<ReminderDTO> createReminder(@Valid ReminderForm form) {
    var entity = reminderService.create(form);
    var dto = toReminderDTO(entity);

    return ResponseEntity.created(URI.create("/reminders/" + dto.getId())).body(dto);
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
