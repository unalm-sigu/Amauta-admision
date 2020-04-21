package pe.edu.lamolina.amauta.controller.academico.departamento;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/departamento")
public class DepartamentoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DepartamentoService service;

    @Autowired
    VerificadorService verificadorService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "academico/departamento/departamento";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<DepartamentoAcademico> departamentosUser = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds);
            
            List<DepartamentoAcademico> departamentos = service.allDepartamentoAcademico(filter, departamentosUser);
            
            List<DepartamentoCursoDocente> departamentoCursoDocente = service.allDepartamentoCursoDocente(departamentos);

            Map<Long, DepartamentoCursoDocente> departamentoMap = TypesUtil.convertListToMap("id", departamentoCursoDocente);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (DepartamentoAcademico departamentoAcademico : departamentos) {

                DepartamentoCursoDocente depCurDocMap = departamentoMap.get(departamentoAcademico.getId());

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", departamentoAcademico.getId());
                node.put("nombre", departamentoAcademico.getNombre());
                node.put("codigo", departamentoAcademico.getCodigo());
                node.put("nombreLargo", departamentoAcademico.getNombre());
                node.put("estado", departamentoAcademico.getEstado());
                node.put("motivoDesactivacion", departamentoAcademico.getMotivoDesactivacion());
                node.put("fecha", new DateTime(departamentoAcademico.getFechaDesactivacion()).toString("dd/MM/yyyy"));
                node.put("docenteActivos", depCurDocMap.getDocenteActivos());
                node.put("docenteInactivos", depCurDocMap.getDocenteInactivos());
                node.put("cursoActivos", depCurDocMap.getCursoActivos());
                node.put("cursoInactivos", depCurDocMap.getCursoInactivos());
                node.put("facultad", departamentoAcademico.getFacultad().getNombre());
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

    @RequestMapping("{departamento}/update")
    public String update(@PathVariable("departamento") Long idDepartamentoAcademico, Model model, HttpSession session) {

        DepartamentoAcademico departamento = service.findDepartamentoAcademico(idDepartamentoAcademico);
        model.addAttribute("departamento", departamento);
        return "academico/departamento/departamentoForm";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        model.addAttribute("departamento", new DepartamentoAcademico());
        return "academico/departamento/departamentoForm";
    }

    @RequestMapping("save")
    public String save(DepartamentoAcademico departamento, RedirectAttributes redirectAttr, HttpSession session) {

        try {

            if (departamento.getId() != null) {
                service.update(departamento);
                Notificaciones.crearMsg("Departamento académico actualizado satisfactoriamente", redirectAttr);
            } else {
                service.save(departamento);
                Notificaciones.crearMsg("Departamento académico creado satisfactoriamente", redirectAttr);
            }

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
        }

        return "redirect:/academico/departamento";
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(DepartamentoAcademico departamento) {

        JsonResponse response = new JsonResponse();

        try {

            service.delete(departamento);
            response.setMessage("Departamento académico eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("estado")
    public JsonResponse estado(DepartamentoAcademico departamento) {

        JsonResponse response = new JsonResponse();

        try {

            service.estado(departamento);
            response.setMessage("Departamento académico actualizado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allDepartamento")
    public JsonResponse allDepartamento(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<DepartamentoAcademico> departamentos = service.allDepartemento(nombre, compania);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (DepartamentoAcademico departamento : departamentos) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", departamento.getId());
                a.put("codigo", departamento.getCodigo());
                a.put("nombre", departamento.getNombre());
                a.put("facultadCod", departamento.getFacultad().getCodigo());
                a.put("facultadName", departamento.getFacultad().getNombre());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allFacultad")
    public JsonResponse allFacultad(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Facultad> facultades = service.allFacultad(nombre, compania);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (Facultad facultad : facultades) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", facultad.getId());
                a.put("codigo", facultad.getCodigo());
                a.put("nombre", facultad.getNombre());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allDocentesByDptoEstado")
    public JsonResponse allDocentesByDpto(@RequestParam("id") Long idDpto,
            @RequestParam("estado") String estado,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Docente> docentes = service.allDocenteByDptoEstado(idDpto, estado);

            ArrayNode array = new ArrayNode(jsonFactory);
            for (Docente docente : docentes) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", docente.getId());
                a.put("nombre", docente.getPersona().getApellidosNombres());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCursosByDptoEstado")
    public JsonResponse allCursosByDptoEstado(@RequestParam("id") Long idDpto,
            @RequestParam("estado") String estado,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Curso> cursos = service.allCursoByDptoEstado(idDpto, estado);

            ArrayNode array = new ArrayNode(jsonFactory);
            for (Curso curso : cursos) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", curso.getId());
                a.put("nombre", curso.getNombre());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
