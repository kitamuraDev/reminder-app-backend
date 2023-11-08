package app.reminderappbackend.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
  @Select("SELECT * FROM REMINDER WHERE id = #{id}")
  Optional<ReminderRecord> selectById(Long id);

  /**
   * limitとoffsetに基づくリマインダーのリストを取得するマッパー
   *
   * @param limit リストに含まれるリソースの最大値
   * @param offset オフセット
   * @return List<ReminderRecord>
   */
  @Select("SELECT * FROM REMINDER LIMIT #{limit} OFFSET #{offset}")
  List<ReminderRecord> selectList(@Param("limit") Integer limit, @Param("offset") Long offset);

  /**
   * リマインダー作成するマッパー
   *
   * @param record クライアントからPOSTされるフォームが入ったrecord
   */
  @Options(useGeneratedKeys = true, keyProperty = "id") // 自動採番されたPK（id）を引数の form にセットする
  @Insert(
      "INSERT INTO REMINDER (title, description, due_date, priority, is_completed, created_at, updated_at) VALUES (#{title}, #{description}, #{dueDate}, #{priority}, #{isCompleted}, #{createdAt}, #{updatedAt})")
  void insert(ReminderRecord record);

  /**
   * リマインダー更新するマッパー
   *
   * @param id 更新するリマインダーのID
   * @param reminderForm クライアントからPOSTされるフォーム
   */
  @Update("UPDATE REMINDER SET title = #{reminderForm.title}, description = #{reminderForm.description}, due_date = #{reminderForm.dueDate}, priority = #{reminderForm.priority}, is_completed = #{reminderForm.isCompleted}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
  void update(@Param("id") Long id, @Param("reminderForm") ReminderForm reminderForm);

  /**
   * リマインダー削除するマッパー
   *
   * @param id 削除するリマインダーのID
   */
  @Delete("DELETE FROM REMINDER WHERE id = #{id}")
  void delete(@Min(1) Long id);

}
