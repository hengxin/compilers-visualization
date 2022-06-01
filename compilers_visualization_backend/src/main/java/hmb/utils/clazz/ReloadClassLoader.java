package hmb.utils.clazz;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @author hmb
 */
public class ReloadClassLoader extends URLClassLoader {

    public ReloadClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ReloadClassLoader(URL[] urls) {
        super(urls);
    }

    public ReloadClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public ReloadClassLoader(String name, URL[] urls, ClassLoader parent) {
        super(name, urls, parent);
    }

    public ReloadClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(name, urls, parent, factory);
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    /**
     * Loads the class.  The default implementation of this method searches for
     * classes in the
     * following order:
     *
     * <ol>
     *
     *   <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
     *   has already been loaded.  </p></li>
     *
     *   <li><p> Invoke the {@link #findClass(String)} method to find the
     *   class.  </p></li>
     *
     *   <li><p> Invoke the {@link #loadClass(String) loadClass} method
     *   on the parent class loader.  If the parent is {@code null} the class
     *   loader built into the virtual machine is used, instead.  </p></li>
     *
     * </ol>
     *
     * @param name    The <a href="#binary-name">binary name</a> of the class
     * @param resolve If {@code true} then resolve the class
     * @return The resulting {@code Class} object
     * @throws ClassNotFoundException If the class could not be found
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = this.findLoadedClass(name);
            if (c == null) {
                try {
                    c = this.findClass(name);
                } catch (ClassNotFoundException ignored) {
                }
                if (c == null) {
                    c = getParent().loadClass(name);
                }
            }
            if (resolve) {
                super.resolveClass(c);
            }
            return c;
        }
    }
}