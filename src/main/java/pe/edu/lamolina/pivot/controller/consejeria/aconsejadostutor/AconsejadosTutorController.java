package pe.edu.lamolina.pivot.controller.consejeria.aconsejadostutor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("consejeria/aconsejadostutor")
public class AconsejadosTutorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AconsejadosTutorService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("persona", ds.getPersona());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());

        return "consejeria/aconsejadostutor/aconsejadosTutor";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<AlumnoConsejero> alumnosTutor = service.allByDynatable(filter, ds.getCicloAcademico(), ds.getPersona());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoConsejero alumnoTutor : alumnosTutor) {
                ObjectNode node = JsonHelper.createJson(alumnoTutor, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "alumno.id",
                            "alumno.codigo",
                            "alumno.creditosCursados",
                            "alumno.creditosAprobados",
                            "alumno.promedioAcumulado",
                            "alumno.cicloIngreso.descripcion",
                            "alumno.situacionAcademica.codigo",
                            "alumno.situacionAcademica.nombre",
                            "alumno.persona.emailCompania",
                            "alumno.persona.tipoFoto",
                            "alumno.persona.rutaFoto",
                            "alumno.persona.apellidosNombres",
                            "alumno.persona.numeroDocIdentidad",
                            "alumno.persona.tipoDocumento.simbolo",
                            "alumno.carrera.nombre",
                            "alumno.carrera.facultad.nombre",
                            "consejero.*",
                            "consejero.colaborador.persona.emailCompania",
                            "consejero.colaborador.persona.numeroDocIdentidad",
                            "consejero.colaborador.persona.apellidosNombres",
                            "consejero.colaborador.persona.tipoDocumento.simbolo"
                        });

                array.add(node);
            }
            json.setFiltered(filter.getFiltered());
            json.setData(array);
            json.setTotal(filter.getTotal());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("countData")
    public JsonResponse countData(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {

            AconsejadoEstadoBean aconsejadoEstadoBean = service.allByPersona(ds.getPersona(), ds.getCicloAcademico());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            json.setData(JsonHelper.createJson(aconsejadoEstadoBean, JsonNodeFactory.instance, new String[]{"*"}));
            json.setMessage("Búsqueda Exitosa");

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("matriculaAutorizacion")
    public JsonResponse matriculaAutorizacion(@RequestBody MatriculaResumen matriculaResumen, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {
            service.matriculaAutorizacion(matriculaResumen, ds);
            json.setMessage("La autorización de matricula fue modificada satisfactoriamente");
            json.setSuccess(true);

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

}
