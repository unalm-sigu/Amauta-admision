package pe.edu.lamolina.pivot.zelper;

import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Laboratory {
    
    public static void main666(String[] args) {
        Date hoy = new LocalDate().toDate();
        Date hoy2 = new DateTime(hoy).plusDays(1).minusSeconds(1).toDate();
        System.out.println(hoy);
        System.out.println(hoy2);
    }
}
