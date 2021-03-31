package pe.edu.lamolina.amauta.controller.academico.profesor.informacionprofesor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.model.rrhh.DedicacionDocente;
import pe.edu.lamolina.model.rrhh.SituacionDocente;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.EmpresaEtiquetadaDAO;
import pe.edu.lamolina.amauta.dao.general.PaisDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaCuentaBancariaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.rrhh.CategoriaDocenteDAO;
import pe.edu.lamolina.amauta.dao.rrhh.DedicacionDocenteDAO;
import pe.edu.lamolina.amauta.dao.rrhh.SituacionDocenteDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.EmpresaEtiquetada;
import pe.edu.lamolina.model.general.PersonaCuentaBancaria;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

@Service
@Transactional(readOnly = true)
public class InformacionProfesorServiceImp implements InformacionProfesorService {

    @Autowired
    CategoriaDocenteDAO categoriaDocenteDAO;
    @Autowired
    DedicacionDocenteDAO dedicacionDocenteDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    EmpresaEtiquetadaDAO empresaEtiquetadaDAO;

    @Autowired
    HoraDAO horaDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    PaisDAO paisDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    PersonaCuentaBancariaDAO personaCuentaBancariaDAO;
    @Autowired
    RolDAO rolDAO;
    @Autowired
    SituacionDocenteDAO situacionDocenteDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    UsuarioDAO usuarioDAO;
    @Autowired
    UsuarioRolDAO usuarioRolDAO;
    @Autowired
    ColaboradorDAO colaboradorDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Docente findDocente(Docente docente) {
        return docenteDAO.findByDocente(docente);
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudio(Compania compania) {
        return modalidadEstudioDAO.allActivoByCompania(compania);
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
    public List<SituacionDocente> allSituaciones() {
        return situacionDocenteDAO.all();
    }

    @Override
    public List<CategoriaDocente> allCategorias() {
        return categoriaDocenteDAO.all();
    }

    @Override
    public List<DedicacionDocente> allDedicaciones() {
        return dedicacionDocenteDAO.all();
    }

    @Override
    public List<Hora> allHoras() {
        return horaDAO.all();
    }

    @Override
    public List<EmpresaEtiquetada> allBancos() {
        return empresaEtiquetadaDAO.allBancos();
    }

    @Override
    public List<PersonaCuentaBancaria> allCtasBancarias(Persona persona) {
        List<PersonaCuentaBancaria> ctasOrden = new ArrayList();
        List<PersonaCuentaBancaria> ctas = personaCuentaBancariaDAO.allByPersona(persona);
        for (PersonaCuentaBancaria cta : ctas) {
            if (cta.getEstadoEnum() == EstadoEnum.ACT) {
                ctasOrden.add(cta);
                break;
            }
        }
        for (PersonaCuentaBancaria cta : ctas) {
            if (cta.getEstadoEnum() != EstadoEnum.ACT) {
                ctasOrden.add(cta);
            }
        }
        return ctasOrden;
    }

    @Override
    public Persona findPersona(Persona persona) {
        return personaDAO.find(persona.getId());
    }

    @Override
    @Transactional
    public void saveCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds) {
        boolean sinCta = StringUtils.isBlank(cuentaBanco.getNumeroCuenta());
        boolean sinCci = StringUtils.isBlank(cuentaBanco.getCuentaInterbancaria());
        Assert.isFalse(sinCci && sinCta, "Debe indicar el Nº de la cuenta bancaria o el CCI");

        EmpresaEtiquetada banco = empresaEtiquetadaDAO.find(cuentaBanco.getBanco().getId());
        boolean esBCP = banco.getEmpresa().getNumeroDocIdentidad().equals(GlobalConstantine.RUC_BCP);
        if (esBCP) {
            Assert.isFalse(sinCta, "Es obligatorio indicar el Nº de la cuenta bancaria si son del BCP");
        } else {
            Assert.isFalse(sinCci, "Es obligatorio indicar el CCI para bancos diferentes del BCP");
        }

        PersonaCuentaBancaria ctaBancoActiva = personaCuentaBancariaDAO.findActivo(cuentaBanco.getPersona());
        if (ctaBancoActiva == null) {
            cuentaBanco.setEstadoEnum(EstadoEnum.ACT);
        } else {
            cuentaBanco.setEstadoEnum(EstadoEnum.INA);
        }

        cuentaBanco.setNumeroCuenta(sinCta ? null : cuentaBanco.getNumeroCuenta().trim());
        cuentaBanco.setCuentaInterbancaria(sinCci ? null : cuentaBanco.getCuentaInterbancaria().trim());
        cuentaBanco.setUserRegistro(ds.getUsuario());
        cuentaBanco.setFechaRegistro(new Date());
        personaCuentaBancariaDAO.save(cuentaBanco);
    }

    @Override
    @Transactional
    public void deleteCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds) {
        PersonaCuentaBancaria cuentaBancoBD = personaCuentaBancariaDAO.find(cuentaBanco.getId());
        Assert.isNotNull(cuentaBancoBD, "No se pudo ubicar el registro de esta cuenta bancaria");
        personaCuentaBancariaDAO.delete(cuentaBancoBD);
    }

