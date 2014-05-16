package com.fasterxml.jackson.module.swagger;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.swagger.model.Model;
import com.fasterxml.jackson.module.swagger.model.ModelProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class SimpleGenerationTest extends SwaggerTestBase
{
	@JsonPropertyOrder({ "a", "b" })
	@ApiModel(description="DESC")
	static class SimpleBean {
		public int b;

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

     public class TheCount {
         @JsonProperty("theCount")
         private Integer count;

         public void setCount(Integer count) {
             this.count = count;
         }

         public Integer getCount() {
             return count;
         }
     }

     /*
     /**********************************************************
     /* Test methods
     /**********************************************************
      */

     public void testSimple() throws Exception
	{
		Model model = modelResolver()
			.resolve(SimpleBean.class);
		assertNotNull(model);

		assertEquals("DESC", model.getDescription());

		Map<String,ModelProperty> props = model.getProperties();
		assertEquals(2, props.size());

		for (Map.Entry<String,ModelProperty> entry : props.entrySet()) {
			String name = entry.getKey();
			ModelProperty prop = entry.getValue();

			if ("a".equals(name)) {
			    assertEquals("string", prop.getType());
			} else if ("b".equals(name)) {
                   assertEquals("integer", prop.getType());
			} else {
				fail("Unknown property '"+name+"'");
			}
		}
		
		/*
		String asJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
		System.out.println("JSON: "+asJson);
		*/
	}

     public void testOrdering() throws Exception
     {
         Model model = modelResolver()
              .resolve(JsonOrderBean.class);

         Map<String,ModelProperty> props = model.getProperties();
         assertEquals(4, props.size());
         assertEquals("abcd", _concat(props.keySet()));

         model = modelResolver().resolve(PositionBean.class);
         props = model.getProperties();
         ModelProperty prop = props.get("c");
         assertNotNull(prop);
         assertEquals(Integer.valueOf(3), prop.getPosition());
         assertEquals(4, props.size());
         assertEquals("abcd", _concat(props.keySet()));
     }

     public void testTheCountBean() throws Exception
     {
         Model model = modelResolver()
                 .resolve(TheCount.class);

         Map<String,ModelProperty> props = model.getProperties();
         assertEquals(1, props.size());
         ModelProperty prop = props.values().iterator().next();

         assertEquals("theCount", prop.getName());

         /*
 String asJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(model);
 System.out.println("JSON: "+asJson);
 */
     }
     
	protected String _concat(Set<String> input) {
         StringBuilder sb = new StringBuilder();
         for (String str : input) {
             sb.append(str);
         }
         return sb.toString();
     }
}
