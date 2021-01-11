package pe.edu.lamolina.amauta.controller.comun.archivo;

import static com.helger.commons.io.stream.StreamHelper.close;
import java.beans.PropertyEditorSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.general.Archivo;

@Controller
@RequestMapping("comun/archivo")
public class ArchivoController {

    @Autowired
    ArchivoService service;
    @Autowired
    StorageService swiftService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @ResponseBody
    @RequestMapping("upload")
    public JsonResponse upload(@RequestParam("file") MultipartFile file) {
        JsonResponse json = new JsonResponse();

        try {
            Archivo archivo = service.upload(file);
            json.setData(JaneHelper.from(archivo).json());
            json.setMessage("Importación finalizada.");

            json.setSuccess(true);

        } catch (Exception e) {
            json.setSuccess(false);
            json.setMessage(GlobalMessages.ERROR_GENERAL);
        }
        return json;

    }

    @ResponseBody
    @RequestMapping("uploadFile")
    public JsonResponse uploadFile(@RequestParam("file") MultipartFile file) {
        JsonResponse json = new JsonResponse();

        try {

            String fileName = service.uploadFile(file);

            json.setMessage("Importación finalizada.");
            json.setData(fileName);
            json.setSuccess(true);

        } catch (Exception e) {
            json.setSuccess(false);
            json.setMessage(GlobalMessages.ERROR_GENERAL);
        }
        return json;

    }

    @ResponseBody
    @RequestMapping("uploadBase64")
    public JsonResponse uploadBase64(@RequestParam("file") String imageString) {
        JsonResponse json = new JsonResponse();

        try {

            String fileName = service.uploadBase64(imageString);

            json.setMessage("Importación finalizada.");
            json.setData(fileName);
            json.setSuccess(true);

        } catch (Exception e) {
            json.setSuccess(false);
            json.setMessage(GlobalMessages.ERROR_GENERAL);
        }
        return json;

    }

    @RequestMapping("downloadTemp/{file:.*}")
    public void downloadTempFile(@PathVariable String file, HttpServletResponse response) {
        service.downloadTemp(file, response);
    }

    @RequestMapping("verArchivoTemporal/{file:.*}")
    public void verArchivoTemporal(
            @PathVariable("file") String file,
            HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {

        response.reset();
        response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
        response.setHeader("Content-Disposition", "inline;filename=\"" + file + "\"");
        response.getOutputStream();
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            String rutaGuiaLocal = GlobalConstantine.TMP_DIR + file;
            InputStream fileStreamLocal;

            File fileGuia = new File(rutaGuiaLocal);
            if (fileGuia.exists() && !fileGuia.isDirectory()) {
                fileStreamLocal = new FileInputStream(fileGuia);

            } else {
                fileStreamLocal = new FileInputStream(fileGuia);

            }

            input = new BufferedInputStream(fileStreamLocal, GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            IOUtils.copy(input, output);
            response.flushBuffer();

        } finally {

            close(output);
            close(input);

        }

    }

}
