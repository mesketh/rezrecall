package au.com.schmick.rezrecall.service;

import au.com.schmick.rezrecall.db.model.Rezource;
import au.com.schmick.rezrecall.db.repository.RezRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class RezService {

  @Autowired
  private RezRepository repository;

  public Flux<Rezource> searchResource(Rezource byCriteria) {

    ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll().
        withMatcher("title", GenericPropertyMatchers.exact().ignoreCase()).
        withMatcher("primaryAuthor.firstName", GenericPropertyMatchers.startsWith().ignoreCase()).
        withMatcher("primaryAuthor.lastName", GenericPropertyMatchers.startsWith().ignoreCase()).
        withMatcher("type", GenericPropertyMatchers.exact().caseSensitive()).
        withMatcher("location", GenericPropertyMatchers.startsWith().ignoreCase());

    return repository.findAll(Example.of(byCriteria, exampleMatcher));
  }
}
