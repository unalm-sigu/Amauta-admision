package pe.edu.lamolina.pivot.zelper;

import org.apache.commons.lang3.StringUtils;

public class Laboratory {

    public static void main(String[] args) {
        
        String base = "/academico/alumno/list";
         int thirdIndex = StringUtils.ordinalIndexOf(base, "/", 3);
        System.out.println(thirdIndex);
        System.out.println(base.substring(0, thirdIndex));
    }

}
