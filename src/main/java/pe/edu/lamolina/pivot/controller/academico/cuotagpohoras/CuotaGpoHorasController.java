package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import javax.servlet.http.HttpServletRequest;
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
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/cuotagpohoras")
public class CuotaGpoHorasController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotaGpoHorasService cuotaGpoHorasService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/cuotagpohoras/cuotagpohoras";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
/*
        try {
            List<Alumno> alumnos = cuotasAlumnoService.allAlumnosPosgrado(filter, ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Alumno alumn : alumnos) {
                ObjectNode node = JsonHelper.createJson(alumn, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "codigo", "estado", "estadoEnum",
                            "promedioAcumulado", "creditosCursados", "creditosAprobados",
                            "persona.apellidosNombres",
                            "persona.rutaFoto",
                            "persona.tipoFoto",
                            "persona.tipoDocumento.simbolo",
                            "persona.numeroDocIdentidad",
                            "persona.telefono",
                            "persona.celular",
                            "persona.email",
                            "persona.emailCompania",
                            "carrera.nombre",
                            "carrera.codigo",
                            "carrera.tipoEnum",
                            "carrera.tipo",
                            "carrera.facultad.codigo",
                            "carrera.facultad.nombre",
                            "modalidadEstudio.codigo",
                            "situacionAcademica.codigo",
                            "situacionAcademica.nombre",
                            "modalidadEstudio.nombre",
                            "cicloIngreso.descripcion",
                            "cicloActivo.descripcion"
                        });

                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }*/
        return json;
    }



}
