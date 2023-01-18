package au.com.schmick.rezrecall.repository;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import au.com.schmick.rezrecall.db.config.AppConfig;
import au.com.schmick.rezrecall.db.model.Author;
import au.com.schmick.rezrecall.db.model.Rezource;
import com.mongodb.reactivestreams.client.MongoCollection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@Testcontainers
@ContextConfiguration(classes = {AppConfig.class})
@DataMongoTest
//@DataMongoTest(includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
//    AppConfig.class})})
@Profile("test")
@Slf4j
class RezrecallQueryIT {

  @Container
  private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(
      DockerImageName.parse("mongo:4.4.18"))
      .withEnv("MONGO_INITDB_ROOT_USERNAME", "testuser")
      .withEnv("MONGO_INITDB_ROOT_PASSWORD", "password")
      .withLogConsumer(new Slf4jLogConsumer(log));

  static {
    mongoDBContainer.start();
  }

  @Autowired
  ReactiveMongoTemplate mongoTemplate;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    // TODO replace with JUnit Extension see here: https://www.baeldung.com/spring-dynamicpropertysource#an-alternative-test-fixtures
    registry.add("spring.data.mongodb.uri", RezrecallQueryIT::uri);
  }

  private static String uri() {
    log.debug("<><><><><><><><><><><> ------------> {}", mongoDBContainer.getReplicaSetUrl());
    return mongoDBContainer.getReplicaSetUrl();
  }

  @Test
  @DisplayName("Test basic insert and findAll")
  public void givenRepositoryWithSingleResource_whenFindingAllResources_thenSuccess() {
    log.debug(mongoTemplate.insert(Rezource.builder()
            .author(Author.builder().firstName("George").familyName("Orwell").build())
            .title("Nineteen Eighty Four")
            .location("L-1-3"), "rezources")
        .toString());

    Flux<Rezource> matchingRezources = mongoTemplate.findAll(Rezource.class, "rezources");

    StepVerifier.create(matchingRezources)
        .expectNextCount(1)
        .assertNext(
            r -> MatcherAssert.assertThat("Query returned null rezource", r, is(notNullValue())))
        .assertNext(
            r -> MatcherAssert.assertThat("Query mismatched results", r, hasProperty("title",
                Matchers.hasValue("Nineteen Eighty Four"))))
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
