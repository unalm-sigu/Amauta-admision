package pe.edu.lamolina.pivot.controller.academico.curso;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoCarreraEnum;
import pe.edu.lamolina.model.enums.TipoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/curso")
public class CursoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
                node.put("codigo2", curso.getCodigoAnterior1());
                node.put("tpc", curso.getTpc());
                node.put("tipoCurso", curso.getTipoCurso() != null ? curso.getTipoCursoEnum().getValue() : "");
                node.put("facultad", curso.getDepartamentoAcademico().getFacultad().getNombre());
                node.put("departamento", curso.getDepartamentoAcademico().getNombre());
                node.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                node.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));
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

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Curso curso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            String mensaje = curso.getId() != null ? Messages.UPDATED : Messages.CREATED;
            Curso cursoBD = service.save(curso, ds);
            response.setData(cursoBD.getId());
            response.setMessage(mensaje);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Curso curso = new Curso();
        Compania cia = ds.getCompania();

        settingJson(curso, cia, model);
        return "academico/curso/cursoForm";
    }

    @RequestMapping("{id}/editar")
    public String editar(@PathVariable("id") Long id, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania cia = ds.getCompania();
        Curso curso = service.find(id);

        settingJson(curso, cia, model);
        return "academico/curso/cursoForm";
    }

    private void settingJson(Curso curso, Compania cia, Model model) {
        model.addAttribute("cursoJson", getCursoJson(curso).toString());
        model.addAttribute("modalidadesJson", getModalidadesJson(cia).toString());
        model.addAttribute("tiposCursoJson", JsonHelper.enumToJson(TipoCursoEnum.values()).toString());
        model.addAttribute("tiposCurriculaJson", JsonHelper.enumToJson(TipoCurriculaEnum.values()).toString());
        model.addAttribute("idiomasJson", getIdiomasJson().toString());
        model.addAttribute("departamentosJson", getDptosAcademicosJson(cia).toString());
        model.addAttribute("carrerasJson", getCarrerasJson().toString());
    }

    private ObjectNode getCursoJson(Curso curso) {
        ObjectNode cursoJson = JsonHelper.createJson(curso, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "codigoAnterior1", "nombre", "tpc", "creditos", "creditosVariables", "creditosTeoria", "creditosPractica",
            "horasTeoria", "horasPractica", "horasTeoriaVerano", "horasPracticaVerano", "tipoCurso", "tipoCursoEnum", "tipoCredito", "tipoCreditoEnum",
            "tipoCurricula", "tipoCurriculaEnum", "nivel", "noEncuestar", "noCargaAdicional",
            "departamentoAcademico.id",
            "departamentoAcademico.codigo",
            "departamentoAcademico.nombre",
            "coordinador.codigo",
            "coordinador.persona.nombreCompleto",
            "coordinador.departamentoAcademico.id",
            "modalidadEstudio.id",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre",
            "carrera.id",
            "carrera.tipoEnum",
            "carrera.codigo",
            "carrera.nombre"
        });
        return cursoJson;
    }

    private ArrayNode getDptosAcademicosJson(Compania cia) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<DepartamentoAcademico> departementos = service.allDepartamentos(cia);
        for (DepartamentoAcademico dpto : departementos) {
            ObjectNode node = JsonHelper.createJson(dpto, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre",
                "facultad.id",
                "facultad.codigo",
                "facultad.nombre"
            });
            array.add(node);
        }
        return array;
    }

    private ArrayNode getCarrerasJson() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<Carrera> carreras = service.allCarrerasByPostgrado();
        Collections.sort(carreras, new Carrera.CompareNombrePosgrado());
        for (Carrera carr : carreras) {
            ObjectNode node = JsonHelper.createJson(carr, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre", "tipoEnum",
                "facultad.id",
                "facultad.codigo",
                "facultad.nombre"
            });
            array.add(node);
        }
        return array;
    }

    private ArrayNode getIdiomasJson() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<Idioma> idiomas = service.allIdiomas();
        for (Idioma idioma : idiomas) {
            ObjectNode node = JsonHelper.createJson(idioma, JsonNodeFactory.instance, true, new String[]{"*"});
            array.add(node);
        }
        return array;
    }

    private ArrayNode getModalidadesJson(Compania cia) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<ModalidadEstudio> modalidades = service.modalidadesEstudioPrePost(cia);
        for (ModalidadEstudio mod : modalidades) {
            ObjectNode node = JsonHelper.createJson(mod, JsonNodeFactory.instance, true, new String[]{"*"});
            array.add(node);
        }
        return array;
    }

    @ResponseBody
    @RequestMapping("{id}/find")
    public JsonResponse find(@PathVariable("id") Long id, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            Curso curso = service.find(id);
            response.setData(getCursoJson(curso));
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
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

    @ResponseBody
    @RequestMapping("allCarreras")
    public JsonResponse allCarreras(@RequestParam("codigo") String codigoEstudio, @RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Carrera> carreras = service.allByModalidadEstudioNombre(codigoEstudio, nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Carrera carrera : carreras) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", carrera.getId());
                json.put("nombre", carrera.getNombre());
                json.put("codigo", carrera.getCodigo());
                json.put("tipoEstudio", !"".equals(this.getTipoEstudio(carrera.getTipo())) ? TipoCarreraEnum.valueOf(carrera.getTipo()).getValue() : "");
                json.put("modalidadEstudio", carrera.getModalidadEstudio().getNombre());

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

    public String getTipoEstudio(String tipo) {
        if (tipo.equals(TipoCarreraEnum.SEM.name())) {
            return "";
        }
        return tipo;
    }

    @ResponseBody
    @RequestMapping("nivel")
    public JsonResponse nivelByModalidadEstudio(@RequestParam("codigo") String codigo) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            List<Integer> niveles = new ArrayList();
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            if (codigo.equals(ModalidadEstudioEnum.PRE.name())) {
                niveles = Arrays.asList(1, 2, 3, 4, 5, 6);
            } else {
                niveles = Arrays.asList(7, 8, 9);
            }

            for (Integer nivel : niveles) {
                ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
                json.put("id", nivel);
                json.put("text", nivel);
                array.add(json);
            }
            response.setData(array);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idDepartamento}/allDocentes")
    public JsonResponse allDocentes(
            @PathVariable("idDepartamento") Long idDepartamento,
            @RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Docente> docentes = service.allDocentesByDepartamento(nombre, new DepartamentoAcademico(idDepartamento));
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Docente profe : docentes) {
                ObjectNode json = JsonHelper.createJson(profe, jsonFactory, true, new String[]{
                    "id", "codigo",
                    "persona.apellidosNombres",
                    "departamentoAcademico.codigo",
                    "departamentoAcademico.nombre"
                });

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
