package pe.edu.lamolina.pivot.controller.academico.profesor.informacionprofesor;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/profesor")
public class InformacionProfesorController {

    @Autowired
    InformacionProfesorService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();

        model.addAttribute("docente", new Docente(idDocente));
        model.addAttribute("tiposDocIdentidad", service.allDocumentos());
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
        model.addAttribute("categorias", service.allCategorias());
        model.addAttribute("situaciones", service.allSituaciones());
        model.addAttribute("dedicaciones", service.allDedicaciones());
        return "academico/profesor/informacion/informacion";
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

}
