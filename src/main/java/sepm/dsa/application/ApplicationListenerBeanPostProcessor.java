package sepm.dsa.application;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;

/**
 *
 * {@link BeanPostProcessor} which registers ApplicationListeners after Object
 * creation. Is mainly useful when using prototype scoped beans to automatically
 * register them on the {@link ApplicationContext}
 * @author Marten Deinum
 * Taken from http://forum.spring.io/forum/spring-projects/container/35965-applicationlistener-interface-makes-beans-eagerly-instantiated
 */
public class ApplicationListenerBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

	private AbstractApplicationContext ctx;

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (ctx != null) {
			if (bean instanceof ApplicationListener) {
				if (!ctx.getApplicationListeners().contains(bean)) {
					ctx.addApplicationListener((ApplicationListener) bean);
				}
			}
		}
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext instanceof AbstractApplicationContext) {
			this.ctx=(AbstractApplicationContext) applicationContext;
		}
	}
}