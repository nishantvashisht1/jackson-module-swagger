package com.fasterxml.jackson.module.swagger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.swagger.model.*;

public class ModelResolver
{
	protected final ObjectMapper _mapper;

	protected final AnnotationIntrospector _intr;

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
		_intr = mapper.getSerializationConfig().getAnnotationIntrospector();
	}

	public Model resolve(Class<?> cls) {
		return resolve(_mapper.constructType(cls));
	}
	
	public Model resolve(JavaType type)
	{
		final Class<?> raw = type.getRawClass();
		final MapperConfig<?> mapperConfig = _mapper.getSerializationConfig();
		final BasicBeanDescription beanDesc = (BasicBeanDescription) _mapper.getSerializationConfig().introspect(type);

		Model model = new Model();
		final String name = raw.getSimpleName();
		model.setId(name);
		model.setName(name);
		model.setQualifiedType(_typeQName(type));

		model.setDescription(_description(beanDesc.getClassInfo()));

		ArrayList<String> subtypeNames = new ArrayList<String>();
		for (NamedType subtype : _intr.findSubtypes(beanDesc.getClassInfo())) {
			subtypeNames.add(_subTypeName(subtype));
		}
		model.setSubTypes(subtypeNames);

		TypeResolverBuilder<?> res0 = _intr.findTypeResolver(mapperConfig, beanDesc.getClassInfo(), type);
		if (res0 != null && res0 instanceof StdTypeResolverBuilder) {
			StdTypeResolverBuilder res = (StdTypeResolverBuilder) res0;
			String prop = res.getTypeProperty();
			if (prop != null) {
				model.setDiscriminator(prop);
			}
		}

		/*
  private List<AllowableValue> allowableValues = new ArrayList<AllowableValue>();
  private ModelRef items = null;
		 */
		List<ModelProperty> modelProps = new ArrayList<ModelProperty>();
		for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
			ModelProperty modelProp = new ModelProperty();
			modelProps.add(modelProp);
			PropertyMetadata md = propDef.getMetadata();
			final AnnotatedMember member = propDef.getPrimaryMember();
			JavaType propType = member.getType(beanDesc.bindingsForBeanType());
			modelProp.setType(_typeName(propType));
			modelProp.setQualifiedType(_typeQName(propType));
			Integer index = _intr.findPropertyIndex(member);
			if (index != null) {
				modelProp.setPosition(index);
			}
			modelProp.setRequired(md.getRequired());
			
			if (propType.isEnumType()) {
				// Q: Should we also support "WRITE_ENUMS_USING_INDEX"?
				_addEnumProps(propDef, propType.getRawClass(), modelProp);
			} else if (propType.isContainerType()) {
				JavaType valueType = propType.getContentType();
				if (valueType != null) {
					modelProp.setItems(_modelRef(valueType));
				}
			}
		}
		return model;
	}

	protected ModelRef _modelRef(JavaType type)
	{
		ModelRef ref = new ModelRef();
		ref.setType(_typeName(type));
		ref.setQualifiedType(_typeQName(type));
		// What does 'ref' property within 'ModelRef' mean?
		return ref;
	}
	
	protected void _addEnumProps(BeanPropertyDefinition propDef, Class<?> propClass,
		ModelProperty result)
	{
		final boolean useToString = _mapper.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		List<AllowableValue> enums = new ArrayList<AllowableValue>();
		@SuppressWarnings("unchecked")
		Class<Enum<?>> enumClass = (Class<Enum<?>>) propClass;
		for (Enum<?> en : enumClass.getEnumConstants()) {
			String n;
			if (useToString) {
				n = en.toString();
			} else {
				n = _intr.findEnumValue(en);
			}
			enums.add(new AllowableValue(n));
		}
		
		result.setAllowableValues(enums);
	}

	protected String _description(Annotated ann) {
		// while name suggests it's only for properties, should work for any Annotated thing:
		return _intr.findPropertyDescription(ann);
	}
	
	protected String _typeName(JavaType type) {
		return type.getRawClass().getSimpleName();
	}

	protected String _typeQName(JavaType type) {
		return type.getRawClass().getName();
	}
	
	protected String _subTypeName(NamedType type)
	{
		// !!! TODO: should this use 'name' instead?
		return type.getType().getName();
	}
}
