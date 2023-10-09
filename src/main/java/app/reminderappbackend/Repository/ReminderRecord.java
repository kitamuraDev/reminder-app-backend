package app.reminderappbackend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Value;

@Value
public class ReminderRecord {
  Long id;
  String title;
  String description;
  LocalDate dueDate;
  Integer priority;
  boolean isCompleted;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
