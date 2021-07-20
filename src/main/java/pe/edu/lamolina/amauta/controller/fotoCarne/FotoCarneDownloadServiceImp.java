package pe.edu.lamolina.amauta.controller.fotoCarne;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Service
@Transactional(readOnly = true)
public class FotoCarneDownloadServiceImp implements FotoCarneDownloadService {

    @Autowired
    FotosCarneDown fotosCarneComponent;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Async
    public void compilarInformacion(DataSessionPivot ds, String carrera) {
        logger.debug("inicia descarga foto {}", carrera);
        if (fotosCarneComponent == null) {
            logger.debug("componente no creado");
            fotosCarneComponent = new FotosCarneDown();
        }

        if (fotosCarneComponent.isIniciado()) {
            logger.debug("componente esta activo");
            return;
        }

        logger.debug("componente no iniciado");

        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allMatriculadosByCicloAndCarreraForFoto(ds.getCicloAcademico(), carrera);

        logger.debug("total de matriculas resumen {}", matriculaResumens.size());

        if (matriculaResumens.isEmpty()) {
            fotosCarneComponent.finalizarProceso();
            return;
        }

        fotosCarneComponent.iniciarProceso(matriculaResumens);

        String folder = GlobalConstantine.TMP_DIR + "path_demonium_foto_carnet/";

        File directoryWorkSpace = new File(folder);

        try {
            FileUtils.deleteDirectory(directoryWorkSpace);
        } catch (IOException ex) {
            logger.debug("no existe workspace");
        }

        directoryWorkSpace.mkdir();

        String hash = TypesUtil.toMD5(System.currentTimeMillis() + "");

        String fotosZipPath = GlobalConstantine.TMP_DIR + "fotos-carnet-" + hash + ".zip";

        File fotosZip = new File(fotosZipPath);

        if (fotosZip.exists()) {

            fotosZip.delete();

        }

        for (MatriculaResumen matriculaResumen : matriculaResumens) {

            String name = matriculaResumen.getAlumno().getCodigo() + ".jpg";

            File file = new File(folder + name);
            if (StringUtils.isBlank(matriculaResumen.getAlumno().getPersona().getFoto())) {

                fotosCarneComponent.getErrores()
                        .add(new MsjError("Error descargando foto : " + name));
                continue;
            }

            logger.debug("{}", matriculaResumen.getAlumno().getPersona().getFoto());

            try {

                URL url = new URL(matriculaResumen.getAlumno().getPersona().getFoto());
                FileUtils.copyURLToFile(url, file);

            } catch (MalformedURLException ex) {
                ex.printStackTrace();

                fotosCarneComponent.getErrores()
                        .add(new MsjError("Error descargando foto : " + name));

            } catch (IOException ex) {
                ex.printStackTrace();

                fotosCarneComponent.getErrores()
                        .add(new MsjError("Error descargando foto : " + name));

            }

            fotosCarneComponent.setAvance(fotosCarneComponent.getAvance() + 1);

        }

        comprimirArchivo(fotosZip, folder);

        fotosCarneComponent.setPathFile(fotosZipPath);

        fotosCarneComponent.finalizarProceso();

    }

    private void comprimirArchivo(File fotosZip, String rutaArchivos) {

        File carpetaComprimir = new File(rutaArchivos);

        try {

            if (carpetaComprimir.exists()) {

                ZipOutputStream zous = new ZipOutputStream(new FileOutputStream(fotosZip));
                File[] archivos = carpetaComprimir.listFiles();

                logger.error("Número de archivos encontrados: {}", archivos.length);

                for (int i = 0; i < archivos.length; i++) {
                    logger.debug("Nombre del fichero: " + archivos[i].getName());
                    try {

                        ZipEntry entrada = new ZipEntry(archivos[i].getName());
                        zous.putNextEntry(entrada);

                        logger.debug("Comprimiendo..... {} ", archivos[i].getName());
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
                logger.error("Directorio de salida: {}", rutaArchivos);
            }

        } catch (Exception ex) {

            logger.error("Error Descarga de Archivo: {}, fileName: {}", ex, rutaArchivos);
            fotosCarneComponent.getErrores()
                    .add(new MsjError("Error comprimiendo archivo: " + rutaArchivos));

        }

    }

}
