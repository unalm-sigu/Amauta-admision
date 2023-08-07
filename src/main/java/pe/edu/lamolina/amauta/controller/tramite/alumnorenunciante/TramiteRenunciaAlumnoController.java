package pe.edu.lamolina.amauta.controller.tramite.alumnorenunciante;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.TramiteRenunciaAlumno;

@Controller
@RequestMapping("academico/tramiteacademico/tramitealumnorenuncia")
public class TramiteRenunciaAlumnoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteRenunciaAlumnoService tramiteRenunciaService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

       return "academico/tramitescademicos/alumnorenunciante/tramitesAlumnoRenunciante";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);
        List<TramiteRenunciaAlumno> tramitesRenuncia = tramiteRenunciaService.allTramitesRenuciaByFilter(filter);

        ArrayNode array = JaneHelper.from(tramitesRenuncia)
                .join("resolucion")
                .join("usuarioAnulaTramite.persona", "apellidosNombres")
                .join("tramite")
                .join("tramite.alumno")
                .join("tramite.alumno.persona")
                .join("tramite.alumno.planCurricular")
                .join("tramite.alumno.carrera")
                .join("tramite.alumno.carrera.facultad")
                .join("tramite.cicloAcademico")
                .join("tramite.tipoTramite")
                .join("tramite.tipoTramite.oficina")
                .join("tramite.userRegistro")
                .join("tramite.userRegistro.persona")
                .join("tramite.userRespuesta")
                .join("tramite.formularioEstadoTramite")
                .join("tramite.estadoTramite", "id,nombre")
                .array();

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public String Renuncia(@RequestBody TramiteRenunciaAlumno tramiteRenunciaAlumno, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        tramiteRenunciaService.saveAlumnoRenuncia(tramiteRenunciaAlumno, ds);
        return GlobalMessages.CREATED;
    }

}
