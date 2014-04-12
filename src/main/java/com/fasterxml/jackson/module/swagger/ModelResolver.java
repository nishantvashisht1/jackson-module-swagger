package com.fasterxml.jackson.module.swagger;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.swagger.model.Model;

public class ModelResolver
{
	protected final ObjectMapper _mapper;
	
	@SuppressWarnings("serial")
	public ModelResolver(ObjectMapper mapper) {
		mapper.registerModule(
				new SimpleModule("swagger", Version.unknownVersion()) {
					@Override
					public void setupModule(SetupContext context) {
						context.insertAnnotationIntrospector(new SwaggerAnnotationIntrospector());
					}
				}
		);
		_mapper = mapper;
	}

	public Model resolve(Class<?> cls) {
		return resolve(_mapper.constructType(cls));
	}

	public Model resolve(JavaType type) {
		// !!! TODO
		return null;
	}
}
