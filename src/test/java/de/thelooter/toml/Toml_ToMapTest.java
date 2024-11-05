package de.thelooter.toml;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Toml_ToMapTest {
    
    @Test
    public void should_convert_simple_values() {
        Map<String, Object> toml = new Toml().read("a = 1").toMap();
        
        assertEquals(1L, toml.get("a"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void should_convert_table() {
      Map<String, Object> toml = new Toml().read("c = 2\n  [a]\n  b = 1").toMap();
      
      assertEquals(1L, ((Map<String, Object>) toml.get("a")).get("b"));
      assertEquals(2L, toml.get("c"));
    }
}
