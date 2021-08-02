package pe.edu.lamolina.amauta.controller.fotocarne;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        try {
            FileUtils.deleteDirectory(directoryWorkSpace);
        } catch (IOException ex) {
            logger.debug("no existe workspace");
        }

        directoryWorkSpace.mkdir();

        File fotosZip = new File(rutaFotos);

        if (!fotosZip.exists()) {
            throw new PhobosException("No existe el archivo en el servidor");
        }

        logger.debug("componente no iniciado");
        fotosCarneUploadComponent.iniciarProceso();

        try {
            logger.debug("unzip folder ");
            new ZipFile(rutaFotos).extractAll(destino);
            logger.debug("end unzip folder ");
        } catch (IOException ex) {
            fotosCarneUploadComponent.getErrores().add(new MsjError("Error al descomprimir archivo de fotos"));
        }

        logger.debug("Iniciado FilenameFilter");

        logger.debug("find files");

        List<File> allfiles = allFile(directoryWorkSpace.getPath());

        List<File> files = allfiles.stream()
                .filter(x -> !x.getPath().contains("__MACOSX"))
                .filter(x -> (x.getName().endsWith(".jpg") || x.getName().endsWith(".JPG") || x.getName().endsWith(".jpeg") || x.getName().endsWith(".JPEG")))
                .collect(Collectors.toList());

        logger.debug("files {}", files.size());

        if (files.size() <= 0) {
            fotosCarneUploadComponent.getErrores().add(new MsjError("No se han encontrado archivos"));
            fotosCarneUploadComponent.finalizarProceso();
            return;
        }

        logger.debug("init");

        fotosCarneUploadComponent.setTotal(files.size());

        logger.debug("files {}", files.size());

        for (File file : files) {

            logger.debug("file {}", file.getPath());

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

            try {

                this.uploadS3(namaFileUpload);
                logger.debug(" {} {} ", namaFileUpload, copied.exists());

            } catch (Exception ex) {
                fotosCarneUploadComponent.getErrores().add(new MsjError("Error al subir el archivo : " + codigoAlumno));
                continue;
            }

            String urlfoto = uploadFileS3.getPathFile(AcademicoConstantine.S3_DIR_FOTO_CARNET, namaFileUpload);

            Persona persona = alumno.getPersona();

            persona.setFoto(urlfoto);

            personaDAO.updateColumns(persona, "foto");

            fotosCarneUploadComponent.setAvance(fotosCarneUploadComponent.getAvance() + 1);

        }

        logger.debug("end process");

        fotosCarneUploadComponent.finalizarProceso();

    }

    private void uploadS3(String fileName) {
        File f = new File(GlobalConstantine.TMP_DIR + fileName);
        if (f.exists() && !f.isDirectory()) {
            uploadFileS3.uploadSync(AcademicoConstantine.S3_DIR_FOTO_CARNET, GlobalConstantine.TMP_DIR, fileName, true);
        }
    }

    private void allFiles(String rutaDir) throws IOException {

        Path start = Paths.get(rutaDir);

        try ( Stream<Path> stream = Files.walk(start, 1)) {

            List<String> collect = stream
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());

            collect.forEach(System.out::println);

        }

    }

    public List<File> allFile(String directoryName) {
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList<File>();
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                logger.debug("{}", file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(allFile(file.getAbsolutePath()));
            }
        }
        return resultList;
    }

}
