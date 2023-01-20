package au.com.schmick.rezrecall.db.model;

import lombok.ToString;

@ToString
public enum RezType {
  BOOK("Book"), ART_LESSON("Art Lesson"), MATH_LESSON("Math Lesson"), LANG_LESSON(
      "Language Lesson"), SCIENCE_LESSON("Science Lesson");

  private String description;

  RezType(String description) {
    this.description = description;
  }


}
