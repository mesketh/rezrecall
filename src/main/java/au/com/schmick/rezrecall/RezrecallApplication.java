package au.com.schmick.rezrecall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@SpringBootApplication
@Slf4j
public class RezrecallApplication {

  @Autowired
  ReactiveMongoTemplate template;

  public static void main(String[] args) {
    SpringApplication.run(RezrecallApplication.class, args);
  }

  @EventListener(classes = {ApplicationReadyEvent.class})
  public void onApplicationStartLoadData(ApplicationReadyEvent event) {

//    Mono<Rezource> testRecord = template.save(Rezource.builder()
//        .primaryAuthor(Author.builder().firstName("George").familyName("Orwell").build())
//        .title("Nineteen Eighty Four")
//        .type(RezType.BOOK)  // TODO builder default not working for enum?
//        .location("L-1-3").build(), "rezources");
//
//    log.debug("Saved test record? {}", Objects.nonNull(testRecord.block()));
  }

}
