package pe.edu.lamolina.amauta.controller.academico.profesor;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.general.persona.PersonaService;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaHistorialDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.PersonaHistorial;
import pe.edu.lamolina.model.horario.HorarioSeccion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ProfesorServiceImp implements ProfesorService {

    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final ContenidoCartaDAO contenidoCartaDAO;
    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private final DocenteDAO docenteDAO;
    private final DocenteSeccionDAO docenteSeccionDAO;
    private final HorarioSeccionDAO horarioSeccionDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;
    private final OficinaDAO oficinaDAO;
    private final PersonaDAO personaDAO;
    private final PersonaHistorialDAO personaHistorialDAO;
    private final RolDAO rolDAO;
    private final TipoDocIdentidadDAO tipoDocIdentidadDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioRolDAO usuarioRolDAO;

    private final StorageService swiftService;
    private final PersonaService personaService;

    @Override
    public List<Docente> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> dptos) {
        return docenteDAO.allByFilter(filter, dptos);
    }

    @Override
    public List<Docente> allByDepartamentoDynatable(DynatableFilter filter, List<DepartamentoAcademico> departament, CicloAcademico cicloAcademicos) {
        List<Docente> docentes = docenteDAO.allByFacultadesDyantable(filter, departament);
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allByDocente(docentes, cicloAcademicos);
        Map<Long, List<DocenteSeccion>> mapDocentesSeccion = TypesUtil.convertListToMapList("docente.id", docentesSeccion);
        for (Docente docente : docentes) {
            List<DocenteSeccion> docentesSeccionByDocente = mapDocentesSeccion.getOrDefault(docente.getId(), new ArrayList<>());
            List<Seccion> secciones = docentesSeccionByDocente.stream()
                    .filter(x -> Arrays.asList(SeccionEstadoEnum.ACT).contains(x.getSeccion().getEstadoEnum()))
                    .map(x -> x.getSeccion())
                    .collect(Collectors.toList());
//            Long seccionesPreCount = secciones.stream()
//                    .filter(x -> x.getGrupoSeccion().getCurso().isPregrado())
//                    .distinct().count();
//            Long seccionesPosCount = secciones.stream()
//                    .filter(x -> x.getGrupoSeccion().getCurso().isPostgrado())
//                    .distinct().count();
            Long seccionesPreCount = secciones.stream()
                    .filter(x -> !x.getGrupoSeccion().getAnexoBoletin().getAnexoSuperior().isAnexoCursosPostgrado())
                    .distinct().count();
            Long seccionesPosCount = secciones.stream()
                    .filter(x -> x.getGrupoSeccion().getAnexoBoletin().getAnexoSuperior().isAnexoCursosPostgrado())
                    .distinct().count();
            docente.setCantSeccionesPos(seccionesPosCount);
            docente.setCantSeccionesPre(seccionesPreCount);
        }
        return docentes;
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudioByCodes(List<ModalidadEstudioEnum> codes, Compania compania) {
        return modalidadEstudioDAO.allActivoByCodesCompania(codes, compania);
    }

    @Override
    public Docente find(Docente docente) {
        return docenteDAO.findByDocente(docente);
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    @Transactional
    public void save(Docente docente, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Usuario user = ds.getUsuario();
        log.debug("save docente");

        Persona personaDoc = this.findPersonaByDocIdentidad(docente.getPersona());
        log.debug("existe persona {}", (personaDoc != null));

        Persona personaForm = docente.getPersona();
        String personaJsonInicial = null;
        boolean esPersonaNueva = false;
        boolean hayFotoNueva = true;

        if (personaDoc == null) {
            Assert.isNotBlank(personaForm.getEmailCompania(), "El correo principal es obligatorio");

            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            if (!Strings.isNullOrEmpty(personaForm.getEmail())) {
                this.validarEmailsinPersona(personaForm.getEmail());
            }

            if (Strings.isNullOrEmpty(personaForm.getFoto())) {
                personaForm.setFoto(null);
            } else {
                this.uploadS3(personaForm.getFoto());
            }

            personaForm.setEstadoEnum(PersonaEstadoEnum.ACT);
            personaForm.setFechaRegistro(today.toDate());
            personaForm.setUserRegistro(user);
            personaDAO.save(personaForm);

            docente.setPersona(personaForm);
            esPersonaNueva = true;

        } else {
            this.validarDNI(personaForm);
            Assert.isNotBlank(personaForm.getEmailCompania(), "El correo principal es obligatorio");

            this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaForm);
            if (!Strings.isNullOrEmpty(personaForm.getEmail())) {
                this.validarEmailConPersona(personaForm.getEmail(), personaForm);
            }

            Persona personaBD = personaDAO.find(personaForm.getId());
            hayFotoNueva = this.hayFotoNueva(personaBD, personaForm);
            boolean hayCambios = this.revisarPersona(personaBD, personaForm, hayFotoNueva, null, null);
            personaJsonInicial = personaService.getPersonaJsonValidacion(personaBD);
            if (hayCambios) {
                this.savePersona(personaBD, personaForm, ds);
            }

            docente.setPersona(personaBD);
        }

        List<Docente> docentesBD = docenteDAO.allByPersona(docente.getPersona());
        List<Docente> docentesPRE_EPG = docentesBD.stream()
                .filter(x -> !x.getModalidadEstudio().isPregrado() || !x.getModalidadEstudio().isPostgrado())
                .collect(Collectors.toList());
        log.debug("existe docente en db {}", (docentesPRE_EPG != null));
        Assert.isTrue(docentesPRE_EPG.isEmpty(), "Docente ya existe");

        log.debug("guardando docente ...");
        docente.setEstadoEnum(DocenteEstadoEnum.ACT);
        docente.setCodigo(this.getCodigo());
        docente.setFechaRegistro(today.toDate());
        docente.setUserRegistro(user);
        docenteDAO.save(docente);
        log.debug("docente  guardado  {}", docente.getId());

        if (hayFotoNueva) {
            this.uploadS3(personaForm.getFoto());
            docente.getPersona().setFoto(personaForm.getFoto());
            docente.getPersona().setUserModificacion(ds.getUsuario());
            personaDAO.update(docente.getPersona());
        }

        if (esPersonaNueva) {
            personaService.registrarValidacionDocente(docente.getPersona(), docente, ds);
        } else {
            String personaJsonFinal = personaService.getPersonaJsonValidacion(docente.getPersona());
            if (!personaJsonFinal.equals(personaJsonInicial)) {
                personaService.registrarValidacionDocente2(docente.getPersona(), docente, personaJsonInicial, ds);
            }
        }

        this.crearUsuario(docente.getPersona(), ds);

    }

    @Override
    @Transactional
    public Persona update(Docente docente, DataSessionPivot ds) {
        log.debug("Docente Actualizado -> {} ...", docente.getId());
        log.debug("Actualizado por usuario -> {}", ds.getUsuario().getId());

        Persona personaForm = docente.getPersona();

        log.debug("Actualizando persona -> {}", personaForm.getId());
        this.validarDNI(personaForm);
        Assert.isNotBlank(personaForm.getEmailCompania(), "El correo principal es obligatorio");

        this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaForm);
        if (!Strings.isNullOrEmpty(personaForm.getEmail())) {
            this.validarEmailConPersona(personaForm.getEmail(), personaForm);
        }

        Persona personaBD = personaDAO.find(personaForm.getId());
        String personaJsonInicial = personaService.getPersonaJsonValidacion(personaBD);

        Docente docenteBDD = docenteDAO.find(docente.getId());
        Docente docenteForm = docente;

        boolean hayFotoNueva = !Strings.isNullOrEmpty(personaForm.getRutaFotoTemporal());
        boolean hayCambios = this.revisarPersona(personaBD, personaForm, hayFotoNueva, docenteBDD, docenteForm);

//        hayCambios = this.revisarDocente(docenteBDD, docenteForm);
        if (hayCambios) {
            this.savePersona(personaBD, personaForm, ds);
        }

        if (hayFotoNueva) {
            this.uploadS3(personaForm.getRutaFotoTemporal());
            personaBD.setRutaFotoDocumento(this.getPathFotoDocente(personaForm.getRutaFotoTemporal()));
            personaBD.setUserModificacion(ds.getUsuario());
            personaDAO.update(personaBD);
        }

        String personaJsonFinal = personaService.getPersonaJsonValidacion(personaBD);
        if (!personaJsonFinal.equals(personaJsonInicial)) {
            personaService.registrarValidacionDocente2(personaBD, docente, personaJsonInicial, ds);
        }

        log.debug("***Resolviendo en Tabla Docente***");
        Docente docenteBD = docenteDAO.findByDocente(docente);
        docenteBD.setPersona(personaBD);
        docenteBD.setFechaModifica(new Date());
        docenteBD.setUserModifica(ds.getUsuario());
        docenteBD.setDepartamentoAcademico(docente.getDepartamentoAcademico());
        docenteBD.setModalidadEstudio(docente.getModalidadEstudio());
        docenteDAO.update(docenteBD);

        this.crearUsuario(personaBD, ds);

        return null;
    }

    private String getCodigo() {
        log.debug("generando codigo");
        String timestamp = TypesUtil.getUnixTime().toString();
        log.debug("timestamp  {}", timestamp);
        String codigo = timestamp.substring(timestamp.length() - 4, timestamp.length());
        log.debug("codigo  {}", codigo);
        Docente docente = docenteDAO.findByCode(codigo);
        log.debug("docente  {}", (docente != null));
        while (docente != null) {
            timestamp = TypesUtil.getUnixTime().toString();
            codigo = timestamp.substring(timestamp.length() - 4, timestamp.length());
            docente = docenteDAO.findByCode(codigo);
        }
        log.debug("codigo unico  {}", codigo);
        return codigo;
    }

    private void validarDNI(Persona personaForm) {
        TipoDocIdentidad tipoDocForm = personaForm.getTipoDocumento();
        Assert.isNotNull(tipoDocForm, "Debe indicar el tipo de documento de identidad");
        Assert.isNotNull(tipoDocForm.getId(), "Debe indicar el tipo de documento de identidad");

        TipoDocIdentidad tipoDocBD = tipoDocIdentidadDAO.find(tipoDocForm.getId());
        personaForm.setTipoDocumento(tipoDocBD);

        Persona personaBD = personaDAO.findByDocIdentidad(tipoDocForm, personaForm.getNumeroDocIdentidad());
        if (personaBD == null) {
            return;
        }

        Assert.isTrue(personaBD.getId().equals(personaForm.getId()),
                "El " + tipoDocBD.getSimbolo()
                + " ingresado ya se encuentra relacionado con otra persona: "
                + personaBD.getApellidosNombres() + " (" + personaBD.getIdentificacion() + ")");
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

    private boolean revisarPersona(Persona personaBD, Persona personaForm, boolean hayFotoNueva, Docente docenteBDD, Docente docenteForm) {
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "paisNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "nacionalidad");
        ObjectUtil.eliminarAttrSinId(personaForm, "paisDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "tipoDocumento");

        String personaJsonBD = this.getPersonaJson(personaBD);
        String personaJsonForm = this.getPersonaJson(personaForm);
        Boolean actualizarDocente = this.validarDptoModalidad(docenteBDD, docenteForm);

        Assert.isTrue(hayFotoNueva || !personaJsonBD.equals(personaJsonForm) || actualizarDocente, "No hay cambios registrados");

        return !personaJsonBD.equals(personaJsonForm);
    }

    private void crearUsuario(Persona persona, DataSessionPivot ds) {
        Usuario usuario = usuarioDAO.findActivoByPersona(persona);
        log.debug("existe usuario en db {}", (usuario != null));

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setEstadoEnum(UserEstadoEnum.ACT);
            usuario.setFechaRegistro(new Date());
            usuario.setUserRegistro(ds.getUsuario());
            usuario.setPersona(persona);
            usuario.setGoogle(persona.getEmailCompania());
            usuarioDAO.save(usuario);

        } else {
            log.debug("actualizando usuario");
            if (!usuario.getGoogle().equals(persona.getEmailCompania())) {
                Usuario usuarioNew = new Usuario();
                usuarioNew.setEstadoEnum(UserEstadoEnum.INA);
                usuarioNew.setFechaRegistro(new Date());
                usuarioNew.setUserRegistro(ds.getUsuario());
                usuarioNew.setPersona(persona);
                usuarioNew.setGoogle(persona.getEmailCompania());
                usuarioNew.setUserActivo(usuario);
                usuarioDAO.save(usuarioNew);
            }

        }

        Rol rol = rolDAO.findByCode(RolEnum.DOC);
        UsuarioRol userRol = usuarioRolDAO.findByUsuarioRol(usuario, rol);
        if (userRol == null) {
            userRol = new UsuarioRol();
            userRol.setEstadoEnum(UserEstadoEnum.ACT);
            userRol.setFechaInicio(new Date());
            userRol.setRol(rol);
            userRol.setUsuario(usuario);
            userRol.setUserRegistro(ds.getUsuario());
            usuarioRolDAO.save(userRol);
        }
    }

    private boolean hayFotoNueva(Persona personaBD, Persona personaForm) {
        String fotoBD = personaBD.getFoto();
        String fotoForm = personaForm.getFoto();

        if (StringUtils.isBlank(fotoBD)) {
            fotoBD = null;
        }

        if (StringUtils.isBlank(fotoForm)) {
            fotoForm = null;
        }

        personaForm.setFoto(fotoForm);
        return !Objects.equal(fotoBD, fotoForm) && fotoForm != null;
    }

    private void savePersona(Persona personaBD, Persona personaForm, DataSessionPivot ds) {
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setNombres(personaForm.getNombres());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setFechaNacer(personaForm.getFechaNacer());
        personaBD.setEmail(personaForm.getEmail());
        personaBD.setEmailCompania(personaForm.getEmailCompania());
        personaBD.setDireccion(personaForm.getDireccion());
        personaBD.setCelular(personaForm.getCelular());
        personaBD.setTelefono(personaForm.getTelefono());

        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        personaBD.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
        personaBD.setUbicacionNacer(personaForm.getUbicacionNacer());
        personaBD.setPaisDomicilio(personaForm.getPaisDomicilio());
        personaBD.setPaisNacer(personaForm.getPaisNacer());
        personaBD.setNacionalidad(personaForm.getNacionalidad());
        personaBD.setUserModificacion(ds.getUsuario());
        personaDAO.update(personaBD);

        String tipoDocBDJson = this.getPersonaTipoDocJson(personaBD);
        String tipoDocFormJson = this.getPersonaTipoDocJson(personaForm);
        boolean sonTipoDocDesiguales = !tipoDocBDJson.equals(tipoDocFormJson);

        if (sonTipoDocDesiguales) {
            PersonaHistorial personaHistorial = new PersonaHistorial();
            personaHistorial.setUsuario(ds.getUsuario());
            personaHistorial.setPersona(personaBD);
            personaHistorial.setFecha(new Date());
            personaHistorial.setNumeroDocumentoFrom(personaBD.getNumeroDocIdentidad());
            personaHistorial.setNumeroDocumentoTo(personaForm.getNumeroDocIdentidad());
            personaHistorial.setTipoDocumentoFrom(personaBD.getTipoDocumento());
            personaHistorial.setTipoDocumentoTo(personaForm.getTipoDocumento());
            personaHistorialDAO.save(personaHistorial);
        }
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
            docenteBD.setEstadoEnum(DocenteEstadoEnum.ACT);
        } else {
            docenteBD.setEstadoEnum(DocenteEstadoEnum.INA);
        }
        docenteDAO.update(docenteBD);
    }

    @Override
    public Persona findPersonaByDocIdentidad(Persona personaTmp) {
        Assert.isNotNull(personaTmp.getTipoDocumento(), "El tipo de documento no debe de ser nulo");
        Assert.isNotNull(personaTmp.getTipoDocumento().getId(), "El tipo de documento no debe de ser nulo");
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

    @Override
    public Persona findPersona(Persona persona) {
        return personaDAO.find(persona.getId());
    }

    private void uploadS3(String fileName) {
        log.debug("upload to s3 args   {}  {}   {}  {} {}", AcademicoConstantine.S3_BUCKET_ACADEMICO, "public-unalm/profile/", GlobalConstantine.TMP_DIR, fileName, true);
        File f = new File(GlobalConstantine.TMP_DIR + fileName);
        if (f.exists() && !f.isDirectory()) {
            swiftService.uploadFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, AcademicoConstantine.S3_FOTO_DOCENTE, GlobalConstantine.TMP_DIR, fileName, true);
        }
    }

    private String getPathFotoDocente(String fileName) {

        if (!Strings.isNullOrEmpty(fileName)) {

            StringBuilder sb = new StringBuilder();
            sb.append(AcademicoConstantine.S3_URL_ACADEMICO);
            sb.append(AcademicoConstantine.S3_FOTO_DOCENTE);
            sb.append(fileName);
            return sb.toString();

        }

        return "";

    }

    @Override
    public List<GrupoSeccion> allGpoSecciones(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        Map<Long, GrupoSeccion> mapGpoSecc = new LinkedHashMap();

        List<DocenteSeccion> profeSecciones = docenteSeccionDAO.allActivosByDocenteCiclo(docente, ciclo);
        for (DocenteSeccion profeSecc : profeSecciones) {
            Seccion secc = profeSecc.getSeccion();
            secc.setDocenteSeccion(new ArrayList());
            secc.getDocenteSeccion().add(profeSecc);
            GrupoSeccion gpoSeccBD = secc.getGrupoSeccion();
            GrupoSeccion gpoSecc = mapGpoSecc.get(gpoSeccBD.getId());
            if (gpoSecc == null) {
                mapGpoSecc.put(gpoSeccBD.getId(), gpoSeccBD);
                gpoSecc = gpoSeccBD;
                gpoSecc.setSecciones(new ArrayList());
            }
            gpoSecc.getSecciones().add(secc);
        }
        return new ArrayList(mapGpoSecc.values());
    }

    @Override
    public ContenidoCarta findContenidoCartaByEnum(ContenidoCartaEnum enumval) {
        return contenidoCartaDAO.findByCodigoEnum(enumval);
    }

    @Override
    public DepartamentoAcademico findDepartamento(DepartamentoAcademico departamentoAcademico) {
        return departamentoAcademicoDAO.find(departamentoAcademico.getId());
    }

    @Override
    public Oficina findOficina(OficinaEnum oficinaEnum) {
        return oficinaDAO.findByCode(oficinaEnum.name());
    }

    @Override
    public List<Docente> allDocenteByDepartamentosAcademicoEstado(List<DepartamentoAcademico> departamentos, EnteAcademicoEstadoEnum enteAcademicoEstadoEnum) {
        return docenteDAO.allByDepartamentosAcademicoEstado(departamentos, enteAcademicoEstadoEnum);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionActivosByDocentesCiclos(List<Docente> docentes, List<CicloAcademico> cicloAcademicos) {
        return docenteSeccionDAO.allActivosByDocentesCiclosCodigo(docentes, cicloAcademicos);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionActivosByDocentesCiclo(List<Docente> docentes, CicloAcademico cicloAcademico) {
        return docenteSeccionDAO.allActivosByDocentesCicloCodigo(docentes, cicloAcademico);
    }

    @Override
    public List<HorarioSeccion> allHorarioSeccionBySecciones(List<Seccion> secciones) {
        return horarioSeccionDAO.allBySecciones(secciones);
    }

    @Override
    public CicloAcademico findCicloAcademico(Long idCicloAcademico) {
        return cicloAcademicoDAO.find(idCicloAcademico);
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        return cicloAcademicoDAO.allPregradoByRange(1980, 2050);
    }

    @Override
    public List<CicloAcademico> allCicloAcademicoNivel() {
        return cicloAcademicoDAO.allPregradoNivelByRange(1980, 2050);
    }

    @Override
    public List<Docente> allByNombre(String nombre) {
        return docenteDAO.allByName(nombre);
    }

    private String getPersonaJson(Persona persona) {
        return JaneHelper
                .from(persona)
                .only("id,paterno,materno,nombres,numeroDocIdentidad,sexo,fechaNacer,email,emailCompania,direccion,celular,telefono")
                .join("tipoDocumento", "id")
                .join("ubicacionDomicilio", "id")
                .join("ubicacionNacer", "id")
                .join("paisDomicilio", "id")
                .join("paisNacer", "id")
                .join("nacionalidad", "id")
                .json().toString();
    }

    private String getPersonaTipoDocJson(Persona persona) {
        return JaneHelper
                .from(persona)
                .only("id,numeroDocIdentidad")
                .join("tipoDocumento", "id")
                .json().toString();
    }

    private Boolean validarDptoModalidad(Docente docenteBDD, Docente docenteForm) {
        if (docenteBDD != null && docenteBDD.getDepartamentoAcademico().getId() == docenteForm.getDepartamentoAcademico().getId().longValue()
                && docenteBDD.getModalidadEstudio().getId() == docenteForm.getModalidadEstudio().getId().longValue()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public List<DocenteCicloBean> allDocentecicloAcademico(List<CicloAcademico> cicloAcademicos) {
        List<DocenteCicloBean> docenteCicloBean = new ArrayList<>();
        docenteCicloBean = docenteDAO.AllDocentecicloAcademico(cicloAcademicos);
        return docenteCicloBean;
    }

    }
