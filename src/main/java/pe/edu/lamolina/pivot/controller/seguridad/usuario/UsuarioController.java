package pe.edu.lamolina.pivot.controller.seguridad.usuario;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
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
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("seguridad/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService service;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        //dataBinder.setDisallowedFields("id");

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

        List<Rol> userRoles = service.listRol();
        model.addAttribute("roles", userRoles);

        return "seguridad/usuario/usuario";
    }

    @ResponseBody
    @RequestMapping("allUsuarios")
    public DynatableResponse allUsuarios(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Usuario> usuarios = service.allByDynatable(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            FotoHelper helper = new FotoHelper();

            for (Usuario usuario : usuarios) {
                Persona persona = usuario.getPersona();
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                String roles = "";
                List<UsuarioRol> userRoles = usuario.getUsuarioRol();
                for (UsuarioRol userRol : userRoles) {
                    roles += roles.equals("") ? "" : "::::";
                    roles += userRol.getRol().getNombre();
                }

                node.put("id", usuario.getId());
                node.put("nombre", persona.getApellidosNombres());
                node.put("simbolo", persona.getTipoDocumento().getSimbolo());
                node.put("tipoDocId", persona.getTipoDocumento().getId());
                node.put("documento", persona.getNumeroDocIdentidad());
                node.put("telefono", persona.getTelefono());
                node.put("celular", persona.getCelular());
                node.put("email", persona.getEmail());
                node.put("emailCompania", persona.getEmailCompania());
                node.put("estado", usuario.getEstadoEnum().name());
                node.put("roles", roles);
                node.put("estadoEnum", usuario.getEstadoEnum().getValue());
                node.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));

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

    @RequestMapping("infoUsuario")
    public String infoUsuario(@RequestParam("usuario") Long idUsuario, Model model) {
        model.addAttribute("usuario", service.findUsuario(new Usuario(idUsuario)));
        model.addAttribute("fotoHelper", new FotoHelper());
        return "seguridad/usuario/infoUsuario";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session, @RequestParam("origen") String origen) {
        byte[] decoded = Base64.getDecoder().decode(origen);
        String decodedString = new String(decoded);
        System.out.println(decodedString);
        
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("origen", decodedString);
        return "seguridad/usuario/usuarioForm";

    }

    @RequestMapping("{usuario}/edicion")
    public String editUsuario(
            @PathVariable("usuario") Long idUsuario,
            @RequestParam("origen") String origen, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Usuario usuario = service.findUsuario(new Usuario(idUsuario));

        model.addAttribute("usuario", usuario);
        model.addAttribute("rolesUsuario", service.allRolesByUser(usuario));
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("origen", origen);
        return "seguridad/usuario/usuarioForm";
    }

    @RequestMapping("rolesUsuario")
    public String showRolesUsuario(@RequestParam("usuario") Long idUsuario, Model model, HttpSession session) {
        List<UsuarioRol> rolesUsuario = service.allRolesByUser(new Usuario(idUsuario));
        model.addAttribute("rolesUsuario", rolesUsuario);
        return "seguridad/usuario/rolesUsuarios";
    }

    @ResponseBody
    @RequestMapping("deshabilitarPerfil")
    public JsonResponse deshabilitarPerfil(@RequestParam("userRol") Long idUsuarioRol, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deshabilitarPerfil(new UsuarioRol(idUsuarioRol), ds);
            response.setMessage("Registro actualizado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveUsuario")
    public JsonResponse saveUsuario(Usuario usuario, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            response.setMessage("Usuario modificado satisfactoriamente");
            if (usuario.getId() == null) {
                response.setMessage("Usuario creado satisfactoriamente");
            }
            service.saveUsuario(usuario, ds);

            response.setSuccess(true);
            response.setData(node);

            node.put("usuarioId", usuario.getId());
            node.put("personaId", usuario.getPersona().getId());
            node.put("nombreCompleto", usuario.getPersona().getApellidosNombres());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("findPersona")
    public JsonResponse updateUsuario(
            @RequestParam("tipoDNI") Long idTipoDNI,
            @RequestParam("numeroDNI") String numeroDNI, HttpSession session) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Persona personaTmp = new Persona(new TipoDocIdentidad(idTipoDNI), numeroDNI);
            Persona persona = service.findPersona(personaTmp);

            node.put("id", persona.getId());
            node.put("paterno", persona.getPaterno());
            node.put("materno", persona.getMaterno());
            node.put("nombres", persona.getNombres());
            node.put("sexo", persona.getSexo());
            node.put("fechaNacer", (persona.getFechaNacer() == null) ? "" : new DateTime(persona.getFechaNacer()).toString("dd/MM/yyyy"));
            node.put("direccion", persona.getDireccion());
            node.put("email", persona.getEmail());
            node.put("emailCompania", persona.getEmailCompania());
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
    @RequestMapping("desactivaUsuario")
    public JsonResponse desactivaUsuario(@RequestParam("usuario") Long idUsuario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.desactivaUsuario(new Usuario(idUsuario), ds);
            response.setMessage("Usuario deshabilitado satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activaUsuario")
    public JsonResponse activaUsuario(@RequestParam("usuario") Long idUsuario, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.activaUsuario(new Usuario(idUsuario), ds);
            response.setMessage("Usuario habilitado satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("savePerfil")
    public JsonResponse save(UsuarioRol userRol, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.saveUserRol(userRol, ds);
            response.setMessage("Perfíl asignado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping(value = "usuarioRol", method = RequestMethod.GET)
    public String index(@RequestParam("usuario") Long idUsuario, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Usuario user = service.findUsuario(new Usuario(idUsuario));
        model.addAttribute("usuario", user);
        model.addAttribute("roles", service.allRolesWithoutUser(user));

        return "seguridad/usuario/usuarioRol";
    }

    @ResponseBody
    @RequestMapping("validarEmailCompania")
    public JsonResponse validarEmailCompania(@RequestParam("email") String email, @RequestParam("persona") Long idPersona) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String msg = service.validarEmailCompaniaByPersona(email, new Persona(idPersona));
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

}
