package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("docente/ampliacionvacante")
public class AmpliacionVacanteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AmpliacionVacanteService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());

        return "docente/ampliacionvacante/ampliacionvacante";

    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            JsonNodeFactory factory = JsonNodeFactory.instance;

            ArrayNode array = new ArrayNode(factory);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Docente docente = ds.getDocente();

            List<GrupoSeccion> gruposSeccion = service.allGrupoByDocente(docente, ciclo, ds);

            for (GrupoSeccion grupoSeccion : gruposSeccion) {

                ObjectNode nodeGpoSecc = JsonHelper.createJson(grupoSeccion, factory, true, new String[]{
                    "id", "estado", "estadoEnum", "codigo2", "cursoDirigido",
                    "tipoDictado", "fechaInicioModular", "fechaFinModular",
                    "curso.id",
                    "curso.codigo",
                    "curso.nombre",
                    "curso.tpc",
                    "curso.precioFormato",
                    "curso.precioTpcFormato",
                    "curso.tipoCursoEnum",
                    "curso.departamentoAcademico.codigo",
                    "curso.departamentoAcademico.nombre",
                    "docenteResponsable.id",
                    "docenteResponsable.persona.nombreCompleto",
                    "curso.tipoCursoTEO",
                    "curso.tipoCursoPRA",
                    "curso.tipoCursoTEOPRA",
                    "anexoBoletin.codigo",
                    "anexoBoletin.nombre",
                    "anexoBoletin.anexoSuperior.codigo",
                    "anexoBoletin.anexoSuperior.nombre",
                    "secciones.id",
                    "secciones.codigo",
                    "secciones.codigo2",
                    "secciones.vacantes",
                    "secciones.matriculados",
                    "secciones.tipoSeccion",
                    "secciones.ampliacionVacante",
                    "secciones.solicitudesMatricula",
                    "secciones.grupoHoras.id",
                    "secciones.grupoHoras.codigo",
                    "secciones.aula.id",
                    "secciones.aula.codigo",
                    "secciones.aula.aforo",
                    "secciones.aula.capacidadAula",
                    "secciones.seccionSuperior.id",
                    "secciones.seccionSuperior.codigo",
                    "secciones.seccionSuperior.codigo2",
                    "secciones.seccionSuperior.tipoSeccion",
                    "secciones.seccionSuperior.ampliacionVacante",
                    "secciones.seccionSuperior.aula.id",
                    "secciones.seccionSuperior.aula.codigo",
                    "secciones.seccionSuperior.aula.aforo",
                    "secciones.seccionSuperior.aula.capacidadAula",
                    "secciones.seccionSuperior.grupoHoras.id",
                    "secciones.seccionSuperior.grupoHoras.codigo",
                    "secciones.docentePrincipal.codigo",
                    "secciones.docentePrincipal.persona.nombreCompleto",});

                array.add(nodeGpoSecc);
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

    @ResponseBody
    @RequestMapping("allAlumno")
    public JsonResponse allAlumno(@RequestParam("nombre") String nombre,
            Seccion seccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode jsonList = new ArrayNode(jFactory);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<Alumno> alumnos = service.allAlumnoByName(nombre, cicloAcademico, seccion);

            for (Alumno alumno : alumnos) {
                ObjectNode json = JsonHelper.createJson(alumno, jFactory, true,
                        new String[]{
                            "id",
                            "codigo",
                            "situacion",
                            "motivoMatriculable",
                            "persona.nombreCompleto",
                            "persona.rutaFotoDocumento",
                            "persona.rutaFotoPostulante",
                            "carrera.nombre",
                            "carrera.facultad.nombre",});
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("matricular")
    public JsonResponse matricular(@RequestBody AmpliacionVacanteForm ampliacionVacanteForm, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            service.matricular(ampliacionVacanteForm, cicloAcademico, ds);
            response.setMessage(Messages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
