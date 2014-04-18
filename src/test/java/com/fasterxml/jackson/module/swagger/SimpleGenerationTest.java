package com.fasterxml.jackson.module.swagger;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.swagger.model.Model;

public class SimpleGenerationTest extends SwaggerTestBase
{
	@JsonPropertyOrder({ "a", "b" })
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

		/*
		String asJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
		System.out.println("JSON: "+asJson);
		*/
	}
}
