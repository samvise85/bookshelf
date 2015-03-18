package it.samvise85.bookshelf.context;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ServiceLocator implements ApplicationContextAware {
	private static Logger log = Logger.getLogger(ServiceLocator.class);
	
	private ApplicationContext ac;
	
	private static ServiceLocator instance;
	
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		this.ac = arg0;
	}

	private ServiceLocator() {
		instance = this;
	}
	
	public static ServiceLocator getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getService(String serviceName) {
		try {
			Class<T> clazz = (Class<T>) Class.forName(serviceName);
			return ac.getBean(clazz);
		} catch (ClassNotFoundException e) {
			log.error(e);
			return null;
		}
	}
	
	public <T> T getService(Class<T> clazz) {
		return ac.getBean(clazz);
	}
}
