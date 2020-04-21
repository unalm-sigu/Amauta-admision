/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.amauta.zelper.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author AlbatrossCloud
 */
public enum ConfiguracionTipoMatriculaEnum {
    
     BARR("Por Barrido"), ONLINE("En línea");
   
 private final String value;
   private static final Map<String, ConfiguracionTipoMatriculaEnum> lookup = new HashMap<>();

   static {
       for (ConfiguracionTipoMatriculaEnum d : ConfiguracionTipoMatriculaEnum.values()) {
           lookup.put(d.getValue(), d);
       }
   }

   private ConfiguracionTipoMatriculaEnum(String value) {
       this.value = value;
   }

   public String getValue() {
       return value;
   }

   public static ConfiguracionTipoMatriculaEnum get(String abbreviation) {
       return lookup.get(abbreviation);
   }

   public static String getNombre(String nombre) {

       for (ConfiguracionTipoMatriculaEnum d : ConfiguracionTipoMatriculaEnum.values()) {
           if (d.name().equalsIgnoreCase(nombre)) {
               return d.getValue();
           }
       }

       return nombre;
   }
}
