package com.itzstonlex.fastblock.api.util;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class ReflectionHelper {

    private final ImmutableMap<Class<?>, Class<?>> primitiveWrappersMap
            = ImmutableMap.<Class<?>, Class<?>>builder()

            .put(Boolean.class, boolean.class)
            .put(Integer.class, int.class)
            .put(Short.class, short.class)
            .put(Float.class, float.class)
            .put(Double.class, double.class)
            .put(Byte.class, byte.class)

            .build();

    @SneakyThrows
    public Object invokeMoved(Object src, String movedField, String targetMethod, Object... parameters) {
        Object movedSrc = src.getClass().getField(movedField).get(src);
        return invoke(movedSrc, targetMethod, parameters);
    }

    @SneakyThrows
    public Object invokeStatic(Class<?> cls, String name, Object... parameters) {
        return cls.getMethod(name, Arrays.stream(parameters).map(ReflectionHelper::toClass).toArray(Class[]::new)).invoke(null, parameters);
    }

    @SneakyThrows
    public Object invoke(Object src, String method, Object... parameters) {
        return src.getClass().getMethod(method, Arrays.stream(parameters).map(ReflectionHelper::toClass).toArray(Class[]::new)).invoke(src, parameters);
    }

    @SneakyThrows
    public Class<?> toClass(Object src) {
        Class<?> cls = src.getClass();

        Class<?> wrapper = primitiveWrappersMap.get(cls);

        if (wrapper != null) {
            return wrapper;
        }

        return cls;
    }
}
