package app.reminderappbackend.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
