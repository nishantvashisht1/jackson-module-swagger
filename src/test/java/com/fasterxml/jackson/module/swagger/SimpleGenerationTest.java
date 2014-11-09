package com.fasterxml.jackson.module.swagger;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.models.*;
import com.wordnik.swagger.models.properties.*;
import com.wordnik.swagger.annotations.*;

import org.junit.Ignore;

public class SimpleGenerationTest extends SwaggerTestBase {
//  ObjectMapper mapper = new ObjectMapper();

  @JsonPropertyOrder({ "a", "b" })
  @ApiModel(description="DESC")
  static class SimpleBean {
   public int b;
   public long c;
   public float d;
   public double e;
   public java.util.Date f;
   public String getA() { return null; }
  }

  @JsonPropertyOrder({ "a", "b", "c", "d" })
  static class JsonOrderBean {
    public int d;
    public int a;
    public int c;
    public int b;
  }

  static class PositionBean {
    @ApiModelProperty(position=4)
    public int d;

    @ApiModelProperty(position=1)
    public int a;

    @ApiModelProperty(position=3)
    public int c;

    @ApiModelProperty(position=2)
    public int b;
  }

  static class TheCount {
    @JsonProperty("theCount")
    private Integer count;

    public void setCount(Integer count) {
     this.count = count;
    }

    public Integer getCount() {
     return count;
    }
  }

  static class StringDateMapBean {
    public Map<String,Date> stuff;
  }

  @JsonPropertyOrder({ "a", "b" })
  static class IntArrayBean {
    public int[] b;
  }
  
  /*
  /**********************************************************
  /* Test methods
  /**********************************************************
   */

  public void shouldResolvePropertiesMaintainingDeclaredOrder() throws Exception {
    Model model = modelResolver()
            .resolve(SimpleBean.class);
    assertNotNull(model);

    assertEquals("DESC", model.getDescription());

    Map<String, Property> props = model.getProperties();
    assertEquals(6, props.size());

    List<Property> properties = new ArrayList<Property>(props.values());

    Property a = properties.get(0);
    assertPropertyName("a", a);
    assertEquals("string", a.getType());

    Property b = properties.get(1);
    assertPropertyName("b", b);
    assertEquals("a", a.getName());
    assertEquals("integer", b.getType());
    assertEquals("int32", b.getFormat());

    Property c = properties.get(2);
    assertPropertyName("c", c);
    assertEquals("integer", c.getType());
    assertEquals("int64", c.getFormat());

    Property d = properties.get(3);
    assertPropertyName("d", d);
    assertEquals("number", d.getType());
    assertEquals("float", d.getFormat());

    Property e = properties.get(4);
    assertPropertyName("e", e);
    assertEquals("number", e.getType());
    assertEquals("double", e.getFormat());

    Property f = properties.get(5);
    assertPropertyName("f", f);
    assertEquals("string", f.getType());
    assertEquals("date-time", f.getFormat());
  }

  private void assertPropertyName(String name, Property property) {
    assertEquals(name, property.getName());
  }

  @Ignore
  public void testOrdering() throws Exception {
    Model model = modelResolver()
      .resolve(JsonOrderBean.class);

    Map<String, Property> props = model.getProperties();

    assertEquals(4, props.size());
    /*
    assertEquals("abcd", _concat(props.keySet()));

    model = modelResolver().resolve(PositionBean.class);

    props = model.getProperties();
    Property prop = props.get("c");
    assertNotNull(prop);
    // assertEquals(Integer.valueOf(3), prop.getPosition());
    assertEquals(4, props.size());
    assertEquals("abcd", _concat(props.keySet()));
    */
  }

  public void testTheCountBean() throws Exception {
    Model model = modelResolver()
      .resolve(TheCount.class);

    Map<String, Property> props = model.getProperties();
    assertEquals(1, props.size());
    Property prop = props.values().iterator().next();

    assertEquals("theCount", prop.getName());
  }

  public void testStringDateMap() throws Exception {
    final ObjectMapper M = new ObjectMapper();
    Model model = new ModelResolver(M)
      .resolve(StringDateMapBean.class);

    Map<String, Property> props = model.getProperties();
    assertEquals(1, props.size());
    Property prop = props.values().iterator().next();
    assertEquals("stuff", prop.getName());
  }

  public void testIntArray() throws Exception {
      Model model = new ModelResolver(new ObjectMapper())
        .resolve(IntArrayBean.class);

      Map<String, Property> props = model.getProperties();
      assertEquals(1, props.size());
      Property prop = props.values().iterator().next();
      assertEquals("b", prop.getName());
      
      // !!! TODO
  }
  
  protected String _concat(Set<String> input) {
    StringBuilder sb = new StringBuilder();
    for (String str : input) {
     sb.append(str);
    }
    return sb.toString();
  }
}
