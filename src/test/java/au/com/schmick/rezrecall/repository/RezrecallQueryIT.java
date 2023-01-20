package au.com.schmick.rezrecall.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import au.com.schmick.rezrecall.RezrecallApplication;
import au.com.schmick.rezrecall.db.model.Author;
import au.com.schmick.rezrecall.db.model.RezType;
import au.com.schmick.rezrecall.db.model.Rezource;
import au.com.schmick.test.extensions.MongoDBContainerExtension;
import com.mongodb.reactivestreams.client.MongoCollection;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@Testcontainers
@ExtendWith(MongoDBContainerExtension.class)
@DataMongoTest(excludeAutoConfiguration = {RezrecallApplication.class}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {SpringBootApplication.class})})
@ActiveProfiles(profiles = "test")
@Slf4j
class RezrecallQueryIT {

  @Autowired
  ReactiveMongoTemplate mongoTemplate;

  @Test
  @DisplayName("Test basic insert and findAll")
  public void givenRepositoryWithSingleResource_whenFindingAllResources_thenSuccess() {
    mongoTemplate.insert(Rezource.builder()
        .primaryAuthor(Author.builder().firstName("George").familyName("Orwell").build())
        .title("Nineteen Eighty Four")
        .type(RezType.BOOK)  // TODO builder default not working for enum?
        .location("L-1-3")
        .build(), "rezources").block();

    Flux<Rezource> matchingRezources = mongoTemplate.findAll(Rezource.class, "rezources").log();

    StepVerifier.create(matchingRezources)
        .assertNext(r ->
        {
          MatcherAssert.assertThat("Query returned null rezource", r, is(notNullValue()));
          MatcherAssert.assertThat("Query mismatched results", r,
              hasProperty("title", equalTo("Nineteen Eighty Four")));
        })
        .thenConsumeWhile(r -> Objects.nonNull(r))
//        .thenCancel().verify();
        .verifyComplete();
  }

  @AfterEach
  void cleanUp() {
    Optional.ofNullable(mongoTemplate.getMongoDatabase().block())
        .map(mdb -> mdb.getCollection("rezources"))
        .ifPresent(
            MongoCollection::drop);
  }
}
