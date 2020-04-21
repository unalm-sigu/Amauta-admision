package pe.edu.lamolina.amauta.controller.comun.s3;

import java.io.InputStream;
import pe.albatross.zelpers.file.model.Inode;

public interface UploadFileS3 {

    void uploadSync(String remoteDirectory, String localDirectory, String fileName, Boolean publico);

    void deleteFile(String dirName, String fileName);

    void deleteFile(String fileName);

    InputStream getFile(String dirName, String fileName);

    InputStream getFile(String fileName);

    boolean doesExist(String dirName, String fileName);

    Inode allFile(String dirName, Boolean publico);

    void createDirectory(String folderName);

    String getPathFile(String dirName, String fileName);

    void checkAndSendToS3(String fileName, String folderName);

}
