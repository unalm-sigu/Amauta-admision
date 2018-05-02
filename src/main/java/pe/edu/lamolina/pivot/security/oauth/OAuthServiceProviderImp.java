package pe.edu.lamolina.pivot.security.oauth;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.model.enums.EntidadOficinaEnum;
import pe.edu.lamolina.model.enums.NivelOficinaEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import static pe.edu.lamolina.model.enums.RolEnum.ADM_UNALM;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.enums.TipoSesionEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.InstanciaEntidad;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
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
import pe.edu.lamolina.pivot.dao.general.InstanciaEntidadDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class OAuthServiceProviderImp implements OAuthServiceProvider {

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
    InstanciaEntidadDAO instanciaEntidadDAO;

    @Autowired
    OAuthServiceConfig config;
    @Autowired
    InterceptorService interceptorService;
    @Autowired
    MenuService menuService;
    @Autowired
    DespliegueConfig despliegueConfig;

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

        List<Rol> roles = rolDAO.allActivoByUsuario(usuario);
        List<Rol> rolesMain = generateRolesMain(roles);
        Map<String, Rol> mapCodeRoles = TypesUtil.convertListToMap("codigo", roles);

        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(usuario.getPersona());
        Rol rolAdmUnalm = mapCodeRoles.get(ADM_UNALM.name());
        if (rolAdmUnalm != null && colaboradores.isEmpty()) {
            throw new PhobosException("Usted no está registrado como colaborador en la universidad");
        }

        Collection<GrantedAuthority> authorities = new ArrayList();
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

        List<Oficina> oficinasUnalm = allEstructuraOficinas();
        List<Oficina> oficinasMain = allOficinasMain(colaboradores, oficinasUnalm);
        Oficina ofiMain = oficinasMain.isEmpty() ? null : oficinasMain.get(0);

        DataSessionPivot ds = new DataSessionPivot();
        ds.setEmail(email);
        ds.setUsuario(usuario);
        ds.setPersona(usuario.getPersona());
        ds.setRoles(roles);
        ds.setRolesMain(rolesMain);
        ds.setCicloAcademico(cicloAcademico);

        ds.setColaborador(colaboradores);
        ds.setOficinas(oficinasMain);
        ds.setOficinaMain(ofiMain);
        settingOficinaMain(ofiMain, ds);

        ds.setBrowser(servlet.getHeader("User-Agent"));
        ds.setDireccionIp(servlet.getRemoteAddr());
        ds.setSistemaOperativo(getClientOS(servlet));

        ds.setDocente(null);
        ds.setDepartamentoAcademico(null);

        Compania compania = companiaDAO.find(1L);
        ds.setCompania(compania);

        session.setAttribute(Constantine.SESSION_USUARIO, ds);
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

    private String getClientOS(HttpServletRequest request) {
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

    private List<Oficina> allEstructuraOficinas() {
        List<Oficina> oficinas = oficinaDAO.all();
        List<InstanciaEntidad> instancias = instanciaEntidadDAO.all();
        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("id", oficinas);

        for (Oficina oficina : oficinas) {
            oficina.setOficinasDependientes(new ArrayList());
            oficina.setInstanciaEntidades(new ArrayList());
            if (oficina.getOficinaSuperior() != null) {
                Oficina ofiSuperior = mapOficinas.get(oficina.getOficinaSuperior().getId());
                oficina.setOficinaSuperior(ofiSuperior);
            }
        }

        for (Oficina oficina : oficinas) {
            Oficina ofiSuperior = oficina.getOficinaSuperior();
            if (ofiSuperior != null) {
                ofiSuperior.getOficinasDependientes().add(oficina);
            }
        }

        for (InstanciaEntidad instancia : instancias) {
            Oficina oficina = mapOficinas.get(instancia.getOficina().getId());
            oficina.getInstanciaEntidades().add(instancia);
        }

        return oficinas;
    }

    private List<Oficina> allOficinasMain(List<Colaborador> colaboradores, List<Oficina> oficinasUnalm) {
        Map<Long, Oficina> mapOficinasMain = new LinkedHashMap();
        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("id", oficinasUnalm);
        for (Colaborador colaborador : colaboradores) {
            Oficina oficina = mapOficinas.get(colaborador.getOficina().getId());
            colaborador.setOficina(oficina);
            Oficina oficinaMain = findOficinaMain(oficina);
            mapOficinasMain.put(oficinaMain.getId(), oficinaMain);
        }
        return new ArrayList(mapOficinasMain.values());
    }

    private Oficina findOficinaMain(Oficina oficina) {
        TipoOficina tipoOficina = oficina.getTipoOficina();
        if (tipoOficina.getNivelEnum() == NivelOficinaEnum.OFI) {
            return oficina;
        }
        return findOficinaMain(oficina.getOficinaSuperior());

    }

    private void settingOficinaMain(Oficina oficinaMain, DataSessionPivot ds) {
        ds.setDepartamentos(new ArrayList());
        ds.setFacultades(new ArrayList());
        ds.setCarreras(new ArrayList());
        ds.setModalidades(new ArrayList());

        if (oficinaMain == null) {
            return;
        }

        List<ModalidadEstudio> modalidades = modalidadEstudioDAO.all();
        List<Facultad> facultades = facultadDAO.all();
        List<DepartamentoAcademico> dptos = departamentoAcademicoDAO.all();
        List<Carrera> carreras = carreraDAO.all();

        TipoOficina tipoOfi = oficinaMain.getTipoOficina();
        if (tipoOfi.getCodigoEnum() == TipoOficinaEnum.DPTO) {
            DepartamentoAcademico dpto = departamentoAcademicoDAO.find(oficinaMain.getInstanciaOficina());
            ds.getDepartamentos().add(dpto);
        }
        if (tipoOfi.getCodigoEnum() == TipoOficinaEnum.ESP) {
            Carrera carr = carreraDAO.find(oficinaMain.getInstanciaOficina());
            ds.getCarreras().add(carr);
        }
        if (tipoOfi.getCodigoEnum() == TipoOficinaEnum.FAC) {
            Facultad fac = facultadDAO.find(oficinaMain.getInstanciaOficina());
            ds.getFacultades().add(fac);
            for (Carrera carrera : carreras) {
                if (carrera.getFacultad().getId() == fac.getId().longValue()) {
                    ds.getCarreras().add(carrera);
                }
            }
            for (DepartamentoAcademico dpto : dptos) {
                if (dpto.getFacultad().getId() == fac.getId().longValue()) {
                    ds.getDepartamentos().add(dpto);
                }
            }
        }

        if (oficinaMain.getInstanciaEntidades().isEmpty()) {
            return;
        }

        Map<Long, Facultad> mapFacultad = facultades.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        Map<Long, Carrera> mapCarrera = carreras.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        Map<Long, DepartamentoAcademico> mapDptos = dptos.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        Map<Long, ModalidadEstudio> mapModalidad = modalidades.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

        for (InstanciaEntidad instanciaEnte : oficinaMain.getInstanciaEntidades()) {
            if (instanciaEnte.getEntidadEnum() == EntidadOficinaEnum.DPTO) {
                if (instanciaEnte.getContieneTodos() == 1) {
                    ds.getDepartamentos().addAll(dptos);
                } else {
                    DepartamentoAcademico dpto = mapDptos.get(instanciaEnte.getValorInstancia());
                    if (dpto == null) {
                        throw new PhobosException("Error en la configuración de instancias de los departamentos académicos para la oficina de nombre: " + oficinaMain.getNombre());
                    }
                    ds.getDepartamentos().add(dpto);
                }
            }
            if (instanciaEnte.getEntidadEnum() == EntidadOficinaEnum.ESP) {
                if (instanciaEnte.getContieneTodos() == 1) {
                    ds.getCarreras().addAll(carreras);
                } else {
                    Carrera carrera = mapCarrera.get(instanciaEnte.getValorInstancia());
                    if (carrera == null) {
                        throw new PhobosException("Error en la configuración de instancias de las especialidades para la oficina de nombre: " + oficinaMain.getNombre());
                    }
                    ds.getCarreras().add(carrera);
                }
            }
            if (instanciaEnte.getEntidadEnum() == EntidadOficinaEnum.FAC) {
                if (instanciaEnte.getContieneTodos() == 1) {
                    ds.getFacultades().addAll(facultades);
                } else {
                    Facultad facultad = mapFacultad.get(instanciaEnte.getValorInstancia());
                    if (facultad == null) {
                        throw new PhobosException("Error en la configuración de instancias de las facultades para la oficina de nombre: " + oficinaMain.getNombre());
                    }
                    ds.getFacultades().add(facultad);
                    for (Carrera carrera : carreras) {
                        if (carrera.getFacultad().getId() == facultad.getId().longValue()) {
                            ds.getCarreras().add(carrera);
                        }
                    }
                    for (DepartamentoAcademico dpto : dptos) {
                        if (dpto.getFacultad().getId() == facultad.getId().longValue()) {
                            ds.getDepartamentos().add(dpto);
                        }
                    }
                }
            }
            if (instanciaEnte.getEntidadEnum() == EntidadOficinaEnum.MOD) {
                if (instanciaEnte.getContieneTodos() == 1) {
                    ds.getModalidades().addAll(modalidades);
                } else {
                    ModalidadEstudio modalidad = mapModalidad.get(instanciaEnte.getValorInstancia());
                    if (modalidad == null) {
                        throw new PhobosException("Error en la configuración de instancias de las modalidades de estudio para la oficina de nombre: " + oficinaMain.getNombre());
                    }
                    ds.getModalidades().add(modalidad);
                    for (Carrera carrera : carreras) {
                        if (carrera.getModalidadEstudio().getId() == modalidad.getId().longValue()) {
                            ds.getCarreras().add(carrera);
                        }
                    }
                }
            }
        }

    }

    private List<Rol> generateRolesMain(List<Rol> roles) {
        List<Rol> rolesNuevos = new ArrayList();
        List<Rol> rolesMain = new ArrayList();
        Map<Long, Rol> mapRoles = TypesUtil.convertListToMap("id", roles);
        for (Rol role : roles) {
            role.setRolesInferiores(new ArrayList());
            Rol rolSuperior = role.getRolSuperior();
            if (rolSuperior != null) {
                Rol rolSuper = mapRoles.get(rolSuperior.getId());
                if (rolSuper == null) {
                    rolSuper = rolSuperior;
                    rolSuper.setRolesInferiores(new ArrayList());
                    mapRoles.put(rolSuper.getId(), rolSuper);
                    rolesNuevos.add(rolSuper);
                }
            }
        }
        roles.addAll(rolesNuevos);

        for (Rol role : roles) {
            Rol rolSuperior = role.getRolSuperior();
            if (rolSuperior != null) {
                rolSuperior = mapRoles.get(rolSuperior.getId());
                rolSuperior.getRolesInferiores().add(role);
                role.setRolSuperior(rolSuperior);
            } else {
                rolesMain.add(role);
            }
        }
        return rolesMain;
    }

    @Override
    public void asignarRolActivo(Rol rol, DataSessionPivot ds, HttpSession session) {
        if (ds.getRolActivo() != null && rol.getId().longValue() == ds.getRolActivo().getId()) {
            return;
        }

        ds.setDocente(null);
        ds.setDepartamentoAcademico(null);
        ds.setOficinaMain(null);
        ds.setDepartamentos(new ArrayList());
        ds.setFacultades(new ArrayList());
        ds.setCarreras(new ArrayList());
        ds.setModalidades(new ArrayList());

        if (rol.getCodigoEnum() == RolEnum.ADM_UNALM) {

            Oficina ofiMain = ds.getOficinas().isEmpty() ? null : ds.getOficinas().get(0);
            ds.setOficinaMain(ofiMain);
            settingOficinaMain(ofiMain, ds);
        }

        if (rol.getCodigoEnum() == RolEnum.DOC) {
            List<Docente> docentes = docenteDAO.allByPersona(ds.getPersona());
            if (!docentes.isEmpty()) {
                ds.setDocente(docentes.get(0));
                ds.setDepartamentoAcademico(docentes.get(0).getDepartamentoAcademico());
            }
        }

        if (rol.getCodigoEnum() == RolEnum.ALU) {

        }

        Sistema sistema = new Sistema(despliegueConfig.getSistema());
        List<Menu> menus = allMenusByRolMain(rol, sistema, ds);
        ds.setMenu(menus);
        ds.setRolActivo(rol);

        session.setAttribute(Constantine.SESSION_USUARIO, ds);

    }

    //@Override
    private List<Menu> allMenusByRolMain(Rol rol, Sistema sistema, DataSessionPivot ds) {
        List<Rol> roles = new ArrayList(rol.getRolesInferiores());
        roles.add(rol);

        List<Menu> menusBD = menuDAO.allByRolSistema(roles, sistema);
        return menuService.allMenuOrdered(menusBD);
    }

}
