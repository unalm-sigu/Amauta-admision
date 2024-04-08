package pe.edu.lamolina.amauta.controller.comun.s3;

import java.io.File;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.model.Inode;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Service
public class UploadFileS3Imp implements UploadFileS3 {

    @Autowired
    DespliegueConfig despliegueConfig;
    @Autowired
    StorageService minioService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void uploadSync(String remoteDirectory, String localDirectory, String fileName, Boolean publico) {
        if (despliegueConfig.getStorage()) {
            minioService.uploadFileSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, remoteDirectory, localDirectory, fileName, publico);
        } else {
            minioService.uploadFileSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, localDirectory, fileName, publico);
        }
    }

    @Override
    public void deleteFile(String dirName, String fileName) {
//        if (despliegueConfig.getStorage()) {
//            minioService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, fileName);
//        } else {
//            minioService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
//        }
    }

    @Override
    public void deleteFile(String fileName) {
//        if (despliegueConfig.getStorage()) {
//            minioService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, fileName);
//        } else {
//            if (fileName.startsWith("trash")) {
//                minioService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, fileName);
//            }
//        }
    }

    @Override
    public InputStream getFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return minioService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, fileName);
        } else {
            return minioService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public InputStream getFile(String fileName) {
        if (despliegueConfig.getStorage()) {
            return minioService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, fileName);
        } else {
            return minioService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public boolean doesExist(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return minioService.doesExist(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, fileName);
        } else {
            return minioService.doesExist(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public Inode allFile(String dirName, Boolean publico) {
        if (despliegueConfig.getStorage()) {
            return minioService.allFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, publico);
        } else {
            return minioService.allFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, publico);
        }
    }

    @Override
    public void createDirectory(String folderName) {
        if (despliegueConfig.getStorage()) {
            minioService.createDirectory(AcademicoConstantine.S3_BUCKET_ACADEMICO, folderName);
        } else {
            minioService.createDirectory(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH + folderName);
        }

    }

    @Override
    public String getPathFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return AcademicoConstantine.S3_URL_ACADEMICO + dirName + fileName;
        }
        return AcademicoConstantine.S3_URL_ACADEMICO + GlobalConstantine.S3_TRASH + fileName;
    }

    @Override
    public void checkAndSendToS3(String nombreArchivo, String folderName) {
        File file = new File(GlobalConstantine.TMP_DIR + nombreArchivo);
        logger.debug("el archivo {} existe {} ", (GlobalConstantine.TMP_DIR + nombreArchivo), (file.exists()));
        Assert.isTrue(file.exists(), "No existe el archivo en el servidor");
        uploadSync(folderName, GlobalConstantine.TMP_DIR, nombreArchivo, true);
    }

}
