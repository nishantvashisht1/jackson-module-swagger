package com.fasterxml.jackson.module.swagger;

import java.util.Map;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.swagger.model.Model;
import com.fasterxml.jackson.module.swagger.model.ModelProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class JodaTest extends SwaggerTestBase
{
    static class ModelWithJodaDateTime {
        @ApiModelProperty(value="Name!", position = 2)
        public String name;

        @ApiModelProperty(value = "creation timestamp", required = true, position = 1)
        public DateTime createdAt;
    }

    public void testSimple() throws Exception
    {
        final ModelResolver mr = modelResolver();
        Model model = mr.resolve(ModelWithJodaDateTime.class);
        assertNotNull(model);

        Map<String,ModelProperty> props = model.getProperties();
        assertEquals(2, props.size());

        for (Map.Entry<String,ModelProperty> entry : props.entrySet()) {
            String name = entry.getKey();
            ModelProperty prop = entry.getValue();

            if ("name".equals(name)) {
                assertEquals("string", prop.getType());
            } else if ("createdAt".equals(name)) {
                assertEquals("dateTime", prop.getType());
            } else {
                fail("Unknown property '"+name+"'");
            }
         }

         String asJson = mr.objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(model);
         System.out.println("JSON: "+asJson);
    }
}
