package com.fasterxml.jackson.module.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;

public abstract class SwaggerTestBase extends TestCase
{
    protected ModelResolver modelResolver() {
        return new ModelResolver(new ObjectMapper());
    }
}
