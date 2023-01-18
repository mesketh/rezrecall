package au.com.schmick.rezrecall.db.repository;

import au.com.schmick.rezrecall.db.model.Author;
import au.com.schmick.rezrecall.db.model.RezType;
import au.com.schmick.rezrecall.db.model.Rezource;
import java.util.List;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RezRepository extends ReactiveSortingRepository<Rezource, Integer> {

  Flux<Rezource> findAllByTitle(String title);
  Flux<Rezource> findAllByType(RezType type);

  Flux<Rezource> findByAuthors(List<Author> author);

}
