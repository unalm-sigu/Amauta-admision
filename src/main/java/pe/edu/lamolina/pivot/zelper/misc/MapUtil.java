package pe.edu.lamolina.pivot.zelper.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.miscelanea.ObjectUtil;

public class MapUtil {

    public static Map storeItems(String attr, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attr);
            if (!(key == null)) {
                map.put(key, item);
            }
        });
        return map;
    }

    public static Map storeItems(String attrKey, String attrValue, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attrKey);
            if (!(key == null)) {
                Object val = ObjectUtil.getParentTree(item, attrValue);
                map.put(key, val);
            }
        });
        return map;
    }

    public static Map storeLists(String attr, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attr);
            if (!(key == null)) {
                List lista = (List) map.get(key);
                if (lista == null) {
                    lista = new ArrayList();
                    map.put(key, lista);
                }
                lista.add(item);
            }
        });
        return map;
    }

    public static Map storeLists(String attrKey, String attrValue, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attrKey);
            if (!(key == null)) {
                Object val = ObjectUtil.getParentTree(item, attrValue);
                List lista = (List) map.get(key);
                if (lista == null) {
                    lista = new ArrayList();
                    map.put(key, lista);
                }
                lista.add(val);
            }
        });
        return map;
    }

}
