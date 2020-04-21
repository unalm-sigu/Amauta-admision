package pe.edu.lamolina.pivot.controller.comun.s3;

import java.io.File;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.model.Inode;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

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
            swiftService.uploadFileSync(Constantine.S3_BUCKET, remoteDirectory, localDirectory, fileName, publico);
        } else {
            swiftService.uploadFileSync(Constantine.S3_BUCKET, GlobalConstantine.S3_TRASH, localDirectory, fileName, publico);
        }
    }

    @Override
    public void deleteFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            swiftService.deleteFile(Constantine.S3_BUCKET, dirName, fileName);
        } else {
            swiftService.deleteFile(Constantine.S3_BUCKET, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (despliegueConfig.getStorage()) {
            swiftService.deleteFile(Constantine.S3_BUCKET, fileName);
        } else {
            if (fileName.startsWith("trash")) {
                swiftService.deleteFile(Constantine.S3_BUCKET, fileName);
            }
        }
    }

    @Override
    public InputStream getFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return swiftService.getFile(Constantine.S3_BUCKET, dirName, fileName);
        } else {
            return swiftService.getFile(Constantine.S3_BUCKET, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public InputStream getFile(String fileName) {
        if (despliegueConfig.getStorage()) {
            return swiftService.getFile(Constantine.S3_BUCKET, fileName);
        } else {
            return swiftService.getFile(Constantine.S3_BUCKET, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public boolean doesExist(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return swiftService.doesExist(Constantine.S3_BUCKET, dirName, fileName);
        } else {
            return swiftService.doesExist(Constantine.S3_BUCKET, GlobalConstantine.S3_TRASH, fileName);
        }
    }

    @Override
    public Inode allFile(String dirName, Boolean publico) {
        if (despliegueConfig.getStorage()) {
            return swiftService.allFile(Constantine.S3_BUCKET, dirName, publico);
        } else {
            return swiftService.allFile(Constantine.S3_BUCKET, GlobalConstantine.S3_TRASH, publico);
        }
    }

    @Override
    public void createDirectory(String folderName) {
        if (despliegueConfig.getStorage()) {
            swiftService.createDirectory(Constantine.S3_BUCKET, folderName);
        } else {
            swiftService.createDirectory(Constantine.S3_BUCKET, GlobalConstantine.S3_TRASH + folderName);
        }

    }

    @Override
    public String getPathFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return Constantine.S3_RUTA + dirName + fileName;
        }
        return Constantine.S3_RUTA + GlobalConstantine.S3_TRASH + fileName;
    }

    @Override
    public void checkAndSendToS3(String nombreArchivo, String folderName) {
        File file = new File(GlobalConstantine.TMP_DIR + nombreArchivo);
        logger.debug("el archivo {} existe {} ", (GlobalConstantine.TMP_DIR + nombreArchivo), (file.exists()));
        Assert.isTrue(file.exists(), "No existe el archivo en el servidor");
        uploadSync(folderName, GlobalConstantine.TMP_DIR, nombreArchivo, true);
    }

}
