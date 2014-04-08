package com.fasterxml.jackson.module.swagger;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class SwaggerModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    public SwaggerModule() {
        super(PackageVersion.VERSION);
    }
    
    @Override
    public void setupModule(SetupContext context)
    {
        super.setupModule(context);
    }

    /*
    /**********************************************************
    /* Extended API
    /**********************************************************
     */
}
