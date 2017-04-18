package pe.edu.lamolina.pivot.zelper.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.miscelanea.ObjectUtil;

public class MapUtil {

    public static Map storeItems(String attr, List items) {
        Map map = new LinkedHashMap();
        for (Object item : items) {
            Object id = ObjectUtil.getParentTree(item, attr);
            map.put(id, item);
        }
        return map;
    }

    public static Map storeLists(String attr, List items) {
        Map map = new LinkedHashMap();
        for (Object item : items) {
            Long id = (Long) ObjectUtil.getParentTree(item, attr);
            List lista = (List) map.get(id);
            if (lista == null) {
                lista = new ArrayList();
                map.put(id, lista);
            }
            lista.add(item);
        }
        return map;
    }

}
