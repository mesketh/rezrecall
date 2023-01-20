package au.com.schmick.rezrecall.db.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.util.Collection;
import java.util.Set;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = {"au.com.schmick.rezrecall.db.repository"})
public class DbConfig extends AbstractReactiveMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "rezourcedb";
  }

  @Override
  public MongoClient reactiveMongoClient() {
    return MongoClients.create();
  }

  @Override
  protected Collection<String> getMappingBasePackages() {
    return Set.of("au.com.schmick.rezrecall.db.model");
  }

  @Override
  public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory databaseFactory,
      MappingMongoConverter mongoConverter) {
    return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName()) ;
  }
}
