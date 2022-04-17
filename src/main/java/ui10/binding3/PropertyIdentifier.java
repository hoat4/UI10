package ui10.binding3;

import java.io.Serializable;

public class PropertyIdentifier {

    public static <T1, T2> PropertyIdentifier prop(I<T1, T2> i) {
        return null; // TODO
    }

    public interface I<T1, T2> extends Serializable {

        T2 m(T1 i);
    }
}
