package ui10.binding;

import java.util.Map;
import java.util.Set;

public interface ObservableMap<K, V> extends Map<K, V>, Observable<ObservableMap.MapChange<K, V>> {

    interface MapChange<K, V> {

        record Put<K, V>(Map<K, V> oldValues, Map<K, V> entries) implements MapChange<K, V> {
        }

        record Remove<K, V>(Set<K> entries) implements MapChange<K, V> {
        }
    }

}
