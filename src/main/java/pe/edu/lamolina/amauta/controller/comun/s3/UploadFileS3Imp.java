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
    StorageService swiftService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void uploadSync(String remoteDirectory, String localDirectory, String fileName, Boolean publico) {
        if (despliegueConfig.getStorage()) {
            swiftService.uploadFileSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, remoteDirectory, localDirectory, fileName, publico);
        } else {
            swiftService.uploadFileSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, localDirectory, fileName, publico);
        }
    }

    @Override
    public void deleteFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            swiftService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, fileName);
        } else {
            swiftService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (despliegueConfig.getStorage()) {
            swiftService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, fileName);
        } else {
            if (fileName.startsWith("trash")) {
                swiftService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, fileName);
            }
        }
    }

    @Override
    public InputStream getFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return swiftService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, fileName);
        } else {
            return swiftService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public InputStream getFile(String fileName) {
        if (despliegueConfig.getStorage()) {
            return swiftService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, fileName);
        } else {
            return swiftService.getFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public boolean doesExist(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return swiftService.doesExist(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, fileName);
        } else {
            return swiftService.doesExist(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public Inode allFile(String dirName, Boolean publico) {
        if (despliegueConfig.getStorage()) {
            return swiftService.allFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, dirName, publico);
        } else {
            return swiftService.allFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH, publico);
        }
    }

    @Override
    public void createDirectory(String folderName) {
        if (despliegueConfig.getStorage()) {
            swiftService.createDirectory(AcademicoConstantine.S3_BUCKET_ACADEMICO, folderName);
        } else {
            swiftService.createDirectory(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.S3_TRASH + folderName);
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
