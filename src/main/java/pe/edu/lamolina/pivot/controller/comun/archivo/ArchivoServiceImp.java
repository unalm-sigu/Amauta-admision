package pe.edu.lamolina.pivot.controller.comun.archivo;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Service
@Transactional(readOnly = true)
public class ArchivoServiceImp implements ArchivoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        logger.debug("getContentType {}", multipartFile.getContentType());
        logger.debug("getOriginalFilename {}", multipartFile.getOriginalFilename());
        logger.debug("getSize {}", multipartFile.getSize());

        String fileName = TypesUtil.getUnixTime() + "." + TypesUtil.getClean(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));

        FileHelper.createDirectory(GlobalConstantine.TMP_DIR);
        String absoluteName = GlobalConstantine.TMP_DIR + fileName;
        try {
            FileHelper.saveToDisk(multipartFile, absoluteName);
        } catch (Exception e) {
            logger.debug("ERROR AL GUARDAR EL ARCHIVO {}", absoluteName);
        }

        return fileName;
    }

    @Override
    public String uploadBase64(String imageString) {

        try {
            String[] imagenFromBase = imageString.split(",");
            String absoluteName;

            byte[] imagenBytes = DatatypeConverter.parseBase64Binary(imagenFromBase[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(imagenBytes);
            BufferedImage image;

            image = ImageIO.read(bis);

            bis.close();

            String fileName = TypesUtil.getUnixTime() + ".jpeg";
            absoluteName = GlobalConstantine.TMP_DIR + fileName;
            File nuevo = new File(absoluteName);
            ImageIO.write(image, "jpeg", nuevo);

            return fileName;

        } catch (IOException ex) {
            throw new PhobosException("Error al cargar imagen.");
        }

    }

    @Override
    public void downloadTemp(String fileName, HttpServletResponse response) {

        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            File file = new File(GlobalConstantine.TMP_DIR + fileName);

            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.reset();
            response.setBufferSize(Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            response.setHeader("Cache-Control", "max-age=604800");

            in = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(response.getOutputStream());

            IOUtils.copy(in, out);
            response.flushBuffer();

        } catch (IOException ex) {
            logger.error("(downloadTemporal)Error Descarga de Archivo: {}, fileName: {}", ex.getLocalizedMessage(), fileName);
        } finally {
            this.close(in);
            this.close(out);

        }

    }

    private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.error("Error al cerrar el Out/In: {}", e.getLocalizedMessage());
            }
        }
    }

}
