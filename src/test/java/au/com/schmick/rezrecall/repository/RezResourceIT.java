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
import au.com.schmick.rezrecall.db.repository.RezRepository;
import au.com.schmick.rezrecall.service.RezService;
import au.com.schmick.test.extensions.MongoDBContainerExtension;
import com.mongodb.client.result.DeleteResult;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@Testcontainers
@ExtendWith(MongoDBContainerExtension.class)
@ExtendWith(SpringExtension.class)
@DataMongoTest(excludeAutoConfiguration = {RezrecallApplication.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
            SpringBootApplication.class})})
@Import({RezService.class, RezRepository.class})
@ActiveProfiles(profiles = "test")
@Slf4j
class RezResourceIT {

  @Autowired
  ReactiveMongoTemplate mongoTemplate;

  @Autowired
  RezRepository repository;

  @Autowired
  RezService rezService;

  //  @BeforeEach
  public Rezource insertTestData() {
    var rezource = Rezource.builder()
        .primaryAuthor(Author.builder().firstName("George").familyName("Orwell").build())
        .title("Nineteen Eighty Four")
        .type(RezType.BOOK)  // TODO builder default not working for enum?
        .location("L-1-3")
        .build();
    rezource = mongoTemplate.insert(rezource, "rezources").block();
    log.debug("Inserting rezource {}", rezource);
    return rezource;
  }

  @Test
  @DisplayName("Test basic insert and findAll")
  public void givenRepositoryWithSingleResource_whenFindingAllResources_thenSuccess() {

    insertTestData();
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

    var rezource = insertTestData();
    Flux<Rezource> rezourceFlux = rezService.searchResource(Rezource.builder().build());
//    Rezource rezource = insertTestData();
    StepVerifier.create(rezourceFlux.log())
        .expectSubscription()
        .expectNext(rezource)
        .verifyComplete();
  }

  @Test
  @DisplayName("Service layer test search - find by single criteria (family name)")
  public void givenRepositoryWithResources_whenSearchingByAuthorFamilyName_thenSuccess() {

    var rezource = insertTestData();
    RezourceBuilder orwell = Rezource.builder()
        .primaryAuthor(Author.builder().familyName("Orwell").build())
        .type(null); // overwrite default type
    Flux<Rezource> rezourceFlux = rezService.searchResource(orwell.build());
    StepVerifier.create(rezourceFlux.log())
        .expectSubscription()
        .recordWith(ArrayList::new)
        .expectNextCount(1)
        .expectRecordedMatches(c -> c.contains(rezource))
        .verifyComplete();
  }

  @AfterEach
  void deleteTestData() {
    Optional.ofNullable(mongoTemplate.getMongoDatabase().block(Duration.ofMillis(5)))
        .map(mdb -> mdb.getCollection("rezources"))
        .ifPresent(c -> c.deleteMany(BsonDocument.parse("{}")).subscribe(
            new Subscriber<DeleteResult>() {
              @Override
              public void onSubscribe(Subscription subscription) {
                subscription.request(1);
              }

              @Override
              public void onNext(DeleteResult deleteResult) {
                log.debug("deleted? {}",
                    deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() > 0);

              }


              @Override
              public void onError(Throwable throwable) {
                log.error("Failed to drop rezources collection after test", throwable);
              }

              @Override
              public void onComplete() {
                log.info("Dropped rezources successfully 🚀");
              }
            }));
  }
}
