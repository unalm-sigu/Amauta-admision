package pe.edu.lamolina.amauta.security.oauth;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.EntidadOficinaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.NivelOficinaEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import static pe.edu.lamolina.model.enums.RolEnum.ADM_UNALM;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.enums.TipoSesionEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.InstanciaEntidad;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.interceptor.InterceptorService;
import pe.edu.lamolina.amauta.controller.seguridad.menu.MenuService;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.CompaniaDAO;
import pe.edu.lamolina.amauta.dao.general.InstanciaEntidadDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.MenuDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class OAuthServiceProviderImp implements OAuthServiceProvider {

    private final CarreraDAO carreraDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final CompaniaDAO companiaDAO;
    private final DocenteDAO docenteDAO;
    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private final FacultadDAO facultadDAO;
    private final InstanciaEntidadDAO instanciaEntidadDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;
    private final MenuDAO menuDAO;
    private final OficinaDAO oficinaDAO;
    private final RolDAO rolDAO;
    private final UsuarioDAO usuarioDAO;

    private final DespliegueConfig despliegueConfig;
    private final InterceptorService interceptorService;
    private final MenuService menuService;

    @Override
    public void loginManually(String email, HttpSession session, HttpServletRequest servlet) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivo(modalidad);
        Usuario usuario = usuarioDAO.findByGoogleEmail(email);
        if (usuario == null) {
            throw new PhobosException("Usuario no identificado.");
        }
        if (usuario.getEstadoEnum() != UserEstadoEnum.ACT) {
            usuario = usuario.getUserActivo();
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
        if (findCiclosVisibles().isEmpty()) {
            ds.setCicloAcademico(cicloAcademico);
        }

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
        session.setAttribute(GlobalConstantine.SESSION_USUARIO, ds);
    }

    private Usuario findUser(String email) {
        log.info("[findUser] Busqueda de usuario = {}", email);
        Usuario usuario = usuarioDAO.findByGoogleEmail(email);
        if (usuario == null) {
            usuario = usuarioDAO.findByOutlookEmail(email);
        }

        Assert.isNotNull(usuario, "Usuario no identificado");
        return usuario;
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
            log.debug("oficina-main: {}", oficinaMain.getNombre());
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
            log.debug("No tiene oficinaMain.. sale sin asignacion de menu");
            return;
        }
        log.debug("oficinaMain instanciasEntidades {} {}", oficinaMain.getNombre(), oficinaMain.getInstanciaEntidades().isEmpty());

        List<ModalidadEstudio> modalidades = modalidadEstudioDAO.all();
        List<Facultad> facultades = facultadDAO.all();
        List<DepartamentoAcademico> dptos = departamentoAcademicoDAO.all();
        List<Carrera> carreras = carreraDAO.all();

        if (contieneTipoOficina(ds.getOficinas(), TipoOficinaEnum.VICE)
                && ds.getRoles().stream().filter(x -> x.getCodigoEnum() == RolEnum.VICE_ACAD).findAny().isPresent()) {
            ds.getDepartamentos().addAll(dptos);
        }

        if (contieneTipoOficina(ds.getOficinas(), TipoOficinaEnum.DPTO)) {
            Oficina oficinaDepartamento = getOficinaByTipo(ds.getOficinas(), TipoOficinaEnum.DPTO);
            DepartamentoAcademico dpto = departamentoAcademicoDAO.find(oficinaDepartamento.getInstanciaOficina());
            ds.getDepartamentos().add(dpto);
        }
        if (contieneTipoOficina(ds.getOficinas(), TipoOficinaEnum.ESP)) {
            Oficina oficinaCarrera = getOficinaByTipo(ds.getOficinas(), TipoOficinaEnum.ESP);
            Carrera carr = carreraDAO.find(oficinaCarrera.getInstanciaOficina());
            ds.getCarreras().add(carr);
        }
        if (contieneTipoOficina(ds.getOficinas(), TipoOficinaEnum.FAC)) {
            Oficina oficinaFacultad = getOficinaByTipo(ds.getOficinas(), TipoOficinaEnum.FAC);
            Facultad fac = facultadDAO.find(oficinaFacultad.getInstanciaOficina());
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
                if (role.getCodigoEnum() == RolEnum.ALU) {
                } else if (role.getCodigoEnum() == RolEnum.ADM_CPA) {
                } else {
                    rolesMain.add(role);
                }
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
            log.debug("Configurando rol adm_unalm");
            Oficina ofiMain = ds.getOficinas().isEmpty() ? null : ds.getOficinas().get(0);
            ds.setOficinaMain(ofiMain);
            settingOficinaMain(ofiMain, ds);
        }

        if (rol.getCodigoEnum() == RolEnum.DOC) {
            log.debug("Configurando rol docente");
            List<Docente> docentes = docenteDAO.allByPersona(ds.getPersona());
            for (Docente docente : docentes) {
                log.info("add-docente-to-session {}", docente);
                if (docente.getEstadoEnum() == DocenteEstadoEnum.ACT) {
                    ds.setDocente(docente);
                    ds.setDepartamentoAcademico(docente.getDepartamentoAcademico());
                    break;
                }
            }
        }

        if (rol.getCodigoEnum() == RolEnum.ALU) {
            log.debug("Configurando rol alumno que sera expulsado del amauta");
        }

        log.debug("Configurando menus para este rol");
        Sistema sistema = new Sistema(despliegueConfig.getSistema());
        List<Menu> menus = allMenusByRolMain(rol, sistema, ds);
        ds.setMenu(menus);
        ds.setRolActivo(rol);

        session.setAttribute(GlobalConstantine.SESSION_USUARIO, ds);

    }

    private List<Menu> allMenusByRolMain(Rol rol, Sistema sistema, DataSessionPivot ds) {
        List<Rol> roles = new ArrayList(rol.getRolesInferiores());
        for (Rol roli : roles) {
            log.debug("rol-inferior {} {}", roli.getId(), roli.getNombre());
        }
        roles.add(rol);

        String entorno = despliegueConfig.getAmbiente().toUpperCase();
        List<Menu> menusBD = menuDAO.allByRolSistema(roles, sistema, entorno);
        ds.setMenusAcceso(menusBD);
        System.out.println("menusBD.size = " + menusBD.size());
        return menuService.allMenuOrdered(menusBD);
    }

    @Override
    public List<CicloAcademico> findCiclosVisibles() {
        return cicloAcademicoDAO.allVisibles(ModalidadEstudioEnum.PRE);
    }

    private boolean contieneTipoOficina(List<Oficina> oficinas, TipoOficinaEnum tipoOficinaEnum) {
        if (oficinas == null) {
            return false;
        }
        for (Oficina oficina : oficinas) {
            if (tipoOficinaEnum == oficina.getTipoOficina().getCodigoEnum()) {
                return true;
            }
        }
        return false;
    }

    private Oficina getOficinaByTipo(List<Oficina> oficinas, TipoOficinaEnum tipoOficinaEnum) {
        if (oficinas == null) {
            return null;
        }
        for (Oficina oficina : oficinas) {
            if (tipoOficinaEnum == oficina.getTipoOficina().getCodigoEnum()) {
                return oficina;
            }
        }
        return null;
    }

}
