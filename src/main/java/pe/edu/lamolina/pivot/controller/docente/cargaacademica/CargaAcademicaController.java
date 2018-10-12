package pe.edu.lamolina.pivot.controller.docente.cargaacademica;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("docente/cargaacademica")
public class CargaAcademicaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CargaAcademicaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "docente/cargaacademica";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        logger.debug("??????");
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<GrupoSeccion> gruposSeccion = service.allGpoSecciones(ds.getDocente(), ciclo);

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                ObjectNode node = JsonHelper.createJson(grupoSeccion, JsonNodeFactory.instance, true, new String[]{
                    "id", "estadoEnum", "estadoGrupoEnum",
                    "cicloAcademico.tipoEnum",
                    "curso.codigo",
                    "curso.nombre",
                    "curso.tpc",
                    "planCalificacion.id",
                    "secciones.tipoSeccionEnum",
                    "secciones.codigo2",
                    "secciones.matriculados",
                    "secciones.aula.codigo",
                    "secciones.aula.nombre",
                    "secciones.grupoHoras.codigo",
                    "secciones.docenteSeccion.*",
                    "secciones.verInformacion"
                });

                array.add(node);
            }

            json.setData(array);
            json.setTotal(gruposSeccion.size());
            json.setFiltered(gruposSeccion.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }
}
