package hmb.utils.clazz;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;


public class ObjectManager<T> implements Closeable {
    private boolean shouldClose = false;
    private final ReloadClassLoader classLoader;
    private final Class<T> thisClass;
    private final T thisObject;

    @SuppressWarnings("unchecked")
    private ObjectManager(ReloadClassLoader classLoader, String fullClassName, Class<?>[] parameterTypes, Object... initArgs) {
        this.classLoader = classLoader;
        try {
            thisClass = (Class<T>) classLoader.loadClass(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        thisObject = ClassLoaderUtils.newInstance(thisClass, parameterTypes, initArgs);
    }

    public ObjectManager(URL url, String fullClassName, Class<?>[] parameterTypes, Object... initArgs) {
        this(new ReloadClassLoader("reload " + url.toString(), new URL[]{url}, ClassLoader.getSystemClassLoader()),
                fullClassName,
                parameterTypes,
                initArgs);
        shouldClose = true;
    }

    public ObjectManager(ObjectManager<?> objectManager, String fullClassName, Class<?>[] parameterTypes, Object... initArgs) {
        this(objectManager.classLoader,
                fullClassName,
                parameterTypes,
                initArgs);
    }

    public T get() {
        return thisObject;
    }

    public Method findMethod(String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException  {
        return ClassLoaderUtils.findMethod(thisClass, methodName, parameterTypes);
    }

    public <U> U invokeStaticMethod(String methodName, Class<?>[] parameterTypes, Object... args) throws NoSuchMethodException  {
        return ClassLoaderUtils.invokeStaticMethod(findMethod(methodName, parameterTypes), args);
    }

    public <U> U invokeMemberMethod(Method method, Object... args) {
        return ClassLoaderUtils.invokeMemberMethod(thisObject, method, args);
    }

    public <U> U invokeMemberMethod(String methodName, Class<?>[] parameterTypes, Object... args) throws NoSuchMethodException  {
        return this.invokeMemberMethod(findMethod(methodName, parameterTypes), args);
    }

    public <U> U invokeMemberMethod(String methodName) throws NoSuchMethodException  {
        return this.invokeMemberMethod(methodName, new Class[]{});
    }

    @Override
    public void close() {
        try {
            if (shouldClose) {
                classLoader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            shouldClose = false;
        }
    }
}
