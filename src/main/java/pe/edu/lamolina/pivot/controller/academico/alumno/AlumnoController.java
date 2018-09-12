package pe.edu.lamolina.pivot.controller.academico.alumno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class AlumnoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoService service;

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
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        verificadorService.revisarPermiso(request, ds);
        model.addAttribute("resumen", service.findResumen());

        return "academico/alumno/alumno";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);
            List<Alumno> alumnos = null;

            if (facultades.isEmpty()) {
                alumnos = service.allAlumnosByCicloDynatable(filter, ds.getCarreras());
            } else {
                alumnos = service.allAlumnosByFacultadDynatable(filter, facultades);
            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Alumno alumn : alumnos) {
                ObjectNode node = JsonHelper.createJson(alumn, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "codigo", "estado", "estadoEnum",
                            "promedioAcumulado", "creditosCursados", "creditosAprobados",
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
        model.addAttribute("origen", getOrigen(origen));

        return "academico/alumno/fisico/alumnoFisico";
    }

    private String getOrigen(String origen) {
        if (StringUtils.isEmpty(origen)) {
            return "/academico/alumno";
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
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
    public String goMatricula(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        String codigo = service.goMatricula(idAlumno);

        session.invalidate();
        return "redirect:http://localhost:9977/amauta/" + codigo;
    }

}
