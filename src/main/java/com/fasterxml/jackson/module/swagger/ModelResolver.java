package com.fasterxml.jackson.module.swagger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.swagger.model.Model;
import com.fasterxml.jackson.module.swagger.model.ModelProperty;

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
	
	public Model resolve(JavaType type)
	{
		final Class<?> raw = type.getRawClass();
		final MapperConfig<?> mapperConfig = _mapper.getSerializationConfig();
		final AnnotationIntrospector intr = mapperConfig.getAnnotationIntrospector();
		final BasicBeanDescription beanDesc = (BasicBeanDescription) _mapper.getSerializationConfig().introspect(type);

		Model model = new Model();
		final String name = raw.getSimpleName();
		model.setId(name);
		model.setName(name);
		model.setQualifiedType(raw.getName());

		// while name suggests it's only for properties, should work for any Annotated thing:
		String desc = intr.findPropertyDescription(beanDesc.getClassInfo());
		if (desc == null || desc.isEmpty()) {
			desc = name;
		}
		model.setDescription(desc);

		ArrayList<String> subtypeNames = new ArrayList<String>();
		for (NamedType subtype : intr.findSubtypes(beanDesc.getClassInfo())) {
			subtypeNames.add(_subTypeName(subtype));
		}
		model.setSubTypes(subtypeNames);

		TypeResolverBuilder<?> res0 = intr.findTypeResolver(mapperConfig, beanDesc.getClassInfo(), type);
		if (res0 != null && res0 instanceof StdTypeResolverBuilder) {
			StdTypeResolverBuilder res = (StdTypeResolverBuilder) res0;
			String prop = res.getTypeProperty();
			if (prop != null) {
				model.setDiscriminator(prop);
			}
		}
		
		for (BeanPropertyDefinition prop : beanDesc.findProperties()) {
			
		}
		
		/*
		 */
		return null;
	}

	protected String _subTypeName(NamedType type)
	{
		// !!! TODO: should this use 'name' instead?
		return type.getType().getName();
	}
}
