package pe.edu.lamolina.pivot.controller.academico.alumno;

import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.util.StringUtil;
import org.joda.time.DateTime;
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
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.ContenidoEmailEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.mail.MailerService;

@Service
@Transactional(readOnly = true)
public class AlumnoServiceImp implements AlumnoService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;
    @Autowired
    UsuarioDAO usuarioDAO;
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    DiaDAO diaDAO;
    @Autowired
    HoraDAO horaDAO;
    @Autowired
    MailerService mailerService;
    @Autowired
    AvanceCurricularService avanceCurricularService;
    @Autowired
    RolDAO rolDAO;
    @Autowired
    UsuarioRolDAO usuarioRolDAO;
    @Autowired
    OrientacionCarreraDAO orientacionCarreraDAO;
    
    @Override
    public List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, List<Carrera> carreras) {
        return alumnoDAO.allByRolDynatable(filter, carreras);
    }
    
    @Override
    public List<Alumno> allAlumnosByFacultadDynatable(DynatableFilter filter, List<Facultad> facultades) {
        return alumnoDAO.allByFacultadDynatable(filter, facultades);
    }
    
    @Override
    public AlumnoResumen findResumen() {
        return alumnoDAO.findResumen();
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
    public void saveAlumnoFisico(Alumno alumno, Usuario usuario) {
        
        Persona personaForm = alumno.getPersona();
        this.clearAlumnoPersonaForm(alumno, personaForm);
        
        if (alumno.getModalidadEstudio() == null) {
            throw new PhobosException("Debe especificar la modalidad de estudio");
        }
        
        if (alumno.getCarrera() == null) {
            throw new PhobosException("Debe especificar la carrera");
        }
        
        if (alumno.getCicloIngreso() == null) {
            throw new PhobosException("Debe especificar el ciclo de ingreso");
        }
        
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
            personaForm.setUserRegistro(usuario);
            personaForm.setFechaRegistro(new Date());
            this.validarDNI(personaForm);
            personaDAO.save(personaForm);
            
            this.crearUsuarioAlumno(emailCompania, personaForm, usuario);
            this.saveAlumno(alumno, personaForm, ciclo, codigoMatricula);
            this.enviarNotificacionUsuarioCreacion(personaForm);
            this.updateCicloSgteMatricula(ciclo);
            return;
        }
        
        Alumno alumnoDB = alumnoDAO.findByPersonaCicloIngreso(personaDB, ciclo);//ojo alumno por persona ciclo
        if (alumnoDB != null) {
            throw new PhobosException("El documento ya pertenece a otro alumno");
        }
        
        this.updatePersona(personaDB, personaForm);
        
        Usuario usuarioAlumno = usuarioDAO.findByPersona(personaDB);
        
        if (usuarioAlumno == null) {
            this.crearUsuarioAlumno(emailCompania, personaDB, usuario);
        }
        
        this.saveAlumno(alumno, personaDB, ciclo, codigoMatricula);
        this.enviarNotificacionUsuarioCreacion(personaForm);
        this.updateCicloSgteMatricula(ciclo);
        
    }
    
    @Transactional
    public void crearUsuarioAlumno(String emailCompania, Persona persona, Usuario usuarioRegistra) {
        Usuario usuarioAlumno = new Usuario();
        usuarioAlumno.setGoogle(emailCompania);
        usuarioAlumno.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioAlumno.setFechaRegistro(new Date());
        usuarioAlumno.setPersona(persona);
        usuarioAlumno.setUserRegistro(usuarioRegistra);
        usuarioDAO.save(usuarioAlumno);
        
        Rol rol = rolDAO.findByCode(RolEnum.ALU);
        UsuarioRol ur = new UsuarioRol();
        ur.setEstado(UserEstadoEnum.ACT);
        ur.setFechaInicio(new Date());
        ur.setFechaRegistro(new Date());
        ur.setRol(rol);
        ur.setUserRegistro(usuarioRegistra);
        ur.setUsuario(usuarioAlumno);
        usuarioRolDAO.save(ur);
        
    }
    
    @Transactional
    public void saveAlumno(Alumno alumno, Persona persona, CicloAcademico ciclo, String codigoMatricula) {
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
    
    @Transactional
    private Persona updatePersona(Persona personaBD, Persona personaForm) {
        
        personaBD.setPaisNacer(personaForm.getPaisNacer());
        personaBD.setPaisDomicilio(personaForm.getPaisDomicilio());
        personaBD.setUbicacionNacer(personaForm.getUbicacionNacer());
        personaBD.setNacionalidad(personaForm.getNacionalidad());
        personaBD.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        
        personaBD.setNombres(personaForm.getNombres());
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setFechaNacer(personaForm.getFechaNacer());
        personaBD.setDireccion(personaForm.getDireccion());
        personaBD.setCelular(personaForm.getCelular());
        personaBD.setTelefono(personaForm.getTelefono());
        personaBD.setEmail(personaForm.getEmail());
        personaBD.setEmailCompania(personaForm.getEmailCompania());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());
        
        this.validarEmailConPersona(personaForm.getEmail(), personaBD);
        this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaBD);
        
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
    
    @Transactional
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
        if (personaForm.getNumeroDocIdentidad().equals(Constantine.CODE_POSTULANTE_DUMMY)) {
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
    public void updateAlumnoFisico(Alumno alumno, Usuario usuarioRegistra) {
        
        Persona personaForm = alumno.getPersona();
        this.clearAlumnoPersonaForm(alumno, personaForm);
        
        this.verificarPersona(personaForm);
        this.validarDNI(personaForm);
        Persona personaBD = personaDAO.find(personaForm.getId());
        if (personaBD == null) {
            throw new PhobosException("Alumno sin persona registrada.");
        }
        
        Usuario usuario = usuarioDAO.findByPersona(personaBD);
        
        if (usuario == null) {
            this.crearUsuarioAlumno(personaForm.getEmailCompania(), personaBD, usuarioRegistra);
        } else {
            logger.debug("{} =? {}", personaBD.getEmailCompania(), personaForm.getEmailCompania());
            if (!personaBD.getEmailCompania().equals(personaForm.getEmailCompania())) {
                this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaBD);
                logger.debug("not eq");
                usuario.setGoogle(personaForm.getEmailCompania());
                usuario.setFechaModifica(new Date());
                usuario.setUserModifica(usuarioRegistra);
                usuarioDAO.update(usuario);
                
                Rol rol = rolDAO.findByCode(RolEnum.ALU);
                this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
                
                UsuarioRol ur = usuarioRolDAO.findByUsuarioRol(usuario, rol);
                
                if (ur == null) {
                    ur = new UsuarioRol();
                    ur.setEstado(UserEstadoEnum.ACT);
                    ur.setFechaInicio(new Date());
                    ur.setFechaRegistro(new Date());
                    ur.setRol(rol);
                    ur.setUserRegistro(usuarioRegistra);
                    ur.setUsuario(usuario);
                    usuarioRolDAO.save(ur);
                } else {
                    ur.setEstado(UserEstadoEnum.ACT);
                    usuarioRolDAO.update(ur);
                }
                
            }
            
        }
        this.updatePersona(personaBD, personaForm);
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
    public void updateAlumnoEspecial(Alumno alumno, Usuario usuarioRegistra) {
        Carrera carrera = carreraDAO.findByCodigo(Constantine.COD_CARRERA_ALUMNO_ESPECIAL);
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.ESP);
        alumno.setCarrera(carrera);
        alumno.setModalidadEstudio(modalidadEstudio);
        this.updateAlumnoFisico(alumno, usuarioRegistra);
    }
    
    @Override
    @Transactional
    public void saveAlumnoEspecial(Alumno alumno, Usuario usuarioRegistra) {
        Carrera carrera = carreraDAO.findByCodigo(Constantine.COD_CARRERA_ALUMNO_ESPECIAL);
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.ESP);
        alumno.setCarrera(carrera);
        alumno.setModalidadEstudio(modalidadEstudio);
        if (StringUtils.isBlank(alumno.getEstado())) {
            alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);
        }
        this.saveAlumnoFisico(alumno, usuarioRegistra);
    }
    
    @Override
    @Transactional
    public String goMatricula(Long idAlumno) {
        
        Alumno alumno = alumnoDAO.find(new Alumno(idAlumno));
        String valor = RandomStringUtils.randomAlphanumeric(45);
        TokenIngresante token = new TokenIngresante();
        token.setEstado(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusSeconds(5).toDate());
        token.setPersona(alumno.getPersona());
        token.setValor(valor);
        tokenIngresanteDAO.save(token);
        return alumno.getCodigo();
    }
    
}
