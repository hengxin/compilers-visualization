package hmb.utils.clazz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hmb
 */
public final class ClassLoaderUtils {

    public static <T> T newInstance(Class<T> aClass, Class<?>[] parameterTypes, Object... initArgs) {
        try {
            return aClass.getDeclaredConstructor(parameterTypes).newInstance(initArgs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T invokeMethod(Method method, Object thisObject, Object... args) {
        try {
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(thisObject, args);
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method findMethod(Class<?> aClass, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        return aClass.getDeclaredMethod(methodName, parameterTypes);
    }

    public static <T> T invokeMemberMethod(Class<?> aClass, Object thisObject, String methodName,
                                           Class<?>[] parameterTypes, Object... args) throws NoSuchMethodException  {
        return invokeMethod(findMethod(aClass, methodName, parameterTypes), thisObject, args);
    }

    public static <T> T invokeStaticMethod(Class<?> aClass, String methodName, Class<?>[] parameterTypes, Object... args) throws NoSuchMethodException {
        return invokeMethod(findMethod(aClass, methodName, parameterTypes), null, args);
    }

    public static <T> T invokeMemberMethod(Object thisObject, Method method, Object... args) {
        return invokeMethod(method, thisObject, args);
    }

    public static <T> T invokeStaticMethod(Method method, Object... args) {
        return invokeMethod(method, null, args);
    }


}