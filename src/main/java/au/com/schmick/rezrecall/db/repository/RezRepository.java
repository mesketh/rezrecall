package au.com.schmick.rezrecall.db.repository;

import au.com.schmick.rezrecall.db.model.Rezource;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RezRepository extends ReactiveCrudRepository<Rezource, Integer>,
    ReactiveMongoRepository<Rezource, Integer> {

  public Flux<Rezource> findByTitle(String title);

  Flux<Rezource> findByTitleContaining(String title);
}
