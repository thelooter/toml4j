package de.thelooter.toml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class QuotedKeysTest {

  @Test
  public void should_accept_quoted_key_for_value() {
    Toml toml = new Toml().read("\"127.0.0.1\" = \"localhost\"  \n  \"character encoding\" = \"UTF-8\" \n  \"ʎǝʞ\" = \"value\"");
    
    assertEquals("localhost", toml.getString("\"127.0.0.1\""));
    assertEquals("UTF-8", toml.getString("\"character encoding\""));
    assertEquals("value", toml.getString("\"ʎǝʞ\""));
  }
  
  @Test
  public void should_accept_quoted_key_for_table_name() {
    Toml toml = new Toml().read("[\"abc def\"]\n  val = 1");
    
    assertEquals(1L, toml.getTable("\"abc def\"").getLong("val").longValue());
  }
  
  @Test
  public void should_accept_partially_quoted_table_name() {
    Toml toml = new Toml().read("[dog.\"tater.man\"]  \n  type = \"pug0\"  \n[dog.tater]  \n  type = \"pug1\"\n[dog.tater.man]  \n  type = \"pug2\"");
    Toml dogs = toml.getTable("dog");
    
    assertEquals("pug0", dogs.getTable("\"tater.man\"").getString("type"));
    assertEquals("pug1", dogs.getTable("tater").getString("type"));
    assertEquals("pug2", dogs.getTable("tater").getTable("man").getString("type"));
    assertEquals("pug0", toml.getString("dog.\"tater.man\".type"));
    assertEquals("pug2", toml.getString("dog.tater.man.type"));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_conserve_quoted_key_in_map() {
    Toml toml = new Toml().read("[dog.\"tater.man\"]  \n  type = \"pug0\"  \n[dog.tater]  \n  type = \"pug1\"\n[dog.tater.man]  \n  type = \"pug2\"");
    Toml dogs = toml.getTable("dog");
    
    Map<String, Map<String, Object>> map = dogs.to(Map.class);
    
    assertEquals("pug0", map.get("\"tater.man\"").get("type"));
    assertEquals("pug1", map.get("tater").get("type"));
    assertEquals("pug2", ((Map<String, Object>) map.get("tater").get("man")).get("type"));
  }
  
  @Test
  public void should_convert_quoted_keys_to_map_but_not_to_object_fields() {
    Quoted quoted = new Toml().read("\"ʎǝʞ\" = \"value\"  \n[map]  \n  \"ʎǝʞ\" = \"value\"").to(Quoted.class);
    
    assertNull(quoted.ʎǝʞ);
    assertEquals("value", quoted.map.get("\"ʎǝʞ\""));
  }
  
  @Test
  public void should_support_table_array_index_with_quoted_key() {
    Toml toml = new Toml().read("[[ dog. \" type\" ]] \n  name = \"type0\"  \n  [[dog.\" type\"]]  \n  name = \"type1\"");
    
    assertEquals("type0", toml.getString("dog.\" type\"[0].name"));
    assertEquals("type1", toml.getString("dog.\" type\"[1].name"));
  }
  
  @Test
  public void should_support_table_array_index_with_dot_in_quoted_key() {
    Toml toml = new Toml().read("[[ dog. \"a.type\" ]] \n  name = \"type0\"");
    
    assertEquals("type0", toml.getString("dog.\"a.type\"[0].name"));
  }
  
  @Test
  public void should_support_quoted_key_containing_square_brackets() {
    Toml toml = new Toml().read("[dog.\" type[abc]\"] \n  name = \"type0\"  \n  [dog.\" type[1]\"]  \n  \"name[]\" = \"type1\"");
    
    assertEquals("type0", toml.getString("dog.\" type[abc]\".name"));
    assertEquals("type1", toml.getString("dog.\" type[1]\".\"name[]\""));
  }
  
  @Test
  public void should_support_quoted_key_containing_escaped_quote() {
    Toml toml = new Toml().read("[dog.\"ty\\\"pe\"] \n  \"na\\\"me\" = \"type0\"");
    
    assertEquals("type0", toml.getString("dog.\"ty\\\"pe\".\"na\\\"me\""));
  }
  
  @Test
  public void should_support_fully_quoted_table_name() {
    Toml toml = new Toml().read("[\"abc.def\"]  \n  key = 1");
    
    assertEquals(1, toml.getLong("\"abc.def\".key").intValue());
  }
  
  @Test
  public void should_support_whitespace_around_key_segments() {
    Toml toml = new Toml().read("[  dog. \"type\". breed   ] \n  name = \"type0\"");
    
    assertEquals("type0", toml.getString("dog.\"type\".breed.name"));
  }
  
  @Test
  public void should_support_unicode() {
    Toml toml = new Toml().read("[[\"\\u00B1\"]]\n  \"\\u00B1\" = \"a\"\n [\"\\u00B11\"]\n  \"±\" = 1");
    
    assertThat(toml.getTables("\"±\""), hasSize(1));
    assertEquals("a", toml.getTables("\"±\"").get(0).getString("\"±\""));
    assertEquals(1, toml.getTable("\"±1\"").getLong("\"±\"").intValue());
  }

  @Test()
  public void should_fail_on_malformed_quoted_key() {
    assertThrows(IllegalStateException.class,() -> new Toml().read("k\"ey\" = 1"));
  }
  
  @Test()
  public void should_fail_on_malformed_quoted_table() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("[a\"bc\"]"));
  }
  
  @Test()
  public void should_fail_on_malformed_quoted_nested_table() {
    assertThrows(IllegalStateException.class,()->new Toml().read("[a.a\"bc\"]"));
  }
  
  private static class Quoted {
    
    String ʎǝʞ;
    
    Map<String, Object> map;
  }
}
