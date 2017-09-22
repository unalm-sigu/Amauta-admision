package pe.edu.lamolina.pivot.controller.general.persona;

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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/persona")
public class PersonaController {

    @Autowired
    PersonaService service;

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
    public String index(Model model) {

        return "general/persona/persona";
    }

    @ResponseBody
    @RequestMapping("allPersona")
    public DynatableResponse allUsuarios(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            FotoHelper helper = new FotoHelper();
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Persona> personas = service.allByDynatable(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Persona persona : personas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", persona.getId());
                node.put("nombre", persona.getApellidosNombres());
                node.put("simbolo", persona.getTipoDocumento().getSimbolo());
                node.put("tipodocid", persona.getTipoDocumento().getId());
                node.put("documento", persona.getNumeroDocIdentidad());
                node.put("telefono", persona.getTelefono());
                node.put("celular", persona.getCelular());
                node.put("email", persona.getEmail());
                node.put("emailEmpresa", persona.getEmailCompania());
                node.put("rutaFoto", helper.getRutaFoto(null, persona.getSexo()));

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

    @RequestMapping("infoPersona")
    public String infoPersona(@RequestParam("persona") Long idUsuario, Model model) {
        model.addAttribute("persona", service.find(new Persona(idUsuario)));
        model.addAttribute("fotoHelper", new FotoHelper());
        return "general/persona/infoPersona";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session, @RequestParam("origen") String origen) {
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("persona", new Persona());
        model.addAttribute("origen", origen);
        return "general/persona/personaForm";

    }

    @RequestMapping("{persona}/edicion")
    public String editUsuario(
            @PathVariable("persona") Long idPersona,
            @RequestParam("origen") String origen, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Persona persona = service.find(new Persona(idPersona));

        model.addAttribute("persona", persona);
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("fotoHelper", new FotoHelper());
        model.addAttribute("origen", origen);
        return "general/persona/personaForm";
    }


    @ResponseBody
    @RequestMapping("savePersona")
    public JsonResponse savePersona(Persona persona, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            response.setMessage("Usuario modificado satisfactoriamente");
            if (persona.getId() == null) {
                response.setMessage("Usuario creado satisfactoriamente");
            }
            service.savePersona(persona, ds);

            response.setSuccess(true);
            response.setData(node);

            node.put("personaId", persona.getId());
            node.put("nombreCompleto", persona.getApellidosNombres());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("findPersona")
    public JsonResponse updateUsuario(Persona personaTmp, HttpSession session) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Persona persona = service.findPersona(personaTmp);

            node.put("id", persona.getId());
            node.put("paterno", persona.getPaterno());
            node.put("materno", persona.getMaterno());
            node.put("nombres", persona.getNombres());
            node.put("sexo", persona.getSexo());
            node.put("fechaNacer", (persona.getFechaNacer() == null) ? "" : new DateTime(persona.getFechaNacer()).toString("dd/MM/yyyy"));
            node.put("domicilio", persona.getUbicacionDomicilio() != null ? persona.getUbicacionDomicilio().getNombre() : "");
            node.put("email", persona.getEmail());
            node.put("emailEmpresa", persona.getEmailCompania());
            node.put("celular", persona.getCelular());
            node.put("telefono", persona.getTelefono());

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
    @RequestMapping("validarEmail")
    public JsonResponse validarEmail(@RequestParam("email") String email, @RequestParam("persona") Long idPersona) {

        JsonResponse response = new JsonResponse();
        try {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String msg = service.validarEmailByPersona(email, new Persona(idPersona));

            node.put("respuesta", msg);
            response.setData(node);
            response.setSuccess(StringUtils.isEmpty(msg));

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
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String msg = service.validarEmailEmpresaByPersona(email, new Persona(idPersona));
            node.put("respuesta", msg);
            response.setData(node);
            response.setSuccess(StringUtils.isEmpty(msg));

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
