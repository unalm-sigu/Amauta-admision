package pe.edu.lamolina.amauta.controller.fotoCarne;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.comun.BuscarService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Controller
@RequestMapping("fotos/carne")
public class FotoCarneController {

    @Autowired
    FotoCarneDownloadService service;

    @Autowired
    FotoCarneUploadService fotoCarneUploadService;

    @Autowired
    BuscarService buscarService;

    @Autowired
    FotosCarneDown fotosCarneDownComponent;

    @Autowired
    FotosCarneUpload fotosCarneUploadComponent;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ModalidadEstudio> modalidades = buscarService.allModalidadEstudios();
        ArrayNode modalidadesJson = JaneHelper.from(modalidades).array();

        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("modalidades", modalidadesJson.toString());

        return "fotoscarne/fotosCarne";
    }

    @ResponseBody
    @RequestMapping(value = "compilarInformacion/{carrera}")
    public JsonResponse compilarInformacion(@PathVariable("carrera") String carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.compilarInformacion(ds, carrera);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping(value = "descargarFotos", method = RequestMethod.GET)
    public void descargarFotos(HttpServletResponse response) throws IOException {

        if (fotosCarneDownComponent == null) {
            throw new PhobosException("No se ha iniciado ninguna descarga");
        }

        if (StringUtils.isBlank(fotosCarneDownComponent.getPathFile())) {
            throw new PhobosException("No existe la ruta del archivo");
        }

        logger.debug("{}", fotosCarneDownComponent.getPathFile());

        File filex = new File(fotosCarneDownComponent.getPathFile());

        if (!filex.exists()) {
            throw new PhobosException("No existe el archivo");
        }

        if (!filex.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        DateTime hoy = new DateTime();

        response.reset();
        response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=fotos.zip");

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new FileInputStream(filex), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            IOUtils.copy(input, output);
            response.flushBuffer();
        } finally {
            close(output);
            close(input);
        }

    }

    @ResponseBody
    @RequestMapping(value = "infoDown", method = RequestMethod.GET)
    public ObjectNode infoDown(HttpSession session) {
        return JaneHelper.from(fotosCarneDownComponent).join("errores").json();
    }

    @ResponseBody
    @RequestMapping(value = "infoUp", method = RequestMethod.GET)
    public ObjectNode infoUp(HttpSession session) {
        return JaneHelper.from(fotosCarneUploadComponent).join("errores").json();
    }

    @ResponseBody
    @RequestMapping("procesarFotos")
    public JsonResponse procesarFotos(@RequestParam("rutaFotos") String rutaFotos, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            fotoCarneUploadService.procesarFotos(ds, rutaFotos);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

}
