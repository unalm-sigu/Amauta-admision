package pe.edu.lamolina.pivot.zelper.aws;

import java.io.InputStream;
import pe.albatross.zelpers.file.model.Inode;

public interface S3Service {

    void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void deleteFile(String buket, String path);

    void deleteFile(String buket, String directory, String fileName);

    InputStream getFile(String bucket, String path);

    InputStream getFile(String bucket, String directory, String fileName);

    boolean doesExist(String bucket, String path);

    boolean doesExist(String bucket, String directory, String fileName);

    Inode allFile(String bucket, String directory, boolean recursive);

    boolean createDirectory(String bucket, String directory);
}
