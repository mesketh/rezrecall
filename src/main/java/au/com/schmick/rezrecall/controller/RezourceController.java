package au.com.schmick.rezrecall.controller;

import au.com.schmick.rezrecall.db.model.Rezource;
import au.com.schmick.rezrecall.service.RezService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Rezource api", description = "API for searching for Rezources")
public class RezourceController {

  @Autowired
  private RezService rezService;

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the book",
          content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = Rezource.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
      @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)})
  // @formatter:on
  @PostMapping(path="/search", produces = {MediaType.APPLICATION_NDJSON_VALUE })
  public ResponseEntity<Flux<Rezource>> searchRezource(@RequestBody Rezource rezource) {

    // query by example - delegate to service and map to Mono list of results
    return ResponseEntity.of(Optional.of(rezService.searchResource(rezource)));
  }

}
