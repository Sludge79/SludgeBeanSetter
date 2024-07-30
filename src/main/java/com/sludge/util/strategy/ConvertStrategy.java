package com.sludge.util.strategy;

import com.sludge.util.strategy.api.InstanceProperty;
import com.sludge.util.strategy.api.TypeConvertor;
import com.sludge.util.strategy.convertor.factory.ConvertFactory;

/**
 * @author Sludge
 * @since 2024/7/1 10:24
 */
public class ConvertStrategy {

    static final String TEMPLATE = "%s,%s";

    public static boolean append(StringBuilder sb, InstanceProperty property) {
        TypeConvertor strategy = ConvertFactory.getStrategy(String.format(TEMPLATE, property.getGetterType(), property.getSetterType()));
        if (strategy != null) {
            strategy.append(sb, property);
            return true;
        }
        return false;
    }

}
