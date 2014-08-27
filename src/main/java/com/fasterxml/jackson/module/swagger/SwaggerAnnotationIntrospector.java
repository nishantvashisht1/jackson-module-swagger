package com.fasterxml.jackson.module.swagger;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import javax.xml.bind.annotation.XmlElement;

import java.util.List;

public class SwaggerAnnotationIntrospector extends AnnotationIntrospector {
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
    XmlElement elem = m.getAnnotation(XmlElement.class);
    if (elem != null)
      if(elem.required())
        return true;
    return null;
  }

  @Override
  public String findPropertyDescription(Annotated a)
  {
    ApiModel model = a.getAnnotation(ApiModel.class);
    if (model != null) {
      return model.description();
    }
    ApiModelProperty prop = a.getAnnotation(ApiModelProperty.class);
    if (prop != null) {
      return prop.notes();
    }
    return null;
  }

  @Override
  public Integer findPropertyIndex(Annotated a) {
    ApiModelProperty prop = a.getAnnotation(ApiModelProperty.class);
    if (prop != null) {
      return prop.position();
    }
    return null;
  }

  public String findExampleValue(Annotated a) {
    ApiModelProperty prop = a.getAnnotation(ApiModelProperty.class);
    if (prop != null) {
      return "".equals(prop.example()) ? null : prop.example();
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
