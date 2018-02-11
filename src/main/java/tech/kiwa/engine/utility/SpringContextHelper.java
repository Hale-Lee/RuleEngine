//Copyright Hale [hale2000@163.com]
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package tech.kiwa.engine.utility;
//package tech.kiwa.engine.framework;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.context.ApplicationContextAware;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
//public class SpringContextHelper implements ApplicationContextAware {
//
//	private static SpringContextUtils thisInstance = null;
//
//	private static WebApplicationContext applicationContext = null;
//
//	public static WebApplicationContext getApplicationContext() {
//		return applicationContext;
//	}
//
//	public static SpringContextUtils getInstance() {
//		if (null == thisInstance) {
//			return null;
//		}
//
//		return thisInstance;
//	}
//
//	/***
//	 * 根据�?个bean的id获取配置文件中相应的bean
//	 *
//	 * @param name
//	 * @return
//	 * @throws BeansException
//	 */
//	public static Object getBean(String name) throws BeansException {
//
//		return (applicationContext).getBean(name);
//	}
//
//	/***
//	 * 类似于getBean(String name)只是在参数中提供了需要返回到的类型�??
//	 *
//	 * @param name
//	 * @param requiredType
//	 * @return
//	 * @throws BeansException
//	 */
//
//	public static <T> T getBean(String name,  Class<T> requiredType) throws BeansException {
//		return applicationContext.getBean(name, requiredType);
//	}
//
//	/***
//	 * 类似于getBean(String name)只是在参数中提供了需要返回到的类型�??
//	 *
//	 * @param requiredType
//	 * @return
//	 * @throws BeansException
//	 */
//
//	public static <T> T getBean( Class<T> requiredType) throws BeansException {
//		return applicationContext.getBean(requiredType);
//	}
//
//
//
//	/***
//	 * 类似于getBean(String name)只是在参数中提供了需要返回到的类型�??
//	 *
//	 * @param requiredType
//	 * @return
//	 * @throws BeansException
//	 */
//
//	public static Object getBean(String name, Object... args) throws BeansException {
//		return applicationContext.getBean(name, args);
//	}
//
//
//	/**
//	 * 如果BeanFactory包含�?个与�?给名称匹配的bean定义，则返回true
//	 *
//	 * @param name
//	 * @return boolean
//	 */
//	public static boolean containsBean(String name) {
//
//		return applicationContext.containsBean(name);
//	}
//
//	/**
//	 * 判断以给定名字注册的bean定义是一个singleton还是�?个prototype�?
//	 * 如果与给定名字相应的bean定义没有被找到，将会抛出�?个异常（NoSuchBeanDefinitionException�?
//	 *
//	 * @param name
//	 * @return boolean
//	 * @throws NoSuchBeanDefinitionException
//	 */
//	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
//		return applicationContext.isSingleton(name);
//	}
//
//	/**
//	 * @param name
//	 * @return Class 注册对象的类�?
//	 * @throws NoSuchBeanDefinitionException
//	 */
//	@SuppressWarnings("rawtypes")
//	public static Class getType(String name) throws NoSuchBeanDefinitionException {
//		return applicationContext.getType(name);
//	}
//
//	/**
//	 * 如果给定的bean名字在bean定义中有别名，则返回这些别名
//	 *
//	 * @param name
//	 * @return
//	 * @throws NoSuchBeanDefinitionException
//	 */
//	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
//		return applicationContext.getAliases(name);
//	}
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		SpringContextUtils.applicationContext = (WebApplicationContext) applicationContext;
//	}
//
//}
