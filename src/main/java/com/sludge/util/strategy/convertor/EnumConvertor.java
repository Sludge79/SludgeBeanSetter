package com.sludge.util.strategy.convertor;

import com.sludge.util.strategy.api.InstanceProperty;
import com.sludge.util.strategy.api.TypeConvertor;

/**
 * @author Sludge
 * @since 2024/7/1 10:24
 */
public class EnumConvertor implements TypeConvertor {
    @Override
    public void append(StringBuilder sb, InstanceProperty property) {
        sb
                .append("\t\t")
                .append(property.getSourceInstanceName())
                .append(".")
                .append(property.getSetterMethod())
                .append("(")

                .append("new BaseEnumDto(")
                .append(property.getSetterMethod().substring(3))
                .append(".getByKey(")

                .append(property.getTargetInstanceName())
                .append(".")
                .append(property.getGetterMethod())
                .append("()")

                .append(")));\n");
    }
}

