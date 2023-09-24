package app.reminderappbackend.Repository;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReminderRepository {

  @Select("SELECT * FROM REMINDER WHERE id = #{id}")
  Optional<ReminderRecord> selectById(Long id);
}
