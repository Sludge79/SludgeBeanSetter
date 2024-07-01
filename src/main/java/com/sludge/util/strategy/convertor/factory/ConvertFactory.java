package com.sludge.util.strategy.convertor.factory;

import com.sludge.util.strategy.api.TypeConvertor;
import com.sludge.util.strategy.convertor.EnumConvertor;
import com.sludge.util.strategy.convertor.InstantConvertor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sludge
 * @since 2024/7/1 10:24
 */
public class ConvertFactory {
    private static final Map<String, TypeConvertor> strategies = new HashMap<>();

    static {
        strategies.put("com.zt3000.common.base.BaseEnumDto,java.lang.Integer", new EnumConvertor());
        strategies.put("java.lang.String,java.time.Instant", new InstantConvertor());
    }

    public static TypeConvertor getStrategy(String type) {
        return strategies.get(type);
    }
}
