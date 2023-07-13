package au.com.schmick.tools;

import static net.datafaker.transformations.Field.compositeField;
import static net.datafaker.transformations.Field.field;

import au.com.schmick.rezrecall.db.model.RezType;
import au.com.schmick.rezrecall.db.model.Rezource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.datafaker.Faker;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.JsonTransformer.JsonTransformerBuilder.FormattedAs;
import net.datafaker.transformations.Schema;

public class TestDataGenerator {

  public static void main(String[] args) {
    try {
      new TestDataGenerator().generateTestData();
    } catch (IOException e) {
      System.err.println("Failed to generate and save rezource test data");
    }
  }

  public void generateTestData() throws IOException {

    // exportedData resource records
    Faker faker = new Faker();

    JsonTransformer<Rezource> transformer = JsonTransformer.<Rezource>builder().formattedAs(
        FormattedAs.JSON_ARRAY).build();
    String exportedData = transformer.generate(rezourceSchema(faker), 200);

    System.out.println(exportedData);

    File dataFile = Path.of("./data/generated-rezource-data.json").toFile();
    if (dataFile.exists()) {
      dataFile.delete();
    }

    Files.writeString(dataFile.toPath(), exportedData);
  }

  private Schema rezourceSchema(Faker faker) {

    return Schema.of(compositeField("primaryAuthor",
            new Field[]{
                field("firstName", faker.name()::firstName),
                field("familyName", faker.name()::lastName)}),
        field("title", faker.book()::title),
//        field("type", RezType.BOOK::name),
        field("type", this::buildRezType),
        field("_class", () -> "au.com.schmick.rezrecall.db.model.Rezource"),
        field("location",
            () -> faker.expression("#{regexify '(L|R|B)-[1-4]-[1-4]'}")));
  }

  private String buildRezType() {
    return RezType.values()[Math.round((float) (Math.random() * (RezType.values().length - 1)))].name();
  }

}
