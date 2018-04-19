package pe.edu.lamolina.pivot.controller.seguridad.usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.controller.general.persona.PersonaService;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.SistemaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImp implements UsuarioService {

    @Autowired
    UsuarioDAO usuarioDAO;
    @Autowired
    UsuarioRolDAO usuarioRolDAO;
    @Autowired
    SistemaDAO sistemaDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    RolDAO rolDAO;

    @Autowired
    PersonaService personaService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Usuario> allByDynatable(DynatableFilter filter) {
        List<Usuario> users = usuarioDAO.allByFilter(filter);
        Map<Long, Usuario> mapUsers = TypesUtil.convertListToMap("id", users);
        for (Usuario user : users) {
            user.setUsuarioRol(new ArrayList());
        }

        List<UsuarioRol> usersRoles = usuarioRolDAO.allByUsuarios(users);
        for (UsuarioRol userRol : usersRoles) {
            Usuario user = mapUsers.get(userRol.getUsuario().getId());
            user.getUsuarioRol().add(userRol);
        }

        return users;
    }

    @Override
    public Usuario findUsuario(Usuario user) {
        return usuarioDAO.find(user);
    }

    @Override
    @Transactional
    public void desactivaUsuario(Usuario usu, DataSessionPivot ds) {

        Usuario usuario = usuarioDAO.find(usu);
        usuario.setEstadoEnum(UserEstadoEnum.INA);
        usuarioDAO.update(usuario);

        List<UsuarioRol> userRoles = usuarioRolDAO.allByUser(usuario);
        for (UsuarioRol userRol : userRoles) {
            if (userRol.getEstadoEnum() == UserEstadoEnum.ACT) {
                userRol.setEstado(UserEstadoEnum.INA);
                userRol.setFechaFin(new Date());

                userRol.setFechaFinaliza(new Date());
                userRol.setUserFinaliza(ds.getUsuario());
                usuarioRolDAO.update(userRol);
            }
        }
    }

    @Override
    @Transactional
    public void activaUsuario(Usuario usu, DataSessionPivot ds) {
        Usuario usuario = usuarioDAO.find(usu);
        usuario.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioDAO.update(usuario);
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.all();
    }

    @Override
    public List<UsuarioRol> allRolesByUser(Usuario user) {
        List<UsuarioRol> rolesUser = usuarioRolDAO.allByUser(user);
        return rolesUser;
    }

    @Override
    @Transactional
    public void saveUsuario(Usuario usuario, DataSessionPivot ds) {

        Persona personaForm = usuario.getPersona();
        String emailEmpresa = StringUtils.isEmpty(personaForm.getEmailCompania()) ? null : personaForm.getEmailCompania();
        if (emailEmpresa == null) {
            throw new PhobosException("El correo Institucional es obligatorio");
        }

        String email = StringUtils.isEmpty(personaForm.getEmail()) ? null : personaForm.getEmail();
        personaForm.setEmail(email);
        personaForm.setEmailCompania(emailEmpresa);

        validarDNI(personaForm);
        if (personaForm.getId() == null) {
            validarEmailsinPersona(personaForm.getEmail());
            validarEmailCompaniaSinPersona(personaForm.getEmailCompania());
            personaForm.setUserRegistro(ds.getUsuario());
            personaForm.setFechaRegistro(new Date());
            personaForm.setEstadoEnum(PersonaEstadoEnum.ACT);
            personaDAO.save(personaForm);

        } else {
            validarEmailConPersona(email, personaForm);
            validarEmailCompaniaConPersona(emailEmpresa, personaForm);
            Persona personaBD = getPersonaBD(personaForm, ds);

            personaForm = personaBD;
        }

        Usuario usuarioBD;
        if (usuario.getId() == null) {
            usuarioBD = new Usuario();
            usuarioBD.setEstadoEnum(UserEstadoEnum.ACT);
            usuarioBD.setGoogle(personaForm.getEmailCompania());
            usuarioBD.setPersona(personaForm);
            usuarioBD.setUserRegistro(ds.getUsuario());
            usuarioBD.setFechaRegistro(new Date());
            usuarioDAO.save(usuarioBD);

        } else {
            usuarioBD = usuarioDAO.findByPersona(personaForm);
            if (!usuarioBD.getGoogle().equals(personaForm.getEmailCompania())) {
                usuarioBD.setGoogle(personaForm.getEmailCompania());
                usuarioBD.setUserModifica(ds.getUsuario());
                usuarioBD.setFechaModifica(new Date());
                usuarioDAO.update(usuarioBD);
            }
        }

        usuario.setId(usuarioBD.getId());
        usuario.setEstadoEnum(usuarioBD.getEstadoEnum());
        usuario.setPersona(usuarioBD.getPersona());
        usuario.setGoogle(usuarioBD.getGoogle());

        logger.debug("id :::: {}", usuario.getId());
    }

    private void validarDNI(Persona personaForm) {
        TipoDocIdentidad doc = personaForm.getTipoDocumento();

        Persona personaBD = personaDAO.findByDocIdentidad(doc, personaForm.getNumeroDocIdentidad());
        if (personaForm.getId() != null && personaBD != null && personaBD.getId().longValue() != personaForm.getId()) {
            throw new PhobosException("El DNI ingresado ya se encuentra relacionado con otra persona: " + personaBD.getApellidosNombres());

        } else if (personaForm.getId() == null && personaBD != null) {
            throw new PhobosException("El DNI ingresado ya se encuentra relacionado con otra persona: " + personaBD.getApellidosNombres());
        }
    }

    private void validarEmailsinPersona(String email) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmail(email);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailConPersona(String email, Persona persona) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailWithoutPersona(persona);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailCompaniaSinPersona(String email) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailCompania(email);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo institucional ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailCompaniaConPersona(String email, Persona persona) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailCompaniaWithoutPersona(persona);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo institucional ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private Persona getPersonaBD(Persona persona, DataSessionPivot ds) {

        Persona personaBD = personaDAO.find(persona.getId());

        personaBD.setNombres(persona.getNombres());
        personaBD.setPaterno(persona.getPaterno());
        personaBD.setMaterno(persona.getMaterno());
        personaBD.setSexo(persona.getSexo());
        personaBD.setFechaNacer(persona.getFechaNacer());
        personaBD.setDireccion(persona.getDireccion());
        personaBD.setCelular(persona.getCelular());
        personaBD.setTelefono(persona.getTelefono());
        personaBD.setEmail(persona.getEmail());
        personaBD.setEmailCompania(persona.getEmailCompania());
        personaDAO.update(personaBD);

        return personaBD;
    }

    @Override
    public String validarEmailByPersona(String email, Persona persona) {
        return personaService.validarEmailByPersona(email, persona);
    }

    @Override
    public String validarEmailCompaniaByPersona(String email, Persona persona) {
        return personaService.validarEmailCompaniaByPersona(email, persona);
    }

    @Override
    public Persona findPersona(Persona personaTmp) {
        Persona persona = personaDAO.findByDocIdentidad(personaTmp.getTipoDocumento(), personaTmp.getNumeroDocIdentidad());
        if (persona == null) {
            return new Persona();
        }

        Usuario user = usuarioDAO.findByPersona(persona);
        if (user != null) {
            throw new PhobosException("La persona con documento de identidad " + persona.getTipoDocumento().getSimbolo() + " " + persona.getNumeroDocIdentidad() + " ya se encuentra registrado como usuario del sistema.");
        }

        return persona;
    }

    @Override
    public List<Rol> allRolesWithoutUser(Usuario user) {
        List<Rol> roles = new ArrayList();
        List<Rol> rolesTodos = rolDAO.all();
        List<Rol> rolesUser = rolDAO.allActivoByUsuario(user);
        Map<Long, Rol> mapRolesUser = TypesUtil.convertListToMap("id", rolesUser);

        for (Rol rol : rolesTodos) {
            Rol rolUser = mapRolesUser.get(rol.getId());
            if (rolUser == null) {
                roles.add(rol);
            }
        }

        return roles;
    }

    @Override
    @Transactional
    public void saveUserRol(UsuarioRol userRol, DataSessionPivot ds) {

        Usuario usuario = userRol.getUsuario();
        Rol rol = userRol.getRol();
        Usuario userBD = usuarioDAO.find(usuario);
        if (userBD.getEstadoEnum() != UserEstadoEnum.ACT) {
            throw new PhobosException("Este usuario no se encuentra activo. Ya no puede modificarse sus perfiles.");
        }

        UsuarioRol usuarioRol = usuarioRolDAO.findByUsuarioRol(usuario, rol);
        if (usuarioRol != null && usuarioRol.getEstadoEnum() == UserEstadoEnum.ACT) {
            throw new PhobosException("El rol seleccionado ya se encuentra asignado");
        }

        userRol.setEstado(UserEstadoEnum.ACT);
        userRol.setUserRegistro(ds.getUsuario());
        userRol.setFechaRegistro(new Date());
        usuarioRolDAO.save(userRol);

    }

    @Override
    public List<Rol> listRol() {
        return rolDAO.all();
    }

    @Override
    @Transactional
    public void deshabilitarPerfil(UsuarioRol userRol, DataSessionPivot ds) {
        UsuarioRol usuarioRol = usuarioRolDAO.find(userRol);
        if (usuarioRol.getEstadoEnum() == UserEstadoEnum.INA) {
            throw new PhobosException("Ya se encuentra deshabilitado este perfíl en este usuario");
        }

        usuarioRol.setFechaFin(new Date());
        usuarioRol.setEstado(UserEstadoEnum.INA);
        usuarioRol.setUserFinaliza(ds.getUsuario());
        usuarioRol.setFechaFinaliza(new Date());
        usuarioRolDAO.update(usuarioRol);
    }

}
