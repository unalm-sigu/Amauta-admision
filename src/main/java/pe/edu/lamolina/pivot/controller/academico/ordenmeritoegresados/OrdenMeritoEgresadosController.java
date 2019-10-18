package pe.edu.lamolina.pivot.controller.academico.ordenmeritoegresados;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/ordenmeritoegresados")
public class OrdenMeritoEgresadosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OrdenMeritoEgresadosService service;
    @Autowired
    ReportePdfOrdenMeritoEgresadoCiclo reportePdfOrdenMeritoEgresadoCiclo;
    @Autowired
    ReportePdfOrdenMeritoEgresadoEspecialidad reportePdfOrdenMeritoEgresadoEspecialidad;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        CicloAcademico ciclo = findCicloAcademico(ds, session);
        CicloAcademico cicloActivo = service.findCicloActivo();
        List<CicloAcademico> ciclos = service.allCicloAcademicoForSelect();

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("esCicloActivo", cicloActivo.getId().equals(ciclo.getId()));

        return "academico/ordenmeritoegresados/ordenmeritoegresados";
    }

    @RequestMapping("{id}/control")
    public String infoAcademico(
            @PathVariable Long id,
            Model model, HttpSession session) {

        ControlMeritoEgresado control = service.find(id);
        ObjectNode json = JsonHelper.createJson(control, JsonNodeFactory.instance, new String[]{
            "id",
            "cicloAcademico.descripcion",
            "cicloAcademico.descripcion2",
            "carrera.nombre",
            "facultad.nombre",
            "escalaEnum"
        });
        model.addAttribute("control", control);
        model.addAttribute("controlJson", json);
        return "academico/ordenmeritoegresados/ordenmeritoegresadosControl";
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<ControlMeritoEgresado> list = service.allByDynatable(filter, findCicloAcademico(ds, session));
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ControlMeritoEgresado item : list) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "id",
                    "escalaEnum",
                    "estadoEnum",
                    "facultad.nombre",
                    "facultad.codigo",
                    "carrera.nombre",
                    "carrera.codigo",
                    "totalAlumnos",}));
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "{id}/control/alumnos", method = RequestMethod.GET)
    public DynatableResponse alumnos(DynatableFilter filter, HttpSession session, @PathVariable Long id) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Egresado> list = service.allAlumnoCicloByControl(filter, new ControlMeritoEgresado(id));
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Egresado item : list) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "alumno.persona.nombreCompleto",
                    "alumno.codigo",
                    "carrera.nombre",
                    "carrera.codigo",
                    "carrera.facultad.nombre",
                    "carrera.facultad.codigo",
                    //--  --//
                    "ordenMeritoCarrera",
                    "ordenMeritoCiclo",
                    "ordenMeritoFacultad",
                    //--  --//
                    "cuadroHonorCarrera",
                    "cuadroHonorCiclo",
                    "cuadroHonorFacultad",
                    //--  --//
                    "quintoSuperiorCarrera",
                    "quintoSuperiorCiclo",
                    "quintoSuperiorFacultad",
                    //--  --//
                    "tercioSuperiorCarrera",
                    "tercioSuperiorCiclo",
                    "tercioSuperiorFacultad",
                    //--  --//
                    "promedioAcumulado"
                }));
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/generardatos", method = RequestMethod.POST)
    public JsonResponse generarDatos(@RequestBody CicloAcademico cicloAcademico, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.generarDatos(findCicloAcademico(ds, session), ds);
            response.setMessage("Datos generados");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/calcularmeritos", method = RequestMethod.POST)
    public JsonResponse calcularMeritos(@RequestBody CicloAcademico cicloAcademico, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.calcularMeritos(findCicloAcademico(ds, session), ds);
            response.setMessage("Orden de mérito generado");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("changeciclo")
    public JsonResponse changeCiclo(@RequestParam("ciclo") Long idCiclo, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            CicloAcademico ciclo = service.findCicloAcademico(new CicloAcademico(idCiclo));
            session.setAttribute(Constantine.CICLO_ORDEN_MERITO, ciclo);
            json.setSuccess(true);
            json.setMessage("Se cambio el ciclo académico satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("allAlumnoLikeNombres")
    public JsonResponse allAlumnoLikeNombres(@RequestParam("parametro") String parametro, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            ArrayNode jsonList = new ArrayNode(JsonNodeFactory.instance);
            List<Alumno> listAlumno = service.allAlumnoLikeNombres(parametro);
            logger.debug("cantidad listAlumno {}", listAlumno.size());
            for (Alumno alumno : listAlumno) {
                jsonList.add(JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{
                    "id", "codigo", "persona.id",
                    "persona.nombreCompleto", "persona.nombres", "persona.paterno", "persona.materno",
                    "carrera.id", "carrera.codigo", "carrera.nombre",
                    "carrera.facultad.id", "carrera.facultad.nombre",
                    "cicloIngreso.id", "cicloIngreso.descripcion", "cicloIngreso.codigo"
                }));
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
    @RequestMapping("saveEgresado")
    public JsonResponse saveEgresado(@RequestBody Egresado egresado, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.saveEgresado(egresado, ds.getUsuario());
            response.setMessage("El alumno fue registrado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("reportePdfOrdenMeritoCiclo")
    public ModelAndView reportePdfOrdenMeritoCiclo(@RequestParam("cicloId") Long cicloId, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            CicloAcademico cicloAcademico = service.findCicloAcademico(new CicloAcademico(cicloId));

            List<Egresado> egresados = service.getEgresadosForPdf(cicloAcademico);
            List<Facultad> facultades = service.allFacultadesForReporte();
            model.addAttribute("cicloAcademico", cicloAcademico);
            model.addAttribute("egresados", egresados);
            model.addAttribute("facultades", facultades);
            model.addAttribute("tipoReporte", "ciclo");
        } catch (PhobosException e) {
            e.printStackTrace();
            logger.debug("*** PhobosException {}", e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("*** Exception {}", e);
        }
        return new ModelAndView(reportePdfOrdenMeritoEgresadoCiclo);
    }

    @RequestMapping("reportePdfOrdenMeritoFacultad")
    public ModelAndView reportePdfOrdenMeritoFacultad(@RequestParam("cicloId") Long cicloId, Model model, HttpSession session) {
        try {
            CicloAcademico cicloAcademico = service.findCicloAcademico(new CicloAcademico(cicloId));
            List<Egresado> egresados = service.getEgresadosForPdf(cicloAcademico);
            List<Facultad> facultades = service.allFacultadesForReporte();
            model.addAttribute("cicloAcademico", cicloAcademico);
            model.addAttribute("egresados", egresados);
            model.addAttribute("facultades", facultades);
            model.addAttribute("tipoReporte", "facultad");
        } catch (PhobosException e) {
            e.printStackTrace();
            logger.debug("*** PhobosException {}", e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("*** Exception {}", e);
        }
        return new ModelAndView(reportePdfOrdenMeritoEgresadoCiclo);
    }

    @RequestMapping("reportePdfOrdenMeritoEspecialidad")
    public ModelAndView reportePdfOrdenMeritoEspecialidad(@RequestParam("cicloId") Long cicloId, Model model, HttpSession session) {
        try {
            CicloAcademico cicloAcademico = service.findCicloAcademico(new CicloAcademico(cicloId));
            List<Facultad> facultades = service.allFacultadesForReporte();
            List<Egresado> egresados = service.getEgresadosForPdf(cicloAcademico);
            model.addAttribute("cicloAcademico", cicloAcademico);
            model.addAttribute("egresados", egresados);
            model.addAttribute("facultades", facultades);
        } catch (PhobosException e) {
            e.printStackTrace();
            logger.debug("*** PhobosException {}", e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("*** Exception {}", e);
        }
        return new ModelAndView(reportePdfOrdenMeritoEgresadoEspecialidad);
    }

    private CicloAcademico findCicloAcademico(DataSessionPivot ds, HttpSession session) {
        CicloAcademico ciclo = (CicloAcademico) session.getAttribute(Constantine.CICLO_ORDEN_MERITO);
        if (ciclo == null) {
            ciclo = service.findCicloAcademico(ds.getCicloAcademico());
            session.setAttribute(Constantine.CICLO_ORDEN_MERITO, ciclo);
        }
        return ciclo;
    }
}
