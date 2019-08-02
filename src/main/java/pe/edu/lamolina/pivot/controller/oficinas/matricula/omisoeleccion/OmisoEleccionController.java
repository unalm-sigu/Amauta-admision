package pe.edu.lamolina.pivot.controller.oficinas.matricula.omisoeleccion;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.MotivoOmisoEnum;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("oficinas/matricula/omisoeleccion")
public class OmisoEleccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OmisoEleccionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.allCicloAcademico(ds.getCicloAcademico());
        ArrayNode arrayCiclo = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode arrayEnum = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arrayCiclo.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"}));
        }
        for (MotivoOmisoEnum enums : MotivoOmisoEnum.values()) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("name", enums.name());
            node.put("value", enums.getValue());
            arrayEnum.add(node);
        }
        model.addAttribute("motivos", arrayEnum);
        model.addAttribute("ciclos", arrayCiclo);
        return "oficinas/matricula/omisoeleccion/omisoeleccion";
    }

    @RequestMapping("load")
    public String load(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.allCicloAcademico(ds.getCicloAcademico());
        ArrayNode arrayCiclo = new ArrayNode(JsonNodeFactory.instance);

        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arrayCiclo.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"}));
        }

        model.addAttribute("ciclos", arrayCiclo);
        return "oficinas/matricula/omisoeleccion/loadOmisoEleccion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(factory);
            List<Alumno> alumnos = service.allDeudaAlumno(filter);

            for (Alumno alumno : alumnos) {
                ObjectNode node = JsonHelper.createJson(alumno, factory, new String[]{
                    "id",
                    "codigo",
                    "modalidadEstudio.*",
                    "persona.nombreCompleto",
                    "persona.tipoDocumento.*",
                    "persona.numeroDocIdentidad",
                    "persona.rutaFoto",
                    "carrera.*",
                    "carrera.facultad.*"});

                ArrayNode arrayOmiso = new ArrayNode(factory);
                for (AlumnoOmisoEleccion alumnoOmisoEleccion : alumno.getAlumnoOmisoEleccions()) {
                    ObjectNode nodeOmiso = JsonHelper.createJson(alumnoOmisoEleccion, factory, new String[]{
                        "*",
                        "alumno.id",
                        "cicloAcademico.*"});
                    arrayOmiso.add(nodeOmiso);
                }
                node.set("alumnoOmisoEleccions", arrayOmiso);
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

    @ResponseBody
    @RequestMapping("saveOmision")
    public JsonResponse saveOmision(@RequestBody AlumnoOmisoEleccion omisoEleccion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            service.saveOmision(omisoEleccion, ds);
            response.setSuccess(true);
            response.setMessage("Se realizó el registro.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anularOmision")
    public JsonResponse anularOmision(@RequestBody Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            service.anularOmision(alumno.getAlumnoOmisoEleccions(), ds);
            response.setSuccess(true);
            response.setMessage("Se realizó la actualización satisfactoriamente.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cargarDatos")
    public JsonResponse cargarDatos(@RequestParam("file") MultipartFile file,
            @RequestParam("cicloAcademico") String codigoCiclo,
            Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<String> observados = service.cargarDeudas(file, codigoCiclo, ds);
            if (observados.isEmpty()) {
                json.setData(null);
            } else {
                logger.debug("Hay observaciones");
                JsonNodeFactory factory = JsonNodeFactory.instance;
                ArrayNode observaciones = new ArrayNode(factory);
                for (String observado : observados) {
                    observaciones.add(observado);
                }
                json.setData(observaciones);
            }
            json.setSuccess(true);
            json.setMessage("Deudas por elecciones guardadas");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }

    @ResponseBody
    @RequestMapping("allAlumnoByNombre")
    public JsonResponse allAlumnoByNombre(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Alumno> lista = service.allAlumnoByNombre(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Alumno alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                            "id",
                            "codigo",
                            "modalidadEstudio.id",
                            "modalidadEstudio.nombre",
                            "carrera.codigo",
                            "carrera.nombre",
                            "carrera.facultad.codigo",
                            "carrera.facultad.nombre",
                            "persona.numeroDocIdentidad",
                            "persona.apellidosNombres",
                            "persona.nombreCompleto",
                            "persona.rutaFoto",
                            "persona.tipoDocumento.*"}));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
