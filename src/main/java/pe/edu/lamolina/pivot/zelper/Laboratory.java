package pe.edu.lamolina.pivot.zelper;

import java.net.URI;
import java.net.URL;

public class Laboratory {

    public static void main2(String[] args) {
        URL url;
        try {
            url = new URL("http://www.google.com/caso/wer?oaso=2323&dfdf=2232"); //Some instantiated URL object
            URI uri = url.toURI();
            
            System.out.println(url.getPath());
            System.out.println(uri.getPath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
