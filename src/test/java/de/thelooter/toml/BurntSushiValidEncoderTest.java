package de.thelooter.toml;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BurntSushiValidEncoderTest {

  @Test
  public void array_empty() {
    runEncoder("array-empty");
  }

  @Test
  public void arrays_hetergeneous() {
    runEncoder("arrays-hetergeneous");
  }
  
  @Test
  public void arrays_nested() {
    runEncoder("arrays-nested");
  }
  
  @Test
  public void datetime() {
    runEncoder("datetime");
  }
  
  @Test
  public void empty() {
    runEncoder("empty");
  }
  
  @Test
  public void example() {
    runEncoder("example");
  }
  
  @Test
  public void float_() {
    runEncoder("float");
  }

  
  @Test
  public void implicit_and_explicit_before() {
    runEncoder("implicit-and-explicit-before");
  }
  
  @Test
  public void implicit_groups() {
    runEncoder("implicit-groups");
  }
  
  @Test
  public void long_float() {
    runEncoder("long-float");
  }
  
  @Test
  public void long_integer() {
    runEncoder("long-integer");
  }
  
  @Test
  public void key_special_chars_modified() {
    runEncoder("key-special-chars-modified");
  }
  
  @Test
  public void integer() throws Exception {
    runEncoder("integer");
  }
  
  @Test
  public void string_empty() {
    runEncoder("string-empty");
  }
  
  @Test
  public void string_escapes_modified() {
    runEncoder("string-escapes-modified");
  }
  
  @Test
  public void string_simple() {
    runEncoder("string-simple");
  }

  @Test
  public void table_array_implicit() {
    runEncoder("table-array-implicit");
  }

  @Test
  public void table_array_many() {
    runEncoder("table-array-many");
  }

  @Test
  public void table_array_nest_modified() {
    // Modified to remove stray spaces in the expected TOML
    runEncoder("table-array-nest-modified",
        new TomlWriter.Builder().indentTablesBy(2).build());
  }

  @Test
  public void table_array_one() {
    runEncoder("table-array-one");
  }
  
  private static final Gson GSON = new Gson();

  private void runEncoder(String testName) {
    runEncoder(testName, new TomlWriter());
  }

  private void runEncoder(String testName, TomlWriter tomlWriter) {
    InputStream inputTomlStream = getClass().getResourceAsStream("burntsushi/valid/" + testName + ".toml");
    String expectedToml = convertStreamToString(inputTomlStream).replaceAll("\r\n", "\n");

    Reader inputJsonReader = new InputStreamReader(getClass().getResourceAsStream("burntsushi/valid/" + testName + ".json"));
    JsonElement jsonInput = GSON.fromJson(inputJsonReader, JsonElement.class);
    Map<String, Object> enriched = enrichJson(jsonInput.getAsJsonObject());

    String encoded = tomlWriter.write(enriched);
    assertEquals(expectedToml, encoded);
  }

  // Enrich toml-test JSON trees into native Java types, suiteable
  // for consumption by TomlWriter.
  private Map<String, Object> enrichJson(JsonObject jsonObject) {
    Map<String, Object> enriched = new LinkedHashMap<>();
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      enriched.put(entry.getKey(), enrichJsonElement(entry.getValue()));
    }

    return enriched;
  }

  Object enrichJsonElement(JsonElement jsonElement) {
    if (jsonElement.isJsonObject()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      if (jsonObject.has("type") && jsonObject.has("value")) {
        return enrichPrimitive(jsonObject);
      }
      return enrichJson(jsonElement.getAsJsonObject());
    } else if (jsonElement.isJsonArray()) {
      List<Object> tables = new LinkedList<>();
      for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
        tables.add(enrichJsonElement(arrayElement));
      }

      return tables;
    }

    throw new AssertionError("received unexpected JsonElement: " + jsonElement);
  }

  private Object enrichPrimitive(JsonObject jsonObject) {
    String type = jsonObject.getAsJsonPrimitive("type").getAsString();
    if ("bool".equals(type)) {
      return jsonObject.getAsJsonPrimitive("value").getAsBoolean();
    } else if ("integer".equals(type)) {
      return jsonObject.getAsJsonPrimitive("value").getAsBigInteger();
    } else if ("float".equals(type)) {
      return jsonObject.getAsJsonPrimitive("value").getAsDouble();
    } else if ("string".equals(type)) {
      return jsonObject.getAsJsonPrimitive("value").getAsString();
    } else if ("datetime".equals(type)) {
      DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
      iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
      String dateString = jsonObject.getAsJsonPrimitive("value").getAsString();
      try {
        return iso8601Format.parse(dateString);
      } catch (ParseException e) {
        throw new AssertionError("failed to parse datetime '" + dateString + "': " + e.getMessage());
      }
    } else if ("array".equals(type)) {
      JsonArray jsonArray = jsonObject.getAsJsonArray("value");
      List<Object> enriched = new LinkedList<>();
      for (JsonElement arrayElement : jsonArray) {
        enriched.add(enrichJsonElement(arrayElement));
      }

      return enriched;
    }

    throw new AssertionError("enrichPrimitive: received unknown type " + type);
  }

  static String convertStreamToString(java.io.InputStream is) {
      java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
  }

}
