package pe.edu.lamolina.pivot.controller.general.personaperfil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaPerfil;
import pe.edu.lamolina.model.seguridad.PerfilRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.dao.general.CompaniaDAO;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaPerfilDAO;
import pe.edu.lamolina.pivot.dao.seguridad.PerfilRolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;

@Service
@Transactional(readOnly = true)
public class PersonaPerfilServiceImp implements PersonaPerfilService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PerfilCompaniaDAO perfilCompaniaDAO;

    @Autowired
    PersonaPerfilDAO personaPerfilDAO;

    @Autowired
    PerfilRolDAO perfilRolDAO;

    @Autowired
    CompaniaDAO companiaDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    UsuarioRolDAO usuarioRolDAO;

    @Override
    public List<PerfilCompania> allPerfilCompania() {
        return perfilCompaniaDAO.all();
    }

    @Override
    public List<Compania> allCompania() {
        return companiaDAO.all();
    }

    @Override
    public List<PersonaPerfil> allPersonasPefiles(DynatableFilter filter) {
        return personaPerfilDAO.allByFiltersDynaTable(filter);
    }

    @Override
    public List<Persona> allPersonasByNombre(String nombre) {
        return personaDAO.allByNombre(nombre);
    }

    @Override
    public PersonaPerfil findPersonaPerfil(PersonaPerfil personaPerfil) {
        return personaPerfilDAO.find(personaPerfil.getId());
    }

    @Override
    @Transactional
    public void save(PersonaPerfil personaPerfil, Usuario usuario) {

        if (personaPerfil.getOficina().getId() == null) {
            personaPerfil.setOficina(null);
        }

        personaPerfil.setEstado(EstadoEnum.ACT);

        personaPerfil.setUserRegistro(usuario);
        personaPerfil.setFechaRegistro(new Date());
        personaPerfilDAO.save(personaPerfil);

    }

    @Override
    @Transactional
    public void update(PersonaPerfil personaPerfil) {
        if (personaPerfil.getOficina().getId() == null) {
            personaPerfil.setOficina(null);
        }

        personaPerfilDAO.update(personaPerfil);

    }

    @Override
    @Transactional
    public void activate(Long idPersonaPerfil) {

        PersonaPerfil personaPerfil = personaPerfilDAO.find(idPersonaPerfil);
        personaPerfil.setEstado(EstadoEnum.ACT);
        personaPerfilDAO.update(personaPerfil);

        List<PerfilRol> allPerfilRol = perfilRolDAO.allByPerfilCompania(personaPerfil.getPerfilCompania());

        if (!allPerfilRol.isEmpty()) {
            Usuario usuario = this.getExistorCreateUser(personaPerfil.getPersona());

            UsuarioRol usuarioRol = null;
            for (PerfilRol rolCargo : allPerfilRol) {

                usuarioRol = usuarioRolDAO.findByUsuarioAndRol(usuario, rolCargo.getRol());

                if (usuarioRol == null) {
                    usuarioRol = new UsuarioRol();
                    usuarioRol.setUsuario(usuario);
                    usuarioRol.setRol(rolCargo.getRol());
                    usuarioRolDAO.save(usuarioRol);
                }
            }
        }
    }

    @Override
    @Transactional
    public void desactivar(Long idPersonaPerfil) {

        PersonaPerfil personaPerfil = personaPerfilDAO.find(idPersonaPerfil);
        personaPerfil.setEstado(EstadoEnum.INA);
        personaPerfilDAO.update(personaPerfil);

        List<PerfilRol> allPerfilRol = perfilRolDAO.allByPerfilCompania(personaPerfil.getPerfilCompania());

        if (!allPerfilRol.isEmpty()) {
            Usuario usuario = this.getExistorCreateUser(personaPerfil.getPersona());

            Set<Long> roles = allPerfilRol.stream()
                    .map(PerfilRol::getRol)
                    .map(Rol::getId)
                    .collect(Collectors.toSet());

            usuarioRolDAO.deleteByUsuarioRol(usuario, new ArrayList(roles));

        }

    }

    @Transactional
    private Usuario getExistorCreateUser(Persona persona) {

        Usuario user = usuarioDAO.findByPersona(persona);

        if (user == null) {
            user = new Usuario();
            user.setPersona(persona);
            user.setGoogle(persona.getEmailCompania());
            user.setEstado(UserEstadoEnum.ACT);

            usuarioDAO.save(user);
        }

        return user;
    }

}
