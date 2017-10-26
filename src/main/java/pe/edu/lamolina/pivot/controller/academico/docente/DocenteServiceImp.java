package pe.edu.lamolina.pivot.controller.academico.docente;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.enums.DocenteEstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class DocenteServiceImp implements DocenteService {

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Docente> allByDynatable(DynatableFilter filter) {
        return docenteDAO.allByFilter(filter);
    }

    @Override
    public Docente find(Docente docente) {
        return docenteDAO.findDocente(docente);
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.all();
    }

    @Override
    @Transactional
    public void save(Docente docente, DataSessionPivot ds) {
        logger.debug("save docente");
        Persona personaForm = docente.getPersona();
        Persona persona = this.findPersona(personaForm);
        logger.debug("existe persona {}", (persona != null));
        docente.setPersona(persona);
        if (persona == null) {
            logger.debug("creado persona {}", (persona != null));
            if (Strings.isNullOrEmpty(personaForm.getEmailCompania())) {
                throw new PhobosException("El correo principal es obligatorio");
            }
            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            if (!Strings.isNullOrEmpty(personaForm.getEmail())) {
                this.validarEmailsinPersona(personaForm.getEmail());
            }
            logger.debug("guardando persona {}", (persona != null));
            personaForm.setEstado(EstadoEnum.ACT.name());
            personaDAO.save(personaForm);
            logger.debug("guardado persona {}", personaForm.getId());
            docente.setPersona(personaForm);
            logger.debug("creando docente para persona {}", personaForm.getId());
            docente.setEstado(DocenteEstadoEnum.ACT.name());
            docente.setCodigo(this.getCodigo());
            logger.debug("validando codiggo docente xxx {}", docente.getCodigo());
            docenteDAO.save(docente);
            logger.debug("creado docente {}", docente.getId());
            return;
        }

        Docente docenteDb = docenteDAO.findPersona(persona);
        logger.debug("existe docente en db {}", (docenteDb != null));
        if (docenteDb != null) {
            logger.debug("actualizando docente  {}", docenteDb.getId());
            docenteDb.setDepartamentoAcademico(docente.getDepartamentoAcademico());
            docenteDb.setModalidadEstudio(docente.getModalidadEstudio());
            docenteDAO.update(docenteDb);
            return;
        }

        logger.debug("guardando docente ...");
        docente.setEstado(DocenteEstadoEnum.ACT.name());
        docente.setCodigo(this.getCodigo());
        docenteDAO.save(docente);
        logger.debug("docente  guardado  {}", docente.getId());
    }

    private String getCodigo() {
        logger.debug("generando codigo");
        String timestamp = TypesUtil.getUnixTime().toString();
        logger.debug("timestamp  {}", timestamp);
        String codigo = timestamp.substring(timestamp.length() - 4, timestamp.length());
        logger.debug("codigo  {}", codigo);
        Docente docente = docenteDAO.findByCode(codigo);
        logger.debug("docente  {}", (docente != null));
        while (docente != null) {
            timestamp = TypesUtil.getUnixTime().toString();
            codigo = timestamp.substring(timestamp.length() - 4, timestamp.length());
            docente = docenteDAO.findByCode(codigo);
        }
        logger.debug("codigo unico  {}", codigo);
        return codigo;
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
                throw new PhobosException("El correo ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailEmpresaConPersona(String email, Persona persona) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailEmpresaWithoutPersona(persona);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
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
    public String validarEmailByDocente(String email, Docente docente) {
        Persona persona = docente.getPersona();
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
    public String validarEmailEmpresaByDocente(String email, Docente docente) {

        Persona persona = docente.getPersona();
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

    @Override
    @Transactional
    public void estado(Docente docente) {

        Docente docenteBD = docenteDAO.find(docente.getId());

        if (DocenteEstadoEnum.INA.name().equalsIgnoreCase(docenteBD.getEstado())) {
            docenteBD.setEstado(DocenteEstadoEnum.ACT.name());
        } else {
            docenteBD.setEstado(DocenteEstadoEnum.INA.name());
        }
        docenteDAO.update(docenteBD);
    }

    @Override
    public Persona findPersona(Persona personaTmp) {
        if (personaTmp.getTipoDocumento().getId() == null) {
            throw new PhobosException("El tipo de documento no debe de ser nulo ");
        }
        return personaDAO.findByDocIdentidad(personaTmp.getTipoDocumento(), personaTmp.getNumeroDocIdentidad());
    }

    @Override
    public Docente findDocenteByPersona(Persona persona) {
        return docenteDAO.findDocenteByPersona(persona);
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudio(Compania compania) {
        return modalidadEstudioDAO.allActivoByCompania(compania);
    }

}
