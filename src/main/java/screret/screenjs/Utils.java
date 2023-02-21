package screret.screenjs;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

public class Utils {

    public static <K extends Enum<K>, V> ListMultimap<K, V> newEnumListMultimap(Class<K> keyClass) {
        return Multimaps.newListMultimap(Maps.newEnumMap(keyClass), Lists::newArrayList);
    }
}
