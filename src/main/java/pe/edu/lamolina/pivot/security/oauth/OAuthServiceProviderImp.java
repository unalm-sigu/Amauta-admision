package pe.edu.lamolina.pivot.security.oauth;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import javax.servlet.http.HttpSession;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import static pe.edu.lamolina.model.enums.RolEnum.ADM_UNALM;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.enums.TipoSesionEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.controller.interceptor.InterceptorService;
import pe.edu.lamolina.pivot.controller.seguridad.menu.MenuService;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.CompaniaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class OAuthServiceProviderImp implements OAuthServiceProvider {

    @Autowired
    OAuthServiceConfig config;

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    CompaniaDAO companiaDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    MenuDAO menuDAO;

    @Autowired
    InterceptorService interceptorService;
    @Autowired
    MenuService menuService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public OAuthService getService() {
        return new ServiceBuilder()
                .provider(config.getApiClass())
                .apiKey(config.getKey())
                .apiSecret(config.getSecret())
                .callback(config.getCallback())
                .scope("https://www.googleapis.com/auth/userinfo.email "
                        + "https://www.googleapis.com/auth/userinfo.profile")
                .build();
    }

    @Override
    public void loginManually(String email, HttpSession session, HttpServletRequest servlet) {

        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivo();
        Usuario usuario = usuarioDAO.findByGoogleEmail(email);
        if (usuario == null) {
            throw new PhobosException("Usuario no identificado.");
        }

        Collection<GrantedAuthority> authorities = new ArrayList();

        List<Rol> rolesNuevos = new ArrayList();
        List<Rol> rolesMain = new ArrayList();
        List<Rol> roles = rolDAO.allActivoByUsuario(usuario);
        Map<Long, Rol> mapIdRoles = TypesUtil.convertListToMap("id", roles);
        for (Rol role : roles) {
            role.setRolesInferiores(new ArrayList());
            Rol rolSuperior = role.getRolSuperior();
            if (rolSuperior != null) {
                Rol rolSuper = mapIdRoles.get(rolSuperior.getId());
                if (rolSuper == null) {
                    rolSuper = rolSuperior;
                    rolSuper.setRolesInferiores(new ArrayList());
                    mapIdRoles.put(rolSuper.getId(), rolSuper);
                    rolesNuevos.add(rolSuper);
                }
            }
        }

        roles.addAll(rolesNuevos);
        Map<String, Rol> mapCodeRoles = TypesUtil.convertListToMap("codigo", roles);

        for (Rol role : roles) {
            Rol rolSuperior = role.getRolSuperior();
            if (rolSuperior != null) {
                rolSuperior = mapIdRoles.get(rolSuperior.getId());
                rolSuperior.getRolesInferiores().add(role);
                role.setRolSuperior(rolSuperior);
            } else {
                rolesMain.add(role);
            }
        }

        for (Rol rol : roles) {
            authorities.add(new SimpleGrantedAuthority(rol.getCodigo().toUpperCase()));
        }

        if (authorities.isEmpty()) {
            throw new PhobosException("Usuario sin rol asignado.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, email, authorities);
        SecurityContext cntx = SecurityContextHolder.getContext();
        cntx.setAuthentication(authentication);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, cntx);

        List<Colaborador> colaboradors = colaboradorDAO.allActivosByPersona(usuario.getPersona());
        Rol rolAdmUnalm = mapCodeRoles.get(ADM_UNALM.name());
        if (rolAdmUnalm != null && colaboradors.isEmpty()) {
            throw new PhobosException("Usted no está registrado como colaborador en la universidad");
        }

        DataSessionPivot dataSession = new DataSessionPivot();
        dataSession.setEmail(email);
        dataSession.setUsuario(usuario);
        dataSession.setPersona(usuario.getPersona());
        dataSession.setRoles(roles);
        dataSession.setRolesMain(rolesMain);
        dataSession.setCicloAcademico(cicloAcademico);
        dataSession.setBrowser(servlet.getHeader("User-Agent"));
        dataSession.setDireccionIp(servlet.getRemoteAddr());
        dataSession.setSistemaOperativo(getClientOS(servlet));
        dataSession.setColaborador(colaboradors);

        List<Docente> docentes = docenteDAO.allByPersona(usuario.getPersona());
        if (!docentes.isEmpty()) {
            if (docentes.size() == 1) {
                dataSession.setDocente(docentes.get(0));
                dataSession.setDepartamentoAcademico(docentes.get(0).getDepartamentoAcademico());
            }
        }

        List<Oficina> oficinasByJefe = oficinaDAO.allByJefe(usuario.getPersona());
        for (Oficina oficina : oficinasByJefe) {
            if (oficina.getTipoOficina().getCodigo().equals("DPTO")) {
                DepartamentoAcademico dpto = departamentoAcademicoDAO.find(oficina.getInstanciaOficina());
                dataSession.setDepartamentoAcademico(dpto);
            }
        }
        List<Oficina> oficinas = oficinaDAO.all();

        dataSession.setOficinas(oficinas);

        Compania compania = companiaDAO.find(1L);
        dataSession.setCompania(compania);

        List<Facultad> facultades = facultadDAO.allByCompania(compania);
        dataSession.setFacultados(facultades);

        List<ModalidadEstudio> modalidades = modalidadEstudioDAO.allByCompania(compania);
        dataSession.setModalidades(modalidades);

        List<DepartamentoAcademico> departamentos = departamentoAcademicoDAO.allByCompania(compania);
        dataSession.setDepartamentos(departamentos);

        List<Carrera> carreras = carreraDAO.allByCompania(compania);
        dataSession.setCarreras(carreras);

        session.setAttribute(Constantine.SESSION_USUARIO, dataSession);
    }

    @Async
    @Override
    public void createLogJson(DataSessionPivot ds, HttpSession session) {
        ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode objData = new ObjectNode(JsonNodeFactory.instance);
        data.put("usuario", ds.getUsuario().getPersona().getNombreCompleto());
        objData.set("data", data);
        objData.put("tipo", "Inicio Sesión");
        interceptorService.saveInterceptor(objData, session);
    }

    @Async
    @Override
    public void createLogJsonLogout(DataSessionPivot ds, HttpSession session) {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode objData = new ObjectNode(JsonNodeFactory.instance);
        obj.put("usuario", ds.getUsuario().getPersona().getNombreCompleto());
        objData.put("data", obj);
        objData.put("tipo", TipoSesionEnum.LOGIN.name());
        interceptorService.saveInterceptor(objData, session);
    }

    public String getClientOS(HttpServletRequest request) {
        final String browserDetails = request.getHeader("User-Agent");

        //=================OS=======================
        final String lowerCaseBrowser = browserDetails.toLowerCase();
        if (lowerCaseBrowser.contains("windows")) {
            return "Windows";
        } else if (lowerCaseBrowser.contains("mac")) {
            return "Mac";
        } else if (lowerCaseBrowser.contains("x11")) {
            return "Unix";
        } else if (lowerCaseBrowser.contains("android")) {
            return "Android";
        } else if (lowerCaseBrowser.contains("iphone")) {
            return "IPhone";
        } else {
            return "UnKnown, More-Info: " + browserDetails;
        }
    }

    @Override
    public List<Menu> allMenuRolActivo(Rol rolAsignar, Sistema sistema) {
        List<Menu> menusBD = menuDAO.allMenuRolActivo(rolAsignar, sistema);
        return menuService.allMenuOrdered(menusBD);
    }
}
