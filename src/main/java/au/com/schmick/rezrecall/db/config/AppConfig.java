package au.com.schmick.rezrecall.db.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.util.Collection;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories("au.com.schmick.rezrecall.db.repository")
public class AppConfig extends AbstractReactiveMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "rezource-store";
  }

  @Override
  @Bean
  public MongoClient reactiveMongoClient() {
    return MongoClients.create();
  }

  @Bean
  ReactiveMongoTemplate mongoTemplate(MongoClient mongoClient) {
    return new ReactiveMongoTemplate(mongoClient, getDatabaseName());
  }
  @Override
  protected Collection<String> getMappingBasePackages() {
    return Set.of("au.com.schmick.rezrecall.db.model");
  }


}
