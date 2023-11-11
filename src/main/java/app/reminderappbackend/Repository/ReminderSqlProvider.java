package app.reminderappbackend.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.jdbc.SQL;

import jakarta.validation.constraints.Min;
import reminderapi.model.ReminderForm;

public class ReminderSqlProvider implements ProviderMethodResolver {

  public String selectById(Long id) {
    return new SQL() {{
      SELECT("*");
      FROM("REMINDER");
      WHERE("id = #{id}");
    }}.toString();
  }

  public String selectList(@Param("limit") Integer limit, @Param("offset") Long offset) {
    return new SQL() {{
      SELECT("*");
      FROM("REMINDER");
      LIMIT(limit);
      OFFSET(offset);
    }}.toString();
  }

  public String insert(ReminderRecord record) {
    return new SQL() {{
      INSERT_INTO("REMINDER");
      VALUES("title", "#{title}");
      VALUES("description", "#{description}");
      VALUES("due_date", "#{dueDate}");
      VALUES("priority", "#{priority}");
      VALUES("is_completed", "#{isCompleted}");
      VALUES("created_at", "#{createdAt}");
      VALUES("updated_at", "#{updatedAt}");
    }}.toString();
  }

  public String update(@Param("id") Long id, @Param("reminderForm") ReminderForm reminderForm) {
    return new SQL() {{
      UPDATE("REMINDER");
      SET("title = #{reminderForm.title}");
      SET("description = #{reminderForm.description}");
      SET("due_date = #{reminderForm.dueDate}");
      SET("priority = #{reminderForm.priority}");
      SET("is_completed = #{reminderForm.isCompleted}");
      SET("updated_at = CURRENT_TIMESTAMP");
      WHERE("id = #{id}");
    }}.toString();
  }

  public String delete(@Min(1) Long id) {
    return new SQL() {{
      DELETE_FROM("REMINDER");
      WHERE("id = #{id}");
    }}.toString();
  }

}
