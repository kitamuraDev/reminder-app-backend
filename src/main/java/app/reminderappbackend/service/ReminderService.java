package app.reminderappbackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.reminderappbackend.repository.ReminderRecord;
import app.reminderappbackend.repository.ReminderRepository;
import app.reminderappbackend.util.DataTypeConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import reminderapi.model.ReminderForm;

@Service
@RequiredArgsConstructor
public class ReminderService {

  private final ReminderRepository reminderRepository;
  private final DataTypeConverter converter;

  /**
   * IDに紐づくリマインダーを取得するサービス
   *
   * @param id リマインダーを取得する一意ID
   * @return ReminderEntity or ReminderEntityNotFoundException
   */
  public ReminderEntity findById(Long id) {
    var optRecord = reminderRepository.selectById(id);
    var entity = optRecord
      .map(record -> converter.toReminderEntity(record))
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
      .map(record -> converter.toReminderEntity(record))
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
    var record = converter.toReminderRecord(form);
    reminderRepository.insert(record);

    // record to entity
    var entity = converter.toReminderEntity(record);

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

}
