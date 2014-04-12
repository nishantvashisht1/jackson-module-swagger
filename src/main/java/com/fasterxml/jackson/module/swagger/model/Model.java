package com.fasterxml.jackson.module.swagger.model;

import java.util.*;

public class Model {
  private String id = null;
  private String name = null;
  private String qualifiedType = null;
  /* LinkedHashMap */
  private List<ModelProperty> properties = new ArrayList<ModelProperty>();
  private String description = null;
  private String baseModel = null;
  private String discriminator = null;
  private List<String> subTypes = new ArrayList<String>();
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }
  public void setQualifiedType(String qualifiedType) {
    this.qualifiedType = qualifiedType;
  }

  public List<ModelProperty> getProperties() {
    return properties;
  }
  public void setProperties(List<ModelProperty> properties) {
    this.properties = properties;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public String getBaseModel() {
    return baseModel;
  }
  public void setBaseModel(String baseModel) {
    this.baseModel = baseModel;
  }

  public String getDiscriminator() {
    return discriminator;
  }
  public void setDiscriminator(String discriminator) {
    this.discriminator = discriminator;
  }

  public List<String> getSubTypes() {
    return subTypes;
  }
  public void setSubTypes(List<String> subTypes) {
    this.subTypes = subTypes;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Model {\n");
    sb.append("  id: ").append(id).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  qualifiedType: ").append(qualifiedType).append("\n");
    sb.append("  properties: ").append(properties).append("\n");
    sb.append("  description: ").append(description).append("\n");
    sb.append("  baseModel: ").append(baseModel).append("\n");
    sb.append("  discriminator: ").append(discriminator).append("\n");
    sb.append("  subTypes: ").append(subTypes).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

