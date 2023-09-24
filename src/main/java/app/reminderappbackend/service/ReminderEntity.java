package app.reminderappbackend.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

@Value
public class ReminderEntity {
  Long id;
  String title;
  String description;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  LocalDate dueDate;

  Integer priority;
  Boolean isCompleted;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  OffsetDateTime updatedAt;
}
