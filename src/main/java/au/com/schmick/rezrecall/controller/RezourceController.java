package au.com.schmick.rezrecall.controller;

import au.com.schmick.rezrecall.db.model.Rezource;
import au.com.schmick.rezrecall.service.RezService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class RezourceController {

  @Autowired
  private RezService rezService;

  @PostMapping(path="/search", produces = {MediaType.APPLICATION_NDJSON_VALUE })
  public ResponseEntity<Flux<Rezource>> searchRezource(@RequestBody Rezource rezource) {

    // query by example - delegate to service and map to Mono list of results
    return ResponseEntity.of(Optional.of(rezService.searchResource(rezource)));
  }

}
