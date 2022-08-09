package com.itzstonlex.fastblock.api.util;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class ReflectionHelper {

    private final ImmutableMap<Class<?>, Class<?>> primitiveWrappersMap
            = ImmutableMap.<Class<?>, Class<?>>builder()

            .put(Boolean.class, boolean.class)
            .put(Integer.class, int.class)
            .put(Short.class, short.class)
            .put(Byte.class, byte.class)
            .put(Float.class, float.class)
            .put(Double.class, double.class)
            .build();

    private final Map<String, Method> staticMethodsMap = new ConcurrentHashMap<>();
    private final Map<String, Method> methodsMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public Object invokeMoved(@NonNull Object src, @NonNull String movedField, @NonNull String targetMethod, Object... parameters) {
        Object movedSrc = src.getClass().getField(movedField).get(src);
        return invoke(movedSrc, targetMethod, parameters);
    }

    @SneakyThrows
    public Object invokeStatic(@NonNull Class<?> cls, @NonNull String methodName, Object... parameters) {
        String mappedName = cls + methodName;
        Method method = staticMethodsMap.get(mappedName);

        if (method == null) {
            method = cls.getMethod(methodName, Arrays.stream(parameters).map(ReflectionHelper::toClass).toArray(Class[]::new));

            staticMethodsMap.put(mappedName, method);
        }

        return method.invoke(null, parameters);
    }

    @SneakyThrows
    public Object invoke(@NonNull Object src, @NonNull String methodName, Object... parameters) {
        Class<?> cls = src.getClass();

        String mappedName = cls + methodName;
        Method method = methodsMap.get(mappedName);

        if (method == null) {
            method = cls.getMethod(methodName, Arrays.stream(parameters).map(ReflectionHelper::toClass).toArray(Class[]::new));

            methodsMap.put(mappedName, method);
        }

        return method.invoke(src, parameters);
    }

    @SneakyThrows
    public Class<?> toClass(@NonNull Object src) {
        Class<?> cls = src.getClass();

        Class<?> wrapper = primitiveWrappersMap.get(cls);

        if (wrapper != null) {
            return wrapper;
        }

        return cls;
    }
}
