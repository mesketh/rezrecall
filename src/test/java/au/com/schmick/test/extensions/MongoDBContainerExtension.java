package au.com.schmick.test.extensions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoDBContainerExtension implements BeforeAllCallback, AfterAllCallback {

  private MongoDBContainer mongoDBContainer;

  @Override
  public void beforeAll(ExtensionContext context) {

    MongoDBContainer mongoDBContainer = new MongoDBContainer(
        DockerImageName.parse("mongo:4.4.18"));

    mongoDBContainer.start();
    System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
  }

  @Override
  public void afterAll(ExtensionContext context) {
    // do nothing, Testcontainers handles container shutdown
  }
}
