package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/horariocachimbo/horario")
public class GenerarHorarioIngresanteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GenerarHorarioIngresanteService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "academico/horariocachimbo/generar/horariogenerar";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<HorarioCachimbos> horarioCachimbos = service.allHorarioCachimbos(filter, cicloAcademico);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (HorarioCachimbos horarioCachimbo : horarioCachimbos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", horarioCachimbo.getId());
                node.put("codigo", horarioCachimbo.getCodigo());
                node.put("carrera", horarioCachimbo.getCarrera().getNombre());
                node.put("cursos", horarioCachimbo.getCursos());
                node.put("capacidad", horarioCachimbo.getCapacidad());
                node.put("suscritos", horarioCachimbo.getSuscritos());
                node.put("matriculados", horarioCachimbo.getMatriculados());
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
    @RequestMapping("delete")
    public JsonResponse delete(HorarioCachimbos horarioCachimbos) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(horarioCachimbos);
            response.setMessage("Horario eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deleteGrupo")
    public JsonResponse deleteGrupo(HorarioCachimboForm form) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(form);
            response.setMessage("Horarios eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("generador")
    public String generador(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ModalidadEstudio modalidadEstudio = new ModalidadEstudio(1);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        List<Carrera> carreras = service.allCarrera(modalidadEstudio);
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("carreras", carreras);
        return "academico/horariocachimbo/generador/generador";
    }

    @ResponseBody
    @RequestMapping("allHorario")
    public JsonResponse allHorario(Carrera carrera, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            List<Curso> cursos = service.allCursoCachimbosByCicloAcademico(cicloAcademico, carrera);
            for (Curso curso : cursos) {
                logger.debug("curso XXXX  {}", curso.getId());
            }
            ObjectNode node = new ObjectNode(jsonFactory);

            List<HorarioCachimbos> horarioCachimbos = service.allHorarioCachimbosByCicloAcademico(cicloAcademico, carrera);
            logger.debug("==== {} {} {} {}", horarioCachimbos.size(), cursos.size(), cicloAcademico.getId());
            List<SeccionHorarioCachimbos> seccionHorarioCachimbos = service.allSeccionHorarioCachimbosByCursoHora(carrera, cursos, cicloAcademico);
            logger.debug("seccion Horario Cachimbos xxxxxxxx  ::: {}", seccionHorarioCachimbos.size());

            Map<Long, List<SeccionHorarioCachimbos>> seccionHorarioCachimbosMap = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", seccionHorarioCachimbos);
            logger.debug("curso map size  {}", seccionHorarioCachimbosMap.size());
            for (Long long1 : seccionHorarioCachimbosMap.keySet()) {
                logger.debug("curso map  {}", long1);
            }

            for (Curso curso : cursos) {
                logger.debug("curso {}", curso.getNombre());
                node.put("curso", curso.getNombre());
                List<SeccionHorarioCachimbos> seccionHorarioCachimboLIst = seccionHorarioCachimbosMap.get(curso.getId());
                logger.debug("has  seccionHorarioCachimboLIst {}", (seccionHorarioCachimboLIst != null));
                if (seccionHorarioCachimboLIst == null) {
                    continue;
                }
                Map<Long, List<SeccionHorarioCachimbos>> horarioCachimbosMap = TypesUtil.convertListToMapList("horarioCachimbos.id", seccionHorarioCachimboLIst);
                ArrayNode array = new ArrayNode(jsonFactory);
                for (HorarioCachimbos horarioCachimbo : horarioCachimbos) {
                    ObjectNode hora = new ObjectNode(jsonFactory);
                    List<SeccionHorarioCachimbos> shcHorario = horarioCachimbosMap.get(horarioCachimbo.getId());
                    logger.debug("********curso {}", horarioCachimbo.getCodigo());
                    hora.put("claveTeorica", service.getClave(TipoSeccionEnum.TEO.name(), shcHorario));
                    hora.put("clavePractica", service.getClave(TipoSeccionEnum.PRA.name(), shcHorario));
                    hora.put("grupoTeoria", service.getClave(TipoSeccionEnum.TEO.name(), shcHorario));
                    hora.put("grupoPractica", service.getClave(TipoSeccionEnum.PRA.name(), shcHorario));
                    array.add(hora);
                }
                node.set("horario", array);
            }

            response.setData(node);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchAlumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<AlumnoHorario> alumnos = service.allAlumnoHorarioByName(nombre, cicloAcademico);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (AlumnoHorario alumnoHorario : alumnos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", alumnoHorario.getAlumno().getId());
                json.put("nombre", alumnoHorario.getAlumno().getPersona().getNombreCompleto());
                json.put("codigoMatricula", alumnoHorario.getAlumno().getCodigo());
                json.put("carrera", alumnoHorario.getAlumno().getCarrera().getNombre());
                json.put("facultad", alumnoHorario.getAlumno().getCarrera().getFacultad().getNombre());
                json.put("tipo", alumnoHorario.getAlumno().getPersona().getTipoDocumento().getSimbolo());
                json.put("numero", alumnoHorario.getAlumno().getPersona().getNumeroDocIdentidad());
                jsonList.add(json);
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
