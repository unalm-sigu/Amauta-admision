package pe.edu.lamolina.pivot.zelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Laboratory {

    public static void main2(String[] args) {
        DateTime dia = new DateTime();
        DateTime dia1 = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        dia = formatter.parseDateTime("11:30");
        if (dia.isAfter(dia1)) {
              System.out.println("Siii");
        }
      
    }
}
