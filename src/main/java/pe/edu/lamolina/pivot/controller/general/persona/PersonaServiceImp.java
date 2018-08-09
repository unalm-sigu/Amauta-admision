package pe.edu.lamolina.pivot.controller.general.persona;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PersonaServiceImp implements PersonaService {

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    UsuarioDAO usuarioDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Persona> allByDynatable(DynatableFilter filter) {
        return personaDAO.allByFilter(filter);
    }

    @Transactional
    private void crearUsuario(Persona persona, DataSessionPivot ds) {
        String emailEmpresa = StringUtils.isEmpty(persona.getEmailCompania()) ? null : persona.getEmailCompania();
        if (emailEmpresa == null) {
            throw new PhobosException("El correo Institucional es obligatorio");
        }
        Usuario usuario = new Usuario();
        usuario = new Usuario();
        usuario.setEstadoEnum(UserEstadoEnum.ACT);
        usuario.setGoogle(persona.getEmailCompania());
        usuario.setPersona(persona);
        usuario.setUserRegistro(ds.getUsuario());
        usuario.setFechaRegistro(new Date());
        usuarioDAO.save(usuario);

    }

    @Override
    public Persona find(Persona persona) {
        return personaDAO.find(persona.getId());
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.all();
    }

    @Override
    @Transactional
    public void savePersona(Persona persona, DataSessionPivot ds) {

        boolean personaNueva = persona.getId() == null;
        Persona personaForm = persona;
        String emailCompania = StringUtils.isEmpty(personaForm.getEmailCompania()) ? null : personaForm.getEmailCompania();

        String email = StringUtils.isEmpty(personaForm.getEmail()) ? null : personaForm.getEmail();
        personaForm.setEmail(email);
        personaForm.setEmailCompania(emailCompania);

        this.validarDNI(personaForm);
        if (personaForm.getId() == null) {
            this.validarEmailsinPersona(personaForm.getEmail());
            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            personaDAO.save(personaForm);

        } else {
            this.validarEmailConPersona(email, personaForm);
            this.validarEmailEmpresaConPersona(emailCompania, personaForm);
            Persona personaBD = this.getPersonaBD(personaForm, ds);

            personaForm = personaBD;
        }

        if (persona.getId() == null) {
            personaDAO.save(personaForm);

        } else {
            personaDAO.update(personaForm);
        }

        if (!personaNueva) {
            Usuario usuario = usuarioDAO.findByPersona(persona);
            if (usuario == null) {
                if (emailCompania != null) {
                    this.crearUsuario(persona, ds);
                }
            } else {
                usuario.setUserModifica(ds.getUsuario());
                usuario.setFechaModifica(new Date());
                usuario.setGoogle(personaForm.getEmailCompania());
                usuarioDAO.update(usuario);
            }
        } else {
            if (emailCompania != null) {
                this.crearUsuario(persona, ds);
            }
        }

        logger.debug("PERSONA ID- {}", persona.getId());
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

    private void validarEmailEmpresaSinPersona(String email) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailEmpresa(email);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo UNALM ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailEmpresaConPersona(String email, Persona persona) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailEmpresaWithoutPersona(persona);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo InnovaSchools ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private Persona getPersonaBD(Persona persona, DataSessionPivot ds) {

        Persona personaBD = personaDAO.find(persona.getId());

        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, persona, Arrays.asList("email", "emailCompania", "paterno", "materno", "nombres", "sexo", "fechaNacer", "direccion", "celular", "telefono"));
        if (sinCambios) {
            logger.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }

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
        List<Persona> personas = null;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmail(email);

        } else {
            persona.setEmail(email);
            personas = personaDAO.allByEmailWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Override
    public String validarEmailEmpresaByPersona(String email, Persona persona) {
        List<Persona> personas = null;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmailEmpresa(email);

        } else {
            persona.setEmailCompania(email);
            personas = personaDAO.allByEmailEmpresaWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Override
    public Persona findPersona(Persona personaTmp) {
        Persona persona = personaDAO.findByDocIdentidad(personaTmp.getTipoDocumento(), personaTmp.getNumeroDocIdentidad());
        if (persona != null) {
            throw new PhobosException("La persona con documento de identidad " + persona.getTipoDocumento().getSimbolo() + " " + persona.getNumeroDocIdentidad() + " ya se encuentra registrado como usuario del sistema.");
        }

        return new Persona();
    }

    @Override
    public String validarEmailCompaniaByPersona(String email, Persona persona) {
        List<Persona> personas;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmailCompania(email);

        } else {
            persona.setEmailCompania(email);
            personas = personaDAO.allByEmailCompaniaWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Transactional
    @Override
    public void updatePersonaAlumno(Persona persona, Usuario usuario) {
        Persona personaDB = personaDAO.find(persona.getId());

        personaDB.setNombres(persona.getNombres());

        personaDAO.update(personaDB);
    }

}
