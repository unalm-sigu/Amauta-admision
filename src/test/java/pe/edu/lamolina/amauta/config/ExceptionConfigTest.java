package pe.edu.lamolina.amauta.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import pe.albatross.zelpers.miscelanea.Assert;

@Slf4j
public class ExceptionConfigTest {

    @Test
    public void test_cantidad_lineas_exceptions() throws Exception {
        try {
            Assert.isTrue(false, "error fatalmente errático");
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String[] lines = sw.toString().split("\n");
            for (int i = 0; i < lines.length; i++) {
                String linea = lines[i];
                if (i == 0) {
                    log.error(linea);
                } else {
                    if (linea.contains("pe.edu.lamolina.amauta")) {
                        log.error(linea);
                    } else if (linea.contains("pe.albatross")) {
                        log.error(linea);
                    }
                }
            }
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");

        try {
            Assert.isTrue(false, "error fatalmente constante");
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String[] lines = sw.toString().split("\n");
            for (int i = 0; i < lines.length; i++) {
                String linea = lines[i];
                log.error(linea);
            }
        }
    }

}
