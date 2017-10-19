package pe.edu.lamolina.pivot.security.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import javax.servlet.http.HttpSession;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.CompaniaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
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
    public void loginManually(String email, HttpSession session) {
        
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivo();
        Usuario usuario = usuarioDAO.findByEmail(email);
        if (usuario == null) {
            throw new PhobosException("Usuario no identificado.");
        }
        
        
        
        Collection<GrantedAuthority> authorities = new ArrayList();
        
        List<Rol> roles = rolDAO.allActivoByUsuario(usuario);
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
        
        DataSessionPivot dataSession = new DataSessionPivot();
        dataSession.setEmail(email);
        dataSession.setUsuario(usuario);
        dataSession.setPersona(usuario.getPersona());
        dataSession.setRoles(roles);
        dataSession.setCicloAcademico(cicloAcademico);
        
        Docente docente = docenteDAO.findPersona(usuario.getPersona());
        if (docente != null) {
            dataSession.setDocente(docente);
            dataSession.setDepartamentoAcademico(docente.getDepartamentoAcademico());
        }
        
        List<Oficina> oficinas = oficinaDAO.allByJefe(usuario.getPersona());
        for (Oficina oficina : oficinas) {
            if (oficina.getTipoOficina().equals("DPTO")) {
                DepartamentoAcademico dpto = departamentoAcademicoDAO.find(oficina.getInstanciaOficina());
                dataSession.setDepartamentoAcademico(dpto);
            }
        }
        
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
    
}
