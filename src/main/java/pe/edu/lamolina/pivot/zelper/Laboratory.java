package pe.edu.lamolina.pivot.zelper;

import java.util.Base64;

public class Laboratory {

    public static void main666(String[] args) {
        byte[] decoded = Base64.getMimeDecoder().decode("aHR0cDovL2xvY2FsaG9zdDo5OTAwL2FjYWRlbWljby9hbHVtbm8/cXVlcmllc1tzZWFyY2hdPXBhbGl6YSZxdWVyaWVzW21vZS5jb2RpZ29dPXByZWdyYWRv");
        String output = new String(decoded);
        System.out.println(output);

    }
}
