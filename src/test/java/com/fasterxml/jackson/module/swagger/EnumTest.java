package com.fasterxml.jackson.module.swagger;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.models.*;
import com.wordnik.swagger.models.properties.*;

public class EnumTest extends SwaggerTestBase {
  public enum Currency { USA, CANADA }

  public void testEnum() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Model model = new ModelResolver(mapper)
       .resolve(Currency.class);
    assertNotNull(model);

    for(String key : model.getProperties().keySet()) {
      Property property = model.getProperties().get(key);
      assertFalse(property.getName().equals("declaringClass"));
    }
  }
}