package pe.edu.lamolina.amauta.controller.academico.profesor.informacionprofesor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.*;
import pe.edu.lamolina.amauta.controller.academico.profesor.ProfesorService;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto.HorarioDocenteDTO;
import pe.edu.lamolina.amauta.controller.reporte.view.HorarioDocentePDF;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.EmpresaEtiquetada;
import pe.edu.lamolina.model.general.PersonaCuentaBancaria;

@Controller
@RequestMapping("academico/profesor")
public class InformacionProfesorController {

    @Autowired
    InformacionProfesorService service;
    @Autowired
    VerificadorService verificadorService;
    @Autowired
    ProfesorService profesorService;

    @Autowired
    HorarioDocentePDF horarioDocentePDF;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

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

    @RequestMapping("{docente}/informacionacademica")
    public String informacionacademica(@PathVariable("docente") Long idDocente, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();
        boolean isRevisorDocente = ds.getRoles().stream()
                .anyMatch(rol -> "JEFE_DPTO_ACA".equals(rol.getCodigo()) || "SOPORTE_TECNICO_DERA".equals(rol.getCodigo()));
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        ObjectNode cicloJson = JaneHelper.from(cicloAcademico).json();

        ArrayNode horasJson = new ArrayNode(JsonNodeFactory.instance);
        List<Hora> horas = service.allHoras();
        for (Hora hora : horas) {
            horasJson.add(JsonHelper.createJson(hora, JsonNodeFactory.instance, true, new String[]{"*"}));
        }

        Docente docente = service.findDocente(new Docente(idDocente));
        List<EmpresaEtiquetada> bancos = service.allBancos();
        List<PersonaCuentaBancaria> cuentasBancarias = service.allCtasBancarias(docente.getPersona());
        ObjectNode personaJson = JaneHelper.from(docente.getPersona()).only("id,nombreCompleto").json();

        model.addAttribute("horasBD", horasJson.toString());
        model.addAttribute("docente", new Docente(idDocente));
        model.addAttribute("tiposDocIdentidad", service.allDocumentos());
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
        model.addAttribute("categorias", service.allCategorias());
        model.addAttribute("situaciones", service.allSituaciones());
        model.addAttribute("dedicaciones", service.allDedicaciones());
        model.addAttribute("personaJson", personaJson);
        model.addAttribute("bancos", createBancosJson(bancos));
        model.addAttribute("cuentasBancarias", createCtasBancariasJson(cuentasBancarias));
        model.addAttribute("ciclo", cicloJson.toString());
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("esOperadorGastoEPG", verificadorService.isOperadorGastoPosgrado(ds));
        model.addAttribute("isRevisorDocente", isRevisorDocente);

        return "academico/profesor/informacion/informacion";
    }

    private ArrayNode createCtasBancariasJson(List<PersonaCuentaBancaria> ctasBancos) {
        return JaneHelper
                .from(ctasBancos)
                .join("banco", "id")
                .join("banco.empresa", "id,razonSocial,nombreComercial,numeroDocIdentidad")
                .join("banco.empresa.tipoDocIdentidad", "simbolo")
                .join("banco.etiqueta", "id,codigo,nombre")
                .array();
    }

    private ArrayNode createBancosJson(List<EmpresaEtiquetada> bancos) {
        return JaneHelper
                .from(bancos)
                .join("empresa", "id,razonSocial,nombreComercial,numeroDocIdentidad")
                .join("empresa.tipoDocIdentidad", "simbolo")
                .join("etiqueta", "id,codigo,nombre")
                .array();
    }

    @ResponseBody
    @RequestMapping("find")
    public JsonResponse find(HttpSession session, Docente profesorForm) {

        JsonResponse response = new JsonResponse();

        try {

            Docente docente = service.findDocente(profesorForm);
            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

            ObjectNode jDocente = JsonHelper.createJson(docente, jsonFactory, true, new String[]{
                "*",
                "modalidadEstudio.*",
                "departamentoAcademico.*",
                "persona.id",
                "persona.fechaNacer",
                "persona.nombreCompleto",
                "persona.nombres",
                "persona.paterno",
                "persona.materno",
                "persona.sexo",
                "persona.rutaFoto",
                "persona.foto",
                "persona.fullRutaFotoTemporal",
                "persona.email",
                "persona.celular",
                "persona.telefono",
                "persona.numeroDocIdentidad",
                "persona.direccion",
                "persona.emailCompania",
                "persona.tipoDocumento.*",
                "persona.ubicacionNacer.*",
                "persona.paisNacer.*",
                "persona.nacionalidad.*",
                "persona.paisDomicilio.*",
                "persona.ubicacionDomicilio.*"
            });

            response.setData(jDocente);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("validarEmail")
    public JsonResponse validarEmail(@RequestParam("email") String email, @RequestParam("persona") Long idPersona) {

        JsonResponse response = new JsonResponse();
        try {

            String msg = service.validarEmailByPersona(email, new Persona(idPersona));
            response.setMessage(msg);
            response.setSuccess(Strings.isNullOrEmpty(msg));

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("validarEmailEmpresa")
    public JsonResponse validarEmailEmpresa(@RequestParam("email") String email, @RequestParam("persona") Long idPersona) {
        JsonResponse response = new JsonResponse();
        try {

            String msg = service.validarEmailEmpresaByPersona(email, new Persona(idPersona));
            response.setMessage(msg);
            response.setSuccess(Strings.isNullOrEmpty(msg));

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idDocente}/updateDocentePersona")
    public JsonResponse updateDocentePersona(@PathVariable("idDocente") Long idDocente, @RequestBody Persona persona, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.updateDocentePersona(persona, idDocente, ds);

            response.setMessage("Se actualizó el docente satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idPersona}/allCuentasBancarias")
    public JsonResponse allCuentasBancarias(@PathVariable Long idPersona, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Persona persona = service.findPersona(new Persona(idPersona));
            List<PersonaCuentaBancaria> cuentasBancarias = service.allCtasBancarias(persona);

            response.setData(createCtasBancariasJson(cuentasBancarias));
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("saveCtaBanco")
    public JsonResponse saveCtaBanco(@RequestBody PersonaCuentaBancaria cuentaBanco, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.saveCtaBanco(cuentaBanco, ds);

            response.setSuccess(true);
            response.setMessage("Se agregó satisfactoriamente la cuenta bancaria");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("deleteCtaBanco")
    public JsonResponse deleteCtaBanco(@RequestBody PersonaCuentaBancaria cuentaBanco, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.deleteCtaBanco(cuentaBanco, ds);

            response.setSuccess(true);
            response.setMessage("Se eliminó satisfactoriamente la cuenta bancaria");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("activarCtaBanco")
    public JsonResponse activarCtaBanco(@RequestBody PersonaCuentaBancaria cuentaBanco, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.activarCtaBanco(cuentaBanco, ds);

            response.setSuccess(true);
            response.setMessage("Se activar satisfactoriamente la cuenta bancaria");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("reporteHorarioDocente")
    public ModelAndView reporteProgramacion(@RequestParam("id") String id, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<HorarioDocenteDTO> horarioDocenteDTO = service.horarioDocente(ds.getCicloAcademico(), id);

        model.addAttribute("horarioDocente", horarioDocenteDTO);
//          List<HorarioDocenteDTO> horarioDocenteDTO = service.horarioDocentes(ds.getCicloAcademico());
//
        model.addAttribute("horarioDocente", horarioDocenteDTO);
        return new ModelAndView(horarioDocentePDF);
    }

}
