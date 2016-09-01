package com.nd.resource.transform.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by way on 2016/8/30.
 */
@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;     //Spring应用上下文环境

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.applicationContext = applicationContext;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws org.springframework.beans.BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return SpringContext.applicationContext.getBean(name);
    }

    /**
     * @param requiredType
     * @param <T>
     * @return
     * @throws org.springframework.beans.BeansException
     */
    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return SpringContext.applicationContext.getBean(requiredType);
    }
}
