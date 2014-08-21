package com.fasterxml.jackson.module.swagger;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.models.*;
import com.wordnik.swagger.models.properties.*;
import com.wordnik.swagger.annotations.*;

public class ComplexPropertyTest extends SwaggerTestBase {
  ObjectMapper mapper = new ObjectMapper();

  static class OuterBean {
    public int counter;
    public InnerBean inner;
  }

  static class InnerBean {
    public int d;
    public int a;
    public int c;
    public int b;
  }

  /*
  /**********************************************************
  /* Test methods
  /**********************************************************
   */

  public void testOuterBean() throws Exception  {
    Model model = modelResolver()
      .resolve(OuterBean.class);
    assertNotNull(model);
  }
}
