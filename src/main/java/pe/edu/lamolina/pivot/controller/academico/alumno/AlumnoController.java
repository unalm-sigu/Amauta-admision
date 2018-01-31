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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.RolEnum;
import static pe.edu.lamolina.model.enums.RolEnum.FAC;
import static pe.edu.lamolina.model.enums.RolEnum.MOD;
import static pe.edu.lamolina.model.enums.RolEnum.TODO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class AlumnoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoService service;
    @Autowired
    AlumnoEspecialService especialService;
    @Autowired
    AlumnoFisicoService fisicoService;
    @Autowired
    AlumnoVisitanteService visitanteService;

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
                node.put("cicloIngreso", alumn.getCicloIngreso().getDescripcion());
                node.put("cicloActivo", alumn.getCicloActivo().getDescripcion());
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

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", especialService.allDocumentos());
        model.addAttribute("ciclos", especialService.allCiclos());
        model.addAttribute("situaciones", especialService.allSituaciones());

        return "academico/alumno/especial/alumnoEspecial";
    }

    @ResponseBody
    @RequestMapping("saveAlumnoEspecial")
    public JsonResponse saveAlumnoEspecial(Alumno alumno, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            response.setMessage("Usuario modificado satisfactoriamente");
            if (alumno.getId() == null) {
                response.setMessage("Usuario creado satisfactoriamente");
            }
            especialService.saveAlumno(alumno, ds.getUsuario());

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

        List<ModalidadEstudio> modalidades = fisicoService.allModalidadEstudioByCodigos(codigos);

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", especialService.allDocumentos());
        model.addAttribute("ciclos", especialService.allCiclos());
        model.addAttribute("modalidades", modalidades);

        return "academico/alumno/fisico/alumnoFisico";
    }

    @ResponseBody
    @RequestMapping("saveAlumnoFisico")
    public JsonResponse saveAlumnoFisico(Alumno alumno, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            response.setMessage("Usuario modificado satisfactoriamente");
            if (alumno.getId() == null) {
                response.setMessage("Usuario creado satisfactoriamente");
            }
            fisicoService.saveAlumno(alumno, ds.getUsuario());

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

    @RequestMapping("visitante")
    public String alumnoVisitante(Model model, HttpSession session) {

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", especialService.allDocumentos());
        model.addAttribute("ciclos", especialService.allCiclos());
        model.addAttribute("situaciones", especialService.allSituaciones());

        return "academico/alumno/visitante/alumnoVisitante";
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
        model.addAttribute("documentos", especialService.allDocumentos());
        model.addAttribute("ciclos", especialService.allCiclos());

        return "academico/alumno/alumnoHorario";
    }

    @RequestMapping("{alumno}/historia/origen/matriculable")
    public String alumnoHistoria(@PathVariable("persona") Long idPersona,
            Model model, HttpSession session) {

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", especialService.allDocumentos());
        model.addAttribute("ciclos", especialService.allCiclos());

        return "academico/alumno/alumnoHistoria";
    }

    @RequestMapping("{alumno}/avance/origen/matriculable")
    public String alumnoAvance(@PathVariable("persona") Long idPersona,
            Model model, HttpSession session) {

        model.addAttribute("persona", new Persona());
        model.addAttribute("documentos", especialService.allDocumentos());
        model.addAttribute("ciclos", especialService.allCiclos());

        return "academico/alumno/alumnoAvance";
    }

    @RequestMapping("{idAlumno}/infoacademico")
    public String infoAcademico(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("Ciclo academico : --- > {}" + ds.getCicloAcademico().getId());
        Alumno alumno = service.findAlumno(new Alumno(idAlumno),ds.getCicloAcademico());                
        model.addAttribute("datoAlumno", alumno.toJsonInfoAcademico());
    

        return "academico/alumno/infoAcademico";
    }
}
