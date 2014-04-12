package com.fasterxml.jackson.module.swagger.model;

public class ModelRef {
  private String type = null;
  private String ref = null;
  private String qualifiedType = null;
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  public String getRef() {
    return ref;
  }
  public void setRef(String ref) {
    this.ref = ref;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }
  public void setQualifiedType(String qualifiedType) {
    this.qualifiedType = qualifiedType;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelRef {\n");
    sb.append("  type: ").append(type).append("\n");
    sb.append("  ref: ").append(ref).append("\n");
    sb.append("  qualifiedType: ").append(qualifiedType).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

