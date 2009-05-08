package de.jexp.collections;

import java.util.*;

/**
 * @author Michael Hunger
 * @since 08.05.2009
 */
public class IoM<K, V> implements Iterable<Map.Entry<K, V>> {
    private final Map<K, V> it;

    public interface Spec<K, V> {
        boolean matches(K key, V value);
    }

    public interface Convert<K, V, T> {
        T from(K key, V value);
    }
    public interface MapConvert<K, V, K2,V2> {
        Map.Entry<K2,V2> from(K key, V value);
    }

    public interface Reduce<K, V, T> {
        T reduce(K key, V value, T result);
    }


    public IoM(final Map<K, V> it) {
        this.it = it;
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return it.entrySet().iterator();
    }

    public int each(final Spec<K, V> spec) {
        int count = 0;
        for (final Map.Entry<K, V> entry : this) {
            spec.matches(entry.getKey(), entry.getValue());
            count++;
        }
        return count;
    }

    public <T> List<T> mapList(final Convert<K, V, T> convert) {
        final List<T> result = new LinkedList<T>();
        for (final Map.Entry<K, V> entry : this) {
            result.add(convert.from(entry.getKey(), entry.getValue()));
        }
        return result;
    }
    public <K2,V2> Map<K2,V2> map(final MapConvert<K, V, K2,V2> convert) {
        final Map<K2,V2> result = new HashMap<K2,V2>();
        for (final Map.Entry<K, V> entry : this) {
            final Map.Entry<K2, V2> resultEntry = convert.from(entry.getKey(), entry.getValue());
            result.put(resultEntry.getKey(),resultEntry.getValue());
        }
        return result;
    }

    public <T> T reduce(final T start, final Reduce<K, V, T> reduce) {
        T result = start;
        for (final Map.Entry<K, V> entry : this) {
            result = reduce.reduce(entry.getKey(), entry.getValue(), result);
        }
        return result;
    }

    public boolean exists(final Spec<K, V> spec) {
        for (final Map.Entry<K, V> entry : this) {
            if (spec.matches(entry.getKey(), entry.getValue())) return true;
        }
        return false;
    }

    public int remove(final Spec<K, V> spec) {
        int removed = 0;
        for (Iterator<Map.Entry<K, V>> iterator = this.iterator(); iterator.hasNext();) {
            final Map.Entry<K, V> entry = iterator.next();
            if (spec.matches(entry.getKey(), entry.getValue())) {
                iterator.remove();
                removed++;
            }
        }
        return removed;
    }

    public boolean all(final Spec<K, V> spec) {
        for (final Map.Entry<K, V> entry : this) {
            if (!spec.matches(entry.getKey(), entry.getValue())) return false;
        }
        return true;
    }

    public Map.Entry<K, V> first(final Spec<K, V> spec) {
        for (final Map.Entry<K, V> entry : this) {
            if (spec.matches(entry.getKey(), entry.getValue())) return entry;
        }
        return null;
    }

    public Map<K, V> filter(final Spec<K, V> spec) {
        final Map<K, V> result = new HashMap<K, V>();
        for (final Map.Entry<K, V> entry : this) {
            if (spec.matches(entry.getKey(), entry.getValue())) result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public abstract static class Do {
        private Do() {
        }

        public static <K, V> int each(final Map<K, V> it, final Spec<K, V> spec) {
            return new IoM<K, V>(it).each(spec);
        }

        public static <K, V, T> List<T> mapList(final Map<K, V> it, final Convert<K, V, T> convert) {
            return new IoM<K, V>(it).mapList(convert);
        }
        public static <K, V, K2,V2> Map<K2,V2> map(final Map<K, V> it, final MapConvert<K, V, K2,V2> convert) {
            return new IoM<K, V>(it).map(convert);
        }

        public static <K, V, T> T reduce(final Map<K, V> it, final T start, final Reduce<K, V, T> reduce) {
            return new IoM<K, V>(it).reduce(start, reduce);
        }

        public static <K, V> boolean exists(final Map<K, V> it, final Spec<K, V> spec) {
            return new IoM<K, V>(it).exists(spec);
        }

        public static <K, V> boolean all(final Map<K, V> it, final Spec<K, V> spec) {
            return new IoM<K, V>(it).all(spec);
        }

        public static <K, V> Map.Entry<K, V> first(final Map<K, V> it, final Spec<K, V> spec) {
            return new IoM<K, V>(it).first(spec);
        }

        public static <K, V> Map<K, V> filter(final Map<K, V> it, final Spec<K, V> spec) {
            return new IoM<K, V>(it).filter(spec);
        }

        public static <K, V> int remove(final Map<K, V> it, final Spec<K, V> spec) {
            return new IoM<K, V>(it).remove(spec);
        }

        public static <K, V> HashMap<K, V> hash(final Map.Entry<K, V>... values) {
            return values.length == 0 ? new HashMap<K, V>() : new HashMap<K, V>(asMap(values));
        }

        public static <K, V> LinkedHashMap<K, V> linked(final Map.Entry<K, V>... values) {
            return new LinkedHashMap<K, V>(asMap(values));
        }

        private static <K, V> Map<K, V> asMap(final Map.Entry<K, V>... entries) {
            return new EntryMap<K, V>(entries);
        }

        public static <K, V> Map.Entry<K, V> _(final K key, final V value) {
            return new HashMap.SimpleEntry<K, V>(key, value);
        }
    }

    public static class EntryMap<K, V> extends AbstractMap<K, V> {
        final Set<Entry<K, V>> set;

        public EntryMap(final Entry<K, V>... entries) {
            set = IoC.Do.set(entries);
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return set;
        }
    }
}
