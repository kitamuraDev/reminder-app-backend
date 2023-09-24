package app.reminderappbackend.service;

import app.reminderappbackend.Repository.ReminderRecord;
import app.reminderappbackend.Repository.ReminderRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderService {

  private final ReminderRepository reminderRepository;

  public ReminderEntity findById(Long id) {
    var record = reminderRepository.selectById(id);
    var entity = toReminderEntity(record);

    return entity;
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
