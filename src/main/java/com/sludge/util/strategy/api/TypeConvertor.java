package com.sludge.util.strategy.api;

/**
 * @author Sludge
 * @since 2024/7/1 9:57
 */
public interface TypeConvertor {
    void append(StringBuilder sb, InstanceProperty property);
}
