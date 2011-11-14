package es.elv.osgi.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

public abstract class SCRHelper {
	protected BundleContext bundleContext;
	protected ComponentContext componentContext;

    protected void activate(ComponentContext ctxt) throws Exception {
    	this.componentContext = ctxt;
    	this.bundleContext = ctxt.getBundleContext();

	log = createLogInterface(bundleContext);

	autolocateFields(componentContext, this);

		load();
    }

    /**
     * Resolves all class field members annotated with @Autolocate,
     * even private ones.
     */
    public static void autolocateFields(ComponentContext ctx, Object obj) throws
		SecurityException, NoSuchMethodException, IllegalArgumentException,
		IllegalAccessException, InvocationTargetException {

		Autolocate autolocate = null;
		for (final Field f : obj.getClass().getDeclaredFields())
			if ((autolocate = f.getAnnotation(Autolocate.class)) != null) {
				f.setAccessible(true);
				Object service = null;
				Class<?> typeToLocateFor = f.getType();
				if (!autolocate.type().equals(Autolocate.class))
					typeToLocateFor = autolocate.type();

				service = locateServiceFor(ctx, typeToLocateFor);

				if (!autolocate.method().equals("")) {
					Method service_m = service.getClass().getMethod(autolocate.method());
					service = service_m.invoke(service);
				}
				f.set(obj, service);
			}
    }

    protected void deactivate(ComponentContext ctxt) throws Exception {
		unload();

		this.componentContext = null;
		this.bundleContext = null;
    }

	protected void load() throws Exception {}
	protected void unload() throws Exception {}

	/**
	 * A wrapper for SCR to retrieve all Services of the given class.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> locateServicesFor(ComponentContext ctx, Class<T> tt) {
		List<T> ret = new LinkedList<T>();
		String[] name = tt.getName().split("\\.");
		Object[] vv = ctx.locateServices(name[name.length - 1]);
		if (null != vv)
			for (Object v : vv)
				ret.add((T) v);
		return ret;
	}

	/**
	 * A wrapper for SCR to retrieve all Services of the given class.
	 */
	protected <T> List<T> locateServicesFor(Class<T> tt) {
		return locateServicesFor(componentContext, tt);
	}

	/**
	 * A wrapper for SCR to retrieve the first-qualifying service of the given class.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T locateServiceFor(ComponentContext ctx, Class<T> tt) {
		String[] name = tt.getName().split("\\.");
		return (T) ctx.locateService(name[name.length - 1]);
	}

	/**
	 * A wrapper for SCR to retrieve the first-qualifying service of the given class.
	 */

	protected <T> T locateServiceFor(Class<T> tt) {
		return locateServiceFor(componentContext, tt);
	}

	/**
	 * The log variable implements a LogInterface which can be used to emit
	 * OSGi-compatible log messages; if a LogService is available, it will be
	 * used - otherwise, messages go to stderr.
	 */
	protected LogInterface log;

	public static LogInterface createLogInterface(final BundleContext bctx) {
		return 	new LogInterface() {
			private void log(int level, String message, Throwable exception) {
				LogService s = null;
				ServiceReference<?> logRef = bctx.getServiceReference("org.osgi.service.log.LogService");
				if (logRef != null)
					s = (LogService) bctx.getService(logRef);

				if (s != null) {
					if (exception != null)
						s.log(level, message, exception);
					else
						s.log(level, message);
				} else {
					System.err.println(message);
					if (exception != null)
						exception.printStackTrace();
				}
			}
			@Override
			public void debug(String message) {
				log(LogService.LOG_DEBUG, message, null);
			}
			@Override
			public void debug(String message, Throwable exception) {
				log(LogService.LOG_DEBUG, message, exception);
			}
			@Override
			public void error(String message) {
				log(LogService.LOG_ERROR, message, null);
			}
			@Override
			public void error(String message, Throwable exception) {
				log(LogService.LOG_ERROR, message, exception);
			}
			@Override
			public void info(String message) {
				log(LogService.LOG_INFO, message, null);
			}
			@Override
			public void info(String message, Throwable exception) {
				log(LogService.LOG_INFO, message, exception);
			}
			@Override
			public void warn(String message) {
				log(LogService.LOG_WARNING, message, null);
			}
			@Override
			public void warn(String message, Throwable exception) {
				log(LogService.LOG_WARNING, message, exception);
			}
			@Override
			public void error(Throwable exception) {
				log(LogService.LOG_ERROR, "", exception);
			}
		};
	}
}
