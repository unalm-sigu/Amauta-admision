package pe.edu.lamolina.amauta.controller.academico.visitante;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.LinkedHashMap;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.general.persona.PersonaService;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.ContenidoEmailEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoGestionEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoVisitanteDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaHistorialDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.general.UniversidadDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.amauta.zelper.mail.MailerService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.PersonaHistorial;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class AlumnosVisitanteServiceImp implements AlumnosVisitanteService {

    private final AlumnoDAO alumnoDAO;
    private final AlumnoVisitanteDAO alumnoVisitanteDAO;
    private final CarreraDAO carreraDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final ContenidoCartaDAO contenidoCartaDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;
    private final PersonaDAO personaDAO;
    private final PersonaHistorialDAO personaHistorialDAO;
    private final SituacionAcademicaDAO situacionAcademicaDAO;
    private final TipoDocIdentidadDAO tipoDocIdentidadDAO;
    private final UniversidadDAO universidadDAO;
    private final UsuarioDAO usuarioDAO;

    private final MailerService mailerService;
    private final PersonaService personaService;

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<AlumnoVisitante> allAlumnoVisitante(DynatableFilter filter) {
        return alumnoVisitanteDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void save(AlumnoVisitante alumnoVisitante, DataSessionPivot ds) {
        Usuario usuario = ds.getUsuario();
        log.info("**guardando alumno visitante by usr {} {} **", usuario.getId(), usuario.getGoogle());
        Persona personaForm = alumnoVisitante.getPersona();

        this.verificarPersona(personaForm);
        log.info("buscar persona  doc {} num  {} ...", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());

        Persona personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        CicloAcademico ciclo = cicloAcademicoDAO.find(alumnoVisitante.getCicloEstudia().getId());

        if (personaDB == null) {
            personaDB = this.savePersona(personaForm, ciclo, ds);

        } else {
            log.debug("**guardando alumno visitante by usr {} {} **", usuario.getId(), usuario.getGoogle());
            AlumnoVisitante alumnoVisitanteDB = alumnoVisitanteDAO.findByPersona(personaDB);
            Assert.isNull(alumnoVisitante, "El documento ya pertenece a otro alumno visitante");

            PersonaHistorial personaHistorial = new PersonaHistorial();
            personaHistorial.setUsuario(ds.getUsuario());
            personaHistorial.setPersona(personaDB);
            personaHistorial.setFecha(new Date());
            personaHistorial.setNumeroDocumentoFrom(personaDB.getNumeroDocIdentidad());
            personaHistorial.setNumeroDocumentoTo(personaForm.getNumeroDocIdentidad());
            personaHistorial.setTipoDocumentoFrom(personaDB.getTipoDocumento());
            personaHistorial.setTipoDocumentoTo(personaForm.getTipoDocumento());
            personaHistorialDAO.save(personaHistorial);

            personaDB = this.updatePersona(personaDB, personaForm, ds);
            this.updateUsuarioAlumno(personaDB, ciclo, ds);

            Alumno alumno = alumnoDAO.findByPersona(personaDB, ciclo);
            MatriculaResumen mr = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);

            if (mr == null) {
                SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");
                mr = new MatriculaResumen();
                mr.setAlumno(alumno);
                mr.setCicloAcademico(ciclo);
                mr.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                mr.setCreditosMatriculados(0);
                mr.setCreditosRetirados(0);
                mr.setCursosMatriculados(0);
                mr.setCursosRetirados(0);
                mr.setPorcentajeAvance(0);
                mr.setSituacionInicio(situacion);
                mr.setCreditosTrikaPagados(BigDecimal.ZERO.intValue());
                mr.setCreditosTrikaSeparados(BigDecimal.ZERO.intValue());
                matriculaResumenDAO.save(mr);
            }
        }

        alumnoVisitante.setFechaRegistro(new Date());
        alumnoVisitante.setUserRegistro(usuario);
        alumnoVisitante.setPersona(personaDB);

        alumnoVisitanteDAO.save(alumnoVisitante);

    }

    private void updateUsuarioAlumno(Persona personaDB, CicloAcademico ciclo, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.findByPersona(personaDB, ciclo);
        Assert.isNull(alumno, "Esta persona ya está registrada como Alumno Visitante");

        String codigoMatricula = this.generateCodigo(ciclo);
        String emailCompania = this.generateEmailCompania(codigoMatricula);

        Carrera carrera = carreraDAO.findByCodigo(AcademicoConstantine.COD_CARRERA_ALUMNO_VISITANTE);
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.VIS);
        SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");

        alumno = new Alumno();
        alumno.setPersona(personaDB);
        alumno.setCarrera(carrera);
        alumno.setModalidadEstudio(modalidadEstudio);
        alumno.setCicloActivo(ciclo);
        alumno.setCicloIngreso(ciclo);
        alumno.setSituacionAcademica(situacion);
        alumno.setCodigo(codigoMatricula);

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
        alumno.setCiclosEstudiados(0);
        alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);

        alumnoDAO.save(alumno);

        this.enviarNotificacionUsuarioCreacion(personaDB);
        this.updateCicloSgteMatricula(ciclo);
        this.crearUser(personaDB, emailCompania, ds);
    }

    private void crearUser(Persona persona, String email, DataSessionPivot ds) {
        List<Usuario> users = usuarioDAO.allByPersona(persona);

        Usuario activo = users.stream()
                .filter(usr -> usr.getEstadoEnum() == UserEstadoEnum.ACT)
                .findFirst()
                .orElse(null);

        if (activo != null) {
            activo.setGoogle(email);
            usuarioDAO.update(activo);
            return;
        }

        Usuario otro = users.stream()
                .filter(usr -> usr.getEstadoEnum() != UserEstadoEnum.ACT)
                .findFirst()
                .orElse(null);

        if (otro != null) {
            otro.setGoogle(email);
            otro.setEstadoEnum(UserEstadoEnum.ACT);
            usuarioDAO.update(otro);
            return;
        }

        Usuario usuarioVisitante = new Usuario();
        usuarioVisitante.setGoogle(email);
        usuarioVisitante.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioVisitante.setFechaRegistro(new Date());
        usuarioVisitante.setPersona(persona);
        usuarioVisitante.setUserRegistro(ds.getUsuario());
        usuarioDAO.save(usuarioVisitante);

    }

    private Persona savePersona(Persona persona, CicloAcademico ciclo, DataSessionPivot ds) {

        String codigoMatricula = this.generateCodigo(ciclo);
        String emailCompania = this.generateEmailCompania(codigoMatricula);

        persona.setEmailCompania(emailCompania);
        if (StringUtils.isNotBlank(persona.getEmail())) {
            this.validarEmailsinPersona(persona.getEmail());
        }
        this.validarEmailEmpresaSinPersona(persona.getEmailCompania());

        persona.setPaterno(personaService.corregirNombre(persona.getPaterno(), "Paterno"));
        persona.setMaterno(personaService.corregirNombre(persona.getMaterno(), "Materno"));
        persona.setNombres(personaService.corregirNombre(persona.getNombres(), "Nombres"));
        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setUserRegistro(ds.getUsuario());
        persona.setFechaRegistro(new Date());

        personaDAO.save(persona);

        Usuario usuarioVisitante = new Usuario();
        usuarioVisitante.setGoogle(emailCompania);
        usuarioVisitante.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioVisitante.setFechaRegistro(new Date());
        usuarioVisitante.setPersona(persona);
        usuarioVisitante.setUserRegistro(ds.getUsuario());
        usuarioDAO.save(usuarioVisitante);

        Carrera carrera = carreraDAO.findByCodigo(AcademicoConstantine.COD_CARRERA_ALUMNO_VISITANTE);
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.VIS);
        SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");

        Alumno alumno = new Alumno();
        alumno.setPersona(persona);
        alumno.setCarrera(carrera);
        alumno.setModalidadEstudio(modalidadEstudio);
        alumno.setCicloActivo(ciclo);
        alumno.setCicloIngreso(ciclo);
        alumno.setSituacionAcademica(situacion);
        alumno.setCodigo(codigoMatricula);

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
        alumno.setCiclosEstudiados(0);
        alumno.setPromedioCarreraAcumulado(BigDecimal.ZERO);
        alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);
        alumnoDAO.save(alumno);

        MatriculaResumen mr = new MatriculaResumen();
        mr.setAlumno(alumno);
        mr.setCicloAcademico(ciclo);
        mr.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        mr.setCreditosMatriculados(0);
        mr.setCreditosRetirados(0);
        mr.setCursosMatriculados(0);
        mr.setCursosRetirados(0);
        mr.setPorcentajeAvance(0);
        mr.setSituacionInicio(situacion);
        mr.setCreditosTrikaPagados(BigDecimal.ZERO.intValue());
        mr.setCreditosTrikaSeparados(BigDecimal.ZERO.intValue());
        matriculaResumenDAO.save(mr);

        this.enviarNotificacionUsuarioCreacion(persona);
        this.updateCicloSgteMatricula(ciclo);

        return persona;
    }

    private String generateCodigo(CicloAcademico ciclo) {
        StringBuilder ssb = new StringBuilder();
        ssb.append("Configuración del ciclo académico UNALM ");
        ssb.append(ciclo.getDescripcion());
        ssb.append(" no esta completa");

        boolean existeConfig = ciclo.getMatriculaSiguiente() != null && ciclo.getMatriculaInicio() != null;
        if (!existeConfig) {
            Integer inicio = null;
            if (ciclo.getNumeroCiclo().equals("1")) {
                inicio = 1;
            } else if (ciclo.getNumeroCiclo().equals("2")) {
                CicloAcademico cicloAntes = cicloAcademicoDAO.findAnteriorRegular(ciclo).get(0);
                if (cicloAntes.getYear() == ciclo.getYear().intValue()) {
                    inicio = this.incrementToNextHundred(cicloAntes.getMatriculaSiguiente());
                } else {
                    inicio = 1;
                }
            }

            Assert.isNotNull(inicio, "No se pudo determinar el inicio de los códigos de matrícula");
            ciclo.setMatriculaInicio(inicio);
            ciclo.setMatriculaSiguiente(inicio);
        }

        int sgt = ciclo.getMatriculaSiguiente();
        String year = ciclo.getYear().toString();
        String cod = NumberFormat.codigo(sgt, 4);

        return (year + cod);
    }

    private int incrementToNextHundred(int ultimo) {
        int target = ultimo + 300;
        return ((target + 99) / 100) * 100;
    }

    private String generateEmailCompania(String codigoMatricula) {
        return codigoMatricula + "@lamolina.edu.pe";
    }

    private void updateCicloSgteMatricula(CicloAcademico ciclo) {
        int sgt = ciclo.getMatriculaSiguiente();
        ciclo.setMatriculaSiguiente(sgt + 1);
        cicloAcademicoDAO.update(ciclo);
    }

    private String limpiarValor(String valor) {
        if (valor == null) {
            return null;
        }
        valor = valor.trim();
        if (StringUtils.isEmpty(valor)) {
            return null;
        }
        return valor;
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

    private void validarDNI(Persona personaForm) {
        TipoDocIdentidad doc = personaForm.getTipoDocumento();
        Persona personaBD = personaDAO.findByDocIdentidad(doc, personaForm.getNumeroDocIdentidad());
        if (personaForm.getId() != null && personaBD != null && personaBD.getId().longValue() != personaForm.getId()) {
            throw new PhobosException("El DNI ingresado ya se encuentra relacionado con otra persona: " + personaBD.getApellidosNombres());
        } else if (personaForm.getId() == null && personaBD != null) {
            throw new PhobosException("El DNI ingresado ya se encuentra relacionado con otra persona: " + personaBD.getApellidosNombres());
        }
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

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        int year = new DateTime().getYear();
        int yearinit = year - 4;
        int yearend = year + 5;
        return cicloAcademicoDAO.allPregradoByRange(yearinit, yearend);
    }

    private void enviarNotificacionUsuarioCreacion(Persona persona) {
        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(ContenidoEmailEnum.CREATEUSERALUMNOVISITANTE.name());
        mailerService.enviarNotificacionUsuarioCreacion(persona, contenidoCarta);
    }

    @Override
    public Map<Long, Alumno> allAlumnoByVisitante(List<AlumnoVisitante> visitantes) {
        if (visitantes == null || visitantes.isEmpty()) {
            return new LinkedHashMap();
        }
        List<Persona> personas = visitantes.stream().
                map(AlumnoVisitante::getPersona).
                collect(Collectors.toList());
        List<Alumno> alumnos = alumnoDAO.allByPersonas(personas);
        Map<Long, Alumno> alumnosMap = TypesUtil.convertListToMap("persona.id", alumnos);
        return alumnosMap;
    }

    @Override
    @Transactional
    public void delete(AlumnoVisitante alumnoVisitante) {
        alumnoVisitanteDAO.delete(alumnoVisitante.getId());
    }

    @Override
    public AlumnoVisitante findAlumnoVisitante(Long idAlumnoVisitante) {
        return alumnoVisitanteDAO.findAlumnoVisitante(new AlumnoVisitante(idAlumnoVisitante));
    }

    @Override
    public AlumnoVisitante findAlumnoVisitante(AlumnoVisitante alumnoVisitante) {
        return alumnoVisitanteDAO.findAlumnoVisitante(alumnoVisitante);
    }

    @Override
    @Transactional
    public void update(AlumnoVisitante alumnoVisitante, DataSessionPivot ds) {
        Usuario usuario = ds.getUsuario();
        log.debug("**actualizando alumno visitante by usr {} {} **", usuario.getId(), usuario.getGoogle());
        Persona personaForm = alumnoVisitante.getPersona();
        this.verificarPersona(personaForm);
        this.validarDNI(personaForm);
        log.debug("buscar persona  by id  {} ", personaForm.getId());
        Persona personaBD = personaDAO.find(personaForm.getId());
        if (personaBD == null) {
            throw new PhobosException("Alumno visitante sin persona registrada.");
        }
        PersonaHistorial personaHistorial = new PersonaHistorial();
        personaHistorial.setUsuario(ds.getUsuario());
        personaHistorial.setPersona(personaBD);
        personaHistorial.setFecha(new Date());
        personaHistorial.setNumeroDocumentoFrom(personaBD.getNumeroDocIdentidad());
        personaHistorial.setNumeroDocumentoTo(personaForm.getNumeroDocIdentidad());
        personaHistorial.setTipoDocumentoFrom(personaBD.getTipoDocumento());
        personaHistorial.setTipoDocumentoTo(personaForm.getTipoDocumento());
        personaHistorialDAO.save(personaHistorial);

        this.updatePersona(personaBD, personaForm, ds);
        log.debug("persona  doc {} num  {} found  \n update datos ", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());
        this.updateAlumnoVisitante(alumnoVisitante);
    }

    private Persona updatePersona(Persona personaBD, Persona personaForm, DataSessionPivot ds) {

        ObjectUtil.eliminarAttrSinId(personaForm, "paisNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "nacionalidad");
        ObjectUtil.eliminarAttrSinId(personaForm, "paisDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionDomicilio");
        ObjectUtil.eliminarAttrSinId(personaForm, "tipoDocumento");

        if (personaForm.getUbicacionNacer() == null) {
            personaBD.setUbicacionNacer(null);
        }

        personaBD.setPaisNacer(personaForm.getPaisNacer());
        personaBD.setPaisDomicilio(personaForm.getPaisDomicilio());
        personaBD.setUbicacionNacer(personaForm.getUbicacionNacer());
        personaBD.setNacionalidad(personaForm.getNacionalidad());
        personaBD.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        personaBD.setUserModificacion(ds.getUsuario());

        personaDAO.update(personaBD);

        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, personaForm,
                Arrays.asList("email", "paterno", "materno", "nombres", "sexo", "fechaNacer", "direccion", "celular", "telefono", "numeroDocIdentidad"));

        if (sinCambios) {
            log.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }

        personaBD.setNombres(personaService.corregirNombre(personaForm.getNombres(), "Nombres"));
        personaBD.setPaterno(personaService.corregirNombre(personaForm.getPaterno(), "Paterno"));
        personaBD.setMaterno(personaService.corregirNombre(personaForm.getMaterno(), "Materno"));
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setFechaNacer(personaForm.getFechaNacer());
        personaBD.setDireccion(personaForm.getDireccion());
        personaBD.setCelular(personaForm.getCelular());
        personaBD.setTelefono(personaForm.getTelefono());
        personaBD.setEmail(personaForm.getEmail());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());

        this.validarEmailConPersona(personaForm.getEmail(), personaBD);

        personaBD.setUserModificacion(ds.getUsuario());
        personaDAO.update(personaBD);

        return personaBD;
    }

    @Transactional
    private void updateAlumnoVisitante(AlumnoVisitante alumnoVisitante) {

        log.debug("***update Alumno Visitante***");
        AlumnoVisitante alumnoVisitanteDb = alumnoVisitanteDAO.findAlumnoVisitante(alumnoVisitante);
        if (alumnoVisitanteDb == null) {
            throw new PhobosException("El registro de alumno visitante no fue creado");
        }
        alumnoVisitanteDb.setUniversidadExtranjera(alumnoVisitante.getUniversidadExtranjera());
        log.debug("****alumno Visitante setUniversidadExtranjera {} ****", alumnoVisitante.getUniversidadExtranjera());
        alumnoVisitanteDb.setUniversidad(alumnoVisitante.getUniversidad());
        alumnoVisitanteDb.setCicloEstudia(alumnoVisitante.getCicloEstudia());
        alumnoVisitanteDb.setPaisUniversidad(alumnoVisitante.getPaisUniversidad());
        alumnoVisitanteDAO.update(alumnoVisitanteDb);
    }

    @Override
    public Persona findPersonaByDocumento(Persona persona) {
        return personaDAO.findByDocumento(persona.getTipoDocumento(), persona.getNumeroDocIdentidad());
    }

    @Override
    public ObjectNode validarAlumno(AlumnoVisitante alumnoVisitanteForm) {
        Persona persona = alumnoVisitanteForm.getPersona();

        AlumnoVisitante alumnoVisitanteDb = null;

        if (persona == null) {
            persona = new Persona();
        } else {
            alumnoVisitanteDb = alumnoVisitanteDAO.findByPersona(persona);
        }

        if (alumnoVisitanteDb != null) {
            if (alumnoVisitanteForm.getId() != null) {
                if (alumnoVisitanteForm.getId() != alumnoVisitanteDb.getId().longValue()) {
                    throw new PhobosException("Alumno ya registrado");
                }
            }
        }

        log.debug("persona {}", persona.getId());

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ObjectNode node = JsonHelper.createJson(persona, jsonFactory, true, new String[]{
            "*",
            "tipoDocumento.*",
            "ubicacionNacer.*",
            "ubicacionDomicilio.*",
            "paisNacer.id",
            "paisNacer.nombre",
            "paisNacer.codigo",
            "nacionalidad.id",
            "nacionalidad.nombre",
            "nacionalidad.codigo",
            "paisDomicilio.id",
            "paisDomicilio.nombre",
            "paisDomicilio.codigo"
        });
        return node;
    }

    @Override
    @Transactional
    public void saveUniversidad(Universidad universidad, DataSessionPivot ds) {
        Universidad universidadDb = universidadDAO.findNombrePais(universidad.getNombre(), universidad.getPais());
        if (universidadDb != null) {
            throw new PhobosException("Una universidad con el mismo nombre ya fue registrada");
        }
        Universidad universidadTmp = universidadDAO.findLastCodigoEntranjero();

//        String cod = "";
//        if (universidadTmp != null) {
//            cod = NumberFormat.codigo((Integer.parseInt(universidadTmp.getCodigo().substring(3, 8)) + 1), 5);
//        } else {
//            cod = NumberFormat.codigo(1, 5);
//        }
        universidad.setEstado(EstadoEnum.ACT.name());
        universidad.setTipo("UNIV");
        universidad.setFechaRegistro(new Date());
        universidad.setUserRegistro(ds.getUsuario());
        if (!universidad.getPais().esPeru()) {
            //   universidad.setCodigo("EXT" + cod);
            universidad.setGestion(TipoGestionEnum.AMB.name());
        }

        universidadDAO.save(universidad);
    }

}
