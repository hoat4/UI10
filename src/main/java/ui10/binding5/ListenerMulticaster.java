package ui10.binding5;

import java.lang.reflect.Proxy;
import java.util.List;

public class ListenerMulticaster {

    @SuppressWarnings("unchecked")
    public static <L> L makeMulticaster(Class<L> listenerClass, List<L> destinations) {
        return (L) Proxy.newProxyInstance(listenerClass.getClassLoader(), new Class[]{listenerClass}, (obj, method, args) -> {
            for (L dest : destinations)
                method.invoke(dest, args);
            return null;
        });
    }
}
