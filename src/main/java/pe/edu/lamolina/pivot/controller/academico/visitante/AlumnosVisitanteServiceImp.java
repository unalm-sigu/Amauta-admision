package pe.edu.lamolina.pivot.controller.academico.visitante;

import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.ContenidoEmailEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.general.persona.PersonaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoVisitanteDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.zelper.mail.MailerService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AlumnosVisitanteServiceImp implements AlumnosVisitanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    AlumnoVisitanteDAO alumnoVisitanteDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    PersonaService personaService;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Autowired
    MailerService mailerService;

    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;

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
    public void save(AlumnoVisitante alumnoVisitante, DataSessionPivot dataSessionPivot) {

        Usuario usuario = dataSessionPivot.getUsuario();
        logger.debug("**guardando alumno visitante by usr {} {} **", usuario.getId(), usuario.getUsuario());
        Persona personaForm = alumnoVisitante.getPersona();

        personaForm.setNumeroDocIdentidad(this.limpiarValor(personaForm.getNumeroDocIdentidad()));
        if (personaForm.getTipoDocumento() == null || personaForm.getTipoDocumento().getId() == null) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (personaForm.getNumeroDocIdentidad() == null) {
            throw new PhobosException("Debe indicar el número del documento de identidad");
        }

        logger.debug("buscar persona  doc {} num  {} ...", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());
        Persona persona = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());

        if (persona == null) {
            logger.debug("persona  doc {} num  {} not found \n creando datos ", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());
            this.createAlumno(alumnoVisitante, usuario);
            return;
        }

        logger.debug("persona  doc {} num  {} found  \n update datos ", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());
        this.updateAlumno(alumnoVisitante, usuario, persona);

    }

    private String generateCodigo(CicloAcademico ciclo) {
        logger.debug("generateCodigo CicloAcademico {}", ciclo.getId());
        logger.debug("CicloAcademico getMatriculaSiguiente {}", ciclo.getMatriculaSiguiente());
        if (ciclo.getMatriculaSiguiente() == null) {
            ciclo.setMatriculaSiguiente(1);
        }
        if (ciclo.getMatriculaInicio() == null) {
            ciclo.setMatriculaInicio(1);
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

    @Transactional
    private void createAlumno(AlumnoVisitante alumnoVisitante, Usuario usuario) {

        logger.debug("**createAlumno**");
        CicloAcademico ciclo = cicloAcademicoDAO.find(alumnoVisitante.getCicloEstudia().getId());
        logger.debug("**con ciclo academico {} {} **", ciclo.getId(), ciclo.getNumeroCiclo());

        String codigoMatricula = this.generateCodigo(ciclo);
        logger.debug("**codigoMatricula {} {} **", codigoMatricula, codigoMatricula);
        String emailCompania = this.generateEmailCompania(codigoMatricula);
        logger.debug("**emailCompania {} {} **", emailCompania, emailCompania);

        Persona persona = alumnoVisitante.getPersona();
        persona.setEmailCompania(emailCompania);
        this.validarEmailsinPersona(persona.getEmail());
        this.validarEmailEmpresaSinPersona(persona.getEmailCompania());

        persona.setEstado(PersonaEstadoEnum.ACT.name());
        persona.setUserRegistro(usuario);
        persona.setFechaRegistro(new Date());

        personaDAO.save(persona);
        logger.debug("**save persona {} {} **", persona.getId(), persona.getId());

        Usuario usuarioVisitante = new Usuario();
        usuarioVisitante.setUsuario(emailCompania);
        usuarioVisitante.setEstado(UserEstadoEnum.ACT.name());
        usuarioVisitante.setFechaRegistro(new Date());
        usuarioVisitante.setPersona(persona);
        usuarioVisitante.setUserRegistro(usuario);

        usuarioDAO.save(usuarioVisitante);
        logger.debug("**save usuarioVisitante {} {} **", usuarioVisitante.getId(), usuarioVisitante.getId());

        Carrera carrera = carreraDAO.findByCodigo("001");
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
        alumno.setPromedioCarreraAcumulado(BigDecimal.ZERO);

        alumnoDAO.save(alumno);
        logger.debug("**save alumno {} {} **", alumno.getId(), alumno.getId());

        alumnoVisitante.setFechaRegistro(new Date());
        alumnoVisitante.setUserRegistro(usuario);
        alumnoVisitante.setPersona(persona);
        alumnoVisitanteDAO.save(alumnoVisitante);
        logger.debug("**save alumnoVisitante {} {} **", alumnoVisitante.getId(), alumnoVisitante.getId());

        this.updateCicloSgteMatricula(ciclo);
        logger.debug("**updateCicloSgteMatricula {} {} **", ciclo.getId(), ciclo.getId());
        logger.debug("PERSONA ID- {}", persona.getId());
        logger.debug("============");
        String mensaje = usuarioVisitante.getUsuario();
        this.enviarNotificacionUsuarioCreacion(persona, "usuario creación ", mensaje);
        //mailerService.enviarNotificacionUsuarioCreacion(persona, "Usuario creación", mensaje);

    }

    @Transactional
    private void updateAlumno(AlumnoVisitante alumnoVisitante, Usuario usuario, Persona persona) {
        logger.debug("**updateAlumno**");
        Persona personaDb = this.getPersonaBD(persona, alumnoVisitante.getPersona());
        logger.debug("**personaDb {} {} **", personaDb.getId(), personaDb.getId());
        CicloAcademico ciclo = cicloAcademicoDAO.find(alumnoVisitante.getCicloEstudia().getId());
        logger.debug("**CicloAcademico {} {} **", ciclo.getId(), ciclo.getId());

        boolean updateCiclo = false;

        String codigoMatricula = this.generateCodigo(ciclo);
        String emailCompania = this.generateEmailCompania(codigoMatricula);

        if (Strings.isNullOrEmpty(personaDb.getEmailCompania())) {
            personaDb.setEmailCompania(emailCompania);
            personaDAO.update(persona);
            logger.debug("**persona sin emailcompania {} email {} **", personaDb.getId(), emailCompania);
            updateCiclo = true;
        }

        Usuario usuarioVisitante = usuarioDAO.findByPersona(persona);

        if (usuarioVisitante == null) {
            usuarioVisitante = new Usuario();
            usuarioVisitante.setUsuario(emailCompania);
            usuarioVisitante.setEstado(UserEstadoEnum.ACT.name());
            usuarioVisitante.setFechaRegistro(new Date());
            usuarioVisitante.setPersona(persona);
            usuarioVisitante.setUserRegistro(usuario);
            usuarioDAO.save(usuarioVisitante);
            logger.debug("**persona sin usuario {} email {} **", usuarioVisitante.getId(), emailCompania);
            updateCiclo = true;
            String mensaje = usuarioVisitante.getUsuario();
            this.enviarNotificacionUsuarioCreacion(persona, "usuario creación ", mensaje);
            //mailerService.enviarNotificacionUsuarioCreacion(persona, "usuario creación ", mensaje);
        }

        if (Strings.isNullOrEmpty(usuarioVisitante.getUsuario())) {
            usuarioVisitante.setUsuario(emailCompania);
            usuarioDAO.update(usuarioVisitante);
            logger.debug("**usuario sin email compania {} email {} **", usuarioVisitante.getId(), emailCompania);
            updateCiclo = true;
        }

        Alumno alumno = alumnoDAO.findByPersona(persona, ciclo);

        if (alumno == null) {

            Carrera carrera = carreraDAO.findByCodigo("001");
            ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.VIS);
            SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");

            alumno = new Alumno();
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
            alumno.setPromedioCarreraAcumulado(BigDecimal.ZERO);

            alumnoDAO.save(alumno);
            logger.debug("**persona sin alumno {} codigo {} **", alumno.getId(), codigoMatricula);
            updateCiclo = true;
        }

        AlumnoVisitante alumnoVisitanteDb = alumnoVisitanteDAO.findByPersona(persona);

        if (alumnoVisitanteDb == null) {
            alumnoVisitante.setFechaRegistro(new Date());
            alumnoVisitante.setUserRegistro(usuario);
            alumnoVisitante.setPersona(persona);
            alumnoVisitanteDAO.save(alumnoVisitante);
            logger.debug("**creando visitante {} codigo {} **", alumnoVisitante.getId(), codigoMatricula);
        } else {
            alumnoVisitanteDb.setUniversidadExtranjera(alumnoVisitante.getUniversidadExtranjera());
            alumnoVisitanteDb.setUniversidad(alumnoVisitante.getUniversidad());
            alumnoVisitanteDb.setCicloEstudia(alumnoVisitante.getCicloEstudia());
            alumnoVisitanteDb.setPaisUniversidad(alumnoVisitante.getPaisUniversidad());
            alumnoVisitanteDAO.update(alumnoVisitanteDb);
            logger.debug("**actualizando visitante {} codigo {} **", alumnoVisitanteDb.getId(), codigoMatricula);
        }

        if (updateCiclo) {
            logger.debug("**actualizando ciclo {} **", ciclo.getId());
            this.updateCicloSgteMatricula(ciclo);
        }

    }

    @Transactional
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

    private Persona getPersonaBD(Persona persona, Persona personaForm) {
        Persona personaBD = personaDAO.find(persona.getId());
        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, personaForm, Arrays.asList("email", "paterno", "materno", "nombres", "sexo", "fechaNacer", "direccion", "celular", "telefono"));
        if (sinCambios) {
            logger.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }
        this.validarEmailEmpresaConPersona(personaForm.getEmail(), persona);
        personaBD.setNombres(personaForm.getNombres());
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setFechaNacer(personaForm.getFechaNacer());
        personaBD.setDireccion(personaForm.getDireccion());
        personaBD.setCelular(personaForm.getCelular());
        personaBD.setTelefono(personaForm.getTelefono());
        personaBD.setEmail(personaForm.getEmail());
        personaDAO.update(personaBD);
        return personaBD;
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

    private void validarCodigo(String codigo) {
        Alumno alumnoDB = alumnoDAO.findByCodigo(codigo);
        if (alumnoDB != null) {
            throw new PhobosException("El código o matrícula ya se encuentra ocupado por otro alumno.");
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

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int yearinit = year - 4;
        int yearend = year + 3;
        return cicloAcademicoDAO.allCicloAcademicoByRange(yearinit, yearend);
    }

    private void enviarNotificacionUsuarioCreacion(Persona persona, String usuarioCreacion, String mensaje) {
        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(ContenidoEmailEnum.CREATEUSERALUMNOVISITANTE.name());
        mailerService.enviarNotificacionUsuarioCreacion(persona, contenidoCarta);
    }

    @Override
    public Map<Long, Alumno> allAlumnoByVisitante(List<AlumnoVisitante> visitantes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
