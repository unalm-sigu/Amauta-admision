package pe.edu.lamolina.amauta.security.oauth;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.academico.ciclo.CicloAcademicoService;
import pe.edu.lamolina.amauta.controller.restcontroller.RestPivotService;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.general.ParametroDAO;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;

@Slf4j
@Controller
@RequestMapping("/")
public class OAuthController {

    @Autowired
    OAuthServiceProvider serviceProvider;
    @Autowired
    CicloAcademicoService cicloAcademicoService;
    @Autowired
    RestPivotService service;
    @Autowired
    DespliegueConfig despliegueConfig;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    @Autowired
    ParametroDAO parametroDAO;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/")
    public String index(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        if (ds != null) {
            return "redirect:/route66";
        }

        return "security/login";
    }

    @GetMapping("login")
    public String loginGoogle() {
        return "security/login";
    }

    @GetMapping("callback")
    public String callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String scope,
            Model model,
            HttpSession session,
            HttpServletRequest servlet) throws IOException {

        log.info("[callback] code={} error={}", code, error);
        log.info("[callback] scope={}", scope);

        // Manejo de errores de autenticación
        if (error != null) {
            log.error("[callback] Error during OAuth2 authentication: {}", error);
            return "security/nologin";
        }

        // Verificar si el código de autorización está presente
        if (code == null) {
            log.warn("[callback] No authorization code received");
            return "security/nologin";
        }

        try {
            // Obtener el registro del cliente (Google en este caso)
            String registrationId;
            if (scope != null) {
                registrationId = scope.contains("google") ? "google" : null;
            } else {
                registrationId = "microsoft";
            }

            Assert.isNotNull(registrationId, "No se pudo ubicar el proveedor del Authentication");
            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

            // Obtener el token de acceso
            String tokenEndpoint = clientRegistration.getProviderDetails().getTokenUri();
            log.info("[callback] tokenEndpoint={}", tokenEndpoint);
            log.info("[callback] redirectUriTemplate={}", clientRegistration.getRedirectUriTemplate());

            // Preparar los parámetros para la solicitud del token
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("grant_type", "authorization_code");
            parameters.add("code", code);
            parameters.add("redirect_uri", clientRegistration.getRedirectUriTemplate());
            parameters.add("client_id", clientRegistration.getClientId());
            parameters.add("client_secret", clientRegistration.getClientSecret());

            // Realizar la solicitud del token
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenEndpoint,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Extraer el token de acceso
            Map<String, Object> tokenResponse = response.getBody();
            String accessToken = (String) tokenResponse.get("access_token");

            // Guardar el token en la sesión
            session.setAttribute(OAuthConstant.ACCESS_TOKEN, accessToken);

            // Obtener información del usuario usando el token
            String userInfoEndpoint = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(accessToken);
            HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                    userInfoEndpoint,
                    HttpMethod.GET,
                    userInfoRequest,
                    Map.class
            );

            Map<String, Object> userInfo = userInfoResponse.getBody();

            // Registrar el éxito del login
            log.info("[callback] Successful login for user: {}", userInfo.get("email"));
            serviceProvider.loginManually((String) userInfo.get("email"), session, servlet);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ds.setProvider(registrationId);
            session.setAttribute(GlobalConstantine.SESSION_USUARIO, ds);

        } catch (PhobosException e) {
            log.error("[callback] Error processing callback", e);

            session.removeAttribute(OAuthConstant.ACCESS_TOKEN);
            return "security/nologin";
        }

        return "redirect:/route66";
    }

    @GetMapping("bunnies/{email:.*}")
    public String loginGoogle(@PathVariable String email, HttpSession session, Model model, HttpServletRequest servlet) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            if (ds == null && !despliegueConfig.getLagunas()) {
                return "redirect:/";
            }

            if (ds != null && !despliegueConfig.getLagunas()) {
                Rol rol = ds.getRolActivo();
                if (rol == null) {
                    return "redirect:/";
                }

                boolean esIoera = false;
                boolean esLogueoDocente = false;
                List<Rol> roles = ds.getRoles();
                for (Rol role : roles) {
                    if (role.getCodigoEnum() == RolEnum.IOREA) {
                        esIoera = true;
                    }
                    if (role.getCodigoEnum() == RolEnum.LOGUEO_DOCENTE) {
                        esLogueoDocente = true;
                    }
                }

                if (!esIoera && !esLogueoDocente) {
                    return "redirect:/";
                }
            }

            serviceProvider.loginManually(email, session, servlet);
            ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            serviceProvider.createLogJson(ds, session);

            return "redirect:/route66";
        } catch (PhobosException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getLocalizedMessage());
            return "security/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Sus credenciales no tienen acceso al sistema.");
            return "security/login";
        }
    }

    @RequestMapping("route66")
    public String route66(HttpSession session, Model model) {
        log.debug("route66");
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        if (ds == null) {
            log.debug("return redirect:/login");
            return "redirect:/login";
        }

        log.debug("Usuario tiene: {} roles y activo: {}", ds.getRolesMain().size(), ds.getRolActivo());
        if (ds.getRolesMain().size() > 1 && ds.getRolActivo() == null) {
            log.debug("return security/rolland");
            return "security/rolland";
        }
        try {
            if (ds.getRolesMain().size() == 1) {
                Rol rolActivo = ds.getRolesMain().get(0);
                log.debug("rol-activo: {} {}", rolActivo.getId(), rolActivo.getNombre());
                serviceProvider.asignarRolActivo(rolActivo, ds, session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ds.getCicloAcademico() == null) {

            List<CicloAcademico> cicloAcademicos = serviceProvider.findCiclosVisibles();
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (CicloAcademico cicloAcademico : cicloAcademicos) {
                arrayNode.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"}));
            }
            model.addAttribute("cicloAcademico", arrayNode);
            return "academico/cicloacademico/cicloland";

        }
        return this.getRedirect(ds, session);
    }

    @ResponseBody
    @RequestMapping(value = "rolland", method = RequestMethod.POST)
    public void rolesLanding(HttpSession session, @RequestParam("rol") Long rol) throws Exception {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Rol asignar = ds.getMapRoles().get(rol);
            log.debug("rolland");
            serviceProvider.asignarRolActivo(asignar, ds, session);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("changerol")
    public String changeRol(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setRolActivo(null);

        session.setAttribute(GlobalConstantine.SESSION_USUARIO, ds);

        return "security/rolland";
    }

    private String getRedirect(DataSessionPivot ds, HttpSession session) {

        String ruta = findRuta(ds.getMenu());
        String redirect = "redirect:/logout";
        String urlServerExterno = "http://maipi.albatross.pe/login";
        //String urlServerExterno = "http://localhost:9800/lagunas/" + ds.getUsuario().getUsuario();
        if (ruta.equals("")) {
            return redirect;
        }

        switch (ds.getRolActivo().getCodigo()) {

            case "ALU":
                session.invalidate();
                redirect = "redirect:" + urlServerExterno;
                break;

            default:
                redirect = ruta;
                break;
        }

        return redirect;

    }

    private String findRuta(List<Menu> menus) {
        log.debug("Tiene {} menus", menus.size());

        for (Menu menu : menus) {
            log.debug("\tMenu:{} tipo:{}", menu.getNombre(), menu.getTipo());
        }
        for (Menu menu : menus) {
            if (menu.getTipoEnum() == MenuTipoEnum.MENU) {
                return "redirect:" + menu.getRuta();
            }
            if (menu.getTipoEnum() == MenuTipoEnum.SUB_MENU) {
                return "redirect:" + menu.getRuta();
            }
        }
        for (Menu menu : menus) {
            String ruta = findRuta(menu.getMenus());
            if (!ruta.equals("")) {
                return ruta;
            }
        }
        return "";
    }

    @RequestMapping("changeciclo")
    public String changeciclo(HttpSession session, Model model) {
        List<CicloAcademico> cicloAcademicos = serviceProvider.findCiclosVisibles();
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arrayNode.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"}));
        }
        model.addAttribute("cicloAcademico", arrayNode);
        return "academico/cicloacademico/cicloland";
    }

    @ResponseBody
    @PostMapping("cicloland")
    public void cicloland(HttpSession session, @RequestParam("ciclo") Long idCiclo) throws Exception {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = cicloAcademicoService.getCicloAcademico(idCiclo);

        EventoCicloAcademico eventoEncuesta = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(ciclo, EventoAcademicoEnum.ENCU_GEN);
        Date today = LocalDate.now().toDate();

        if (ds.getRolActivo() != null
                && ds.getRolActivo().getCodigoEnum() == RolEnum.DOC
                && eventoEncuesta != null
                && today.compareTo(eventoEncuesta.getFechaInicio()) >= 0
                && eventoEncuesta.getFechaFin().compareTo(today) >= 0) {

            AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());

            Parametro paramRutaEncuenta = parametroDAO.findByAmbienteParametroSistema(ambiente, ParametrosSistemasEnum.ENCUESTA_DOC);
            if (paramRutaEncuenta.getValor() != null) {
                ds.setEncuestaSunedu(true);
                ds.setRutaEncuentaSunedu(paramRutaEncuenta.getValor());
            }
        }

        ds.setCicloAcademico(ciclo);
        session.setAttribute(GlobalConstantine.SESSION_USUARIO, ds);

    }

    @RequestMapping("teentitansgo/{destino}/{idUsuario}/{token}")
    public String teentitansgo(
            @PathVariable("destino") String destino,
            @PathVariable("idUsuario") Long idUsuario,
            @PathVariable("token") String token,
            HttpSession session, HttpServletRequest servlet) {

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            if (ds != null) {
                session.removeAttribute(GlobalConstantine.SESSION_USUARIO);
            }

            TokenIngresante tokenIngresante = service.findToken(token, idUsuario);
            serviceProvider.loginManually(tokenIngresante.getPersona().getEmailCompania(), session, servlet);
            ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            serviceProvider.createLogJson(ds, session);

            if (ds.getCicloAcademico() == null) {
                CicloAcademico ciclo = service.findCicloActivoPregrado();
                ds.setCicloAcademico(ciclo);
            }

            if (ds.getRolesMain().size() == 1) {
                Rol rolActivo = ds.getRolesMain().get(0);
                log.debug("rol-activo: {} {}", rolActivo.getId(), rolActivo.getNombre());
                serviceProvider.asignarRolActivo(rolActivo, ds, session);
            }

            if (ds.getRolesMain().size() != 1) {
                return "redirect:/route66";

            } else if (destino.equals("OFICINA")) {
                return "redirect:/general/oficina";

            } else {
                return "redirect:/route66";
            }

        } catch (PhobosException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("logout2")
    public String logout2(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        log.debug("matando la session");
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();

        log.debug("redireccionando a operaciones");

        return "redirect:/";
    }
}
