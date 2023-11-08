package app.reminderappbackend.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.reminderappbackend.repository.ReminderRecord;
import app.reminderappbackend.repository.ReminderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import reminderapi.model.ReminderForm;

@Service
@RequiredArgsConstructor
public class ReminderService {

  private final ReminderRepository reminderRepository;

  /**
   * IDに紐づくリマインダーを取得するサービス
   *
   * @param id リマインダーを取得する一意ID
   * @return ReminderEntity or ReminderEntityNotFoundException
   */
  public ReminderEntity findById(Long id) {
    var optRecord = reminderRepository.selectById(id);
    var entity =
        optRecord
            .map(record -> toReminderEntity(record))
            .orElseThrow(() -> new ReminderEntityNotFoundException(id));

    return entity;
  }

  /**
   * limitとoffsetに基づくリマインダーのリストを取得するサービス
   *
   * @param limit リストに含まれるリソースの最大値
   * @param offset オフセット
   * @return List<ReminderEntity>
   */
  public List<ReminderEntity> findList(Integer limit, Long offset) {
    List<ReminderRecord> recordList = reminderRepository.selectList(limit, offset);
    var entityList = recordList.stream()
      .map(record -> toReminderEntity(record))
      .collect(Collectors.toList());

    return entityList;
  }

  /**
   * リマインダー作成するサービス
   *
   * @param reminderForm クライアントからPOSTされるフォーム
   * @return ReminderEntity
   */
  public ReminderEntity create(@Valid ReminderForm form) {
    // form を record に詰めて、Repositoryへ渡す
    var record = toReminderRecord(form);
    reminderRepository.insert(record);

    // record to entity
    var entity = toReminderEntity(record);

    return entity;
  }

  /**
   * リマインダー更新するサービス
   *
   * @param id 更新するリマインダーのID
   * @param reminderForm クライアントからPOSTされるフォーム
   * @return ReminderEntity
   */
  public ReminderEntity update(@Min(1) Long id, @Valid ReminderForm reminderForm) {
    // idチェック
    reminderRepository.selectById(id)
      .orElseThrow(() -> new ReminderEntityNotFoundException(id));

    reminderRepository.update(id, reminderForm);

    return findById(id);
  }

  /**
   * リマインダー削除するサービス
   *
   * @param id 削除するリマインダーのID
   */
  public void delete(@Min(1) Long id) {
    // idチェック
    reminderRepository.selectById(id)
      .orElseThrow(() -> new ReminderEntityNotFoundException(id));

    reminderRepository.delete(id);
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
