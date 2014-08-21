package shop.services;

import javax.naming.InitialContext;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ShoppingApplication extends Application {
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public ShoppingApplication() {
		classes.add(EntityNotFoundExceptionMapper.class);
	}

	public Set<Class<?>> getClasses() {
		return classes;
	}

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	public Set<Object> getSingletons() {
		try {
			InitialContext ctx = new InitialContext();
			String xmlFile = (String) ctx
					.lookup("java:comp/env/spring-beans-file");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		HashSet<Object> set = new HashSet();
		return set;
	}

}
