package pe.edu.lamolina.amauta.controller.academico.alumno;

import com.google.common.base.Strings;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.*;
import pe.edu.lamolina.model.academico.*;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.ContenidoEmailEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.CursoHabilEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.TipoTramiteTrasladoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.posgrado.CursoHabilEscuela;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaHistorialDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.general.ValidacionPersonaDAO;
import pe.edu.lamolina.amauta.dao.posgrado.CursoHabilEscuelaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTrasladoDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.mail.MailerService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.persona.OrigenValidacionEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.persona.ValidacionEstadoEnum;
import pe.edu.lamolina.model.general.PersonaHistorial;
import pe.edu.lamolina.model.general.ValidacionPersona;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AlumnoServiceImp implements AlumnoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    private final AlumnoDAO alumnoDAO;
    private final CarreraDAO carreraDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final ContenidoCartaDAO contenidoCartaDAO;
    private final CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    private final CursoConvalidadoDAO cursoConvalidadoDAO;
    private final CursoDAO cursoDAO;
    private final CursoHabilEscuelaDAO cursoHabilEscuelaDAO;
    private final CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;
    private final DocenteDAO docenteDAO;
    private final FacultadDAO facultadDAO;
    private final MailerService mailerService;
    private final ModalidadEstudioDAO modalidadEstudioDAO;
    private final PersonaDAO personaDAO;
    private final PersonaHistorialDAO personaHistorialDAO;
    private final RolDAO rolDAO;
    private final SituacionAcademicaDAO situacionAcademicaDAO;
    private final TipoCursoCurriculaDAO tipoCursoCurriculaDAO;
    private final TipoDocIdentidadDAO tipoDocIdentidadDAO;
    private final TramiteTrasladoDAO tramiteTrasladoDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioRolDAO usuarioRolDAO;
    private final ValidacionPersonaDAO validacionPersonaDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final UploadFileS3 uploadFileS3;

    @Override
    public List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, List<Carrera> carreras) {
        return alumnoDAO.allByRolDynatable(filter, carreras);
    }

    @Override
    public List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        return alumnoDAO.allByCarrerasDynatable(filter, carreras, todo);
    }

    @Override
    public AlumnoResumen findResumen(VerificadorServiceImp.CantidadItemsEnum cantidadEnum, List<Carrera> careras) {
        if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.TODOS) {
            return alumnoDAO.findResumen();

        } else if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL && !careras.isEmpty()) {
            return alumnoDAO.findResumen(careras);

        }
        return new AlumnoResumen(0L, 0L, 0L, 0L);
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int yearinit = year - 6;
        int yearend = year + 3;
        return cicloAcademicoDAO.allPregradoByRange(yearinit, yearend);
    }

    @Override
    public List<TipoDocIdentidad> allDocumento() {
        return tipoDocIdentidadDAO.all();
    }

    @Override
    public List<TipoDocIdentidad> allDocumentosPersonaNatural() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<SituacionAcademica> allSituaciones() {
        return situacionAcademicaDAO.all();
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos) {
        return modalidadEstudioDAO.allByCodigos(codigos);
    }

    @Override
    @Transactional
    public void saveAlumnoFisico(Alumno alumno, DataSessionPivot ds) {

        Persona personaForm = alumno.getPersona();
        this.clearAlumnoPersonaForm(alumno, personaForm);

        Assert.isNotNull(alumno.getModalidadEstudio(), "Debe especificar la modalidad de estudio");
        Assert.isNotNull(alumno.getCarrera(), "Debe especificar la carrera");
        Assert.isNotNull(alumno.getCicloIngreso(), "Debe especificar el ciclo de ingreso");

        this.verificarPersona(personaForm);

        Persona personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        CicloAcademico ciclo = cicloAcademicoDAO.find(alumno.getCicloIngreso().getId());

        String codigoMatricula = StringUtils.isBlank(alumno.getCodigo()) ? this.generateCodigo(ciclo) : alumno.getCodigo();
        String emailCompania = StringUtils.isBlank(alumno.getPersona().getEmailCompania()) ? this.generateEmailCompania(codigoMatricula) : alumno.getPersona().getEmailCompania();

        if (personaDB == null) {

            personaForm.setEmailCompania(emailCompania);

            this.validarEmailsinPersona(personaForm.getEmail());
            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());

            personaForm.setEstadoEnum(PersonaEstadoEnum.ACT);
            personaForm.setUserRegistro(ds.getUsuario());
            personaForm.setFechaRegistro(new Date());
            this.validarDNI(personaForm);
            personaDAO.save(personaForm);

            this.crearUsuarioAlumno(emailCompania, personaForm, ds);
            this.saveAlumno(alumno, personaForm, ciclo, codigoMatricula);
            this.enviarNotificacionUsuarioCreacion(personaForm);
            this.updateCicloSgteMatricula(ciclo);

            String personaFinal = JaneHelper
                    .from(personaForm)
                    .only("id,paterno,materno,nombres,sexo,fechaNacer,numeroDocIdentidad")
                    .join("tipoDocumento", "id,simbolo")
                    .json().toString();
            this.registrarValidacion(personaForm, alumno, null, personaFinal, ds);
            return;
        }

        Alumno alumnoDB = alumnoDAO.findByPersonaCicloIngreso(personaDB, ciclo);//ojo alumno por persona ciclo
        if (alumnoDB != null) {
            throw new PhobosException("El documento ya pertenece a otro alumno");
        }

        PersonaHistorial personaHistorial = new PersonaHistorial();
        personaHistorial.setUsuario(ds.getUsuario());
        personaHistorial.setPersona(personaDB);
        personaHistorial.setFecha(new Date());
        personaHistorial.setNumeroDocumentoFrom(personaDB.getNumeroDocIdentidad());
        personaHistorial.setNumeroDocumentoTo(personaForm.getNumeroDocIdentidad());
        personaHistorial.setTipoDocumentoFrom(personaDB.getTipoDocumento());
        personaHistorial.setTipoDocumentoTo(personaForm.getTipoDocumento());
        personaHistorialDAO.save(personaHistorial);

        String personaInicio = JaneHelper
                .from(personaDB)
                .only("id,paterno,materno,nombres,sexo,fechaNacer,numeroDocIdentidad")
                .join("tipoDocumento", "id,simbolo")
                .json().toString();

        this.updatePersona(personaDB, personaForm, ds);

        Usuario usuarioAlumno = usuarioDAO.findActivoByPersona(personaDB);

        if (usuarioAlumno == null) {
            this.crearUsuarioAlumno(emailCompania, personaDB, ds);
        }

        this.saveAlumno(alumno, personaDB, ciclo, codigoMatricula);
        this.enviarNotificacionUsuarioCreacion(personaForm);
        this.updateCicloSgteMatricula(ciclo);

        String personaFinal = JaneHelper
                .from(personaDB)
                .only("id,paterno,materno,nombres,sexo,fechaNacer,numeroDocIdentidad")
                .join("tipoDocumento", "id,simbolo")
                .json().toString();

        if (!personaInicio.equals(personaFinal)) {
            this.registrarValidacion(personaDB, alumno, personaInicio, personaFinal, ds);

        } else if (personaDB.getEstadoValidacionEnum() == ValidacionEstadoEnum.PENDIENTE) {
            this.registrarValidacion(personaDB, alumno, null, personaFinal, ds);

        } else if (personaDB.getEstadoValidacionEnum() == ValidacionEstadoEnum.VALIDADO) {
            ValidacionPersona validacionAntes = validacionPersonaDAO.findAnterior(personaDB);
            if (validacionAntes == null) {
                this.registrarValidacion(personaDB, alumno, null, personaFinal, ds);

            } else {
                personaInicio = validacionAntes.getDataFinal();
                if (!personaInicio.equals(personaFinal)) {
                    this.registrarValidacion(personaDB, alumno, personaInicio, personaFinal, ds);
                }
            }
        }

    }

    private void registrarValidacion(Persona persona, Alumno alumno, String jsonInicio, String jsonFinal, DataSessionPivot ds) {
        DateTime today = new DateTime();

        persona.setEstadoValidacionEnum(ValidacionEstadoEnum.VALIDADO);
        persona.setOrigenValidacionEnum(OrigenValidacionEnum.ALUMNO_AMAUTA);
        persona.setUserValidacion(ds.getUsuario());
        persona.setFechaValidacion(today.toDate());
        persona.setUserModificacion(ds.getUsuario());
        personaDAO.update(persona);

        ValidacionPersona validacion = new ValidacionPersona();
        validacion.setPersona(persona);
        validacion.setOrigenEnum(OrigenValidacionEnum.ALUMNO_AMAUTA);
        validacion.setInstanciaOrigen(alumno.getId());
        validacion.setDataInicio(jsonInicio);
        validacion.setDataFinal(jsonFinal);
        validacion.setUserValidacion(ds.getUsuario());
        validacion.setFechaValidacion(today.toDate());
        validacionPersonaDAO.save(validacion);
    }

    private void crearUsuarioAlumno(String emailCompania, Persona persona, DataSessionPivot ds) {
        Usuario usuarioAlumno = new Usuario();
        usuarioAlumno.setGoogle(emailCompania);
        usuarioAlumno.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioAlumno.setFechaRegistro(new Date());
        usuarioAlumno.setPersona(persona);
        usuarioAlumno.setUserRegistro(ds.getUsuario());
        usuarioDAO.save(usuarioAlumno);

        Rol rol = rolDAO.findByCode(RolEnum.ALU);
        UsuarioRol ur = new UsuarioRol();
        ur.setEstadoEnum(UserEstadoEnum.ACT);
        ur.setFechaInicio(new Date());
        ur.setFechaRegistro(new Date());
        ur.setRol(rol);
        ur.setUserRegistro(ds.getUsuario());
        ur.setUsuario(usuarioAlumno);
        usuarioRolDAO.save(ur);

    }

    private void saveAlumno(Alumno alumno, Persona persona, CicloAcademico ciclo, String codigoMatricula) {
        SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");

        alumno.setPersona(persona);
        alumno.setCicloActivo(ciclo);
        alumno.setCicloIngreso(ciclo);
        alumno.setSituacionAcademica(situacion);

        if (Strings.isNullOrEmpty(alumno.getCodigo())) {
            alumno.setCodigo(codigoMatricula);
        }

        this.validarCodigoMatricula(alumno);

        alumno.setRetirosCursos(0);
        alumno.setRetirosCiclos(0);
        alumno.setRetirosExtemporaneos(0);
        alumno.setCreditosCursados(0);
        alumno.setCreditosAprobados(0);
        alumno.setCursosInscritos(0);
        alumno.setCursosAprobados(0);
        alumno.setPromedioAcumulado(BigDecimal.ZERO);
        alumno.setCreditosCarreraCursados(0);
        alumno.setCreditosCarreraAprobados(0);
        alumno.setCursosCarreraInscritos(0);
        alumno.setCursosCarreraAprobados(0);
        alumno.setPromedioCarreraAcumulado(BigDecimal.ZERO);
        alumno.setCiclosEstudiados(BigDecimal.ZERO.intValue());
        alumnoDAO.save(alumno);
    }

    private Persona updatePersona(Persona personaBD, Persona personaForm, DataSessionPivot ds) {
        LocalDate fechaNacer = new LocalDate(personaForm.getFechaNacer());
        personaBD.setPaisNacer(personaForm.getPaisNacer());
        personaBD.setPaisDomicilio(personaForm.getPaisDomicilio());
        personaBD.setUbicacionNacer(personaForm.getUbicacionNacer());
        personaBD.setNacionalidad(personaForm.getNacionalidad());
        personaBD.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        personaBD.setUserModificacion(ds.getUsuario());
        personaBD.setNombres(personaForm.getNombres());
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setFechaNacer(fechaNacer.plusDays(1).toDate());
        personaBD.setDireccion(personaForm.getDireccion());
        personaBD.setCelular(personaForm.getCelular());
        personaBD.setTelefono(personaForm.getTelefono());
        personaBD.setEmail(personaForm.getEmail());
        personaBD.setEmailCompania(personaForm.getEmailCompania());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());
        personaBD.setEnviarRecauda(1);

        this.validarEmailConPersona(personaForm.getEmail(), personaBD);
        this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaBD);

        personaBD.setUserModificacion(ds.getUsuario());
        personaDAO.update(personaBD);
        return personaBD;
    }

    private String generateCodigo(CicloAcademico ciclo) {

        StringBuilder ssb = new StringBuilder();
        ssb.append("Configuración del ciclo académico UNALM  ");
        ssb.append(ciclo.getDescripcion());
        ssb.append("  no esta completa");
        if (ciclo.getMatriculaSiguiente() == null || ciclo.getMatriculaInicio() == null) {
            throw new PhobosException(ssb.toString());
        }
        int sgt = ciclo.getMatriculaSiguiente();
        String year = ciclo.getYear().toString();
        String cod;
        if (ciclo.getMatriculaInicio() > sgt) {
            sgt = ciclo.getMatriculaInicio();
        }
        cod = NumberFormat.codigo((sgt + 1), 4);
        return (year + cod);
    }

    private String generateEmailCompania(String codigoMatricula) {
        return codigoMatricula + "@lamolina.edu.pe";
    }

    private void updateCicloSgteMatricula(CicloAcademico ciclo) {
        int sgt = ciclo.getMatriculaSiguiente();
        ciclo.setMatriculaSiguiente(sgt + 1);
        cicloAcademicoDAO.update(ciclo);
    }

    private void enviarNotificacionUsuarioCreacion(Persona persona) {
        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(ContenidoEmailEnum.CREATEUSERALUMNOVISITANTE.name());
        mailerService.enviarNotificacionUsuarioCreacion(persona, contenidoCarta);
    }

    private void verificarPersona(Persona personaForm) {

        personaForm.setNumeroDocIdentidad(limpiarValor(personaForm.getNumeroDocIdentidad()));

        if (personaForm.getTipoDocumento() == null || (personaForm.getTipoDocumento() != null && personaForm.getTipoDocumento().getId() == null)) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (personaForm.getNumeroDocIdentidad() == null) {
            throw new PhobosException("Debe indicar el número del documento de identidad");
        }
        if (personaForm.getNumeroDocIdentidad().equals(AcademicoConstantine.CODE_POSTULANTE_DUMMY)) {
            throw new PhobosException("Este número de documento de identidad no está permitido");
        }

        TipoDocIdentidad tipoDoc = tipoDocIdentidadDAO.find(personaForm.getTipoDocumento().getId());

        if (tipoDoc.getLongitudExacta() == 1) {
            if (personaForm.getNumeroDocIdentidad().length() != tipoDoc.getLongitud()) {
                throw new PhobosException("El número de documento debe tener " + tipoDoc.getLongitud() + " caracteres");
            }
        } else if (tipoDoc.getLongitudExacta() == 0) {
            if (personaForm.getNumeroDocIdentidad().length() < 4) {
                throw new PhobosException("El número de documento debe tener como mínimo 4 caracteres");
            }
            if (personaForm.getNumeroDocIdentidad().length() > tipoDoc.getLongitud()) {
                throw new PhobosException("El número de documento debe tener como máximo " + tipoDoc.getLongitud() + " caracteres");
            }
        }

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

    private void validarCodigoMatricula(Alumno alumnoForm) {
        Alumno alumnoDB = alumnoDAO.findByCodigo(alumnoForm.getCodigo());
        if (alumnoForm.getId() != null && alumnoDB != null && alumnoDB.getId().longValue() != alumnoForm.getId()) {
            throw new PhobosException("El código ingresado ya se encuentra relacionado con otra alumno: " + alumnoDB.getPersona().getApellidosNombres());

        } else if (alumnoForm.getId() == null && alumnoDB != null) {
            throw new PhobosException("El código ingresado ya se encuentra relacionado con otra alumno: " + alumnoDB.getPersona().getApellidosNombres());
        }
    }

    private void validarEmailsinPersona(String email) {
        if (StringUtils.isNotBlank(email)) {
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
                throw new PhobosException("El correo UNALM ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private String limpiarValor(String valor) {
        if (valor == null) {
            return null;
        }
        valor = valor.trim();
        if (Strings.isNullOrEmpty(valor)) {
            return null;
        }
        return valor;
    }

    @Override
    public Alumno findAlumnoFisico(Long idAlumno) {
        return alumnoDAO.find(new Alumno(idAlumno));
    }

    @Override
    @Transactional
    public void updateAlumnoFisico(Alumno alumno, DataSessionPivot ds) {

        Persona personaForm = alumno.getPersona();
        this.clearAlumnoPersonaForm(alumno, personaForm);

        this.verificarPersona(personaForm);
        this.validarDNI(personaForm);
        Persona personaDB = personaDAO.find(personaForm.getId());
        Assert.isNotNull(personaDB, "Alumno sin persona registrada");

        String personaInicio = JaneHelper
                .from(personaDB)
                .only("id,paterno,materno,nombres,sexo,fechaNacer,numeroDocIdentidad")
                .join("tipoDocumento", "id,simbolo")
                .json().toString();

        Usuario usuario = usuarioDAO.findActivoByPersona(personaDB);

        if (usuario == null) {
            this.crearUsuarioAlumno(personaForm.getEmailCompania(), personaDB, ds);

        } else {
            boolean modificar = true;
            Usuario user = usuarioDAO.findByGoogleEmail(personaForm.getEmailCompania());
            if (user != null) {
                Persona persona = user.getPersona();
                String msg = "Este email ya pertenece a " + persona.getNombreCompleto() + " (" + persona.getId() + ")";
                Assert.isTrue(persona.getId() == personaDB.getId().longValue(), msg);
                if (user.getId() != usuario.getId().longValue()) {
                    modificar = false;
                }
            }

            logger.debug("{} =? {}", personaDB.getEmailCompania(), personaForm.getEmailCompania());
            if (personaDB.getEmailCompania() == null) {
                this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
                usuario.setGoogle(personaForm.getEmailCompania());
                usuario.setFechaModifica(new Date());
                usuario.setUserModifica(ds.getUsuario());
                usuarioDAO.update(usuario);

            } else if (!personaDB.getEmailCompania().equals(personaForm.getEmailCompania())) {
                this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaDB);
                logger.debug("not eq");
                if (modificar) {
                    usuario.setGoogle(personaForm.getEmailCompania());
                    usuario.setFechaModifica(new Date());
                    usuario.setUserModifica(ds.getUsuario());
                    usuarioDAO.update(usuario);
                }
                this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            }

            Rol rol = rolDAO.findByCode(RolEnum.ALU);
            List<UsuarioRol> userRoles = usuarioRolDAO.allByUsuarioRol(usuario, rol);

            if (userRoles.isEmpty()) {
                UsuarioRol userRol = new UsuarioRol();
                userRol.setEstadoEnum(UserEstadoEnum.ACT);
                userRol.setFechaInicio(new Date());
                userRol.setFechaRegistro(new Date());
                userRol.setRol(rol);
                userRol.setUserRegistro(ds.getUsuario());
                userRol.setUsuario(usuario);

                usuarioRolDAO.save(userRol);

            } else {
                boolean noTiene = true;
                for (UsuarioRol userRol : userRoles) {
                    if (userRol.getEstadoEnum() == UserEstadoEnum.ACT) {
                        noTiene = false;
                    }
                }
                if (noTiene) {
                    for (UsuarioRol userRol : userRoles) {
                        userRol.setEstadoEnum(UserEstadoEnum.ACT);
                        usuarioRolDAO.update(userRol);
                        break;
                    }
                }

            }

        }

        PersonaHistorial personaHistorial = new PersonaHistorial();
        personaHistorial.setUsuario(usuario);
        personaHistorial.setPersona(personaDB);
        personaHistorial.setFecha(new Date());
        personaHistorial.setNumeroDocumentoFrom(personaDB.getNumeroDocIdentidad());
        personaHistorial.setNumeroDocumentoTo(personaForm.getNumeroDocIdentidad());
        personaHistorial.setTipoDocumentoFrom(personaDB.getTipoDocumento());
        personaHistorial.setTipoDocumentoTo(personaForm.getTipoDocumento());
        personaHistorialDAO.save(personaHistorial);

        this.updatePersona(personaDB, personaForm, ds);

        String personaFinal = JaneHelper
                .from(personaDB)
                .only("id,paterno,materno,nombres,sexo,fechaNacer,numeroDocIdentidad")
                .join("tipoDocumento", "id,simbolo")
                .json().toString();

        if (!personaInicio.equals(personaFinal)) {
            this.registrarValidacion(personaDB, alumno, personaInicio, personaFinal, ds);

        } else if (personaDB.getEstadoValidacionEnum() == ValidacionEstadoEnum.PENDIENTE) {
            this.registrarValidacion(personaDB, alumno, null, personaFinal, ds);

        } else if (personaDB.getEstadoValidacionEnum() == ValidacionEstadoEnum.VALIDADO) {
            ValidacionPersona validacionAntes = validacionPersonaDAO.findAnterior(personaDB);
            if (validacionAntes == null) {
                this.registrarValidacion(personaDB, alumno, null, personaFinal, ds);

            } else {
                personaInicio = validacionAntes.getDataFinal();
                if (!personaInicio.equals(personaFinal)) {
                    this.registrarValidacion(personaDB, alumno, personaInicio, personaFinal, ds);
                }
            }
        }
    }

    @Transactional
    private void clearAlumnoPersonaForm(Alumno alumnoForm, Persona personaForm) {

        ObjectUtil.eliminarAttrSinId(alumnoForm, "postulantePregrado");
        ObjectUtil.eliminarAttrSinId(alumnoForm, "modalidadEstudio");
        ObjectUtil.eliminarAttrSinId(alumnoForm, "situacionAcademica");
        ObjectUtil.eliminarAttrSinId(alumnoForm, "cicloActivo");
        ObjectUtil.eliminarAttrSinId(alumnoForm, "cicloIngreso");
        ObjectUtil.eliminarAttrSinId(alumnoForm, "orientacionCarrera");
        ObjectUtil.eliminarAttrSinId(alumnoForm, "carrera");

        ObjectUtil.eliminarAttrSinId(personaForm, "paisNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "nacionalidad");
        ObjectUtil.eliminarAttrSinId(personaForm, "paisDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "tipoDocumento");
    }

    @Override
    @Transactional
    public void updateAlumnoEspecial(Alumno alumno, DataSessionPivot ds) {
        Carrera carrera = carreraDAO.findByCodigo(AcademicoConstantine.COD_CARRERA_ALUMNO_ESPECIAL);
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.ESP);
        alumno.setCarrera(carrera);
        alumno.setModalidadEstudio(modalidadEstudio);
        this.updateAlumnoFisico(alumno, ds);
    }

    @Override
    @Transactional
    public void saveAlumnoEspecial(Alumno alumno, DataSessionPivot ds) {
        Carrera carrera = carreraDAO.findByCodigo(AcademicoConstantine.COD_CARRERA_ALUMNO_ESPECIAL);
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.ESP);
        alumno.setCarrera(carrera);
        alumno.setModalidadEstudio(modalidadEstudio);
        if (StringUtils.isBlank(alumno.getEstado())) {
            alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);
        }
        this.saveAlumnoFisico(alumno, ds);
    }

    @Override
    public Alumno validarAlumnoEspecial(Alumno alumnoVisitanteForm) {
        Persona persona = alumnoVisitanteForm.getPersona();
        persona = personaDAO.findByDocumento(persona.getTipoDocumento(), persona.getNumeroDocIdentidad());
        alumnoVisitanteForm.setPersona(persona);
        return alumnoVisitanteForm;
    }

    @Override
    public List<Carrera> allCarrerasByuser(Persona persona, DataSessionPivot ds) {

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(new Oficina(OficinaEnum.OERA.getId()), persona);

        if (colaborador != null) {
            return carreraDAO.all();
        }

        List<UsuarioRol> usu = usuarioRolDAO.findByUsuario(ds.getUsuario());

        List<Long> idFac = new ArrayList();
        List<Long> idEsp = new ArrayList();

        for (UsuarioRol usuarioRol : usu) {
            Oficina ofi = usuarioRol.getOficina();
            TipoOficina tipoOfi = ofi.getTipoOficina();
            if (tipoOfi.getCodigo().equals(TipoOficinaEnum.FAC.name())) {
                idFac.add(ofi.getInstanciaOficina());
            } else if (tipoOfi.getCodigo().equals(TipoOficinaEnum.ESP.name())) {
                idEsp.add(ofi.getInstanciaOficina());
            }
        }
        List<Carrera> all = new ArrayList();
        List<Carrera> carrera1 = carreraDAO.all(idEsp);
        List<Carrera> carrera2 = carreraDAO.allOficinaAndIds(idFac);

        all.addAll(carrera1);
        all.addAll(carrera2);
        return all;

    }

    @Override
    public List<AlumnoCursoCurricula> allCursosByAlumno(Alumno alumno, DynatableFilter filter) {
        Alumno alumnoDB = alumnoDAO.find(alumno);
        List<ModalidadEstudioEnum> listEnum = Arrays.asList(ModalidadEstudioEnum.EPG, ModalidadEstudioEnum.VIS, ModalidadEstudioEnum.ESP);
        if (alumnoDB == null) {
            throw new PhobosException("No existe datos del alumno");
        }
        if (!listEnum.contains(alumnoDB.getModalidadEstudio().getCodigoEnum())) {
            throw new PhobosException("El alumno no pertenece a la modalidad");
        }
        return alumnoCursoCurriculaDAO.allByAlumnoAndModalidad(alumno, filter);
    }

    @Override
    public List<CursoCicloAcademico> allCursoCiclo(String nombre, CicloAcademico cicloAcademico) {
        return cursoCicloAcademicoDAO.allByCicloAndNombre(cicloAcademico, forLike(nombre));
    }

    @Override
    @Transactional
    public void saveCursoCurricula(AlumnoCursoCurricula alumnoCursoCurricula, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        if (alumnoCursoCurricula.getAlumno() == null || alumnoCursoCurricula.getCurso() == null) {
            throw new PhobosException("Ingrese los campos requeridos");
        }
        Alumno alumnoDB = alumnoDAO.find(alumnoCursoCurricula.getAlumno());
        Curso cursoDB = cursoDAO.findCurso(alumnoCursoCurricula.getCurso());
        if (alumnoDB == null) {
            throw new PhobosException("No existe datos del alumno");
        }
        AlumnoCursoCurricula alumnoCursoCuri = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumnoDB, cursoDB);
        if (alumnoCursoCuri != null) {
            throw new PhobosException("El curso ya fue asignado a este alumno");
        }
        AlumnoCursoCurricula newcursoCurricula = new AlumnoCursoCurricula();
        newcursoCurricula.setAlumno(alumnoCursoCurricula.getAlumno());
        newcursoCurricula.setCurso(alumnoCursoCurricula.getCurso());
        newcursoCurricula.setVecesCursado(BigDecimal.ZERO.intValue());
        newcursoCurricula.setNumeroCiclo(1);
        newcursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.HAB);
        newcursoCurricula.setEstadoRegistroEnum(EstadoEnum.ACT);
        newcursoCurricula.setCreditos(alumnoCursoCurricula.getCurso().getCreditos());
        newcursoCurricula.setTipoCursoCurricula(tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.EAD));
        alumnoCursoCurriculaDAO.save(newcursoCurricula);

        if (alumnoDB.getModalidadEstudio().getCodigoEnum().equals(ModalidadEstudioEnum.ESP)) {
            CursoHabilEscuela cursoHabilEscuela = new CursoHabilEscuela();
            cursoHabilEscuela.setAlumno(alumnoDB);
            cursoHabilEscuela.setCicloAcademico(cicloAcademico);
            cursoHabilEscuela.setCurso(cursoDB);
            cursoHabilEscuela.setFechaRegistro(new Date());
            cursoHabilEscuela.setUserRegistro(ds.getUsuario());
            cursoHabilEscuela.setEstadoEnum(CursoHabilEstadoEnum.HAB);
            cursoHabilEscuelaDAO.save(cursoHabilEscuela);
        }

    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<AlumnoCursoCurricula> allAlumnoCursoCurso(Alumno alumno) {
        return alumnoCursoCurriculaDAO.all(alumno);
    }

    @Override
    public List<TramiteTraslado> allTramiteTrasladoByAlumno(Alumno alumno) {
        return tramiteTrasladoDAO.allByAlumno(alumno);
    }

    @Override
    @Transactional
    public List<CursoConvalidado> saveListCursoConvalidado(TrasladoBean trasladoBean, CicloAcademico cicloAcademicoSesion, DataSessionPivot ds) {
        Alumno alumno = trasladoBean.getAlumno();
        Integer total = trasladoBean.getTotal();
        TramiteTraslado tramiteTraslado = trasladoBean.getTramiteTraslado(); // obtengo ciclo
        List<CursoConvalidado> listCursoConvalidadoNew = trasladoBean.getListCursoConvalidado().stream().filter(x -> x.getTramiteTraslado().getId() == null).collect(Collectors.toList());
        List<CursoConvalidado> listCursoConvalidadoOld = trasladoBean.getListCursoConvalidado().stream().filter(x -> x.getTramiteTraslado().getId() != null).collect(Collectors.toList());

        AlumnoCiclo alumnoCicloDB = alumnoCicloDAO.findByAlumnoCiclo(alumno, trasladoBean.getTramiteTraslado().getCicloAcademico());
        List<CursoConvalidado> cursoConvalidadosOldCicloDistinto = listCursoConvalidadoOld.stream()
                .filter(x -> !Objects.equals(x.getTramiteTraslado().getCicloAcademico().getId(), tramiteTraslado.getCicloAcademico().getId()))
                .collect(Collectors.toList());

        if (cursoConvalidadosOldCicloDistinto != null && cursoConvalidadosOldCicloDistinto.size() > 0) {
            CursoConvalidado findCursoConvalidado = cursoConvalidadosOldCicloDistinto
                    .stream()
                    .filter((x) -> x.getTramiteTraslado().getCicloAcademico().getId() != null)
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException());
            CicloAcademico cicloAcademico = cicloAcademicoDAO.find(findCursoConvalidado.getTramiteTraslado().getCicloAcademico().getId());
            List<CursoConvalidado> cursoConvalidados = cursoConvalidadoDAO.allByTramiteTraslado(tramiteTraslado);
            Map<Long, CursoConvalidado> mapCursoConvalidados = TypesUtil.convertListToMap("id", cursoConvalidados);
            tramiteTraslado.setCicloAcademico(cicloAcademico);
            tramiteTraslado.setAlumno(alumno);
            tramiteTrasladoDAO.update(tramiteTraslado);
            AlumnoCiclo alumnoCiclo = null;
            if (alumnoCicloDB != null) {
                if (alumnoCicloDB.getEstadoEnum().equals(EstadoMatriculaEnum.RCI)) {
                    alumnoCiclo = this.saveAlumnoCiclo(alumno, cicloAcademico, total, ds);
                } else if (alumnoCicloDB.getEstadoEnum().equals(EstadoMatriculaEnum.MAT)) {
                    alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
                }
                if (alumnoCiclo != null) {
                    for (CursoConvalidado cursoConvalidado : cursoConvalidadosOldCicloDistinto) {
                        cursoConvalidado.setUserModifica(ds.getUsuario());
                        cursoConvalidado.setFechaModificacion(new Date());
                        cursoConvalidado.setNota(cursoConvalidado.getNota() == null ? "TE" : cursoConvalidado.getNota());
                        cursoConvalidado.setTramiteTraslado(tramiteTraslado);
                        AlumnoCicloCurso alumnoCicloCurso = mapCursoConvalidados.get(cursoConvalidado.getId()).getAlumnoCicloCurso();
                        if (alumnoCicloCurso != null) {
                            this.updateAlumnoCicloCurso(cursoConvalidado, alumnoCiclo, alumnoCicloCurso, ds);
                        }
                        cursoConvalidadoDAO.update(cursoConvalidado);
                    }
                    if (alumnoCicloDB.getEstadoEnum().equals(EstadoMatriculaEnum.MAT)) {
                        alumnoCicloDAO.delete(alumnoCicloDB);
                    }
                }
            }
        } else {
            for (CursoConvalidado cursoConvalidado : listCursoConvalidadoNew) {
                tramiteTras(alumno, tramiteTraslado, trasladoBean, total, cursoConvalidado, listCursoConvalidadoNew, ds);
            }
            for (CursoConvalidado cursoConvalidado : listCursoConvalidadoOld) {
                tramiteTras(alumno, cursoConvalidado.getTramiteTraslado(), trasladoBean, total, cursoConvalidado, listCursoConvalidadoOld, ds);
            }
        }
        List<TramiteTraslado> listTramiteTraslado = this.allTramiteTrasladoByAlumno(alumno);
        return cursoConvalidadoDAO.allInTramiteTraslado(listTramiteTraslado);
    }

    private void tramiteTras(Alumno alumno, TramiteTraslado tramiteTraslado, TrasladoBean trasladoBean, Integer total, CursoConvalidado cursoConvalidado, List<CursoConvalidado> listCursoConvalidadoNew, DataSessionPivot ds) {

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, tramiteTraslado.getCicloAcademico());

        if (alumnoCiclo != null) {

            logger.debug("*********** alumnoCiclo existente: id  {}", alumnoCiclo.getId());
            if (total != 0 && Objects.equals(trasladoBean.getTramiteTraslado().getCicloAcademico().getId(), alumnoCiclo.getCicloAcademico().getId())
                    && trasladoBean.getTramiteTraslado().getTipoTramiteTrasladoEnum() == TipoTramiteTrasladoEnum.TRAS) {

                alumnoCiclo.setUserModificacion(ds.getUsuario());
                alumnoCiclo.setFechaModificacion(new Date());
                alumnoCiclo.setCreditosConvalidados(total);
                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
                alumnoCicloDAO.update(alumnoCiclo);
            }

            List<AlumnoCicloCurso> listAlumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnoCiclo(alumnoCiclo);
            logger.debug("*********** listAlumnoCicloCurso   {}", listAlumnoCicloCurso.size());

            Map<Long, AlumnoCicloCurso> mapListAlumnoCicloCurso = TypesUtil.convertListToMap("curso.id", listAlumnoCicloCurso);

            logger.debug("*********** AlumnoCicloCurso CURSO ID {}", cursoConvalidado.getCurso().getId());
            AlumnoCicloCurso alumnoCicloCursoFound = mapListAlumnoCicloCurso.get(cursoConvalidado.getCurso().getId());

            if (alumnoCicloCursoFound == null) {
                logger.debug("*********** AlumnoCicloCurso inexistente");
                this.saveAlumnoCicloCurso(cursoConvalidado, alumnoCiclo, ds);
            } else {
                if (!alumnoCicloCursoFound.getNota().equals(cursoConvalidado.getNota()) || !alumnoCicloCursoFound.getCreditos().equals(cursoConvalidado.getCreditos())) {
                    logger.debug("*********** AlumnoCicloCurso existente: id  {}", alumnoCicloCursoFound.getId());

                    alumnoCicloCursoFound.setFechaModificacion(new Date());
                    alumnoCicloCursoFound.setUserModificacion(ds.getUsuario());
                    alumnoCicloCursoFound.setNota(cursoConvalidado.getNota() == null ? "TE" : cursoConvalidado.getNota());
                    alumnoCicloCursoFound.setRegistroActivo(1);
                    alumnoCicloCursoFound.setCreditos(cursoConvalidado.getCreditos());
                    alumnoCicloCursoDAO.updateColumns(alumnoCicloCursoFound, "fechaModificacion", "userModificacion", "nota", "registroActivo", "creditos");
                } else {
                    return;
                }

            }
            if (cursoConvalidado.getId() == null) {
                cursoConvalidado.setNota(cursoConvalidado.getNota() == null ? "TE" : cursoConvalidado.getNota());
                cursoConvalidado.setUserRegistro(ds.getUsuario());
                cursoConvalidado.setFechaRegistro(new Date());
                cursoConvalidado.setTramiteTraslado(tramiteTraslado);
                cursoConvalidadoDAO.save(cursoConvalidado);
            } else {
                logger.debug("*********** cursoConvalidado existente");
                cursoConvalidado.setUserModifica(ds.getUsuario());
                cursoConvalidado.setFechaModificacion(new Date());
                cursoConvalidadoDAO.updateColumns(cursoConvalidado, "fechaModificacion", "userModifica", "nota", "creditos");

            }

        } else {
            logger.debug("*********** alumnoCiclo inexistente");

            alumnoCiclo = this.saveAlumnoCiclo(alumno, tramiteTraslado.getCicloAcademico(), total, ds);
            for (CursoConvalidado cursoConvalidad : listCursoConvalidadoNew) {
                this.saveAlumnoCicloCurso(cursoConvalidad, alumnoCiclo, ds);
                cursoConvalidado.setNota(cursoConvalidado.getNota() == null ? "TE" : cursoConvalidado.getNota());

                cursoConvalidad.setUserRegistro(ds.getUsuario());
                cursoConvalidad.setFechaRegistro(new Date());
                cursoConvalidad.setTramiteTraslado(tramiteTraslado);
                cursoConvalidadoDAO.save(cursoConvalidad);
            }
        }

    }

    @Override
    public List<CursoConvalidado> alllCursoConvalidadoInTraslado(List<TramiteTraslado> listTramiteTraslado) {
        return cursoConvalidadoDAO.allInTramiteTraslado(listTramiteTraslado);
    }

    private void updateAlumnoCicloCurso(CursoConvalidado cursoConvalidado, AlumnoCiclo alumnoCiclo, AlumnoCicloCurso alumnoCicloCurso, DataSessionPivot ds) {
        alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
        alumnoCicloCurso.setCurso(cursoConvalidado.getCurso());
        alumnoCicloCurso.setCreditos(cursoConvalidado.getCreditos());
        alumnoCicloCurso.setNota(cursoConvalidado.getNota() == null ? "TE" : cursoConvalidado.getNota());
        alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
        alumnoCicloCurso.setEstaAprobado(1);
        alumnoCicloCurso.setRegistroActivo(1);
        alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.TE);
        alumnoCicloCurso.setVecesCursado(1);
        alumnoCicloCurso.setUserModificacion(ds.getUsuario());
        alumnoCicloCurso.setFechaModificacion(new Date());
        alumnoCicloCursoDAO.update(alumnoCicloCurso);
        cursoConvalidado.setAlumnoCicloCurso(alumnoCicloCurso);
    }

    private void saveAlumnoCicloCurso(CursoConvalidado cursoConvalidado, AlumnoCiclo alumnoCiclo, DataSessionPivot ds) {
        AlumnoCicloCurso alumnoCicloCurso = new AlumnoCicloCurso();
        alumnoCicloCurso.setFechaRegistro(new Date());
        alumnoCicloCurso.setUsuarioRegistro(ds.getUsuario());
        alumnoCicloCurso.setCurso(cursoConvalidado.getCurso());
        alumnoCicloCurso.setCreditos(cursoConvalidado.getCreditos());
        alumnoCicloCurso.setNota(cursoConvalidado.getNota() == null ? "TE" : cursoConvalidado.getNota());
        alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
        alumnoCicloCurso.setEstaAprobado(1);
        alumnoCicloCurso.setRegistroActivo(1);
        alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.TE);
        alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
        alumnoCicloCurso.setVecesCursado(1);
        alumnoCicloCursoDAO.save(alumnoCicloCurso);
        cursoConvalidado.setAlumnoCicloCurso(alumnoCicloCurso);
    }

    private AlumnoCiclo saveAlumnoCiclo(Alumno alumno, CicloAcademico cicloTram, Integer total, DataSessionPivot ds) {
        AlumnoCiclo alumnoCiclo = new AlumnoCiclo();
        alumnoCiclo.defaultValuesToCreate(alumno, cicloTram, ds.getUsuario());
        alumnoCiclo.setAlumno(alumno);
        alumnoCiclo.setCicloAcademico(cicloTram);
        alumnoCiclo.setUserRegistro(ds.getUsuario());
        alumnoCiclo.setFechaRegistro(new Date());
        alumnoCiclo.setCarrera(alumno.getCarrera());
        alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
        alumnoCiclo.setEstaAprobado(1);
        alumnoCiclo.setCursosAprobados(0);
        alumnoCiclo.setCursosInscritos(0);
        alumnoCiclo.setPromedioAcumulado(BigDecimal.ZERO);
        alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
        alumnoCiclo.setCreditosConvalidados(total);
        alumnoCiclo.setCreditosAprobadosAcumulados(0);
        alumnoCiclo.setCreditosAprobadosCiclo(0);
        alumnoCiclo.setCreditosCursadosCiclo(0);
        alumnoCiclo.setCreditosAcumulados(0);
        alumnoCicloDAO.save(alumnoCiclo);
        return alumnoCiclo;
    }

    @Override
    public void verificarTramiteTraslado(Alumno alumno) {
        if (tramiteTrasladoDAO.allByAlumno(alumno) == null || tramiteTrasladoDAO.allByAlumno(alumno).isEmpty()) {
            throw new PhobosException("El alumno con id" + alumno.getId() + " no tiene resolución para la convalidación de cursos");
        }
    }

    @Override
    public List<Curso> allCurso(String nombre) {

        return cursoDAO.allCursoByName(nombre);

    }

    //@Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarFalla(Alumno alumno) {
        alumnoDAO.updateColumns(alumno, "conError");
    }

    @Override
    @Transactional
    public void saveAccesoEspecial(AccesoEspecialBean accesoEspecialBean, DataSessionPivot ds) {
        String CorreoForm = accesoEspecialBean.getCorreo(); // correo a remitir las credenciales
        Persona personaForm = accesoEspecialBean.getAlumno().getPersona();

        Usuario usuarioBD = usuarioDAO.findActivoByPersona(personaForm);

        if (usuarioBD == null) {
            throw new PhobosException("El alumno con " + personaForm.getApellidosNombres() + " no tiene registro de usuario");
        }

        usuarioBD.setUserDni(accesoEspecialBean.getDni());
        usuarioBD.setUserDniPass(TypesUtil.toMD5(accesoEspecialBean.getContraseña()));
        usuarioDAO.update(usuarioBD);

        Persona personaBD = personaDAO.find(personaForm.getId());
        personaBD.setEmail(CorreoForm);
        personaBD.setUserModificacion(ds.getUsuario());
        personaDAO.update(personaBD);

//        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(ContenidoEmailEnum.CREATEACCESOESPECIAL.name());   PENDIENTE
//        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(ContenidoEmailEnum.CREATEUSERALUMNOVISITANTE.name());
//        mailerService.enviarCorreoAccesoEspecial(accesoEspecialBean.getCorreo(), usuarioBD, accesoEspecialBean.getContraseña(), "Acceso Especial", contenidoCarta);
    }

    @Override
    public Usuario findUsuarioByPersona(Persona persona) {
        Usuario usuario = usuarioDAO.findActivoByPersona(persona);
        if (usuario == null) {
            throw new PhobosException("El alumno con " + persona.getApellidosNombres() + " no tiene registro de usuario");
        }
        return usuario;
    }

    @Override
    public List<AlumnoCursoCurricula> allAlumnoCursoByalumno(Alumno alumno, DynatableFilter filter) {

        return alumnoCursoCurriculaDAO.allDynaTable(alumno, filter);
    }

    @Override
    @Transactional
    public void habilitarAlumnoCursoCurricula(AlumnoCursoCurricula alumnoCursoCurricula, DataSessionPivot ds) {
        alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.HAB);
        alumnoCursoCurriculaDAO.updateEstado(alumnoCursoCurricula);

    }

    @Override
    @Transactional
    public void deshabilitarAlumnoCursoCurricula(AlumnoCursoCurricula alumnoCursoCurricula, DataSessionPivot ds) {
        alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.NREQ);
        alumnoCursoCurriculaDAO.updateEstado(alumnoCursoCurricula);

    }

    @Override
    @Transactional
    public void agregarAlumnoCursoCurricula(CursoOpcionalCurricula cursOpcional, Alumno alumno) {

        AlumnoCursoCurricula alumnoCursoCurricula = new AlumnoCursoCurricula();
        alumnoCursoCurricula.setCreditos(cursOpcional.getCurso().getCreditos());
        alumnoCursoCurricula.setAlumno(alumno);
        alumnoCursoCurricula.setCurso(cursOpcional.getCurso());
        alumnoCursoCurricula.setCursoOpcional(cursOpcional);
        alumnoCursoCurricula.setTipoCursoCurricula(cursOpcional.getTipoCursoCurricula());
        alumnoCursoCurricula.setNumeroCiclo(10);
        alumnoCursoCurricula.setVecesCursado(alumnoCicloCursoDAO.countByCursoAlumno(cursOpcional.getCurso(), alumno).intValue());
        alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.HAB);
        alumnoCursoCurricula.setEstadoRegistro("ACT");
        alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);

    }

    @Override
    public List<CursoOpcionalCurricula> allcursosOpcional(Long idAlumno) {
        Alumno alumno = alumnoDAO.find(new Alumno(idAlumno));
        if (alumno.getPlanCurricular() != null) {
            return cursoOpcionalCurriculaDAO.allByPlanCurricular(alumno.getPlanCurricular());
        }
        return new ArrayList();
    }

    @Override
    @Transactional
    public Alumno saveFotoCarnet(Alumno alumnoForm, DataSessionPivot ds) {
        Alumno alumnoBD = alumnoDAO.find(alumnoForm);
        String nombreArchivo = alumnoForm.getPersona().getFoto();
        File file = new File(GlobalConstantine.TMP_DIR + nombreArchivo);
        logger.debug("el archivo {} existe {} ", (GlobalConstantine.TMP_DIR + nombreArchivo), (file.exists()));
        Assert.isTrue(file.exists(), "No existe el archivo en el servidor");
        uploadFileS3.uploadSync(AcademicoConstantine.S3_DIR_FOTO_CARNET, GlobalConstantine.TMP_DIR, nombreArchivo, true);
        String path = uploadFileS3.getPathFile(AcademicoConstantine.S3_DIR_FOTO_CARNET, nombreArchivo);
        alumnoBD.getPersona().setFoto(path);
        alumnoBD.getPersona().setUserModificacion(ds.getUsuario());
        personaDAO.update(alumnoBD.getPersona());

        return alumnoBD;
    }

    @Override
    public List<Carrera> allCarrerasOfFacultadEconomia() {
        String codigoFacultadEconomia = "040";
        Facultad facultad = facultadDAO.findByCodigo(codigoFacultadEconomia);
        return carreraDAO.allCarrerasOfFacultadEconomia(facultad);
    }

    @Override
    public List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras) {
        return alumnoDAO.allAlumnosbyDynatable(filter, carreras);
    }

    @Override
    public Docente finDocenteAccesoEspecial() {
        return docenteDAO.findByCode("1272");//tmp
    }

    @Override
    public List<CicloAcademico> ciclosAcademicosConvalidarCurso(Alumno alumno) {
        List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allMatriculaResumenByAlumno(alumno)
                .stream()
                .sorted(Comparator.comparing(MatriculaResumen::getId).reversed())
                .collect(Collectors.toList());
        return TypesUtil.extractListByAttr("cicloAcademico", matriculasResumen);
    }

}
