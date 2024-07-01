package com.sludge.util.strategy.convertor;

import com.sludge.util.strategy.api.InstanceProperty;
import com.sludge.util.strategy.api.TypeConvertor;

/**
 * @author Sludge
 * @since 2024/7/1 10:24
 */
public class InstantConvertor implements TypeConvertor {
    @Override
    public void append(StringBuilder sb, InstanceProperty property) {
        sb
                .append("\t\t")
                .append(property.getSourceInstanceName())
                .append(".")
                .append(property.getSetterMethod())
                .append("(InstantUtil.formatDateFull(")

                .append(property.getTargetInstanceName())
                .append(".")
                .append(property.getGetterMethod())
                .append("()")

                .append("));\n");
    }
}