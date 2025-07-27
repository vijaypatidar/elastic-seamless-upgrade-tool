package co.hyperflex.services.notifications;

public class PrecheckProgressChangeEvent implements NotificationEvent {
  @Override
  public NotificationEventType getType() {
    return NotificationEventType.PRECHECK_PROGRESS_CHANGE;
  }
}