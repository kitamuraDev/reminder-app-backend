package app.reminderappbackend.Repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReminderRepository {

  @Select("SELECT * FROM REMINDER WHERE id = 1")
  ReminderRecord selectById();
}
