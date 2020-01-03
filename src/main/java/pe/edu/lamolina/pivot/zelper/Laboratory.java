package pe.edu.lamolina.pivot.zelper;

import java.util.Calendar;
import org.joda.time.DateTime;

public class Laboratory {

    public static void main666(String[] args) {

        DateTime dateTime = new DateTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime.toDate()); // Configuramos la fecha que se recibe
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getGreatestMinimum(Calendar.DAY_OF_MONTH));
        calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos

        System.out.println("" + dateTime.getMonthOfYear());

    }

}
