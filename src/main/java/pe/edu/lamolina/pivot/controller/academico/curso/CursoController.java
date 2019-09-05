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
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoCarreraEnum;
import pe.edu.lamolina.model.enums.TipoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.controller.academico.curso.view.AlumnoCursoExcelView;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/curso")
public class CursoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoService service;

    @Autowired
    AlumnoCursoExcelView alumnoCursoExcelView;

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
            List<Curso> cursos = service.allByDynatable(filter, ds.getDepartamentos(), ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Curso curso : cursos) {
                ObjectNode node = getCursoSimpleJson(curso);
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

    @ResponseBody
    @RequestMapping("saveIdioma")
    public JsonResponse saveIdioma(@RequestBody NombreCurso nombreCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            NombreCurso nombreCursoBD = nombreCurso.getId() == null
                    ? service.saveIdioma(nombreCurso, ds)
                    : service.updateIdioma(nombreCurso, ds);
            ObjectNode json = JsonHelper.createJson(nombreCursoBD, JsonNodeFactory.instance, true, new String[]{
                "id",
                "nombre",
                "fechaRegistro",
                "curso.id",
                "idioma.id",
                "idioma.codigo",
                "idioma.nombre"
            });
            response.setData(json);
            response.setMessage("Nombre de curso en otro idioma guardado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("deleteIdioma")
    public JsonResponse deleteIdioma(@RequestBody NombreCurso nombreCurso, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.deleteIdioma(nombreCurso, ds);
            response.setMessage("Nombre de curso eliminado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("nuevo")
    public String nuevo(@RequestParam(value = "origen", required = false) String origen, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Curso curso = new Curso();
        Compania cia = ds.getCompania();

        settingJson(curso, cia, model);
        System.out.println("origen ::: " + origen);
        System.out.println("getOrigen(origen) ::: " + getOrigen(origen));
        model.addAttribute("origen", getOrigen(origen));
        return "academico/curso/cursoForm";
    }

    private String getOrigen(String origen) {
        System.out.println("1111");
        if (StringUtils.isEmpty(origen)) {
            System.out.println("2222");
            return "/academico/curso";
        }
        System.out.println("3333");
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

    @RequestMapping("{id}/editar")
    public String editar(
            @PathVariable("id") Long id,
            @RequestParam(value = "origen", required = false) String origen, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania cia = ds.getCompania();
        Curso curso = service.find(id);

        settingJson(curso, cia, model);
        System.out.println("getOrigen(origen) ::: " + getOrigen(origen));
        model.addAttribute("origen", getOrigen(origen));
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
        model.addAttribute("tiposCarpetaJson", getTipoCarpetaJson().toString());
    }

    private ObjectNode getCursoJson(Curso curso) {
        ObjectNode cursoJson = JsonHelper.createJson(curso, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "codigoAnterior1", "nombre", "tpc", "creditos", "creditosVariables", "creditosTeoria", "creditosPractica",
            "horasTeoria", "horasPractica", "horasTeoriaVerano", "horasPracticaVerano", "tipoCurso", "tipoCursoEnum", "tipoCredito", "tipoCreditoEnum",
            "tipoCurricula", "tipoCurriculaEnum", "nivel", "noEncuestar", "noCargaAdicional",
            "tipoCarpetaTeoria.id", "tipoCarpetaTeoria.nombre", "tipoCarpetaTeoria.codigo",
            "tipoCarpetaPractica.id", "tipoCarpetaPractica.nombre", "tipoCarpetaPractica.codigo",
            "departamentoAcademico.id",
            "departamentoAcademico.codigo",
            "departamentoAcademico.nombre",
            "coordinador.codigo",
            "coordinador.id",
            "coordinador.persona.nombreCompleto",
            "coordinador.persona.apellidosNombres",
            "coordinador.departamentoAcademico.id",
            "modalidadEstudio.id",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre",
            "carrera.id",
            "carrera.tipoEnum",
            "carrera.codigo",
            "carrera.nombre",
            "nombreCurso.id",
            "nombreCurso.nombre",
            "nombreCurso.fechaRegistro",
            "nombreCurso.curso.id",
            "nombreCurso.idioma.id",
            "nombreCurso.idioma.codigo",
            "nombreCurso.idioma.nombre"
        });
        return cursoJson;
    }

    private ObjectNode getCursoSimpleJson(Curso curso) {
        ObjectNode cursoJson = JsonHelper.createJson(curso, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "codigoAnterior1", "nombre", "tpc", "tipoCurso", "tipoCursoEnum", "motivoAnulacion", "estado", "estadoEnum", "matriculados",
            "departamentoAcademico.codigo",
            "departamentoAcademico.nombre",
            "departamentoAcademico.facultad.codigo",
            "departamentoAcademico.facultad.nombre",
            "coordinador.persona.apellidosNombres",
            "coordinador.departamentoAcademico.id",
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
        logger.debug("carreras {}", carreras.size());
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

    private ArrayNode getTipoCarpetaJson() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<TipoCarpeta> tiposCarpeta = service.allTiposCarpeta();
        logger.debug("tipo carpeta {}", tiposCarpeta.size());

        for (TipoCarpeta tipoCarpeta : tiposCarpeta) {
            ObjectNode node = JsonHelper.createJson(tipoCarpeta, JsonNodeFactory.instance, true, new String[]{"*"});
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

    @RequestMapping("reproteAlumnos")
    public ModelAndView reporteAlumnos(@RequestParam("curso") Long cursoId, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<MatriculaSeccion> alumnosPorCurso = service.allMatriculasSecciones(Arrays.asList(new Curso(cursoId)), ds.getCicloAcademico());
        List<Seccion> secciones = alumnosPorCurso.stream().map(MatriculaSeccion::getSeccion).distinct().collect(Collectors.toList());
        List<DocenteSeccion> docenteSecciones = service.allDocenteSeccionPrincipalesBySecciones(secciones);

        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("docenteSecciones", docenteSecciones);
        return new ModelAndView(alumnoCursoExcelView);
    }

}
