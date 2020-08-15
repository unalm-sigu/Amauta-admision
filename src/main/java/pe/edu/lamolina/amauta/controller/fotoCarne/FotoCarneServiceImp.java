package pe.edu.lamolina.amauta.controller.fotoCarne;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaResumen;

@Service
@Transactional(readOnly = true)
public class FotoCarneServiceImp implements FotoCarneService {

    @Autowired
    FotosCarneComponent component;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Async
    public void descargarFotos(DataSessionPivot ds) {
        List<MatriculaResumen> matriculaResumens = component.getMatriculaResumens();
        try {
            System.out.println("\ndownload: \n");
            logger.debug("Entre");
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        logger.debug("Entre 1");
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                        logger.debug("Entre 2");
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                        logger.debug("Entre 3");
                    }
                }
            };

// Activate the new trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                logger.debug("error 1");
            }

            String folder = "C:/tmp/";

            File dir = new File(folder);

            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    return;
                }
            }
            InputStream in = null;
            OutputStream out = null;
            for (MatriculaResumen matriculaResumen : matriculaResumens) {
                String name = matriculaResumen.getAlumno().getCodigo() + ".jpg";
                logger.debug("Nombre {}", name);
                File file = new File(folder + name);
// And as before now you can use URL and URLConnection
                if (matriculaResumen.getAlumno().getPersona().getRutaFoto() == null) {
                    continue;
                }
                URL url = null;
                try {

                    url = new URL(matriculaResumen.getAlumno().getPersona().getRutaFoto());
                } catch (Exception e) {
                    logger.debug("error 2");
                    continue;
                }
                URLConnection connection = url.openConnection();
                connection.connect();

                System.out.println("\ndownload: \n");
                System.out.println(">> URL: " + url);
                System.out.println(">> Name: " + name);
                System.out.println(">> size: " + connection.getContentLength()
                        + " bytes");

                in = connection.getInputStream();
                out = new FileOutputStream(file);

                int b = 0;

                while (b != -1) {
                    b = in.read();

                    if (b != -1) {
                        out.write(b);
                    }
                }
                component.setAvance(component.getAvance() + 1);
            }
            component.setEstado("INA");
            out.close();
            in.close();
        } catch (MalformedURLException ex) {
            logger.debug("error 3 {}", ex.toString());
        } catch (IOException ex) {
            logger.debug("error 3 {}", ex.toString());
        }
    }

    @Override
    public FotosCarneComponent info(DataSessionPivot ds) {

        return component;
    }

    @Override
    public FotosCarneComponent activar(DataSessionPivot ds) {
        if (component.getEstado().equals("INA")) {
            List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allMatriculadosByCiclo(ds.getCicloAcademico());
            component.setCantidadTotal(matriculaResumens.size());
            component.setMatriculaResumens(matriculaResumens);
            component.setAvance(0);
            component.setEstado("ACT");
            return component;
        }
        return component;
    }

}
