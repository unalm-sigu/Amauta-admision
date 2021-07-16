package pe.edu.lamolina.amauta.controller.fotoCarne;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.controller.comun.BuscarService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Controller
@RequestMapping("fotos/carne")
public class FotoCarneController {

    @Autowired
    FotoCarneService service;
    
    @Autowired
    BuscarService buscarService;

    @Autowired
    FotosCarneComponent fotosCarneComponent;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ModalidadEstudio> modalidades = buscarService.allModalidadEstudios();
        ArrayNode modalidadesJson = JaneHelper.from(modalidades).array();

        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("modalidades", modalidadesJson.toString());

        return "fotosCarne/fotosCarne";
    }

    @RequestMapping(value = "descargarFotos/{carrera}")
    public void descargarFotos(@PathVariable("carrera") String carrera, HttpSession session, HttpServletResponse response) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=fotos.zip");
        service.descargarFotos(ds, carrera, response);

    }

    @ResponseBody
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public ObjectNode info(HttpSession session) {
        return JaneHelper.from(fotosCarneComponent).json();
    }

}
