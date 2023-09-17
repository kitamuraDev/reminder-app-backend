package app.reminderappbackend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import reminderapi.controller.HealthApi;

@RestController
public class HealthCheckController implements HealthApi {

  @Override
  public ResponseEntity<Void> healthGet() {
    return ResponseEntity.ok().build();
  }

}
