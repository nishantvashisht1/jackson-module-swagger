package com.fasterxml.jackson.module.swagger;

import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class SwaggerAnnotationIntrospector
	extends AnnotationIntrospector
{
	private static final long serialVersionUID = 1L;

	@Override
	public Version version() {
		return PackageVersion.VERSION;
	}

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
    	ApiModelProperty ann = m.getAnnotation(ApiModelProperty.class);
    	if (ann != null && ann.hidden()) {
    		return true;
    	}
    	return false;
    }

    @Override
    public Boolean hasRequiredMarker(AnnotatedMember m) {
    	ApiModelProperty ann = m.getAnnotation(ApiModelProperty.class);
        if (ann != null) {
            return ann.required();
        }
        return null;
    }

    @Override
    public String findPropertyDescription(Annotated a)
    {
    	ApiModelProperty ann = a.getAnnotation(ApiModelProperty.class);
        if (ann != null) {
            return ann.notes();
        }
        return null;
    }

    @Override
    public List<NamedType> findSubtypes(Annotated a) {
    	return null;
    }

    @Override        
    public String findTypeName(AnnotatedClass ac) {
    	return null;
    }
}
