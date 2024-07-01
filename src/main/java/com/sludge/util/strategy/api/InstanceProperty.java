package com.sludge.util.strategy.api;

/**
 * @author Sludge
 * @since 2024/7/1 10:37
 */
public class InstanceProperty {
    private String getterType;
    private String setterType;
    private String sourceInstanceName;
    private String setterMethod;
    private String targetInstanceName;
    private String getterMethod;

    public InstanceProperty() {
    }

    public InstanceProperty(String getterType, String setterType, String sourceInstanceName, String setterMethod, String targetInstanceName, String getterMethod) {
        this.getterType = getterType;
        this.setterType = setterType;
        this.sourceInstanceName = sourceInstanceName;
        this.setterMethod = setterMethod;
        this.targetInstanceName = targetInstanceName;
        this.getterMethod = getterMethod;
    }

    public String getGetterType() {
        return getterType;
    }

    public void setGetterType(String getterType) {
        this.getterType = getterType;
    }

    public String getSetterType() {
        return setterType;
    }

    public void setSetterType(String setterType) {
        this.setterType = setterType;
    }

    public String getSourceInstanceName() {
        return sourceInstanceName;
    }

    public void setSourceInstanceName(String sourceInstanceName) {
        this.sourceInstanceName = sourceInstanceName;
    }

    public String getSetterMethod() {
        return setterMethod;
    }

    public void setSetterMethod(String setterMethod) {
        this.setterMethod = setterMethod;
    }

    public String getTargetInstanceName() {
        return targetInstanceName;
    }

    public void setTargetInstanceName(String targetInstanceName) {
        this.targetInstanceName = targetInstanceName;
    }

    public String getGetterMethod() {
        return getterMethod;
    }

    public void setGetterMethod(String getterMethod) {
        this.getterMethod = getterMethod;
    }
}
