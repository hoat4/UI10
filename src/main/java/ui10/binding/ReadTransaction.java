package ui10.binding;

import java.util.HashSet;
import java.util.Set;

public class ReadTransaction {
    private final Set<Observable<?>> read = new HashSet<>();

    public void onRead(Observable<?> r) {
        read.add(r);
    }
}
