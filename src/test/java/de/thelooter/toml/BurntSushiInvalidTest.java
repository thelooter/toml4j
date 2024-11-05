package de.thelooter.toml;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BurntSushiInvalidTest {
  private InputStream inputToml;
  
  @Test
  public void key_empty() {
    runInvalidTest("key-empty");
  }
  
  @Test
  public void key_hash() {
    runInvalidTest("key-hash");
  }
  
  @Test
  public void key_newline() {
    runInvalidTest("key-newline");
  }
  
  @Test
  public void key_open_bracket() {
    runInvalidTest("key-open-bracket");
  }
  
  @Test
  public void key_single_open_bracket() {
    runInvalidTest("key-single-open-bracket");
  }
  
  @Test
  public void key_start_bracket() {
    runInvalidTest("key-start-bracket");
  }
  
  @Test
  public void key_two_equals() {
    runInvalidTest("key-two-equals");
  }
  
  @Test
  public void string_bad_byte_escape() {
    runInvalidTest("string-bad-byte-escape");
  }
  
  @Test
  public void string_bad_escape() {
    runInvalidTest("string-bad-escape");
  }
  
  @Test
  public void string_byte_escapes() {
    runInvalidTest("string-byte-escapes");
  }
  
  @Test
  public void string_no_close() {
    runInvalidTest("string-no-close");
  }

  @Test
  public void table_array_implicit() {
    runInvalidTest("table-array-implicit");
  }

  @Test
  public void table_array_malformed_bracket() {
    runInvalidTest("table-array-malformed-bracket");
  }
  
  @Test
  public void table_array_malformed_empty() {
    runInvalidTest("table-array-malformed-empty");
  }
  
  @Test
  public void table_empty() {
    runInvalidTest("table-empty");
  }
  
  @Test
  public void table_nested_brackets_close() {
    runInvalidTest("table-nested-brackets-close");
  }
  
  @Test
  public void table_nested_brackets_open() {
    runInvalidTest("table-nested-brackets-open");
  }

  @Test
  public void empty_implicit_table() {
    runInvalidTest("empty-implicit-table");
  }

  @Test
  public void empty_table() {
    runInvalidTest("empty-table");
  }

  @Test
  public void array_mixed_types_ints_and_floats() {
    runInvalidTest("array-mixed-types-ints-and-floats");
  }

  @Test
  public void array_mixed_types_arrays_and_ints() {
    runInvalidTest("array-mixed-types-arrays-and-ints");
  }
  
  @Test
  public void array_mixed_types_strings_and_ints() {
    runInvalidTest("array-mixed-types-strings-and-ints");
  }

  @Test
  public void datetime_malformed_no_leads() {
    runInvalidTest("datetime-malformed-no-leads");
  }

  @Test
  public void datetime_malformed_no_secs() {
    runInvalidTest("datetime-malformed-no-secs");
  }

  @Test
  public void datetime_malformed_no_t() {
    runInvalidTest("datetime-malformed-no-t");
  }

  @Test
  public void datetime_malformed_no_z() {
    runInvalidTest("datetime-malformed-no-z");
  }

  @Test
  public void datetime_malformed_with_milli() {
    runInvalidTest("datetime-malformed-with-milli");
  }
  
  @Test
  public void duplicate_key_table() {
    runInvalidTest("duplicate-key-table");
  }
  
  @Test
  public void duplicate_keys() {
    runInvalidTest("duplicate-keys");
  }
  
  @Test
  public void duplicate_tables() {
    runInvalidTest("duplicate-tables");
  }

  @Test
  public void float_no_leading_zero() {
    runInvalidTest("float-no-leading-zero");
  }
  
  @Test
  public void float_no_trailing_digits() {
    runInvalidTest("float-no-trailing-digits");
  }

  @Test
  public void text_after_array_entries() {
    runInvalidTest("text-after-array-entries");
  }

  @Test
  public void text_after_integer() {
    runInvalidTest("text-after-integer");
  }

  @Test
  public void text_after_string() {
    runInvalidTest("text-after-string");
  }

  @Test
  public void text_after_table() {
    runInvalidTest("text-after-table");
  }

  @Test
  public void text_before_array_separator() {
    runInvalidTest("text-before-array-separator");
  }

  @Test
  public void text_in_array() {
    runInvalidTest("text-in-array");
  }
  
  @AfterEach
  public void after() throws IOException {
    inputToml.close();
  }

  private void runInvalidTest(String testName) {
    inputToml = getClass().getResourceAsStream("burntsushi/invalid/" + testName + ".toml");

    try {
      new Toml().read(inputToml);
      fail("Should have rejected invalid input!");
    } catch (IllegalStateException e) {
      // success
    }
  }
}
