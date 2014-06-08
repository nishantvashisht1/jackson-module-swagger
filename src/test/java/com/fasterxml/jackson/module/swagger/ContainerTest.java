package com.fasterxml.jackson.module.swagger;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.swagger.model.Model;
import com.fasterxml.jackson.module.swagger.model.ModelProperty;
import com.fasterxml.jackson.module.swagger.model.ModelRef;

public class ContainerTest extends SwaggerTestBase
{
    static class ArrayBean {
        public int[] a;
    }

    static class MapBean {
        public Map<Short, java.util.Calendar> stuff;
    }

    public void testArray() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Model model = new ModelResolver(mapper)
             .resolve(ArrayBean.class);

        Map<String,ModelProperty> props = model.getProperties();
        assertEquals(1, props.size());
        ModelProperty prop = model.property("a");
        assertNotNull(prop);
        assertEquals("a", prop.getName());
        assertEquals("Array", prop.getType());
        assertEquals("[I", prop.getQualifiedType());

        ModelRef items = prop.getItems();
        assertNotNull(items);
        assertEquals("integer", items.getType());
        assertEquals("int", items.getQualifiedType());
    }

    public void testArrays() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Model model = new ModelResolver(mapper)
             .resolve(MapBean.class);

        Map<String,ModelProperty> props = model.getProperties();
        assertEquals(1, props.size());
        ModelProperty prop = model.property("stuff");
        assertNotNull(prop);
        assertEquals("stuff", prop.getName());
//        assertEquals("Map[integer,dateTime]", prop.getType());
        assertEquals("Map", prop.getType());
        assertEquals("java.util.Map", prop.getQualifiedType());

        ModelRef items = prop.getItems();
        assertNotNull(items);
        assertEquals("java.util.Calendar", items.getQualifiedType());
    }
}
