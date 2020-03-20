
package com.github.gungnirlaevatain.mq;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

@Slf4j
public class ReflectAnnotationUtil {
    public static <T extends Annotation> Map<String, Object> getAnnotationMemberValues(Object source, Class<T> annotation) {
        Annotation a = source.getClass().getAnnotation(annotation);
        return getAnnotationMemberValues(a);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAnnotationMemberValues(Annotation annotation) {
        //获取注解这个代理实例所持有的 InvocationHandler
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        // 获取 AnnotationInvocationHandler 的 valueCache 字段
        Field declaredField;
        try {
            declaredField = getAnnotationValuesField(invocationHandler.getClass());
            // 因为这个字段事 private final 修饰，所以要打开权限
            declaredField.setAccessible(true);
            // 获取 valueCache
            return (Map<String, Object>) declaredField.get(invocationHandler);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }


    private static <T extends InvocationHandler> Field getAnnotationValuesField(Class<T> clazz) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField("memberValues");
        } catch (NoSuchFieldException e) {
            return clazz.getDeclaredField("valueCache");
        }
    }
}
