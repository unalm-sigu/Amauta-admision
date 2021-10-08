package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.resumen;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;

@Controller
@RequestMapping("academico/encuestaestudiantil/resumen")
public class EncuestaResumenController {

    @Autowired
    EncuestaResumenService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        return "academico/encuestaestudiantil/resumen/resumen";
    }

    @ResponseBody
    @RequestMapping("resumen")
    public ObjectNode resumen(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        EncuestaEstudiantil encuesta = service.findEncuestaCursoWithResumen(ds.getCicloAcademico());

        ObjectNode node = JsonHelper.createJson(encuesta, JsonNodeFactory.instance, true, new String[]{
            "*",
            "configuraEncuesta.*",
            "periodosEncuesta.*"
        });

        return node;

    }

}
