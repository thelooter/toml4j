package de.thelooter.toml;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringTest {

  @Test
  public void should_get_string() {
    Toml toml = new Toml().read("a = \"a\"");

    assertEquals("a", toml.getString("a"));
  }

  @Test
  public void should_get_empty_string() {
    Toml toml = new Toml().read("a = \"\"");
    assertEquals("", toml.getString("a"));
  }

  @Test
  public void should_get_empty_string_with_trailing_new_line() {
    Toml toml = new Toml().read("a = \"\"\n");
    assertEquals("", toml.getString("a"));
  }
  
  @Test
  public void should_get_basic_multiline_string() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_get_basic_multiline_string.toml"));
    
    assertEquals(toml.getString("ref"), toml.getString("one_line"));
    assertEquals(toml.getString("ref"), toml.getString("many_lines"));
  }
  
  @Test
  public void should_get_multiline_string_without_new_lines() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_get_multiline_string_without_new_lines.toml"));
    
    assertEquals(toml.getString("ref"), toml.getString("multi1"));
    assertEquals(toml.getString("ref"), toml.getString("multi2"));
  }
  
  @Test
  public void should_get_literal_string() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_get_literal_string.toml"));
    
    assertEquals("C:\\Users\\nodejs\\templates", toml.getString("winpath"));
    assertEquals("\\\\ServerX\\admin$\\system32\\", toml.getString("winpath2"));
    assertEquals("Tom \"Dubs\" Preston-Werner", toml.getString("quoted"));
    assertEquals("<\\i\\c*\\s*>", toml.getString("regex"));
  }
  
  @Test
  public void should_get_multiline_literal_string() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_get_multiline_literal_string.toml"));
    
    assertTrue(toml.getString("empty_line").isEmpty());
    assertEquals(toml.getString("regex2_ref"), toml.getString("regex2"));
    assertEquals(toml.getString("lines_ref"), toml.getString("lines"));
  }

  @Test
  public void should_support_special_characters_in_strings() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_support_special_characters_in_strings.toml"));

    assertEquals("\" \t \n \r \\ \b \f", toml.getString("key"));
  }

  @Test
  public void should_support_unicode_characters_in_strings() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_support_special_characters_in_strings.toml"));

    assertEquals("more or less ±", toml.getString("unicode_key"));
    assertEquals("more or less ±", toml.getString("unicode_key_uppercase"));
  }

  @Test
  public void should_fail_on_reserved_special_character_in_strings() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("key=\"\\m\""));
  }

  @Test
  public void should_fail_on_escaped_slash() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("key=\"\\/\""));
  }

  @Test
  public void should_fail_on_text_after_literal_string() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a = ' ' jdkf"));
  }
  
  @Test
  public void should_fail_on_unterminated_literal_string() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a = 'some text"));
  }
  
  @Test
  public void should_fail_on_multiline_literal_string_with_malformed_comment() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a = '''some\n text\n''\nb = '''1'''"));
  }
  
  @Test
  public void should_fail_on_unterminated_multiline_literal_string() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a = '''some\n text\n''"));
  }
  
  @Test
  public void should_fail_on_unterminated_multiline_literal_string_on_single_line() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a = '''some text''"));
  }

  @Test
  public void should_fail_on_text_outside_multiline_string() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a = \"\"\" \"\"\" jdkf"));
  }
  
  @Test
  public void should_fail_on_unterminated_multiline_string() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a = \"\"\"some text\"\""));
  }

}
