package com.gitee.pifeng.monitoring.starter.util;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 注解工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/2/11 10:48
 */
public class AnnotationUtils {

    /**
     * <p>
     * 在Spring Bean上查找注释
     * </p>
     *
     * @param <A>            注解
     * @param beanFactory    {@link DefaultListableBeanFactory} bean工厂
     * @param beanName       bean名字
     * @param annotationType 注解类型
     * @return A 注解
     * @author 皮锋
     * @custom.date 2025/2/11 10:56
     */
    public static <A extends Annotation> A findAnnotationOnBean(DefaultListableBeanFactory beanFactory, String beanName, Class<A> annotationType) {
        A annotation = beanFactory.findAnnotationOnBean(beanName, annotationType);
        if (Objects.isNull(annotation)) {
            // 适配较低版本的SpringBoot
            annotation = Optional.of(beanFactory)
                    .map(each -> (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName))
                    .map(RootBeanDefinition::getResolvedFactoryMethod)
                    .map(factoryMethod -> org.springframework.core.annotation.AnnotationUtils.getAnnotation(factoryMethod, annotationType))
                    .orElse(null);
        }
        return annotation;
    }

}