package de.jexp.collections;

import java.util.*;
import static java.util.Arrays.asList;

/**
 * @author Michael Hunger
 * @since 08.05.2009
 */
public class IoC<V> implements Iterable<V> {
    private final Iterable<V> it;

    public interface Spec<T> {
        boolean matches(T value);
    }

    public interface Convert<F, T> {
        T from(F from);
    }

    public interface Reduce<F, T> {
        T reduce(F value, T result);
    }


    public IoC(final Iterable<V> it) {
        this.it = it;
    }
    public IoC(final V...entries) {
        this(new ArrayIterable<V>(entries));
    }

    public Iterator<V> iterator() {
        return it.iterator();
    }

    public int each(final Spec<V> spec) {
        int count = 0;
        for (final V value : it) {
            spec.matches(value);
            count++;
        }
        return count;
    }

    public <T> List<T> map(final Convert<V, T> convert) {
        final List<T> result = new LinkedList<T>();
        for (final V from : it) {
            result.add(convert.from(from));
        }
        return result;
    }

    public String join(final String delim) {
        final StringBuilder sb=new StringBuilder();
        for (final V value : this) {
            sb.append(delim).append(value);
        }
        return sb.length() > 0 ? sb.substring(delim.length()) : "";
    }

    public <T> T reduce(final T start, final Reduce<V, T> reduce) {
        T result = start;
        for (final V value : it) {
            result = reduce.reduce(value, result);
        }
        return result;
    }

    public boolean exists(final Spec<V> spec) {
        for (final V value : it) {
            if (spec.matches(value)) return true;
        }
        return false;
    }

    public int remove(final Spec<V> spec) {
        int removed = 0;
        for (Iterator<V> iterator = it.iterator(); iterator.hasNext();) {
            if (spec.matches(iterator.next())) {
                iterator.remove();
                removed++;
            }
        }
        return removed;
    }

    public boolean all(final Spec<V> spec) {
        for (final V value : it) {
            if (!spec.matches(value)) return false;
        }
        return true;
    }

    public V first(final Spec<V> spec) {
        for (final V value : it) {
            if (spec.matches(value)) return value;
        }
        return null;
    }

    public List<V> filter(final Spec<V> spec) {
        final List<V> result = new LinkedList<V>();
        for (final V value : it) {
            if (spec.matches(value)) result.add(value);
        }
        return result;
    }

    public abstract static class Do {
        private Do() {
        }

        public static <V> int each(final Iterable<V> it, final Spec<V> spec) {
            return new IoC<V>(it).each(spec);
        }

        public static <V, T> List<T> map(final Iterable<V> it, final Convert<V, T> convert) {
            return new IoC<V>(it).map(convert);
        }

        public static <F, T> T reduce(final List<F> it, final T start, final Reduce<F, T> reduce) {
            return new IoC<F>(it).reduce(start, reduce);
        }

        public static <V> String join(final Iterable<V> it, final String delim) {
            return new IoC<V>(it).join(delim);
        }
        public static <V> String join(final String delim, final V...values) {
            return new IoC<V>(new IoC<V>(values)).join(delim);
        }
        
        public static <V> boolean exists(final Iterable<V> it, final Spec<V> spec) {
            return new IoC<V>(it).exists(spec);
        }

        public static <V> boolean all(final Iterable<V> it, final Spec<V> spec) {
            return new IoC<V>(it).all(spec);
        }

        public static <V> V first(final Iterable<V> it, final Spec<V> spec) {
            return new IoC<V>(it).first(spec);
        }

        public static <V> List<V> filter(final Iterable<V> it, final Spec<V> spec) {
            return new IoC<V>(it).filter(spec);
        }

        public static <V> int remove(final Iterable<V> it, final Spec<V> spec) {
            return new IoC<V>(it).remove(spec);
        }

        public static <T> ArrayList<T> array(final T... values) {
            return values.length == 0 ? new ArrayList<T>() : new ArrayList<T>(asList(values));
        }

        public static <T> HashSet<T> set(final T... values) {
            return values.length == 0 ? new HashSet<T>() : new HashSet<T>(asList(values));
        }

        public static <T> LinkedList<T> linked(final T... values) {
            return new LinkedList<T>(asList(values));
        }

        public static <T> Iterator<T> asIterator(final T... values) {
            return new ArrayIterator<T>(values);
        }
    }

    public static class ArraySet<T> extends AbstractSet<T> {
        private final T[] values;

        public ArraySet(final T... values) {
            this.values = values;
        }

        @Override
        public Iterator<T> iterator() {
            return new ArrayIterator<T>(values);
        }

        @Override
        public int size() {
            return values.length;
        }
    }

    public static class ArrayIterable<T> implements Iterable<T> {
        private final T[] entries;

        public ArrayIterable(final T... entries) {
            this.entries = entries;
        }

        public Iterator<T> iterator() {
            return new ArrayIterator<T>(entries);
        }
    }
    public static class ArrayIterator<T> implements Iterator<T> {
        private final T[] entries;
        int current = -1;

        public ArrayIterator(final T... entries) {
            this.entries = entries;
        }

        public boolean hasNext() {
            return current < entries.length-1;
        }

        public T next() {
            current++;
            return entries[current];
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
