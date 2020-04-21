package pe.edu.lamolina.pivot.controller.general.personaperfil;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.PerfilEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@SessionAttributes("personaPerfil")
@RequestMapping("general/personaperfil")
public class PersonaPerfilController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PersonaPerfilService perfilService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");

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

    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("perfiles", perfilService.allPerfilCompania());
        model.addAttribute("companias", perfilService.allCompania());

        return "general/personaperfil/perfil";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, Model model) {

        DynatableResponse json = new DynatableResponse();

        try {

            List<PersonaCargo> personasPerfiles = perfilService.allPersonasPefiles(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (PersonaCargo pp : personasPerfiles) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", pp.getId());
                node.put("perfilNombre", pp.getPerfilCompania().getNombre());
                node.put("personaNombre", pp.getPersona().getApellidosNombres());

                node.put("estadoPersonaPerfil", EstadoEnum.getNombre(pp.getEstado()));
                node.put("esActivo", pp.isEstadoLike(PerfilEstadoEnum.ACT));
                node.put("esInactivo", pp.isEstadoLike(PerfilEstadoEnum.INA));
                node.put("esCreado", pp.isEstadoLike(PerfilEstadoEnum.CRE));
                node.put("fechaIngreso", pp.getFechaInicio() != null ? "" : new DateTime(pp.getFechaInicio()).toString("dd/MM/yyyy"));

                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
            logger.debug("Error", e);
        }
        return json;
    }

    @RequestMapping("nuevo")
    public String nuevo(RedirectAttributes redirectAttr, Model model) {

        PersonaCargo personaPerfil = new PersonaCargo();
        personaPerfil.setPersona(new Persona());
        personaPerfil.setPerfilCompania(new PerfilCompania());
        personaPerfil.setCompania(new Compania());
        personaPerfil.setOficina(new Oficina());

        model.addAttribute("personaPerfil", personaPerfil);

        model.addAttribute("perfiles", perfilService.allPerfilCompania());
        model.addAttribute("companias", perfilService.allCompania());

        return "general/personaperfil/perfilForm";
    }

    @RequestMapping("{perfil}/update")
    public String update(@PathVariable("perfil") Long perfil, RedirectAttributes redirectAttr, Model model, HttpSession session) {

        PersonaCargo personaPerfil = perfilService.findPersonaPerfil(new PersonaCargo(perfil));

        if (personaPerfil.getOficina() == null || personaPerfil.getOficina().getId() == null) {
            personaPerfil.setOficina(new Oficina());
        }

        model.addAttribute("personaPerfil", personaPerfil);

        model.addAttribute("perfiles", perfilService.allPerfilCompania());
        model.addAttribute("companias", perfilService.allCompania());

        return "general/personaperfil/perfilForm";
    }

    @RequestMapping("save")
    public String save(@ModelAttribute("personaPerfil") PersonaCargo personaPerfil, RedirectAttributes redirectAttr, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        logger.debug("Persona {}", personaPerfil.getPersona().getId());
        logger.debug("Cargo {}", personaPerfil.getPerfilCompania().getId());

        if (personaPerfil.getId() == null) {
            perfilService.save(personaPerfil, ds.getUsuario());

            Notificaciones.crearMsg(GlobalMessages.CREATED, redirectAttr);

        } else {
            perfilService.update(personaPerfil);
            Notificaciones.crearMsg(GlobalMessages.UPDATED, redirectAttr);
        }

        return "redirect:/general/personaperfil";
    }

    @ResponseBody
    @RequestMapping("activate")
    public JsonResponse activate(@RequestParam("personaPerfil") Long personaPerfil) {

        JsonResponse response = new JsonResponse();

        try {

            perfilService.activate(personaPerfil);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("desactivar")
    public JsonResponse desactivar(@RequestParam("personaPerfil") Long personaPerfil) {

        JsonResponse response = new JsonResponse();

        try {

            perfilService.desactivar(personaPerfil);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("searchPersona")
    public JsonResponse searchPersona(@RequestParam("nombre") String buscar) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        logger.debug("buscar {}", buscar);
        try {

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Persona> personas = perfilService.allPersonasByNombre(buscar);

            int loop = 0;
            for (Persona persona : personas) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", persona.getId());
                json.put("nombre", persona.getApellidosNombres());
                json.put("row", loop++);

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

}
