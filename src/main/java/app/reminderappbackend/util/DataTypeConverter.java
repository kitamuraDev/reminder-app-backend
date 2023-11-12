package app.reminderappbackend.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import app.reminderappbackend.repository.ReminderRecord;
import app.reminderappbackend.service.ReminderEntity;
import reminderapi.model.ReminderDTO;
import reminderapi.model.ReminderForm;

@Component
public class DataTypeConverter {

  public ReminderDTO toReminderDTO(ReminderEntity entity) {
    return new ReminderDTO(
      entity.getId(),
      entity.getTitle(),
      entity.getDescription(),
      entity.getDueDate(),
      entity.getPriority(),
      entity.getIsCompleted(),
      entity.getCreatedAt(),
      entity.getUpdatedAt()
    );
  }

  public ReminderRecord toReminderRecord(ReminderForm form) {
    return new ReminderRecord(
      null,
      form.getTitle(),
      form.getDescription(),
      form.getDueDate(),
      form.getPriority(),
      form.getIsCompleted(),
      LocalDateTime.now(),
      LocalDateTime.now()
    );
  }

  public ReminderEntity toReminderEntity(ReminderRecord record) {
    return new ReminderEntity(
      record.getId(),
      record.getTitle(),
      record.getDescription(),
      record.getDueDate(),
      record.getPriority(),
      record.isCompleted(),
      toOffsetDateTime(record.getCreatedAt()),
      toOffsetDateTime(record.getUpdatedAt())
    );
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
