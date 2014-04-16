package com.fasterxml.jackson.module.swagger.model;

import java.util.*;

public class ModelProperty {
  private String type = null;
  private String qualifiedType = null;
  private Integer position = null;
  private Boolean required = null;
  private String description = null;
  private List<AllowableValue> allowableValues = new ArrayList<AllowableValue>();
  private ModelRef items = null;
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }
  public void setQualifiedType(String qualifiedType) {
    this.qualifiedType = qualifiedType;
  }

  public Integer getPosition() {
    return position;
  }
  public void setPosition(Integer position) {
    this.position = position;
  }

  public Boolean getRequired() {
    return required;
  }
  public void setRequired(Boolean required) {
    this.required = required;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public List<AllowableValue> getAllowableValues() {
    return allowableValues;
  }
  public void setAllowableValues(List<AllowableValue> allowableValues) {
    this.allowableValues = allowableValues;
  }

  public ModelRef getItems() {
    return items;
  }
  public void setItems(ModelRef items) {
    this.items = items;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelProperty {\n");
    sb.append("  type: ").append(type).append("\n");
    sb.append("  qualifiedType: ").append(qualifiedType).append("\n");
    sb.append("  position: ").append(position).append("\n");
    sb.append("  required: ").append(required).append("\n");
    sb.append("  description: ").append(description).append("\n");
    sb.append("  allowableValues: ").append(allowableValues).append("\n");
    sb.append("  items: ").append(items).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

