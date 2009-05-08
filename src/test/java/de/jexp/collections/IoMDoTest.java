package de.jexp.collections;

import junit.framework.TestCase;

import java.util.*;
import static java.util.Arrays.asList;

import static de.jexp.collections.IoM.Do._;
import static de.jexp.collections.IoM.Do.hash;

/**
 * @author Michael Hunger
 * @since 08.05.2009
 */
public class IoMDoTest extends TestCase {
    Map<String, Integer> values = createTestMap();

    private Map<String, Integer> createTestMap() {
        final Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        result.put("a",1);
        result.put("b",2);
        result.put("c",3);
        return result;
    }

    public void testEach() {
        final Map<String,Integer> result=new HashMap<String,Integer>();
        final int count=IoM.Do.each(values, new IoM.Spec<String,Integer>() {
            public boolean matches(final String key, final Integer value) {
                result.put(key,value);
                return true;
            }
        });
        assertEquals(values.size(),count);
        assertEquals(values, result);
    }

    public void testMapList() {
        final List<Integer> result=IoM.Do.mapList(values, new IoM.Convert<String,Integer,Integer>() {
            public Integer from(final String key, final Integer value) {
                return value*value;
            }
        });
        assertEquals(asList(1,4,9), result);

    }
    public void testMap() {
        final Map<Integer, String> result=IoM.Do.map(values, new IoM.MapConvert<String,Integer,Integer,String>() {
            public Map.Entry<Integer, String> from(final String key, final Integer value) {
                return _(value*value,key);
            }
        });
        assertEquals(hash(_(1,"a"),_(4,"b"),_(9,"c")), result);

    }

    public void testExists() {
        assertTrue(IoM.Do.exists(values, new OddSpec()));
        assertTrue(IoM.Do.exists(values, new GreaterZeroSpec()));
    }

    public void testAll() {
        assertTrue(IoM.Do.all(values, new GreaterZeroSpec()));
        assertFalse(IoM.Do.all(values, new OddSpec()));
    }

    public void testFirst() {
        assertEquals(_("a",1), IoM.Do.first(values, new OddSpec()));
    }

    public void testFilter() {
        assertEquals(hash(_("a",1),_("c",3)), IoM.Do.filter(values, new OddSpec()));
    }

    public void testRemove() {
        assertEquals(2, (int)IoM.Do.remove(values, new OddSpec()));
        assertEquals(hash(_("b",2)),values);
    }

    public void testReduce() {
        assertEquals(6.0, (double)IoM.Do.reduce(values,0.0,new IoM.Reduce<String,Integer,Double>() {
            public Double reduce(final String key, final Integer value, final Double result) {
                return result+value;
            }
        }));
    }
    public void testHash() {
        final HashMap<String, Integer> result = IoM.Do.hash(_("a",1), _("b",2), _("c",3));
        assertEquals(values, result);
        assertEquals(HashMap.class, result.getClass());
    }

    public void testLinked() {
        final LinkedHashMap<String, Integer> result = IoM.Do.linked(_("a",1), _("b",2), _("c",3));
        assertEquals(values, result);
        assertEquals(LinkedHashMap.class, result.getClass());
    }

    public void test_() {
        assertEquals(Collections.singletonMap("a",1).entrySet().iterator().next(),_("a",1));
        assertEquals(new HashMap.SimpleEntry<String,Integer>("a",1),_("a",1));
    }


    private static class OddSpec implements IoM.Spec<String,Integer> {
        public boolean matches(final String key, final Integer value) {
            return value % 2 == 1;
        }
    }

    private static class GreaterZeroSpec implements IoM.Spec<String, Integer> {
        public boolean matches(final String key, final Integer value) {
            return value > 0;
        }
    }
}
