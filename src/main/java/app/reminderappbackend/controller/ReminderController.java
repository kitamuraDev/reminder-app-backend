package app.reminderappbackend.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.reminderappbackend.service.ReminderEntity;
import app.reminderappbackend.service.ReminderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import reminderapi.controller.RemindersApi;
import reminderapi.model.PageDTO;
import reminderapi.model.ReminderDTO;
import reminderapi.model.ReminderForm;
import reminderapi.model.ReminderListDTO;

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
  public ResponseEntity<ReminderListDTO> getReminderList(@RequestParam Integer limit, @RequestParam Long offset) {
    List<ReminderEntity> entityList = reminderService.findList(limit, offset);
    var dtoList = entityList.stream()
      .map(ReminderController::toReminderDTO)
      .collect(Collectors.toList());

    var pageDTO = new PageDTO(limit, offset, dtoList.size());

    var dto = new ReminderListDTO();
    dto.setPage(pageDTO);
    dto.setResults(dtoList);

    return ResponseEntity.ok(dto);
  }

  @Override
  public ResponseEntity<ReminderDTO> createReminder(@Valid ReminderForm form) {
    var entity = reminderService.create(form);
    var dto = toReminderDTO(entity);

    return ResponseEntity.created(URI.create("/reminders/" + dto.getId())).body(dto);
  }

  @Override
  public ResponseEntity<ReminderDTO> updateReminder(@Min(1) Long id, @Valid ReminderForm reminderForm) {
    var entity = reminderService.update(id, reminderForm);
    var dto = toReminderDTO(entity);

    return ResponseEntity.ok(dto);
  }

  private static ReminderDTO toReminderDTO(ReminderEntity entity) {
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
