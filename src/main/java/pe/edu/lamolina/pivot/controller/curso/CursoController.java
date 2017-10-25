package pe.edu.lamolina.pivot.controller.curso;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCursoEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/curso")
public class CursoController {

    @Autowired
    CursoService service;

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
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);
        return "academico/curso/curso";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Curso> cursos = service.allByDynatable(filter, ds.getDepartamentos());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Curso curso : cursos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", curso.getId());
                node.put("curso", curso.getNombre());
                node.put("codigo", curso.getCodigo());
                node.put("hTeoria", curso.getHorasTeoria());
                node.put("hPractica", curso.getHorasPractica());
                node.put("creditos", curso.getCreditos());
                node.put("tipoCurso", curso.getTipoCurso() != null ? curso.getTipoCursoEnum().getValue() : "");
                node.put("facultad", curso.getDepartamentoAcademico().getFacultad().getNombre());
                node.put("departamento", curso.getDepartamentoAcademico().getNombre());
                node.put("coordinador", curso.getCoordinador() != null ? curso.getCoordinador().getPersona().getNombreCompleto() : "");
                node.put("estado", curso.getEstado());
                node.put("estadoName", EstadoEnum.valueOf(curso.getEstado()).getValue());
                node.put("motivo", curso.getMotivoAnulacion());

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

    @RequestMapping("nuevo")
    public String nuevo(Model model) {
        Curso curso = new Curso();
        curso.setDepartamentoAcademico(new DepartamentoAcademico());
        curso.setCoordinador(new Docente());
        model.addAttribute("curso", curso);
        model.addAttribute("tiposCurso", TipoCursoEnum.values());

        return "academico/curso/cursoForm";
    }

    @RequestMapping("save")
    public String save(Curso curso, RedirectAttributes redirectAttr, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            String mensaje = curso.getId() != null ? "Curso Actualizado" : "Curso Agregado";
            service.save(curso, ds.getUsuario());
            Notificaciones.crearMsg(mensaje, redirectAttr);

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);

        }
        return "redirect:/academico/curso";
    }

    @RequestMapping("editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        Curso curso = service.find(id);
        model.addAttribute("curso", curso);
        model.addAttribute("tiposCurso", TipoCursoEnum.values());

        return "academico/curso/cursoForm";
    }

    @ResponseBody
    @RequestMapping("cambiarEstadoCurso")
    public JsonResponse cambiarEstadoCarrera(Curso curso) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.cambiarEstadoCurso(curso);

            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
