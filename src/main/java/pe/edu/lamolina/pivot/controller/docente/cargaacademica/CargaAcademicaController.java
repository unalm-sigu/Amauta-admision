package pe.edu.lamolina.pivot.controller.docente.cargaacademica;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
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
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
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
    public JsonResponse list(HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode arrayPregrado = new ArrayNode(JsonNodeFactory.instance);
            ArrayNode arrayPosgrado = new ArrayNode(JsonNodeFactory.instance);
            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);

            CicloAcademico ciclo = ds.getCicloAcademico();
            List<GrupoSeccion> gruposSeccion = service.allGpoSecciones(ds.getDocente(), ciclo);

            BigDecimal creditosPregrado = BigDecimal.ZERO;
            BigDecimal creditosPosgrado = BigDecimal.ZERO;

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                ModalidadEstudio modalidad = grupoSeccion.getCurso().getModalidadEstudio();

                List<Seccion> secciones = grupoSeccion.getSecciones();
                for (Seccion seccion : secciones) {
                    List<DocenteSeccion> profesSeccion = seccion.getDocenteSeccion();
                    for (DocenteSeccion profeSecc : profesSeccion) {
                        if (profeSecc.getCreditosCarga() == null) {
                            continue;
                        }
                        if (modalidad.getCodigoEnum() == PRE) {
                            creditosPregrado = creditosPregrado.add(profeSecc.getCreditosCarga());
                        } else if (modalidad.getCodigoEnum() == EPG) {
                            creditosPosgrado = creditosPosgrado.add(profeSecc.getCreditosCarga());
                        }
                    }
                }

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
                    "secciones.docenteSeccion.id",
                    "secciones.docenteSeccion.estado",
                    "secciones.docenteSeccion.porcentajeCarga",
                    "secciones.docenteSeccion.fechaInicio",
                    "secciones.docenteSeccion.fechaFin",
                    "secciones.docenteSeccion.creditosCarga",
                    "secciones.verInformacion",
                    "secciones.horarioTexto"
                });

                if (modalidad.getCodigoEnum() == PRE) {
                    arrayPregrado.add(node);
                } else if (modalidad.getCodigoEnum() == EPG) {
                    arrayPosgrado.add(node);
                }
            }

            data.set("pregrado", arrayPregrado);
            data.set("posgrado", arrayPosgrado);
            data.put("creditosPregrado", creditosPregrado);
            data.put("creditosPosgrado", creditosPosgrado);

            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
