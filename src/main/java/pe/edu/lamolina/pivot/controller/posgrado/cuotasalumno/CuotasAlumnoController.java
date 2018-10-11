package pe.edu.lamolina.pivot.controller.posgrado.cuotasalumno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.posgrado.AlumnoConceptoMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("posgrado/cuotasalumno")
public class CuotasAlumnoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotasAlumnoService cuotasAlumnoService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "posgrado/cuotasalumno/generacionCuotas";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

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
        }
        return json;
    }

    @RequestMapping("{alumno}/cuotas")
    public String cuotasAlumno(
            @PathVariable("alumno") Long alumnoId,
            Model model,
            HttpSession session) {
        logger.debug("Alumno Id {}", alumnoId);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        JsonNodeFactory nc = JsonNodeFactory.instance;

        Alumno alumno = cuotasAlumnoService.findAlumno(new Alumno(alumnoId));
        /*    ObjectNode alumnoJson = JsonHelper.createJson(alumno, nc, false, new String[]{
            "*",
            "carrera.*"
        });
         */
        AlumnoResumenCuotas alumnoResumenCuotas = cuotasAlumnoService.findAlumnoResumenCuotaByAlumnoAndCiclo(alumno, ds.getCicloAcademico());
        ObjectNode alumnoResumenCuotasJson = null;
        if (alumnoResumenCuotas == null) {
            alumnoResumenCuotas = new AlumnoResumenCuotas();
            alumnoResumenCuotas.setTarifaCarrera(new TarifaCarrera());
            alumnoResumenCuotas.setPagoCash(Boolean.FALSE);
            alumnoResumenCuotas.setAlumnoConceptosMatricula(null);
            alumnoResumenCuotas.setAlumnoCuotasMatricula(null);

            alumnoResumenCuotasJson = JsonHelper.createJson(alumnoResumenCuotas, nc, true, new String[]{
                "*",
                "cicloAcademico.*",
                "tarifaCarrera.*",
                "userRegistro.*"
            });
        } else {
            for (AlumnoConceptoMatricula alumnoConceptoMatricula : alumnoResumenCuotas.getAlumnoConceptosMatricula()) {
                TarifaConcepto tarifaConcepto = cuotasAlumnoService.findTarifaConceptoByConceptoPosgrado(alumnoConceptoMatricula.getConceptoPosgrado());
                if (tarifaConcepto.getFraccionable()) {
                    BigDecimal porcentajeInicial = BigDecimal.valueOf(100).multiply(alumnoConceptoMatricula.getInicial());
                    porcentajeInicial = porcentajeInicial.divide(alumnoConceptoMatricula.getMonto());
                    alumnoResumenCuotas.setPorcentajeMontoInicial(porcentajeInicial);

                    if (alumnoResumenCuotas.getPagoCash() != null && !alumnoResumenCuotas.getPagoCash()) {
                        if (alumnoResumenCuotas.getPorcentajeMontoInicial().compareTo(new BigDecimal(100)) == 0) {
                            alumnoResumenCuotas.setPagoCash(Boolean.TRUE);
                        } else {
                            alumnoResumenCuotas.setPagoCash(Boolean.FALSE);
                        }
                    }
                }

            }
            if (alumnoResumenCuotas.getPorcentajeMontoInicial().compareTo(new BigDecimal(100)) == 0) {
                alumnoResumenCuotas.setPagoCash(Boolean.TRUE);
            }
            alumnoResumenCuotasJson = JsonHelper.createJson(alumnoResumenCuotas, nc, true, new String[]{
                "*",
                "cicloAcademico.*",
                "tarifaCarrera.*",
                "userRegistro.*",
                "alumnoConceptosMatricula.*",
                "alumnoConceptosMatricula.conceptoPosgrado.*",
                "alumnoCuotasMatricula.*"
            });
        }

        //   alumnoResumenCuotasJson.set("alumno", alumnoJson);
        model.addAttribute("alumnoResumenCuotasJson", alumnoResumenCuotasJson.toString());

        List<TarifaCarrera> tarifasCarreras = cuotasAlumnoService.allByCarrera(alumno.getCarrera());
        ArrayNode tarifasCarrerasJson = new ArrayNode(nc);
        for (TarifaCarrera tarifaCarrera : tarifasCarreras) {
            tarifasCarrerasJson.add(JsonHelper.createJson(tarifaCarrera, nc, true, new String[]{
                "*",
                "cicloInicio.id",
                "cicloInicio.codigo",
                "cicloInicio.descripcion",
                "tarifasConcepto.*",
                "tarifasConcepto.tarifaCarrera.*",
                "tarifasConcepto.conceptoPosgrado.*"
            }));
        }

        model.addAttribute("tarifasCarrerasJson", tarifasCarrerasJson.toString());
        model.addAttribute("ciclo", ds.getCicloAcademico());

        return "posgrado/cuotasalumno/cuotasAlumno";
    }

    /*
    @ResponseBody
    @RequestMapping("loadCuotasAlumnosPage")
    public JsonResponse loadCuotasAlumnosPage(HttpSession session,
            @RequestParam("alumno") Long alumnoId) {

        JsonResponse jsonResponse = new JsonResponse();
        JsonNodeFactory nc = JsonNodeFactory.instance;
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloSesion = ds.getCicloAcademico();

        Alumno alumno = cuotasAlumnoService.findAlumno(new Alumno(alumnoId));
        List<TarifaCarrera> tarifasCarrera = cuotasAlumnoService.allByCarrera(alumno.getCarrera());

        ArrayNode tarifasCarreras = new ArrayNode(nc);
        for (TarifaCarrera tarifaCarrera : tarifasCarrera) {
            tarifasCarreras.add(JsonHelper.createJson(tarifaCarrera, nc, false, new String[]{
                "id",
                "cicloInicio.id",
                "cicloInicio.codigo",
                "cicloInicio.nombre"
            }));
        }
        ObjectNode data = new ObjectNode(nc);

        jsonResponse.setData(data);
        return jsonResponse;
    }
     */
    @ResponseBody
    @RequestMapping("changeTarifaCarrera")
    public JsonResponse changeTarifaCarrera(
            @RequestParam(name = "tarifaCarrera") Long idTarifaCarrera,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        try {
            logger.debug("Tarifa Carrera {}", idTarifaCarrera);
            TarifaCarrera tarifaCarrera = cuotasAlumnoService.findTarifaCarrera(idTarifaCarrera);

            // ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            response.setData(JsonHelper.createJson(tarifaCarrera, JsonNodeFactory.instance, false, new String[]{
                "*"
            }));
            //   data.set("tarifaCarrera", data)
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("generarCuotasAlumno")
    public JsonResponse generarCuotasAlumno(
            @RequestBody AlumnoResumenCuotas alumnoResumenCuotas,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        logger.debug("generarCuotasAlumno");
        alumnoResumenCuotas = cuotasAlumnoService.generarCuotasAlumno(alumnoResumenCuotas, ds);

        response.setData(JsonHelper.createJson(alumnoResumenCuotas, JsonNodeFactory.instance, false, new String[]{
            "*",
            "alumnoConceptosMatricula.*",
            "alumnoConceptosMatricula.conceptoPosgrado.*",
            "alumnoCuotasMatricula.*"
        }));

        return response;
    }

    @ResponseBody
    @RequestMapping("grabarCuotasAlumno")
    public JsonResponse grabarCuotasAlumno(
            @RequestBody AlumnoResumenCuotas alumnoResumenCuotas,
            Model model,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        logger.debug("grabarCuotasAlumno");
        if (ObjectUtil.getParentTree(alumnoResumenCuotas, "id") == null) {
            cuotasAlumnoService.grabarCuotasAlumno(alumnoResumenCuotas, ds);
        } else {

        }
        response.setData(JsonHelper.createJson(alumnoResumenCuotas, JsonNodeFactory.instance, false, new String[]{
            "*",
            "alumnoConceptosMatricula.*",
            "alumnoConceptosMatricula.conceptoPosgrado.*",
            "alumnoCuotasMatricula.*"
        }));

        return response;
    }

}