    @Override
    @Transactional
    public void activarCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds) {
        PersonaCuentaBancaria cuentaBancoBD = personaCuentaBancariaDAO.find(cuentaBanco.getId());
        Assert.isNotNull(cuentaBancoBD, "No se pudo ubicar el registro de esta cuenta bancaria");

        PersonaCuentaBancaria ctaBancoActiva = personaCuentaBancariaDAO.findActivo(cuentaBancoBD.getPersona());
        if (ctaBancoActiva == null) {
        } else {
            ctaBancoActiva.setEstadoEnum(EstadoEnum.INA);
            personaCuentaBancariaDAO.update(ctaBancoActiva);
        }
        cuentaBancoBD.setEstadoEnum(EstadoEnum.ACT);
        personaCuentaBancariaDAO.update(cuentaBancoBD);

    }

    @Override
    @Transactional
    public void updateDocentePersona(Persona persona, Long idDocente, DataSessionPivot ds) {
        persona = personaDAO.find(persona.getId());
        Docente docente = docenteDAO.find(idDocente);

        String email = docente.getPersona().getEmailCompania();

        Persona personaDelete = personaDAO.find(docente.getPersona().getId());
        personaDelete.setEmailCompania(null);
        personaDelete.setEmail(null);

        personaDAO.updateColumns(personaDelete, "emailCompania", "email");

        persona.setEmail(email);
        persona.setEmailCompania(email);
        personaDAO.updateColumns(persona, "emailCompania", "email");

        docente.setPersona(persona);
        docente.setUserModifica(ds.getUsuario());
        docente.setFechaModifica(new Date());
        docenteDAO.updateColumns(docente, "persona", "userModifica", "fechaModifica");

        Usuario usuarioDb = usuarioDAO.findByGoogleEmail(persona.getEmailCompania());
        if (usuarioDb != null) {
            logger.debug("-> Actualizando usuario");
            usuarioDb.setPersona(persona);
            usuarioDb.setUserModifica(ds.getUsuario());
            usuarioDb.setGoogle(docente.getPersona().getEmailCompania());
            usuarioDb.setFechaModifica(new Date());
            usuarioDAO.update(usuarioDb);
        } else {
            logger.debug("-> Creando usuario");
            usuarioDb = new Usuario();
            usuarioDb.setEstadoEnum(UserEstadoEnum.ACT);
            usuarioDb.setFechaRegistro(new Date());
            usuarioDb.setPersona(persona);
            usuarioDb.setUserRegistro(ds.getUsuario());
            usuarioDb.setGoogle(persona.getEmailCompania());
            usuarioDAO.save(usuarioDb);
        }

        Rol rol = rolDAO.findByCode(RolEnum.DOC);
        UsuarioRol userRolDB = usuarioRolDAO.findByUsuarioAndRolAndEstadoUsuRol(usuarioDb, rol, UserEstadoEnum.ACT);
        logger.debug("Tiene Rol? {}", (userRolDB != null));
        if (userRolDB == null) {
            logger.debug("-> Asignando Rol de Docente");
            userRolDB = new UsuarioRol();
            userRolDB.setEstadoEnum(UserEstadoEnum.ACT);
            userRolDB.setFechaInicio(new Date());
            userRolDB.setRol(rol);
            userRolDB.setUsuario(usuarioDb);
            userRolDB.setUserRegistro(ds.getUsuario());
            userRolDB.setFechaRegistro(new Date());
            usuarioRolDAO.save(userRolDB);
        } else {
            logger.debug("Rol Existente como docente.");
        }

        List<Colaborador> colaboradors = colaboradorDAO.allActivosByPersona(personaDelete);
        for (Colaborador colaborador : colaboradors) {
            colaborador.setPersona(persona);
            colaborador.setUserModificacion(ds.getUsuario());
            colaborador.setFechaModificacion(new Date());
            colaboradorDAO.updateColumns(colaborador, "persona", "userModificacion", "fechaModificacion");
        }

    }

}
