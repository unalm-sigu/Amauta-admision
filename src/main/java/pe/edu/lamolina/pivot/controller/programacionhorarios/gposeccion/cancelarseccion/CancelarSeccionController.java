package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.cancelarseccion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class CancelarSeccionController {

    @Autowired
    CancelarSeccionService service;

    public List<MatriculaSeccion> matriculasSecciones(Seccion seccion) {
        return service.allMatriculaSeccionBySeccion(seccion);
    }

    @ResponseBody
    @RequestMapping("loadCancelarSeccionComp")
    public JsonResponse loadCancelarSeccionComp(@RequestParam("seccion") Long seccionId, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jc = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<MatriculaSeccion> matriculasSeccion = service.allMatriculaSeccionBySeccion(new Seccion(seccionId));
            ArrayNode jMatriculasSeccion = new ArrayNode(jc);
            for (MatriculaSeccion matriculaSeccion : matriculasSeccion) {
                ObjectNode jMatriculaSeccion = JsonHelper.createJson(matriculaSeccion, jc, true,
                        new String[]{
                            "estadoEnum",
                            "estado",
                            "matriculaResumen.alumno.id",
                            "matriculaResumen.alumno.codigo",
                            "matriculaResumen.alumno.estado",
                            "matriculaResumen.alumno.estadoEnum.*",
                            "matriculaResumen.alumno.promedioAcumulado",
                            "matriculaResumen.alumno.creditosCursados",
                            "matriculaResumen.alumno.creditosAprobados",
                            "matriculaResumen.alumno.persona.apellidosNombres",
                            "matriculaResumen.alumno.persona.tipoFoto",
                            "matriculaResumen.alumno.persona.rutaFoto",
                            "matriculaResumen.alumno.persona.numeroDocIdentidad",
                            "matriculaResumen.alumno.persona.tipoDocumento",
                            "matriculaResumen.alumno.persona.tipoDocumento.simbolo",
                            "matriculaResumen.alumno.persona.tipoFoto",
                            "matriculaResumen.alumno.persona.telefono",
                            "matriculaResumen.alumno.persona.celular",
                            "matriculaResumen.alumno.persona.email",
                            "matriculaResumen.alumno.persona.emailCompania",
                            "matriculaResumen.alumno.cicloActivo.descripcion",
                            "matriculaResumen.alumno.cicloIngreso.descripcion",
                            "matriculaResumen.alumno.cicloIngreso.estadoEnum",
                            "matriculaResumen.alumno.carrera.nombre",
                            "matriculaResumen.alumno.carrera.codigo",
                            "matriculaResumen.alumno.carrera.tipoEnum",
                            "matriculaResumen.alumno.carrera.tipo",
                            "matriculaResumen.alumno.carrera.facultad.codigo",
                            "matriculaResumen.alumno.carrera.facultad.nombre",
                            "matriculaResumen.alumno.modalidadEstudio.codigo",
                            "matriculaResumen.alumno.situacionAcademica.codigo",
                            "matriculaResumen.alumno.situacionAcademica.nombre"
                        });

                jMatriculasSeccion.add(jMatriculaSeccion);
            }
            response.setData(jMatriculasSeccion);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

}
