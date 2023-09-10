package au.com.schmick.test.extensions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Disables retryable writes for TestContainer orchestrated mongodb (standalone).
 * See <a href="https://www.mongodb.com/docs/manual/core/retryable-writes/#prerequisites">PreRequisites</a>.
 */
public class MongoDBContainerExtension implements BeforeAllCallback, AfterAllCallback {

  MongoDBContainer mongoDBContainer;

  public void beforeAll(ExtensionContext context) {

    mongoDBContainer = new MongoDBContainer(
        DockerImageName.parse("mongo:4.4.18"));

    mongoDBContainer.start();
    System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl()+"?retryWrites=false");
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception {

    mongoDBContainer.close();
  }
}
