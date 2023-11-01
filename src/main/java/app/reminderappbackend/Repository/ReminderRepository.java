package app.reminderappbackend.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import reminderapi.model.ReminderForm;

@Mapper
public interface ReminderRepository {

  @Select("SELECT * FROM REMINDER WHERE id = #{id}")
  Optional<ReminderRecord> selectById(Long id);

  @Select("SELECT * FROM REMINDER LIMIT #{limit} OFFSET #{offset}")
  List<ReminderRecord> selectList(@Param("limit") Integer limit, @Param("offset") Long offset);

  @Options(useGeneratedKeys = true, keyProperty = "id") // 自動採番されたPK（id）を引数の form にセットする
  @Insert(
      "INSERT INTO REMINDER (title, description, due_date, priority, is_completed, created_at, updated_at) VALUES (#{title}, #{description}, #{dueDate}, #{priority}, #{isCompleted}, #{createdAt}, #{updatedAt})")
  void insert(ReminderRecord record);

  @Update("UPDATE REMINDER SET title = #{reminderForm.title}, description = #{reminderForm.description}, due_date = #{reminderForm.dueDate}, priority = #{reminderForm.priority}, is_completed = #{reminderForm.isCompleted}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
  void update(@Param("id") Long id, @Param("reminderForm") ReminderForm reminderForm);
}
