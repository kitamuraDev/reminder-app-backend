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

  /**
   * GET /reminders/{id} : リマインダー取得
   *
   * @param id リマインダーを取得する一意ID (required)
   * @return OK (status code 200) or Not Found (status code 404)
   */
  @Override
  public ResponseEntity<ReminderDTO> getReminder(Long id) {
    var entity = reminderService.findById(id);
    var dto = toReminderDTO(entity);

    return ResponseEntity.ok(dto);
  }

  /**
   * GET /reminders/ : リマインダー一覧取得
   *
   * @param limit リストに含まれるリソースの最大値 (required)
   * @param offset オフセット (required)
   * @return OK (status code 200) or Bad Request (status code 400)
   */
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

  /**
   * POST /reminders/ : リマインダー作成
   *
   * @param reminderForm クライアントからPOSTされるフォーム (required)
   * @return created (status code 201) or Bad Request (status code 400)
   */
  @Override
  public ResponseEntity<ReminderDTO> createReminder(@Valid ReminderForm form) {
    var entity = reminderService.create(form);
    var dto = toReminderDTO(entity);

    return ResponseEntity.created(URI.create("/reminders/" + dto.getId())).body(dto);
  }

  /**
   * PUT /reminders/{id} : リマインダー更新
   *
   * @param id 更新するリマインダーのID (required)
   * @param reminderForm クライアントからPOSTされるフォーム (required)
   * @return OK (status code 200) or Bad Request (status code 400) or Not Found (status code 404)
   */
  @Override
  public ResponseEntity<ReminderDTO> updateReminder(@Min(1) Long id, @Valid ReminderForm reminderForm) {
    var entity = reminderService.update(id, reminderForm);
    var dto = toReminderDTO(entity);

    return ResponseEntity.ok(dto);
  }

  /**
   * DELETE /reminders/{id} : リマインダー削除
   *
   * @param id 削除するリマインダーのID (required)
   * @return No Content (status code 204) or Not Found (status code 404)
   */
  @Override
  public ResponseEntity<Void> deleteReminder(@Min(1) Long id) {
    reminderService.delete(id);

    return ResponseEntity.noContent().build();
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
