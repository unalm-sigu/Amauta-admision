package pe.edu.lamolina.amauta.controller.fotoCarne;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.util.StreamUtils.BUFFER_SIZE;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.general.Persona;

@Service
@Transactional(readOnly = true)
public class FotoCarneUploadServiceImp implements FotoCarneUploadService {

    @Autowired
    FotosCarneUpload fotosCarneUploadComponent;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    UploadFileS3 uploadFileS3;

    @Autowired
    PersonaDAO personaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Async
    public void procesarFotos(DataSessionPivot ds, String rutaFotos) {

        logger.debug("inicia carga de  foto {}", rutaFotos);
        if (fotosCarneUploadComponent == null) {
            logger.debug("componente no creado");
            fotosCarneUploadComponent = new FotosCarneUpload();
        }

        if (fotosCarneUploadComponent.isIniciado()) {
            logger.debug("componente esta activo");
            return;
        }

        String destino = GlobalConstantine.TMP_DIR + "path_demonium_unzip_carnet/";

        File directoryWorkSpace = new File(destino);

        if (!directoryWorkSpace.exists()) {

            logger.debug("mkdir WorkSpace ");

            directoryWorkSpace.mkdir();

        } else {

            logger.debug("clean WorkSpace ");

            directoryWorkSpace.delete();
            directoryWorkSpace.mkdir();

        }

        File fotosZip = new File(rutaFotos);

        if (!fotosZip.exists()) {
            throw new PhobosException("No existe el archivo en el servidor");
        }

        logger.debug("componente no iniciado");
        fotosCarneUploadComponent.iniciarProceso();

        try {
            logger.debug("unzip folder ");
            this.unzip(rutaFotos, destino);
            logger.debug("end unzip folder ");
        } catch (IOException ex) {
            fotosCarneUploadComponent.getErrores().add(new MsjError("Error al descomprimir archivo de fotos"));
        }

        logger.debug("Iniciado FilenameFilter");

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
            }
        };

        logger.debug("find files");

        File[] files = directoryWorkSpace.listFiles(filter);
        if (!(files.length > 0)) {
            fotosCarneUploadComponent.getErrores().add(new MsjError("No se han encontrado archivos"));
            return;
        }

        logger.debug("init");

        fotosCarneUploadComponent.setTotal(files.length);

        logger.debug("fiels {}", files.length);

        for (File file : files) {

            String codigoAlumno = FilenameUtils.removeExtension(file.getName());
            logger.debug("codigo {}", codigoAlumno);

            Alumno alumno = alumnoDAO.findByCodigo(codigoAlumno);

            if (alumno == null) {

                fotosCarneUploadComponent.getErrores().add(new MsjError("Alumno no encontrado: " + codigoAlumno));
                continue;
            }

            String namaFileUpload = TypesUtil.toMD5(codigoAlumno) + System.currentTimeMillis() + ".jpg";

            File copied = new File(GlobalConstantine.TMP_DIR + namaFileUpload);

            try {

                FileUtils.copyFile(file, copied);
                logger.debug(" {} {} ", namaFileUpload, copied.exists());

            } catch (IOException ex) {
                fotosCarneUploadComponent.getErrores().add(new MsjError("Error al renombrar el archivo : " + codigoAlumno));
                continue;
            }

            this.uploadS3(namaFileUpload);

            String urlfoto = uploadFileS3.getPathFile(AcademicoConstantine.S3_DIR_FOTO_CARNET, namaFileUpload);

            Persona persona = alumno.getPersona();

            persona.setFoto(urlfoto);

            personaDAO.updateColumns(persona, "foto");

            fotosCarneUploadComponent.setAvance(fotosCarneUploadComponent.getAvance() + 1);

        }

        logger.debug("end process");

        fotosCarneUploadComponent.finalizarProceso();

    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private void uploadS3(String fileName) {
        File f = new File(GlobalConstantine.TMP_DIR + fileName);
        if (f.exists() && !f.isDirectory()) {
            uploadFileS3.uploadSync(AcademicoConstantine.S3_DIR_FOTO_CARNET, GlobalConstantine.TMP_DIR, fileName, true);
        }
    }

}
