
package com.github.gungnirlaevatain.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ConsumerUtil {

    public static Map<Method, Set<MqListener>> getAnnotatedMethods(Object bean) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        return MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<MqListener>>) method -> {
                    Set<MqListener> listenerMethods = ConsumerUtil.findListenerAnnotations(method, MqListener.class);
                    return (!listenerMethods.isEmpty() ? listenerMethods : null);
                });
    }

    public static <T extends Annotation> Set<T> findListenerAnnotations(Method method, Class<T> cls) {
        Set<T> listeners = new HashSet<>();
        T ann = AnnotationUtils.findAnnotation(method, cls);
        if (ann != null) {
            listeners.add(ann);
        }
        return listeners;
    }

    public static Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                // Found a @MqListener method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (NoSuchMethodException noMethod) {
                        log.error("no method that name is [{}] in this bean that class is {}",
                                method.getName(), iface.getName());
                    }
                }
            } catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "@MqListener method '%s' found on bean target class '%s', " +
                                "but not found in any interface(s) for bean JDK proxy. Either " +
                                "pull the method up to an interface or switch to subclass (CGLIB) " +
                                "proxies by setting proxy-target-class/proxyTargetClass " +
                                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
            }
        }
        return method;
    }


}
