package com.sludge.util.strategy.api;

/**
 * @author Sludge
 * @since 2024/7/1 10:37
 */
public class InstanceProperty {
    private String type;
    private String sourceInstanceName;
    private String setterMethod;
    private String targetInstanceName;
    private String getterMethod;

    public InstanceProperty() {
    }

    public InstanceProperty(String type, String sourceInstanceName, String setterMethod, String targetInstanceName, String getterMethod) {
        this.type = type;
        this.sourceInstanceName = sourceInstanceName;
        this.setterMethod = setterMethod;
        this.targetInstanceName = targetInstanceName;
        this.getterMethod = getterMethod;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
