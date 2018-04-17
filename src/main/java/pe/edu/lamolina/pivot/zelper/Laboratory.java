package pe.edu.lamolina.pivot.zelper;

import java.net.URI;
import java.net.URL;
import org.thymeleaf.util.StringUtils;

public class Laboratory {

    public static void main2(String[] args) {
        for (int i = 0; i < 280; i++) {
            String r = StringUtils.randomAlphanumeric(20);
            System.out.println((i + 1) + "\t" + r);

        }

    }
}
