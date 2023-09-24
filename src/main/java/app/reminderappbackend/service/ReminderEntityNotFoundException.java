package app.reminderappbackend.service;

public class ReminderEntityNotFoundException extends RuntimeException {

  private Long reminderId;

  public ReminderEntityNotFoundException(Long id) {
    super("ReminderEntity (id = " + id + ") is not found.");
    this.reminderId = id;
  }
}
