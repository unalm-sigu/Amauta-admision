package pe.edu.lamolina.amauta.controller.comun.archivo;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ArchivoService {

    String uploadFile(MultipartFile file);

    String uploadBase64(String imageString);

    void downloadTemp(String file, HttpServletResponse response);

}
