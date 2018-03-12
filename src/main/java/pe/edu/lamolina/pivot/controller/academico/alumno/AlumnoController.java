package pe.edu.lamolina.pivot.controller.academico.alumno;

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
import javax.servlet.http.HttpSession;
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
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.RolEnum;
import static pe.edu.lamolina.model.enums.RolEnum.FAC;
import static pe.edu.lamolina.model.enums.RolEnum.MOD;
import static pe.edu.lamolina.model.enums.RolEnum.TODO;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.TipoCarreraEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class AlumnoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoService service;

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
        model.addAttribute("resumen", service.findResumen());

        return "academico/alumno/alumno";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        logger.debug("Rol activo {}", ds.getRolActivo().getCodigo());

        List<Long> filtros = new ArrayList();

        switch (RolEnum.valueOf(ds.getRolActivo().getCodigo())) {
            case TODO:
                break;
            case MOD:
                for (ModalidadEstudio modalidad : ds.getModalidades()) {
                    filtros.add(modalidad.getId());
                }
                break;
            case FAC:
                for (Facultad fac : ds.getFacultados()) {
                    filtros.add(fac.getId());
                }
                break;
            case ESP:
                for (Carrera carrera : ds.getCarreras()) {
                    filtros.add(carrera.getId());
                }
                break;
            default:
                break;
        }

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            FotoHelper helper = new FotoHelper();
            List<Alumno> alumnos = service.allAlumnosByCicloDynatable(filter, ds.getRolActivo().getCodigo(), filtros);

            for (Alumno alumn : alumnos) {
                Persona persona = alumn.getPersona();
                Carrera carrera = alumn.getCarrera();
                Facultad facultad = carrera.getFacultad();

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", alumn.getId());
                node.put("nombre", persona.getApellidosNombres());
                node.put("codigo", alumn.getCodigo());
                node.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));
                node.put("tipoDoc", persona.getTipoDocumento().getSimbolo());
                node.put("nroDocumento", persona.getNumeroDocIdentidad());
                node.put("telefono", persona.getTelefono());
                node.put("celular", persona.getCelular());
                node.put("email", persona.getEmail());
                node.put("emailEmpresa", persona.getEmailCompania());
                node.put("carrera", carrera.getNombre());
                node.put("codigoCarrera", carrera.getCodigo());
                node.put("codigoFacultad", facultad.getCodigo());
                node.put("tipoCarreraValue", carrera.getTipoEnum().getValue());
                node.put("tipoCarrera", carrera.getTipo());
                node.put("facultad", facultad.getNombre());
                node.put("codigoModalidad", carrera.getModalidadEstudio().getCodigo());
                node.put("modalidad", carrera.getModalidadEstudio().getNombre());
                node.put("situacion", alumn.getSituacionAcademica().getNombre());
                node.put("cicloIngreso", (String) ObjectUtil.getParentTree(alumn, "cicloIngreso.descripcion"));
                node.put("cicloActivo", (String) ObjectUtil.getParentTree(alumn, "cicloActivo.descripcion"));
                node.put("estado", alumn.getEstado());
                node.put("estadoEnum", alumn.getEstadoEnum() != null ? alumn.getEstadoEnum().getValue() : "");
                node.put("ppa", alumn.getPromedioAcumulado());
                node.put("cca", alumn.getCreditosCursados());
                node.put("capa", alumn.getCreditosAprobados());

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

    @RequestMapping("especial")
    public String alumnoEspecial(Model model, HttpSession session) {

        Alumno alumno = new Alumno();
        alumno.setPersona(new Persona());

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", service.allDocumento());
        model.addAttribute("ciclos", service.allCicloAcademico());
        model.addAttribute("situaciones", service.allSituaciones());
        model.addAttribute("alumno", alumno);
        model.addAttribute("helper", new AlumnoHelper());
        model.addAttribute("carreras", new AlumnoHelper());

        return "academico/alumno/especial/alumnoEspecial";
    }

    @ResponseBody
    @RequestMapping("saveAlumnoEspecial")
    public JsonResponse saveAlumnoEspecial(Alumno alumno, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();

            if (alumno.getId() == null) {
                service.saveAlumnoEspecial(alumno, usuario);
                response.setMessage("Alumno creado satisfactoriamente");
            } else {
                service.updateAlumnoEspecial(alumno, usuario);
                response.setMessage("Alumno modificado satisfactoriamente");
            }

            response.setSuccess(true);
            response.setData(node);

            node.put("personaId", alumno.getPersona().getId());
            node.put("nombreCompleto", alumno.getPersona().getApellidosNombres());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("fisico")
    public String alumnoFisico(Model model, HttpSession session) {

        List<String> codigos = new ArrayList();
        codigos.add(PRE.name());
        codigos.add(EPG.name());

        List<ModalidadEstudio> modalidades = service.allModalidadEstudioByCodigos(codigos);
        Alumno alumno = new Alumno();
        alumno.setPersona(new Persona());

        List<CicloAcademico> ciclos = service.allCicloAcademico();

        model.addAttribute("ciclos", ciclos);
        model.addAttribute("documentos", service.allDocumento());
        model.addAttribute("ciclos", service.allCicloAcademico());
        model.addAttribute("modalidades", modalidades);
        model.addAttribute("alumno", alumno);
        model.addAttribute("helper", new AlumnoHelper());
        model.addAttribute("carreras", new AlumnoHelper());
        return "academico/alumno/fisico/alumnoFisico";
    }

    @RequestMapping("{idAlumno}/fisicoupdate")
    public String fisicoupdate(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {

        List<String> codigos = new ArrayList();
        codigos.add(PRE.name());
        codigos.add(EPG.name());

        List<ModalidadEstudio> modalidades = service.allModalidadEstudioByCodigos(codigos);
        Alumno alumno = service.findAlumnoFisico(idAlumno);

        List<CicloAcademico> ciclos = service.allCicloAcademico();

        model.addAttribute("ciclos", ciclos);
        model.addAttribute("documentos", service.allDocumento());
        model.addAttribute("ciclos", service.allCicloAcademico());
        model.addAttribute("modalidades", modalidades);
        model.addAttribute("alumno", alumno);
        model.addAttribute("helper", new AlumnoHelper());
        return "academico/alumno/fisico/alumnoFisico";
    }

    @ResponseBody
    @RequestMapping("saveAlumnoFisico")
    public JsonResponse saveAlumnoFisico(Alumno alumno, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();

            if (alumno.getId() == null) {
                service.saveAlumnoFisico(alumno, usuario);
                response.setMessage("Alumno creado satisfactoriamente");
            } else {
                service.updateAlumnoFisico(alumno, usuario);
                response.setMessage("Alumno modificado satisfactoriamente");
            }

            response.setSuccess(true);
            response.setData(node);

            node.put("personaId", alumno.getPersona().getId());
            node.put("nombreCompleto", alumno.getPersona().getApellidosNombres());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{alumno}/matricula/origen/matriculable")
    public String alumnoMatricula(@PathVariable("alumno") Long idAlumno,
            Model model, HttpSession session) {

        List<MatriculaCurso> cursos = service.allMatriculaCursoByAlumno(idAlumno);
        model.addAttribute("cursos", cursos);
        return "academico/alumno/otros/alumnoMatricula";

    }

    @RequestMapping("{alumno}/horario/origen/matriculable")
    public String alumnoHorario(@PathVariable("persona") Long idPersona,
            Model model, HttpSession session) {

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", service.allDocumento());
        model.addAttribute("ciclos", service.allCicloAcademico());

        return "academico/alumno/alumnoHorario";
    }

    @RequestMapping("{alumno}/historia/origen/matriculable")
    public String alumnoHistoria(@PathVariable("persona") Long idPersona,
            Model model, HttpSession session) {

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", service.allDocumento());
        model.addAttribute("ciclos", service.allCicloAcademico());

        return "academico/alumno/alumnoHistoria";
    }

    @RequestMapping("{alumno}/avance/origen/matriculable")
    public String alumnoAvance(@PathVariable("persona") Long idPersona,
            Model model, HttpSession session) {

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", service.allDocumento());
        model.addAttribute("ciclos", service.allCicloAcademico());

        return "academico/alumno/alumnoAvance";
    }

    @ResponseBody
    @RequestMapping("allCarrera")
    public JsonResponse allCarrera(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania cia = ds.getCompania();
            List<Carrera> carreras = service.allCarreraByName(nombre, cia);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Carrera carrera : carreras) {
                ModalidadEstudio modalidadEstudio = carrera.getModalidadEstudio();

                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", carrera.getId());
                json.put("nombre", carrera.getNombre());
                json.put("codigo", carrera.getCodigo());
                json.put("modalidad", modalidadEstudio.getNombre());
                if (modalidadEstudio.getCodigo().equalsIgnoreCase(ModalidadEstudioEnum.EPG.name())) {
                    json.put("tipo", carrera.getTipoEnum().getValue());
                }
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

    @ResponseBody
    @RequestMapping("resumen")
    public JsonResponse resumen(@RequestParam("idAlumno") Long idAlumno, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            Alumno alumno = service.findAlumno(idAlumno);
            Persona persona = alumno.getPersona();
            FotoHelper helper = new FotoHelper();
            Carrera carrera = alumno.getCarrera();
            Facultad facultad = carrera.getFacultad();
            ModalidadEstudio modalidad = carrera.getModalidadEstudio();

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("nombre", persona.getApellidosNombres());
            node.put("codigo", alumno.getCodigo());
            node.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));
            node.put("tipoDoc", persona.getTipoDocumento().getSimbolo());
            node.put("nroDocumento", persona.getNumeroDocIdentidad());
            node.put("telefono", persona.getTelefono());
            node.put("celular", persona.getCelular());
            node.put("email", persona.getEmail());
            node.put("emailEmpresa", persona.getEmailCompania());
            node.put("carrera", carrera.getNombre());
            node.put("codigoCarrera", carrera.getCodigo());
            node.put("codigoFacultad", facultad.getCodigo());
            node.put("tipoCarreraValue", carrera.getTipoEnum().getValue());
            node.put("tipoCarrera", carrera.getTipo());
            node.put("facultad", facultad.getNombre());
            node.put("codigoModalidad", carrera.getModalidadEstudio().getCodigo());
            node.put("modalidad", carrera.getModalidadEstudio().getNombre());

            node.put("situacion", alumno.getSituacionAcademica().getNombre());
            node.put("cicloIngreso", alumno.getCicloIngreso().getDescripcion());
            node.put("cicloActivo", alumno.getCicloActivo().getDescripcion());
            node.put("estado", alumno.getEstado());
            node.put("estadoEnum", alumno.getEstadoEnum() != null ? alumno.getEstadoEnum().getValue() : "");
            node.put("ppa", alumno.getPromedioAcumulado());
            node.put("cca", alumno.getCreditosCursados());
            node.put("capa", alumno.getCreditosAprobados());
            node.put("verFacultad", (ModalidadEstudioEnum.PRE.name().equals(modalidad.getCodigo())
                    && !carrera.getCodigo().equals(facultad.getCodigo())));

            node.put("verTipoCarrera", (TipoCarreraEnum.MAE.name().equals(carrera.getTipo())
                    || TipoCarreraEnum.MAE.name().equals(carrera.getTipo())));

            node.put("sexo", persona.getSexo() != null ? SexoEnum.valueOf(persona.getSexo()).getValue() : "");

            response.setData(node);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{idAlumno}/infoacademico")
    public String infoAcademico(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Alumno alumno = service.allInfo(new Alumno(idAlumno));
        ObjectNode alumnoJson = alumno.toJson();

        model.addAttribute("datoAlumno", alumnoJson);
        model.addAttribute("ciclo", ds.getCicloAcademico().toJson());
        ArrayNode horasJson = new ArrayNode(JsonNodeFactory.instance);
        List<Hora> horas = service.allHoras();
        for (Hora hora : horas) {
            horasJson.add(hora.toJson());
        }
        model.addAttribute("horasBD", horasJson);
        return "academico/alumno/infoAcademico";
    }

    @RequestMapping("{idAlumno}/gomatricula")
    public String goMatricula(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        String codigo = service.goMatricula(idAlumno);

        session.invalidate();
        return "redirect:http://localhost:9977/amauta/" + codigo;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/historial", method = RequestMethod.GET)
    public JsonResponse alumnoHistorial(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            List<AlumnoCiclo> promedios = service.allPromediosByAlumno(new Alumno(idAlumno));
            ArrayNode lstNode = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoCiclo promedio : promedios) {
                ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
                objNode.put("ciclo", promedio.getCicloAcademico().getDescripcion2());
                objNode.put("descripción", promedio.getCicloAcademico().getDescripcion());
                objNode.put("promedio", promedio.getPromedioCiclo());
                objNode.put("promedioPonderadoAcum", promedio.getPromedioAcumulado());
                objNode.put("CreditoCursadosCiclo", promedio.getCreditosCursadosCiclo());
                objNode.put("CreditoAprobadosAcu", promedio.getCreditosAprobadosAcumulados());
                objNode.put("CreditoAprobaCiclo", promedio.getCreditosAprobadosCiclo());
                objNode.put("creditoAcumulado", promedio.getCreditosAcumulados());
                List<AlumnoCicloCurso> cursos = promedio.getAlumnoCicloCurso();

                ArrayNode lstCurso = new ArrayNode(JsonNodeFactory.instance);
                for (AlumnoCicloCurso cicloCurso : cursos) {
                    ObjectNode objCurso = new ObjectNode(JsonNodeFactory.instance);
                    Curso curso = cicloCurso.getCurso();
                    objCurso.put("curso", curso.getNombre());
                    objCurso.put("codigo", curso.getCodigo());
                    objCurso.put("creditos", cicloCurso.getCreditos());
                    objCurso.put("nota", cicloCurso.getNota());

                    lstCurso.add(objCurso);
                }
                objNode.set("cursos", lstCurso);
                lstNode.add(objNode);
                response.setSuccess(true);
            }
            response.setData(lstNode);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/cursosmatriculados", method = RequestMethod.GET)
    public JsonResponse cursosMatriculados(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode data = new ObjectNode(factory);
        ArrayNode cursosJson = new ArrayNode(factory);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Alumno alumno = new Alumno(idAlumno);
            List<MatriculaCurso> matriculaCursos = service.allCursosMatriculadosByAlumnoCiclo(alumno, ciclo);
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                ObjectNode matriculaCursoNode = matriculaCurso.toJson();
                ArrayNode detalle = new ArrayNode(factory);
                List<MatriculaSeccion> matriculaSeccions = matriculaCurso.getMatriculaSeccion();
                if (matriculaSeccions == null) {
                    continue;
                }
                for (MatriculaSeccion matriculaSeccion : matriculaSeccions) {
                    ObjectNode node = new ObjectNode(factory);
                    node.put("tipo", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.tipoSeccion"));
                    node.put("codigo", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.codigo"));
                    node.put("grupo", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.grupoHoras.codigo"));
                    node.put("aula", (String) ObjectUtil.getParentTree(matriculaSeccion, "seccion.aula.codigo"));
                    DocenteSeccion docenteSeccion = matriculaSeccion.getSeccion().getDocenteSeccion().get(0);
                    node.put("docente", (String) ObjectUtil.getParentTree(docenteSeccion, "docente.persona.nombreCompleto"));
                    node.put("docenteCodigo", (String) ObjectUtil.getParentTree(docenteSeccion, "docente.codigo"));
                    detalle.add(node);
                }
                matriculaCursoNode.set("detalle", detalle);
                cursosJson.add(matriculaCursoNode);

            }
            data.set("cursos", cursosJson);
            data.set("ciclo", JsonHelper.createJson(ciclo, factory));
            response.setData(data);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/generaravance", method = RequestMethod.GET)
    public JsonResponse generarAvance(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            service.generarAvance(new Alumno(idAlumno), ds);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/listHistorial", method = RequestMethod.GET)
    public JsonResponse alumnoListHistorial(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            List<AlumnoCicloCurso> alumnoCicloCurso = service.allPromediosByAlumnoOrderByCurso(new Alumno(idAlumno));

            ArrayNode lstCurso = new ArrayNode(JsonNodeFactory.instance);
            ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
            objNode.put("alumnoCodigo", alumnoCicloCurso.get(0).getAlumnoCiclo().getAlumno().getCodigo());
            for (AlumnoCicloCurso curso : alumnoCicloCurso) {
                ObjectNode objCurso = new ObjectNode(JsonNodeFactory.instance);
                objCurso.put("curso", curso.getCurso().getNombre());
                objCurso.put("codigo", curso.getCurso().getCodigo());
                objCurso.put("creditos", curso.getCreditos());
                objCurso.put("nota", curso.getNota());
                lstCurso.add(objCurso);
            }
            objNode.set("cursos", lstCurso);
            response.setData(objNode);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/alumno", method = RequestMethod.GET)
    public JsonResponse allAlumno(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            Alumno alumno = service.allInfo(new Alumno(idAlumno));

            ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
            ObjectNode objNodeInfo = new ObjectNode(JsonNodeFactory.instance);
            objNodeInfo.put("Modalidad", alumno.getModalidadEstudio() == null ? "" : alumno.getModalidadEstudio().getNombre());
            objNodeInfo.put("promedioAcumulado", alumno.getPromedioAcumulado());
            objNodeInfo.put("creditosCursados", alumno.getCreditosCursados());
            objNodeInfo.put("creditosAprobados", alumno.getCreditosAprobados());
            objNodeInfo.put("carrera", alumno.getCarrera().getNombre());
            objNodeInfo.put("facultad", alumno.getCarrera().getFacultad().getNombre());
            objNodeInfo.put("cicloIngreso", alumno.getCicloIngreso() == null ? "" : alumno.getCicloIngreso().getDescripcion());
            objNodeInfo.put("ultimoCiclo", alumno.getCodigoCicloActivo() == null ? "" : alumno.getCicloActivo().getDescripcion());
            if (alumno.getPlanCurricular() != null) {
                objNodeInfo.set("planCurricular", alumno.getPlanCurricular().toJson());
            }
            objNode.set("alumno", objNodeInfo);
            response.setData(objNode);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/horario", method = RequestMethod.GET)
    public JsonResponse alumnoLoadHorario(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico academico = ds.getCicloAcademico();
        try {
            List<HorarioSeccion> seccionesHorarios = service.allSeccionHorarioAlumnoByAlumnoCicloACademico(new Alumno(idAlumno), academico);
            ObjectNode horarios = service.findHorarioBySeccionesHorarios(seccionesHorarios);
            response.setData(horarios);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{numero}/hora", method = RequestMethod.GET)
    public JsonResponse getHoraByNroHora(@PathVariable("numero") Integer numero, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            Hora hora = service.getHoraByNroHora(numero);
            response.setData(hora.toJson());
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
}
