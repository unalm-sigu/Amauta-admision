package pe.edu.lamolina.pivot.controller.comun.archivo;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.aws.S3Service;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Controller
@RequestMapping("comun/archivo")
public class ArchivoController {

    @Autowired
    ArchivoService service;
    @Autowired
    S3Service s3Service;

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
            json.setMessage(Constantine.APP_ERROR_MESSAGE);
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
            json.setMessage(Constantine.APP_ERROR_MESSAGE);
        }
        return json;

    }

    @RequestMapping("downloadTemp/{file:.*}")
    public void downloadTempFile(@PathVariable String file, HttpServletResponse response) {
        service.downloadTemp(file, response);
    }

}
