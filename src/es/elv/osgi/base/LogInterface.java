package es.elv.osgi.base;

public interface LogInterface {
	public void debug(String message);
	public void info(String message);
	public void warn(String message);
	public void error(String message);
	public void debug(String message, Throwable exception);
	public void info(String message, Throwable exception);
	public void warn(String message, Throwable exception);
	public void error(String message, Throwable exception);
	public void error(Throwable exception);
}
