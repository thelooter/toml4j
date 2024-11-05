package de.thelooter.toml;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BurntSushiValidTest {

  
  @Test
  public void array_empty() {
    run("array-empty");
  }
  
  @Test
  public void array_nospaces() {
    run("array-nospaces");
  }
  
  @Test
  public void arrays_hetergeneous() {
    run("arrays-hetergeneous");
  }
  
  @Test
  public void arrays_nested() {
    run("arrays-nested");
  }
  
  @Test
  public void arrays() throws Exception {
    run("arrays");
  }
  
  @Test
  public void bool() {
    run("bool");
  }

  @Test
  public void comments_everywhere() {
    run("comments-everywhere");
  }
  
  @Test
  public void datetime() {
    run("datetime");
  }
  
  @Test
  public void empty() {
    run("empty");
  }
  
  @Test
  public void example() {
    run("example");
  }
  
  @Test
  public void float_() {
    run("float");
  }
  
  @Test
  public void implicit_and_explicit_after() {
    run("implicit-and-explicit-after");
  }
  
  @Test
  public void implicit_and_explicit_before() {
    run("implicit-and-explicit-before");
  }
  
  @Test
  public void implicit_groups() {
    run("implicit-groups");
  }
  
  @Test
  public void integer() throws Exception {
    run("integer");
  }
  
  @Test
  public void key_equals_nospace() {
    run("key-equals-nospace");
  }
  
  @Test @Disabled
  public void key_space() {
    run("key-space");
  }
  
  @Test
  public void key_space_modified() {
    run("key-space-modified");
  }
  
  @Test @Disabled
  public void key_special_chars() {
    run("key-special-chars");
  }
  
  @Test
  public void key_special_chars_modified() {
    run("key-special-chars-modified");
  }
  
  @Test @Disabled
  public void keys_with_dots() {
    run("keys-with-dots");
  }
  
  @Test
  public void keys_with_dots_modified() {
    run("keys-with-dots-modified");
  }
  
  @Test
  public void long_float() {
    run("long-float");
  }
  
  @Test
  public void long_integer() {
    run("long-integer");
  }
  
  @Test @Disabled
  public void multiline_string() {
    run("multiline-string");
  }
  
  @Test
  public void multiline_string_modified() {
    run("multiline-string-modified");
  }
  
  @Test
  public void raw_multiline_string() {
    run("raw-multiline-string");
  }
  
  @Test
  public void raw_string() {
    run("raw-string");
  }
  
  @Test
  public void string_empty() {
    run("string-empty");
  }
  
  @Test @Disabled
  public void string_escapes() {
    run("string-escapes-modified");
  }
  
  @Test
  public void string_escapes_modified() {
    run("string-escapes-modified");
  }
  
  @Test
  public void string_simple() {
    run("string-simple");
  }
  
  @Test
  public void string_with_pound() {
    run("string-with-pound");
  }

  @Test
  public void table_array_implicit() {
    run("table-array-implicit");
  }

  @Test
  public void table_array_many() {
    run("table-array-many");
  }

  @Test
  public void table_array_nest() {
    run("table-array-nest");
  }

  @Test
  public void table_array_one() {
    run("table-array-one");
  }

  @Test
  public void table_empty() {
    run("table-empty");
  }

  @Test
  public void table_sub_empty() {
    run("table-sub-empty");
  }

  @Test @Disabled
  public void table_whitespace() {
    run("table-whitespace");
  }

  @Test @Disabled
  public void table_with_pound() {
    run("table-with-pound");
  }

  @Test
  public void unicode_escape() {
    run("unicode-escape");
  }

  @Test
  public void unicode_literal() {
    run("unicode-literal");
  }

  private void run(String testName) {
    InputStream inputTomlStream = getClass().getResourceAsStream("burntsushi/valid/" + testName + ".toml");
    // InputStream inputToml = getClass().getResourceAsStream("burntsushi/valid/" + testName + ".toml");
    String inputToml = convertStreamToString(inputTomlStream).replaceAll("\r\n", "\n");
    Reader expectedJsonReader = new InputStreamReader(getClass().getResourceAsStream("burntsushi/valid/" + testName + ".json"));
    JsonElement expectedJson = GSON.fromJson(expectedJsonReader, JsonElement.class);

    Toml toml = new Toml().read(inputToml);
    JsonElement actual = TEST_GSON.toJsonTree(toml).getAsJsonObject().get("values");

    assertEquals(expectedJson, actual);

    try {
      inputTomlStream.close();
    } catch (IOException e) {}
    
    try {
      expectedJsonReader.close();
    } catch (IOException e) {}
  }

  static String convertStreamToString(java.io.InputStream is) {
      java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
  }

  private static final Gson GSON = new Gson();
  private static final Gson TEST_GSON = new GsonBuilder()
    .registerTypeAdapter(Boolean.class, serialize(Boolean.class))
    .registerTypeAdapter(String.class, serialize(String.class))
    .registerTypeAdapter(Long.class, serialize(Long.class))
    .registerTypeAdapter(Double.class, serialize(Double.class))
    .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> {
      DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
      iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
      return context.serialize(new Value("datetime", iso8601Format.format(src)));
    })
    .registerTypeHierarchyAdapter(List.class, (JsonSerializer<List<?>>) (src, typeOfSrc, context) -> {
      JsonArray array = new JsonArray();
      for (Object o : src) {
        array.add(context.serialize(o));
      }

      if (!src.isEmpty() && src.get(0) instanceof Map) {
        return array;
      }

      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("array"));
      object.add("value", array);

      return object;
    })
    .registerTypeAdapter(Value.class, (JsonSerializer<Value>) (src, typeOfSrc, context) -> {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive(src.type));
      object.add("value", new JsonPrimitive(src.value));

      return object;
    })
    .create();

  private static class Value {
    public final String type;
    public final String value;

    public Value(String type, String value) {
      this.type = type;
      this.value = value;
    }
  }

  private static <T> JsonSerializer<T> serialize(final Class<T> aClass) {
    return (src, typeOfSrc, context) -> context.serialize(new Value(toTomlType(aClass), src.toString()));
  }

  private static String toTomlType(Class<?> aClass) {
    if (aClass == String.class) {
      return "string";
    }

    if (aClass == Double.class) {
      return "float";
    }

    if (aClass == Long.class) {
      return "integer";
    }

    if (aClass == Date.class) {
      return "datetime";
    }

    if (aClass == Boolean.class) {
      return "bool";
    }

    return "array";
  }
}
