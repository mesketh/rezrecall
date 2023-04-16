package au.com.schmick.rezrecall.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import au.com.schmick.rezrecall.RezrecallApplication;
import au.com.schmick.rezrecall.db.model.Author;
import au.com.schmick.rezrecall.db.model.RezType;
import au.com.schmick.rezrecall.db.model.Rezource;
import au.com.schmick.rezrecall.db.model.Rezource.RezourceBuilder;
import au.com.schmick.rezrecall.service.RezService;
import au.com.schmick.test.extensions.MongoDBContainerExtension;
import com.mongodb.reactivestreams.client.MongoCollection;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@Testcontainers
@ExtendWith(MongoDBContainerExtension.class)
@DataMongoTest(excludeAutoConfiguration = {RezrecallApplication.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
            SpringBootApplication.class})})
@Import({RezService.class})
@ActiveProfiles(profiles = "test")
@Slf4j
class RezResourceIT {

  @Autowired
  ReactiveMongoTemplate mongoTemplate;

  @Autowired
  RezService rezService;

  @BeforeEach
  void insertTestData() {
    mongoTemplate.insert(Rezource.builder()
        .primaryAuthor(Author.builder().firstName("George").familyName("Orwell").build())
        .title("Nineteen Eighty Four")
        .type(RezType.BOOK)  // TODO builder default not working for enum?
        .location("L-1-3")
        .build(), "rezources").block();
  }

  @Test
  @DisplayName("Test basic insert and findAll")
  public void givenRepositoryWithSingleResource_whenFindingAllResources_thenSuccess() {

    Flux<Rezource> matchingRezources = mongoTemplate.findAll(Rezource.class, "rezources");

    StepVerifier.create(matchingRezources.log())
        .assertNext(r ->
        {
          MatcherAssert.assertThat("Query returned null rezource", r, is(notNullValue()));
          MatcherAssert.assertThat("Query mismatched results", r,
              hasProperty("title", equalTo("Nineteen Eighty Four")));
          MatcherAssert.assertThat("Query mismatched results", r,
              hasProperty("location", equalTo("L-1-3")));
          MatcherAssert.assertThat("Query mismatched results", r,
              hasProperty("type", equalTo(RezType.BOOK)));
        })
        .thenConsumeWhile(
            Objects::nonNull) // doing this because can't stop context loading the app with the example record inserted.
//        .thenCancel().verify();
        .verifyComplete();
  }

  @Test
  @DisplayName("Service layer test search - blank criteria (fetch all)")
  public void givenRepositoryWithResources_whenSearchingForAll_thenSuccess() {

    Flux<Rezource> rezourceFlux = rezService.searchResource(Rezource.builder().build());
    StepVerifier.create(rezourceFlux.log())
        .expectNextCount(1).verifyComplete();
  }
  @Test
  @DisplayName("Service layer test search - find by single criteria (family name)")
  public void givenRepositoryWithResources_whenSearchingByAuthorFamilyName_thenSuccess() {

    RezourceBuilder orwell = Rezource.builder()
        .primaryAuthor(Author.builder().familyName("Orwell").build()).type(null); // overwrite default type
    Flux<Rezource> rezourceFlux = rezService.searchResource(orwell.build());
    StepVerifier.create(rezourceFlux.log())
        .expectNextCount(1).verifyComplete();
  }

  @AfterEach
  void deleteTestData() {
    Optional.ofNullable(mongoTemplate.getMongoDatabase().block())
        .map(mdb -> mdb.getCollection("rezources"))
        .ifPresent(
            MongoCollection::drop);
  }
}
