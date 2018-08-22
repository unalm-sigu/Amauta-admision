package pe.edu.lamolina.pivot.controller.mensajeria.gpoalumno;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("mensajeria/gpoalumno")
public class GpoAlumnoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GpoAlumnoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ObjectNode cicloJSON = new ObjectNode(JsonNodeFactory.instance);
        cicloJSON.put("descripcion", ds.getCicloAcademico().getDescripcion());

        model.addAttribute("ciclo", cicloJSON);

        return "mensaje/gpoalumno/gpoalumno";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<GrupoAlumno> gpoAlumnos = service.allByDynatble(filter);
            for (GrupoAlumno gpoAlumno : gpoAlumnos) {
                ObjectNode obj = JsonHelper.createJson(gpoAlumno, JsonNodeFactory.instance, new String[]{
                    "*"
                });
                array.add(obj);
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
    @RequestMapping("saveUpdate")
    public JsonResponse saveUpdate(@RequestBody GrupoAlumno gpoAlumno, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            if (gpoAlumno.getId() == null) {
                service.save(gpoAlumno, ds.getCicloAcademico(), ds.getUsuario());
                response.setMessage(Messages.CREATED);
            } else {
                service.update(gpoAlumno, ds.getCicloAcademico(), ds.getUsuario());
                response.setMessage(Messages.UPDATED);
            }
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(@RequestBody GrupoAlumno gpoAlumno, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            service.eliminar(gpoAlumno);
            response.setMessage(Messages.DELETED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{id}/detalle")
    public String detalleGpoAlumno(@PathVariable("id") Long id, Model model, HttpSession session) {

        GrupoAlumno grupo = service.findGrupoById(id);
        model.addAttribute("grupo", grupo.toJson());
        return "mensaje/gpoalumno/detalleGpoAlumno/detalleGpoAlumno";

    }

    @ResponseBody
    @RequestMapping("detalle/{id}/list")
    public DynatableResponse detalleList(DynatableFilter filter, @PathVariable("id") Long id, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            GrupoAlumno grupo = new GrupoAlumno(id);

            List<DetalleGrupoAlumno> detalleGrupos = service.allDetallesByDynatbleGrupoAlumno(filter, grupo);
            for (DetalleGrupoAlumno detalle : detalleGrupos) {
                ObjectNode obj = JsonHelper.createJson(detalle, JsonNodeFactory.instance, new String[]{
                    "*",
                    "grupoAlumno.id",
                    "grupoAlumno.nombre",
                    "modalidadEstudio.id",
                    "modalidadEstudio.nombre",
                    "situacionAcademica.id",
                    "situacionAcademica.codigo",
                    "situacionAcademica.nombre",
                    "facultad.id",
                    "facultad.nombre",
                    "carrera.id",
                    "carrera.nombre",
                    "grupoSeccion.id",
                    "grupoSeccion.codigo",
                    "grupoSeccion.curso.codigo",
                    "grupoSeccion.curso.nombre",
                    "seccion.id",
                    "seccion.codigo2",
                    "seccion.grupoSeccion.curso.codigo",
                    "seccion.grupoSeccion.curso.nombre",
                    "seccion.grupoHoras.codigo",
                    "curso.id",
                    "curso.codigo",
                    "curso.nombre"
                });
                array.add(obj);
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
    @RequestMapping("detalle/save")
    public JsonResponse saveDetalleGrupo(@RequestBody DetalleGrupoAlumno detalleGrupo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            if (detalleGrupo.getId() == null) {
                service.saveDetalleGrupo(detalleGrupo);
                response.setMessage(Messages.CREATED);
            } else {
                service.saveDetalleGrupo(detalleGrupo);
                response.setMessage(Messages.UPDATED);
            }
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("detalle/eliminar")
    public JsonResponse eliminarDetalle(@RequestBody DetalleGrupoAlumno detalleGrupo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            service.eliminarDetalle(detalleGrupo);
            response.setMessage(Messages.DELETED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
