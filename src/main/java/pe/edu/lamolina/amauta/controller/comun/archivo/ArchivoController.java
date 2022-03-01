package pe.edu.lamolina.amauta.controller.comun.archivo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.general.Archivo;

@Controller
@RequestMapping("comun/archivo")
public class ArchivoController {

    @Autowired
    ArchivoService service;

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

}
