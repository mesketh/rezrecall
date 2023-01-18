package au.com.schmick.rezrecall.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import au.com.schmick.rezrecall.db.model.Author;
import au.com.schmick.rezrecall.db.model.Rezource;
import au.com.schmick.test.extensions.MongoDBContainerExtension;
import com.mongodb.reactivestreams.client.MongoCollection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@Testcontainers
@ExtendWith(MongoDBContainerExtension.class)
@DataMongoTest
@ActiveProfiles("test")
@Slf4j
class RezrecallQueryIT {


  @Autowired
  ReactiveMongoTemplate mongoTemplate;

  @Test
  @DisplayName("Test basic insert and findAll")
  public void givenRepositoryWithSingleResource_whenFindingAllResources_thenSuccess() {
    log.debug(mongoTemplate.insert(Rezource.builder()
        .author(Author.builder().firstName("George").familyName("Orwell").build())
        .title("Nineteen Eighty Four")
        .location("L-1-3"), "rezources").block().toString());

    Flux<Rezource> matchingRezources = mongoTemplate.findAll(Rezource.class, "rezources").log();

    StepVerifier.create(matchingRezources, 1)
//        .thenAwait(Duration.ofSeconds(2))
        .assertNext(r ->
        {
          MatcherAssert.assertThat("Query returned null rezource", r, is(notNullValue()));
          MatcherAssert.assertThat("Query mismatched results", r,
              hasProperty("title", equalTo("Nineteen Eighty Four")));
        })
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
