package de.jexp.collections;

import junit.framework.TestCase;

import java.util.*;
import static java.util.Arrays.asList;

import static de.jexp.collections.IoC.Do.set;

/**
 * @author Michael Hunger
 * @since 08.05.2009
 */
public class IoCDoTest extends TestCase {
    List<Integer> values = new ArrayList<Integer>(Arrays.asList(1,2,3));
    public void testEach() {
        final List<Integer> result=new ArrayList<Integer>();
        final int count=IoC.Do.each(values, new IoC.Spec<Integer>() {
            public boolean matches(final Integer value) {
                result.add(value);
                return true;
            }
        });
        assertEquals(values.size(),count);
        assertEquals(values, result);
    }

    public void testMap() {
        final List<Integer> result=IoC.Do.map(values, new IoC.Convert<Integer,Integer>() {
            public Integer from(final Integer from) {
                return from*from;
            }
        });
        assertEquals(asList(1,4,9), result);
    }

    public void testJoin() {
        assertEquals("",IoC.Do.join(","));
        assertEquals("1",IoC.Do.join(",",1));
        assertEquals("1,2,3",IoC.Do.join(",",1,2,3));

        assertEquals("",IoC.Do.join(Collections.emptyList(),","));
        assertEquals("1",IoC.Do.join(Collections.singletonList(1),","));
        assertEquals("1,2,3",IoC.Do.join(values,","));
    }
    public void testExists() {
        assertTrue(IoC.Do.exists(values, new OddSpec()));
        assertTrue(IoC.Do.exists(values, new GreaterZeroSpec()));
    }

    public void testAll() {
        assertTrue(IoC.Do.all(values, new GreaterZeroSpec()));
        assertFalse(IoC.Do.all(values,new OddSpec()));
    }

    public void testFirst() {
        assertEquals(1, (int)IoC.Do.first(values, new OddSpec()));
    }

    public void testFilter() {
        assertEquals(asList(1,3), IoC.Do.filter(values, new OddSpec()));
    }

    public void testRemove() {
        assertEquals(2, (int)IoC.Do.remove(values, new OddSpec()));
        assertEquals(asList(2),values);
    }

    public void testReduce() {
        assertEquals(6.0, (double)IoC.Do.reduce(values,0.0,new IoC.Reduce<Integer,Double>() {
              public Double reduce(final Integer value, final Double result) {
                  return result+value;
              }
        }));
    }
    public void testArray() {
        final List<Integer> result = IoC.Do.array(1, 2, 3);
        assertEquals(values, result);
        assertEquals(ArrayList.class, result.getClass());
    }

    public void testSet() {
        final HashSet<Integer> result = set(1, 2, 3);
        assertEquals(values, new ArrayList<Integer>(result));
        assertEquals(HashSet.class, result.getClass());
    }

    public void testLinked() {
        final List<Integer> result = IoC.Do.linked(1, 2, 3);
        assertEquals(values, result);
        assertEquals(LinkedList.class, result.getClass());
    }

    public void testArrayIterator() {
        checkIterator(1,2,3);
        checkIterator(1);
        checkIterator();
    }

    public void testAsSet() {
        assertEquals(new HashSet<Integer>(asList(1,2,3)), set(1, 2, 3, 3));
        assertEquals(new HashSet<Integer>(asList(1)), set(1,1));
        assertEquals(new HashSet<Integer>(), set());
    }
    private void checkIterator(final Integer...input) {
        final IoC.ArrayIterator<Integer> it = new IoC.ArrayIterator<Integer>(input);
        final Collection<Integer> result=new ArrayList<Integer>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        assertEquals(asList(input),result);
    }

    private static class OddSpec implements IoC.Spec<Integer> {
        public boolean matches(final Integer value) {
            return value % 2 == 1;
        }
    }

    private static class GreaterZeroSpec implements IoC.Spec<Integer> {
        public boolean matches(final Integer value) {
            return value > 0;
        }
    }
}
