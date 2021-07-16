package pe.edu.lamolina.amauta.controller.fotoCarne;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
    FotosCarneComponent fotosCarneComponent;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void descargarFotos(DataSessionPivot ds, String carrera, HttpServletResponse response) {
        logger.debug("inicia descarga foto {}", carrera);
        if (fotosCarneComponent == null) {
            logger.debug("componente no creado");
            fotosCarneComponent = new FotosCarneComponent();
        }

        if (fotosCarneComponent.isIniciado()) {
            logger.debug("componente esta activo");
            return;
        }

        logger.debug("componente no iniciado");

        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allMatriculadosByCicloAndCarrera(ds.getCicloAcademico(), carrera);

        logger.debug("total de matriculas resumen {}", matriculaResumens.size());

        if (matriculaResumens.isEmpty()) {
            fotosCarneComponent.finalizarProceso();
            return;
        }

        fotosCarneComponent.iniciarProceso(matriculaResumens);

        String folder = GlobalConstantine.TMP_DIR + "path_demonium_foto_carnet/";

        File directoryWorkSpace = new File(folder);

        if (!directoryWorkSpace.exists()) {

            directoryWorkSpace.mkdir();

        } else {

            directoryWorkSpace.delete();
            directoryWorkSpace.mkdir();

        }

        for (MatriculaResumen matriculaResumen : matriculaResumens) {

            String name = matriculaResumen.getAlumno().getCodigo() + ".jpg";

            File file = new File(folder + name);
            if (StringUtils.isBlank(matriculaResumen.getAlumno().getPersona().getFoto())) {
                continue;
            }

            logger.debug("{}", matriculaResumen.getAlumno().getPersona().getFoto());

            try {

                URL url = new URL(matriculaResumen.getAlumno().getPersona().getFoto());
                FileUtils.copyURLToFile(url, file);

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            fotosCarneComponent.setAvance(fotosCarneComponent.getAvance() + 1);
            logger.debug("{}", fotosCarneComponent);
        }

        fotosCarneComponent.finalizarProceso();

        comprimirArchivo(response, folder);

    }

    private void comprimirArchivo(HttpServletResponse response, String rutaArchivos) {

        File carpetaComprimir = new File(rutaArchivos);

        try {

            if (carpetaComprimir.exists()) {
                
                ZipOutputStream zous = new ZipOutputStream(response.getOutputStream());
                File[] ficheros = carpetaComprimir.listFiles();
                logger.error("Número de ficheros encontrados: {}", ficheros.length);

                for (int i = 0; i < ficheros.length; i++) {
                    logger.debug("Nombre del fichero: " + ficheros[i].getName());
                    try {
                        ZipEntry entrada = new ZipEntry(ficheros[i].getName());
                        zous.putNextEntry(entrada);

                        logger.debug("Comprimiendo..... {} ", ficheros[i].getName());
                        FileInputStream file = new FileInputStream(rutaArchivos + entrada.getName());
                        int leer;
                        byte[] buffer = new byte[1024];
                        while (0 < (leer = file.read(buffer))) {
                            zous.write(buffer, 0, leer);
                        }

                        file.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                zous.closeEntry();
                zous.close();
                response.getOutputStream().flush();
                response.getOutputStream().close();
                logger.error("Directorio de salida: {}", rutaArchivos);
                
            }
            
        } catch (Exception ex) {
            logger.error("Error Descarga de Archivo: {}, fileName: {}", ex, rutaArchivos);
        } finally {
            logger.debug("Final correctamente, fileName: {}", rutaArchivos);
        }

    }

}
