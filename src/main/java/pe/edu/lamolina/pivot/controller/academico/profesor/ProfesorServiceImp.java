package pe.edu.lamolina.pivot.controller.academico.profesor;

import com.google.common.base.Strings;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.aws.S3Service;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ProfesorServiceImp implements ProfesorService {

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    UsuarioRolDAO usuarioRolDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    PaisDAO paisDAO;

    @Autowired
    S3Service s3Service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Docente> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> dptos) {
        return docenteDAO.allByFilter(filter, dptos);
    }

    @Override
    public Docente find(Docente docente) {
        return docenteDAO.findByDocente(docente);
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.all();
    }

    @Override
    @Transactional
    public void save(Docente docente, DataSessionPivot ds) {
        Usuario user = ds.getUsuario();
        logger.debug("save docente");

        Persona personaDoc = this.findPersonaByDocIdentidad(docente.getPersona());

        logger.debug("existe persona {}", (personaDoc != null));
        if (personaDoc == null) {
            Persona personaForm = docente.getPersona();
            if (Strings.isNullOrEmpty(personaForm.getEmailCompania())) {
                throw new PhobosException("El correo principal es obligatorio");
            }
            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            if (!Strings.isNullOrEmpty(personaForm.getEmail())) {
                this.validarEmailsinPersona(personaForm.getEmail());
            }
            personaForm.setEstadoEnum(PersonaEstadoEnum.ACT);
            if (Strings.isNullOrEmpty(personaForm.getFoto())) {
                personaForm.setFoto(null);
            } else {
                this.uploadS3(personaForm.getRutaFotoTemporal());
            }
            personaForm.setFechaRegistro(new Date());
            personaForm.setUserRegistro(user);
            personaDAO.save(personaForm);
            docente.setPersona(personaForm);
        } else {

            ObjectUtil.eliminarAttrSinId(personaDoc, "paisNacer");
            ObjectUtil.eliminarAttrSinId(personaDoc, "ubicacionNacer");
            ObjectUtil.eliminarAttrSinId(personaDoc, "nacionalidad");
            ObjectUtil.eliminarAttrSinId(personaDoc, "paisDomicilio");
            ObjectUtil.eliminarAttrSinId(personaDoc, "ubicacionDomicilio");
            ObjectUtil.eliminarAttrSinId(personaDoc, "tipoDocumento");

            Persona personaForm = docente.getPersona();
            this.validarDNI(personaForm);
            if (Strings.isNullOrEmpty(personaForm.getEmailCompania())) {
                throw new PhobosException("El correo principal es obligatorio");
            }
            this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaForm);
            if (!Strings.isNullOrEmpty(personaForm.getEmail())) {
                this.validarEmailConPersona(personaForm.getEmail(), personaForm);
            }
            Persona persona = this.getPersonaBDbasic(personaForm);
            if (persona.getFechaValidacionReniec() == null) {
                persona = this.getPersonaBDreniec(personaForm);
            }
            if (Strings.isNullOrEmpty(personaForm.getRutaFotoTemporal())) {
                persona.setRutaFotoTemporal(null);
            } else {
                persona.setRutaFotoTemporal(personaForm.getFoto());
                this.uploadS3(personaForm.getRutaFotoTemporal());
            }
        }

        List<Docente> docentesBD = docenteDAO.allByPersona(docente.getPersona());
        logger.debug("existe docente en db {}", (docentesBD != null));
        if (docentesBD.isEmpty()) {
            throw new PhobosException("Docente ya existe");
        }

        logger.debug("guardando docente ...");
        docente.setEstado(DocenteEstadoEnum.ACT);
        docente.setCodigo(this.getCodigo());
        docente.setFechaRegistro(new Date());
        docente.setUserRegistro(user);
        docenteDAO.save(docente);
        logger.debug("docente  guardado  {}", docente.getId());

        Usuario usuarioDb = usuarioDAO.findByPersona(docente.getPersona());
        logger.debug("existe usuario en db {}", (usuarioDb != null));
        if (usuarioDb == null) {
            usuarioDb = new Usuario();
            usuarioDb.setEstadoEnum(UserEstadoEnum.ACT);
            usuarioDb.setFechaRegistro(new Date());
            usuarioDb.setUserRegistro(user);
            usuarioDb.setPersona(docente.getPersona());
            usuarioDb.setGoogle(docente.getPersona().getEmailCompania());
            usuarioDAO.save(usuarioDb);
        } else {
            logger.debug("actualizando usuario");
            usuarioDb.setFechaRegistro(new Date());
            usuarioDb.setGoogle(docente.getPersona().getEmailCompania());
            usuarioDAO.update(usuarioDb);
        }

        Rol rol = rolDAO.findByCode(RolEnum.DOC);
        UsuarioRol userRol = usuarioRolDAO.findByUsuarioAndRol(usuarioDb, rol);
        if (userRol == null) {
            userRol = new UsuarioRol();
            userRol.setEstado(UserEstadoEnum.ACT);
            userRol.setFechaInicio(new Date());
            userRol.setRol(rol);
            userRol.setUsuario(usuarioDb);
            userRol.setUserRegistro(ds.getUsuario());
            usuarioRolDAO.save(userRol);
        }
    }

    @Override
    @Transactional
    public void update(Docente docente, DataSessionPivot ds) {
        logger.debug("Docente Actualizado -> {} ...", docente.getId());
        Usuario user = ds.getUsuario();
        logger.debug("Actualizado por usuario -> {}", user.getId());
        Persona personaForm = docente.getPersona();

        ObjectUtil.eliminarAttrSinId(personaForm, "paisNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "nacionalidad");
        ObjectUtil.eliminarAttrSinId(personaForm, "paisDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "tipoDocumento");

        logger.debug("Actualizando persona -> {}", personaForm.getId());
        this.validarDNI(personaForm);
        logger.debug("-> DNI validado");
        if (Strings.isNullOrEmpty(personaForm.getEmailCompania())) {
            throw new PhobosException("El correo principal es obligatorio.");
        }
        this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaForm);
        logger.debug("-> Email-Compania validado.");
        if (!Strings.isNullOrEmpty(personaForm.getEmail())) {
            this.validarEmailConPersona(personaForm.getEmail(), personaForm);
            logger.debug("-> Email-Persona validado.");
        }
        Persona persona = this.getPersonaBDbasic(personaForm);
        logger.debug("-> Dato basicos de persona actualizados");
        if (persona.getFechaValidacionReniec() == null) {
            persona = this.getPersonaBDreniec(personaForm);
            logger.debug("-> Dato basicos de persona actualizados");
        }
        if (Strings.isNullOrEmpty(personaForm.getRutaFotoTemporal())) {
            persona.setRutaFotoTemporal(null);
        } else {
            persona.setRutaFotoTemporal(personaForm.getRutaFotoTemporal());
            this.uploadS3(personaForm.getRutaFotoTemporal());
        }
        personaDAO.update(persona);
        logger.debug("***Resolviendo en Tabla Docente***");
        Docente docenteBD = docenteDAO.findByDocente(docente);
        docenteBD.setPersona(persona);
        docenteBD.setFechaModifica(new Date());
        docenteBD.setUserModifica(user);
        docenteBD.setDepartamentoAcademico(docente.getDepartamentoAcademico());
        docenteBD.setModalidadEstudio(docente.getModalidadEstudio());
        docenteDAO.update(docenteBD);
        logger.debug("***Resolviendo en Tabla Usuario***");
        Usuario usuarioDb = usuarioDAO.findByPersona(docente.getPersona());
        logger.debug("Está como usuario? {}", (usuarioDb != null));
        if (usuarioDb != null) {
            logger.debug("-> Actualizando usuario");
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
            usuarioDb.setUserRegistro(user);
            usuarioDb.setGoogle(persona.getEmailCompania());
            usuarioDAO.save(usuarioDb);
        }
        logger.debug("***Resolviendo en Tabla Usuario_Rol***");
        Rol rol = rolDAO.findByCode(RolEnum.DOC);
        UsuarioRol userRolDB = usuarioRolDAO.findByUsuarioAndRol(usuarioDb, rol);
        logger.debug("Tiene Rol? {}", (userRolDB != null));
        if (userRolDB == null) {
            logger.debug("-> Asignando Rol de Docente");
            userRolDB = new UsuarioRol();
            userRolDB.setEstado(UserEstadoEnum.ACT);
            userRolDB.setFechaInicio(new Date());
            userRolDB.setRol(rol);
            userRolDB.setUsuario(usuarioDb);
            userRolDB.setUserRegistro(ds.getUsuario());
            userRolDB.setFechaRegistro(new Date());
            usuarioRolDAO.save(userRolDB);
        } else {
            logger.debug("Rol Existente como docente.");
        }
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

    @Transactional
    private Persona getPersonaBDbasic(Persona persona) {

        Persona personaBD = personaDAO.find(persona.getId());

        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, persona, Arrays.asList("email", "emailCompania", "sexo", "fechaNacer", "direccion", "celular", "telefono"));
        boolean ubiDomCambio = persona.getUbicacionDomicilio() == null && personaBD.getUbicacionDomicilio() == null;
        if (!ubiDomCambio) {
            Long idForm = (Long) ObjectUtil.getParentTree(persona, "ubicacionDomicilio.id");
            Long idDb = (Long) ObjectUtil.getParentTree(personaBD, "ubicacionDomicilio.id");
            if (idForm != idDb) {
                sinCambios = false;
            }
        }

        boolean ubiNacCambio = persona.getUbicacionNacer() == null && personaBD.getUbicacionNacer() == null;
        if (!ubiNacCambio) {
            Long idForm = (Long) ObjectUtil.getParentTree(persona, "ubicacionNacer.id");
            Long idDb = (Long) ObjectUtil.getParentTree(personaBD, "ubicacionNacer.id");
            if (idForm != idDb) {
                sinCambios = false;
            }
        }
        if (sinCambios) {
            logger.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }

        List<Pais> paisesBD = paisDAO.all();
        Map<Long, Pais> mapPaises = TypesUtil.convertListToMap("id", paisesBD);

        personaBD.setSexo(persona.getSexo());
        personaBD.setFechaNacer(persona.getFechaNacer());
        personaBD.setDireccion(persona.getDireccion());
        personaBD.setCelular(persona.getCelular());
        personaBD.setTelefono(persona.getTelefono());
        personaBD.setEmail(persona.getEmail());
        personaBD.setEmailCompania(persona.getEmailCompania());
        personaBD.setUbicacionDomicilio(persona.getUbicacionDomicilio());
        personaBD.setUbicacionNacer(persona.getUbicacionNacer());

        personaBD.setPaisDomicilio(findPais(persona.getPaisDomicilio(), mapPaises));
        personaBD.setPaisNacer(findPais(persona.getPaisNacer(), mapPaises));
        personaBD.setNacionalidad(findPais(persona.getNacionalidad(), mapPaises));
        personaDAO.update(personaBD);
        return personaBD;
    }

    private Pais findPais(Pais pais, Map<Long, Pais> mapPaisesBD) {
        if (pais == null) {
            return null;
        }
        Pais paisBD = mapPaisesBD.get(pais.getId());
        return paisBD;
    }

    @Transactional
    private Persona getPersonaBDreniec(Persona persona) {

        Persona personaBD = personaDAO.find(persona.getId());
        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, persona, Arrays.asList("paterno", "materno", "nombres", "numeroDocIdentidad"));
        sinCambios = sinCambios && (persona.getTipoDocumento().getId() == personaBD.getTipoDocumento().getId().longValue());
        if (sinCambios) {
            logger.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }

        personaBD.setTipoDocumento(persona.getTipoDocumento());
        personaBD.setNumeroDocIdentidad(persona.getNumeroDocIdentidad());
        personaBD.setNombres(persona.getNombres());
        personaBD.setPaterno(persona.getPaterno());
        personaBD.setMaterno(persona.getMaterno());
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
            docenteBD.setEstado(DocenteEstadoEnum.ACT);
        } else {
            docenteBD.setEstado(DocenteEstadoEnum.INA);
        }
        docenteDAO.update(docenteBD);
    }

    @Override
    public Persona findPersonaByDocIdentidad(Persona personaTmp) {
        if (personaTmp.getTipoDocumento().getId() == null) {
            throw new PhobosException("El tipo de documento no debe de ser nulo ");
        }
        return personaDAO.findByDocIdentidad(personaTmp.getTipoDocumento(), personaTmp.getNumeroDocIdentidad());
    }

    @Override
    public Docente findDocenteByDocente(Docente docente) {
        return docenteDAO.findByDocente(docente);
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudio(Compania compania) {
        return modalidadEstudioDAO.allActivoByCompania(compania);
    }

//    private void saveFoto(String avatar) {
//        String oldName = Constantine.TMP_DIR + avatar;
//        String newName = Constantine.AVATAR_DIR + avatar;
//        File directorio = new File(Constantine.AVATAR_DIR);
//        if (!directorio.isDirectory()) {
//            directorio.mkdirs();
//        }
//        FileHelper.renameFile(oldName, newName);
//    }
    
    @Override
    public Persona findPersona(Persona persona) {
        return personaDAO.find(persona.getId());
    }

    public void uploadS3(String fileName) {
        logger.debug("upload to s3 args   {}  {}   {}  {} {}", Constantine.S3_BUKET, Constantine.S3_DIR_FOTO_TMP, Constantine.TMP_DIR, fileName, true);
        File f = new File(Constantine.TMP_DIR + fileName);
        if (f.exists() && !f.isDirectory()) {
            s3Service.uploadFile(Constantine.S3_BUKET, Constantine.S3_DIR_FOTO_TMP, Constantine.TMP_DIR, fileName, true);
        }
    }

}
