/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author AlbatrossCloud
 */
public enum ConfiguracionEventoMatriculaEnum {
    
   MAT_REG("Matricula regular"), MAR_VER("Matricula verano"), MAT_REI("Reinscripción");
   
 private final String value;
   private static final Map<String, ConfiguracionEventoMatriculaEnum> lookup = new HashMap<>();

   static {
       for (ConfiguracionEventoMatriculaEnum d : ConfiguracionEventoMatriculaEnum.values()) {
           lookup.put(d.getValue(), d);
       }
   }

   private ConfiguracionEventoMatriculaEnum(String value) {
       this.value = value;
   }

   public String getValue() {
       return value;
   }

   public static ConfiguracionEventoMatriculaEnum get(String abbreviation) {
       return lookup.get(abbreviation);
   }

   public static String getNombre(String nombre) {

       for (ConfiguracionEventoMatriculaEnum d : ConfiguracionEventoMatriculaEnum.values()) {
           if (d.name().equalsIgnoreCase(nombre)) {
               return d.getValue();
           }
       }

       return nombre;
   }
}
