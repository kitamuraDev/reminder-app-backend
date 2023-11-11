package app.reminderappbackend.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import jakarta.validation.constraints.Min;
import reminderapi.model.ReminderForm;

@Mapper
public interface ReminderRepository {

  /**
   * IDに紐づくリマインダーを取得するマッパー
   *
   * @param id リマインダーを取得する一意ID
   * @return Optional<ReminderRecord>
   */
  @SelectProvider(type = ReminderSqlProvider.class, method = "selectById")
  Optional<ReminderRecord> selectById(Long id);

  /**
   * limitとoffsetに基づくリマインダーのリストを取得するマッパー
   *
   * @param limit リストに含まれるリソースの最大値
   * @param offset オフセット
   * @return List<ReminderRecord>
   */
  @SelectProvider(type = ReminderSqlProvider.class, method = "selectList")
  List<ReminderRecord> selectList(@Param("limit") Integer limit, @Param("offset") Long offset);

  /**
   * リマインダー作成するマッパー
   *
   * @param record クライアントからPOSTされるフォームが入ったrecord
   */
  @Options(useGeneratedKeys = true, keyProperty = "id") // 自動採番されたPK（id）を引数の form にセットする
  @InsertProvider(type = ReminderSqlProvider.class, method = "insert")
  void insert(ReminderRecord record);

  /**
   * リマインダー更新するマッパー
   *
   * @param id 更新するリマインダーのID
   * @param reminderForm クライアントからPOSTされるフォーム
   */
  @UpdateProvider(type = ReminderSqlProvider.class, method = "update")
  void update(@Param("id") Long id, @Param("reminderForm") ReminderForm reminderForm);

  /**
   * リマインダー削除するマッパー
   *
   * @param id 削除するリマインダーのID
   */
  @DeleteProvider(type = ReminderSqlProvider.class, method = "delete")
  void delete(@Min(1) Long id);

}
