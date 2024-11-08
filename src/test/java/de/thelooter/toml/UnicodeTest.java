package de.thelooter.toml;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnicodeTest {

  @Test
  public void should_support_short_escape_form() {
    Toml toml = new Toml().read("key = \"Jos\u00E9\\nLocation\tSF\"");
    
    assertEquals("José\nLocation\tSF", toml.getString("key"));
  }

  @Test
  public void should_support_unicode_literal() {
    Toml toml = new Toml().read("key = \"José LöcÄtion SF\"");
    
    assertEquals("José LöcÄtion SF", toml.getString("key"));
  }
}
