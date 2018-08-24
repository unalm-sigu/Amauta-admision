package pe.edu.lamolina.pivot.zelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Laboratory {

    public static void main666(String[] args) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy");

        Date dateResult = null;
        String date = "05/08/2018";

        List<SimpleDateFormat> formatos = Arrays.asList(formatter, formatter2, formatter3, formatter4);
        for (SimpleDateFormat formato : formatos) {
            try {
                dateResult = formato.parse(date);
                break;
            } catch (ParseException e) {
                System.out.println(formato.toPattern());
            }
        }
        if (dateResult != null) {
            System.out.println(dateResult);
        }
    }
}
