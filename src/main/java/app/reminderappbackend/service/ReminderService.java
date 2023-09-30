package app.reminderappbackend.service;

import app.reminderappbackend.Repository.ReminderRecord;
import app.reminderappbackend.Repository.ReminderRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reminderapi.model.ReminderForm;

@Service
@RequiredArgsConstructor
public class ReminderService {

  private final ReminderRepository reminderRepository;

  public ReminderEntity findById(Long id) {
    var optRecord = reminderRepository.selectById(id);
    var entity =
        optRecord
            .map(record -> toReminderEntity(record))
            .orElseThrow(() -> new ReminderEntityNotFoundException(id));

    return entity;
  }

  public ReminderEntity create(@Valid ReminderForm form) {
    // form を record に詰めて、Repositoryへ渡す
    var record = toReminderRecord(form);
    reminderRepository.insert(record);

    // record to entity
    var entity = toReminderEntity(record);

    return entity;
  }

  private ReminderRecord toReminderRecord(ReminderForm form) {
    return new ReminderRecord(
        null,
        form.getTitle(),
        form.getDescription(),
        form.getDueDate(),
        form.getPriority(),
        form.getIsCompleted(),
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  private ReminderEntity toReminderEntity(ReminderRecord record) {
    return new ReminderEntity(
        record.getId(),
        record.getTitle(),
        record.getDescription(),
        record.getDueDate(),
        record.getPriority(),
        record.isCompleted(),
        toOffsetDateTime(record.getCreatedAt()),
        toOffsetDateTime(record.getUpdatedAt()));
  }

  /**
   * クライアントへデータを返却する際に、以下のカラムを<OffsetDateTime>型に変換する必要がある 理由は、OpenAPI
   * Specificationで生成される<date-time>が<OffsetDateTime>型であるため - created_at - updated_at
   *
   * @param ldt
   * @return OffsetDateTime
   */
  private OffsetDateTime toOffsetDateTime(LocalDateTime ldt) {
    return ldt.atOffset(ZoneOffset.UTC);
  }
}
