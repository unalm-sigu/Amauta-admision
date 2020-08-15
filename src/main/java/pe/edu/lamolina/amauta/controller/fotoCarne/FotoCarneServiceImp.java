package pe.edu.lamolina.amauta.controller.fotoCarne;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.System.in;
import static java.lang.System.out;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Service
@Transactional(readOnly = true)
public class FotoCarneServiceImp implements FotoCarneService {

    @Autowired
    FotosCarneComponent component;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void descargarFotos(DataSessionPivot ds, HttpServletResponse response) {
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

            String folder = GlobalConstantine.TMP_DIR + "fotosCarne";

            File dir = new File(folder);

            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    return;
                }
            }
            InputStream in = null;
            OutputStream out = null;
            logger.debug("bucle {} ", matriculaResumens.size());
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
                if (component.getAvance() == 50) {
                    break;
                }
            }
            component.setEstado("INA");
            out.close();
            in.close();
            comprimirArchivo(response, folder);
        } catch (MalformedURLException ex) {
            logger.debug("error 3 {}", ex.toString());
        } catch (IOException ex) {
            logger.debug("error 3 {}", ex.toString());
        }
    }

    private void comprimirArchivo(HttpServletResponse response, String rutaArchivos) {
        File carpetaComprimir = new File(rutaArchivos);
        FileInputStream fis = null;
        BufferedOutputStream out = null;
        try {
            if (carpetaComprimir.exists()) {
// lista los archivos que hay dentro del directorio
                File[] ficheros = carpetaComprimir.listFiles();
                System.out.println("Número de ficheros encontrados: " + ficheros.length);

                // ciclo para recorrer todos los archivos a comprimir
                for (int i = 0; i < ficheros.length; i++) {
                    System.out.println("Nombre del fichero: " + ficheros[i].getName());
                    String extension = "";
                    for (int j = 0; j < ficheros[i].getName().length(); j++) {
                        //obtiene la extensión del archivo
                        if (ficheros[i].getName().charAt(j) == '.') {
                            extension = ficheros[i].getName().substring(j, (int) ficheros[i].getName().length());
                            //System.out.println(extension);
                        }
                    }

                    // crea un buffer temporal para ir poniendo los archivos a comprimir
                    ZipOutputStream zous = new ZipOutputStream(new FileOutputStream(rutaArchivos + ficheros[i].getName().replace(extension, ".zip")));

                    //nombre con el que se va guardar el archivo dentro del zip
                    ZipEntry entrada = new ZipEntry(ficheros[i].getName());
                    zous.putNextEntry(entrada);

                    //System.out.println("Nombre del Archivo: " + entrada.getName());
                    logger.debug("Comprimiendo.....");
                    //obtiene el archivo para irlo comprimiendo
                    int leer;
                    byte[] buffer = new byte[1024];
                    while (0 < (leer = fis.read(buffer))) {
                        zous.write(buffer, 0, leer);
                    }

                    response.reset();
                    response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "inline; filename=\"" + "fotos.zip" + "\"");
                    response.setHeader("Cache-Control", "max-age=604800");

                    fis = new FileInputStream(rutaArchivos + entrada.getName());
                    out = new BufferedOutputStream(response.getOutputStream());

                    fis.close();

                    IOUtils.copy(fis, out);
                    response.flushBuffer();
                    zous.closeEntry();
                    zous.close();

                }
                System.out.println("Directorio de salida: " + rutaArchivos);
            }
        } catch (Exception ex) {
            logger.error("(downloadTemporal)Error Descarga de Archivo: {}, fileName: {}", ex.getLocalizedMessage(), rutaArchivos);
        } finally {
            this.close(fis);
            this.close(out);

        }
        // valida si existe el directorio

    }

    private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.error("Error al cerrar el Out/In: {}", e.getLocalizedMessage());
            }
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
