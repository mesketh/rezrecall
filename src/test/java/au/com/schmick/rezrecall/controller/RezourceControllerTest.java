package au.com.schmick.rezrecall.controller;

import static org.mockito.Mockito.when;

import au.com.schmick.rezrecall.config.ControllerConfig;
import au.com.schmick.rezrecall.db.model.Author;
import au.com.schmick.rezrecall.db.model.RezType;
import au.com.schmick.rezrecall.db.model.Rezource;
import au.com.schmick.rezrecall.service.RezService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest
@Import(ControllerConfig.class)
@ExtendWith(MockitoExtension.class)
class RezourceControllerTest {

  private Rezource testAuthor;

  @MockBean
  private RezService mockRezService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebTestClient webTestClient;

  @SneakyThrows
  private String toJson(Rezource testAuthor) {
    return objectMapper.writeValueAsString(testAuthor);
  }

  @BeforeEach
  void setUp() {
  }

  @Test
  public void givenSearchForSingleBook_whenMatching_thenReturnBook() {

    testAuthor = Rezource.builder()
        .id(BigInteger.valueOf(1000))
        .title("A Tale Of Two Cities")
        .primaryAuthor(Author.builder().firstName("Charles").familyName("Dickens").build())
        .type(RezType.BOOK).build();
    when(mockRezService
        .searchResource(ArgumentMatchers.any(Rezource.class)))
        .thenReturn(Flux.just(testAuthor));

    FluxExchangeResult<Rezource> result = webTestClient.post()
        .uri("/api/v1/search")
        .accept(MediaType.APPLICATION_NDJSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(toJson(testAuthor))
        .exchange()
        .expectStatus()
        .isOk()
        .returnResult(Rezource.class);

    StepVerifier
        .create(result.getResponseBody())
        .expectNext(testAuthor)
        .expectNextCount(0)
        .thenCancel()
        .verify();
  }

  @AfterEach
  void tearDown() {
  }
}