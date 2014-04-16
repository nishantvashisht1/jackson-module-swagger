package com.fasterxml.jackson.module.swagger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.swagger.model.*;
import com.wordnik.swagger.annotations.ApiModel;

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
		final BasicBeanDescription beanDesc = (BasicBeanDescription) _mapper.getSerializationConfig().introspect(type);
		
		Model model = new Model();
		final String name = raw.getSimpleName();
		model.setId(name);
		model.setName(name);
		model.setQualifiedType(_typeQName(type));

		model.setDescription(_description(beanDesc.getClassInfo()));

		ApiModel apiModel = beanDesc.getClassAnnotations().get(ApiModel.class);
		if (apiModel != null) {
			Class<?> parent = apiModel.parent();
			if (parent != Void.class) {
				model.setBaseModel(_typeName(_mapper.constructType(parent)));
			}
		}
		
		ArrayList<String> subtypeNames = new ArrayList<String>();
		for (NamedType subtype : _intr.findSubtypes(beanDesc.getClassInfo())) {
			subtypeNames.add(_subTypeName(subtype));
		}
		model.setSubTypes(subtypeNames);

		String disc = (apiModel == null) ? "" : apiModel.discriminator();
		if (disc.isEmpty()) {
			// longer method would involve AnnotationIntrospector.findTypeResolver(...) but:
			JsonTypeInfo typeInfo = beanDesc.getClassAnnotations().get(JsonTypeInfo.class);
			if (typeInfo != null) {
				disc = typeInfo.property();
			}
		}
		if (!disc.isEmpty()) {
			model.setDiscriminator(disc);
		}
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

			// And then properties specific to subset of property types:
			if (propType.isEnumType()) {
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
		final boolean useIndex =  _mapper.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
		final boolean useToString = _mapper.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		List<AllowableValue> enums = new ArrayList<AllowableValue>();
		@SuppressWarnings("unchecked")
		Class<Enum<?>> enumClass = (Class<Enum<?>>) propClass;
		for (Enum<?> en : enumClass.getEnumConstants()) {
			String n;
			if (useIndex) {
				n = String.valueOf(en.ordinal());
			} else if (useToString) {
				n = en.toString();
			} else {
				n = _intr.findEnumValue(en);
			}
			enums.add(new AllowableValue(n));
		}
		
		result.setAllowableValues(enums);
	}

	protected String _description(Annotated ann) {
		// while name suggests it's only for properties, should work for any Annotated thing.
		// also; with Swagger introspector's help, should get it from ApiModel/ApiModelProperty
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
