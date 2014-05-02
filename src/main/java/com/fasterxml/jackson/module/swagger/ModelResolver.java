package com.fasterxml.jackson.module.swagger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    protected TypeNameResolver _typeNameResolver = TypeNameResolver.std;

    /**
     * Minor optimization: no need to keep on resolving same types over and over
     * again.
     */
    protected Map<JavaType, String> _resolvedTypeNames = new ConcurrentHashMap<JavaType, String>();
    
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
        final BeanDescription beanDesc = _mapper.getSerializationConfig().introspect(type);
		
        Model model = new Model();

        // Couple of possibilities for defining
		final String name = _typeName(type, beanDesc);

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
		
		List<NamedType> nts = _intr.findSubtypes(beanDesc.getClassInfo());
		if (nts != null) {
			ArrayList<String> subtypeNames = new ArrayList<String>();
			for (NamedType subtype : nts) {
				subtypeNames.add(_subTypeName(subtype));
			}
			model.setSubTypes(subtypeNames);
		}

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
		List<ModelProperty> props = new ArrayList<ModelProperty>();
		for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
			String propName = propDef.getName();
			ModelProperty modelProp = new ModelProperty(propName);
			props.add(modelProp);
			PropertyMetadata md = propDef.getMetadata();
			final AnnotatedMember member = propDef.getPrimaryMember();
			JavaType propType = member.getType(beanDesc.bindingsForBeanType());
			modelProp.setType(_typeName(propType));
			modelProp.setQualifiedType(_typeQName(propType));
			/* 01-May-2014, tatu: Following is 2.4-only, and we can not quite
			 *   yet use it, as we only require Jackson 2.3.
			 */
			/*
			Integer index = _intr.findPropertyIndex(member);
			if (index != null) {
				modelProp.setPosition(index);
			}
			*/
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
		Collections.sort(props);
		Map<String,ModelProperty> modelProps = new LinkedHashMap<String,ModelProperty>();
		for (ModelProperty prop : props) {
			modelProps.put(prop.getName(), prop);
        }
        model.setProperties(modelProps);
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
         return _typeName(type, null);
     }
	
	protected String _typeName(JavaType type, BeanDescription beanDesc)
	{
	    String name = _resolvedTypeNames.get(type);
	    if (name != null) {
	        return name;
	    }
	    name = _findTypeName(type, beanDesc);
	    _resolvedTypeNames.put(type,  name);
	    return name;
	}

    protected String _findTypeName(JavaType type, BeanDescription beanDesc)
    {
        // First, handle container types; they require recursion
        if (type.isArrayType()) {
            return "Array["+_typeName(type.getContentType())+"]";
        }
        if (type.isMapLikeType()) {
            return "Map["
                    +_typeName(type.getKeyType())
                    +","
                    +_typeName(type.getContentType())
                    +"]";
        }
        if (type.isContainerType()) {
            if (Set.class.isAssignableFrom(type.getRawClass())) {
                return "Set["+_typeName(type.getContentType())+"]";
            }
            return "List["+_typeName(type.getContentType())+"]";
        }
        if (beanDesc == null) {
            beanDesc = _mapper.getSerializationConfig().introspectClassAnnotations(type);
        }
	        
        PropertyName rootName = _intr.findRootName(beanDesc.getClassInfo());
        if (rootName != null && rootName.hasSimpleName()) {
            return rootName.getSimpleName();
        }
        return _typeNameResolver.nameForType(type);
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
