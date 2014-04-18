package com.fasterxml.jackson.module.swagger;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.module.swagger.model.Model;
import com.fasterxml.jackson.module.swagger.model.ModelProperty;
import com.wordnik.swagger.annotations.ApiModel;

public class SimpleGenerationTest extends SwaggerTestBase
{
	@JsonPropertyOrder({ "a", "b" })
	@ApiModel(description="DESC")
	static class SimpleBean {
		public int b;

		public String getA() { return null; }
	}
	
	public void testSimple() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		Model model = new ModelResolver(mapper)
			.resolve(SimpleBean.class);
		assertNotNull(model);

		assertEquals("DESC", model.getDescription());
		
		List<ModelProperty> props = model.getProperties();
		assertEquals(2, props.size());
		
		for (ModelProperty prop : props) {
//			String name = prop.get
		}
		
		/*
		String asJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
		System.out.println("JSON: "+asJson);
		*/
	}
}
