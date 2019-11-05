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
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.CursoConvalidado;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class AlumnoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    @Autowired
    AlumnoService service;

    @Autowired
    VerificadorService verificadorService;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Autowired
    PromedioService promedioService;

    @Autowired
    DespliegueConfig despliegueConfig;

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
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        verificadorService.revisarPermiso(request, ds);

        model.addAttribute("resumen", service.findResumen());
        model.addAttribute("puedeMatricular", verificadorService.puedeOperarMatricula(ds));
        model.addAttribute("puedeEditarAlumno", verificadorService.puedeEditarAlumno(ds));

        return "academico/alumno/alumno";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            List<Carrera> carreras = new ArrayList();
            List<Alumno> alumnos = new ArrayList();
            VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
            logger.info("Acceso alumnos {}", cantidadEnum.name());
            if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
                carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds);
                logger.info("Acceso a {} carreras", carreras.size());
            }
            if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
                alumnos = service.allAlumnosbyDynatable(filter, carreras, cantidadEnum.name());
                logger.info("Se extrajeron {} alumnos", alumnos.size());
            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Alumno alumn : alumnos) {
                ObjectNode node = JsonHelper.createJson(alumn, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "codigo", "estado", "estadoEnum",
                            "promedioAcumulado", "creditosCursados", "creditosAprobados",
                            "persona.id",
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

    @ResponseBody
    @RequestMapping("listCursosHabiles/{alumno}")
    public DynatableResponse listCursosHabiles(@PathVariable("alumno") Long idAlumno, DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            List<AlumnoCursoCurricula> alumnoCursoCur = service.allAlumnoCursoByalumno(new Alumno(idAlumno), filter);
            for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCur) {

                arrayNode.add(JsonHelper.createJson(alumnoCursoCurricula, JsonNodeFactory.instance, new String[]{"*",
                    "curso.*",
                    "tipoCursoCurricula.*"}));
            }

            json.setData(arrayNode);
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
        model.addAttribute("documentos", service.allDocumentosPersonaNatural());
        model.addAttribute("ciclos", service.allCicloAcademico());
        model.addAttribute("situaciones", service.allSituaciones());
        model.addAttribute("alumno", alumno);
        model.addAttribute("helper", new AlumnoHelper());
        model.addAttribute("carreras", new AlumnoHelper());

        ObjectNode alumnoJson = JsonHelper.createJson(alumno, JsonNodeFactory.instance, false,
                new String[]{"*", "persona.*"});
        model.addAttribute("alumnoJson", alumnoJson.toString());

        return "academico/alumno/especial/alumnoEspecial";
    }

    @ResponseBody
    @RequestMapping("especial/existealumno")
    public JsonResponse existealumno(Alumno alumnoForm) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        try {
            Alumno alumno = service.validarAlumnoEspecial(alumnoForm);
            ObjectNode personaJson = null;
            if (alumno != null && alumno.getPersona() != null) {
                personaJson = JsonHelper.createJson(alumno.getPersona(), JsonNodeFactory.instance,
                        false, new String[]{
                            "*",
                            "tipoDocumento.*",
                            "ubicacionDomicilio.*",
                            "ubicacionNacer.*",
                            "paisNacer.*",
                            "nacionalidad.*",
                            "paisDomicilio.*"
                        });
            }
            /*  alumnoVisitanteForm.setPersona(persona);
            ObjectNode jPersona = service.validarAlumno(alumnoVisitanteForm);
            response.setData(jPersona);
             */

            response.setData(personaJson);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
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
            }
            /*else {
                service.updateAlumnoEspecial(alumno, usuario);
                response.setMessage("Alumno modificado satisfactoriamente");
            }*/

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

    @RequestMapping("habilitarCursosHabiles/{alumno}")
    public String habilitarCursosHabiles(@PathVariable("alumno") Long idAlumno, Model model, HttpSession session) {
        Alumno alumno = new Alumno(idAlumno);
        List<CursoOpcionalCurricula> cursos = service.allcursosOpcional(idAlumno);
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (CursoOpcionalCurricula curso : cursos) {
            arrayNode.add(JsonHelper.createJson(curso, JsonNodeFactory.instance, new String[]{"*",
                "curso.*",
                "tipoCursoCurricula.*"}));
        }

        model.addAttribute("cursosElectivos", arrayNode);
        model.addAttribute("alumno", JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{"*"}));
        return "academico/cursoshabiles/cursoshabiles";
    }

    @ResponseBody
    @RequestMapping("habilitar")
    public JsonResponse habilitarCursosHabiles(@RequestBody AlumnoCursoCurricula alumnoCursoCurricula, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.habilitarAlumnoCursoCurricula(alumnoCursoCurricula, ds.getUsuario());

            response.setMessage("Registro Actualizado");
            response.setSuccess(true);
            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("agregarElectivo/{alumno}")
    public JsonResponse habilitarCursosHabiles(@PathVariable("alumno") Long idAlumno, @RequestBody CursoOpcionalCurricula cursoOpcional, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.agregarAlumnoCursoCurricula(cursoOpcional, new Alumno(idAlumno));

            response.setMessage("Registro Actualizado");
            response.setSuccess(true);
            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("noCumpleRequisito")
    public JsonResponse noCumpleRequisito(@RequestBody AlumnoCursoCurricula alumnoCursoCurricula, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.deshabilitarAlumnoCursoCurricula(alumnoCursoCurricula, ds.getUsuario());
            response.setMessage("Registro Actualizado");
            response.setSuccess(true);
            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @RequestMapping("{idAlumno}/fisicoupdate")
    public String fisicoupdate(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestParam(value = "origen", required = false) String origen,
            Model model, HttpSession session) {

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
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/academico/alumno"));

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

    @RequestMapping("{idAlumno}/gomatricula")
    public String goMatricula(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestParam(value = "origen", required = false) String origen, HttpSession session) throws InterruptedException {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Usuario usuario = ds.getUsuario();
        String codigo = service.goMatricula(idAlumno, usuario);
        Parametro paramRutaMatricula = service.findParametroByEnum(ParametrosSistemasEnum.SALTO_PIVOT_MATRICULA);
        if (paramRutaMatricula != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("redirect:");
            sb.append(paramRutaMatricula.getValor());
            sb.append("/amauta/");
            sb.append(codigo);
            sb.append("/");
            sb.append(usuario.getId());
            sb.append("/");
            sb.append(ds.getCicloAcademico().getCodigo());
            sb.append("?origen=");
            sb.append(origen);
            logger.debug("********************** goIntranet {} ", sb.toString());

            AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());
            if (ambiente == AmbienteAplicacionEnum.DESA) {
                session.invalidate();
            }

            return sb.toString();
        }
        return "redirect:/";
    }

    @RequestMapping("{idAlumno}/configcursos")
    public String configcursos(@PathVariable("idAlumno") Long idAlumno,
            @RequestParam(value = "origen", required = false) String origen,
            Model model, HttpSession session) {
        Alumno alumno = service.findAlumnoFisico(idAlumno);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/academico/alumno"));
        model.addAttribute("idAlumno", alumno.getId());
        model.addAttribute("alumnoJson", JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{"*", "persona.*"}));
        model.addAttribute("rutaModulo", rutaModulo);
        return "academico/alumno/cursos/alumnoCursos";
    }

    @ResponseBody
    @RequestMapping("allCursoCurriculaAlumno/{idAlumno}")
    public DynatableResponse allCursos(DynatableFilter filter, @PathVariable("idAlumno") Long idAlumno) {
        DynatableResponse response = new DynatableResponse();
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jsonFactory);
        try {
            System.out.println("ID ALUMNO :::" + idAlumno);
            List<AlumnoCursoCurricula> cursos = service.allCursosByAlumno(new Alumno(idAlumno), filter);
            System.out.println("CURSOS:::" + cursos.size());
            cursos.forEach(curso -> {
                ObjectNode node = JsonHelper.createJson(curso, jsonFactory,
                        new String[]{
                            "*",
                            "alumno.*",
                            "cursoCurricula.*",
                            "tipoCursoCurriculaOrigen.*",
                            "tipoCursoCurricula.*",
                            "cursoOpcional.*",
                            "curso.codigo",
                            "curso.nombre",
                            "curso.tpc",
                            "curso.tipoCurso",
                            "curso.departamentoAcademico.nombre",
                            "cicloAprobado.*",});
                array.add(node);
            });
            response.setData(array);
            response.setTotal(filter.getTotal());
            response.setFiltered(filter.getFiltered());
        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCursoCiclo")
    public JsonResponse allCursoCiclo(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ArrayNode array = new ArrayNode(jsonFactory);
            List<CursoCicloAcademico> cursos = service.allCursoCiclo(nombre, ds.getCicloAcademico());
            for (CursoCicloAcademico curso : cursos) {
                ObjectNode node = JsonHelper.createJson(curso.getCurso(), jsonFactory,
                        new String[]{"id", "codigo", "tpc", "nombre", "creditos", "tipoCurso", "tipoCurricula", "departamentoAcademico.id", "departamentoAcademico.nombre"});
                array.add(node);
            }
            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveCursoCurricula")
    public JsonResponse saveCursoCurricula(@RequestBody AlumnoCursoCurricula alumnoCursoCurricula, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.saveCursoCurricula(alumnoCursoCurricula, ds.getCicloAcademico(), ds.getUsuario());
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Curso Agregado");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @RequestMapping("{idAlumno}/trasladoexterno")
    public String convalTrasladoExterno(@PathVariable("idAlumno") Long idAlumno,
            @RequestParam(value = "origen", required = false) String origen, Model model, HttpSession session) {

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            Alumno alumno = service.findAlumnoFisico(idAlumno);
            List<TramiteTraslado> listTramiteTraslado = service.allTramiteTrasladoByAlumno(alumno);
            model.addAttribute("origen", verificadorService.getOrigen(origen, "/academico/alumno"));
            model.addAttribute("listAlumnoCursoCurriculaJson", createListAlumnoCursoCurricula(service.allAlumnoCursoCurso(alumno)));
            model.addAttribute("listCursoConvalidadoJson", createListCursoConvalidado(service.alllCursoConvalidadoInTraslado(listTramiteTraslado)));
            model.addAttribute("cicloJson", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{"id", "descripcion"}));
            model.addAttribute("alumnoJson", createAlumnoJson(alumno));
            model.addAttribute("rutaModulo", rutaModulo);
            model.addAttribute("listTramiteTrasladoJson", createListTramiteTrasladoJson(listTramiteTraslado));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "academico/alumno/trasladoexterno/trasladoexterno";
    }

    @ResponseBody
    @RequestMapping("allCurso")
    public JsonResponse allCurso(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Curso> cursos = service.allCurso(nombre);
            for (Curso curso : cursos) {
                ObjectNode json = JsonHelper.createJson(curso, jsonFactory, new String[]{"*"});

                jsonList.add(json);
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
    @RequestMapping("saveListCursoConvalidado")
    public JsonResponse saveListCursoConvalidado(@RequestBody TrasladoBean trasladoBean, HttpSession session) {
        JsonResponse response = new JsonResponse();
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jsonFactory);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<CursoConvalidado> listCursoConvalidado = service.saveListCursoConvalidado(trasladoBean, ds.getUsuario(), ds.getCicloAcademico());
            avanceCurricularService.generarAvanceCurricularByAlumno(trasladoBean.getAlumno(), ds);
            promedioService.calulcarSituacionAcademica(trasladoBean.getAlumno(), ds);
            response.setData(createListCursoConvalidado(listCursoConvalidado));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Los cursos fueron registrados satisfactoriamente.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("verificarTramiteTraslado")
    public JsonResponse verificarTramiteTraslado(@RequestBody Alumno alumno, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.verificarTramiteTraslado(alumno);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveAccesoEspecial")
    public JsonResponse saveAccesoEspecial(@RequestBody AccesoEspecialBean accesoEspecialBean, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.saveAccesoEspecial(accesoEspecialBean);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se asignó el acceso especial al alumno satisfactoriamente.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("findUsuario")
    public JsonResponse findUsuario(@RequestBody Persona persona, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            response.setSuccess(Boolean.TRUE);
            Usuario usuario = service.findUsuarioByPersona(persona);
            response.setData(JsonHelper.createJson(usuario, JsonNodeFactory.instance, new String[]{"id", "userDni", "persona.id", "persona.numeroDocIdentidad"}));
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("{idAlumno}/goMaipi")
    public String goMaipi(@PathVariable("idAlumno") Long idAlumno, @RequestParam(value = "origen", required = false) String origen, Model model, HttpSession session) throws InterruptedException {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Usuario usuario = ds.getUsuario();
        TokenIngresante token = service.goMaipi(idAlumno, usuario);
        Parametro paramRutaMatricula = service.findParametroByEnum(ParametrosSistemasEnum.SALTO_PIVOT_INTRA);
        if (paramRutaMatricula != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("redirect:");
            sb.append(paramRutaMatricula.getValor());
            sb.append("/mapache/");
            sb.append(token.getValor());
//            sb.append("?user=").append(ds.getUsuario().getGoogle());
//            sb.append("?pathh=").append(RutaInicioEnum.FICHA_ING.name());

            logger.debug("********************** goMaipi {} ", sb.toString());

            AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());
            if (ambiente == AmbienteAplicacionEnum.DESA) {
                session.invalidate();
            }
            return sb.toString();
        }

        return "redirect:/";
    }

    private ArrayNode createListAlumnoCursoCurricula(List<AlumnoCursoCurricula> listAlumnoCursoCurricula) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AlumnoCursoCurricula item : listAlumnoCursoCurricula) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "id", "numeroCiclo",
                "tipoCursoCurricula.id", "tipoCursoCurricula.nombre", "tipoCursoCurricula.codigo",
                "curso.id", "curso.codigo", "curso.nombre", "curso.creditos", "curso.tipoCurso"});
            array.add(node);
        }
        return array;
    }

    private ArrayNode createListCursoConvalidado(List<CursoConvalidado> listCursoConvalidado) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (CursoConvalidado item : listCursoConvalidado) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "id", "nota", "creditos", "fechaRegistro", "curso.id", "curso.nombre", "curso.codigo", "curso.tpc", "curso.creditos", "curso.tipoCurso", "tramiteTraslado.*", "tramiteTraslado.cicloAcademico.*"});
            array.add(node);
        }
        return array;
    }

    private ObjectNode createAlumnoJson(Alumno alumno) {
        return JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{
            "*", "modalidadEstudio.id", "modalidadEstudio.codigo",
            "carrera.id", "carrera.nombre", "carrera.facultad.id", "carrera.facultad.nombre",
            "persona.*", "persona.tipoDocumento.id", "persona.tipoDocumento.simbolo"
        });
    }

    private ArrayNode createListTramiteTrasladoJson(List<TramiteTraslado> listTramiteTraslado) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (TramiteTraslado item : listTramiteTraslado) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "id", "estado", "tipoTraslado", "tramite.id",
                "tramite.alumno.persona.id", "cicloAcademico.*",
                "resolucion.id", "resolucion.fecha", "resolucion.estado",
                "resolucion.serie", "resolucion.numero", "resolucion.rutaUrl", "resolucion.fechaRegistro",
                "resolucion.userRegistro.persona.apellidosNombres",
                "resolucion.tipoResolucion.id", "resolucion.tipoResolucion.nombre",
                "resolucion.oficina.id", "resolucion.oficina.codigo", "resolucion.oficina.nombre"
            });
            array.add(node);
        }
        return array;
    }

}
