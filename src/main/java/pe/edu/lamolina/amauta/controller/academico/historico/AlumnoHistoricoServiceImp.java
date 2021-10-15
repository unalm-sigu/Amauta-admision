package pe.edu.lamolina.amauta.controller.academico.historico;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.amauta.controller.general.persona.PersonaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoVisitanteDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Transactional(readOnly = true)
public class AlumnoHistoricoServiceImp implements AlumnoHistoricoService {

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
    ContenidoCartaDAO contenidoCartaDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    VerificadorService verificadorService;

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        int year = new DateTime().getYear();
        int yearinit = year - 4;
        int yearend = year + 5;
        return cicloAcademicoDAO.allPregradoByRange(yearinit, yearend);
    }

    @Override
    public List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        return alumnoDAO.allByCarrerasDynatable(filter, carreras, todo);
    }

    @Override
    public String generateCodeRequest() {
        return verificadorService.generateCodeRequest();
    }

    @Override
    public VerificadorServiceImp.CantidadItemsEnum verificarCantidad(TipoOficinaEnum tipoOficinaEnum, HttpServletRequest request, DataSessionPivot ds) {
        return verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);
    }

    @Override
    public List<Carrera> allInstanciasByMenuRol(TipoOficinaEnum tipoOficinaEnum, HttpServletRequest request, DataSessionPivot ds, String codeRequest) {
        return verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds, codeRequest);
    }

    @Override
    public Alumno findAlumno(Long idAlumno) {
        return alumnoDAO.find(idAlumno);
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
    public void save(Alumno alumnoForm, DataSessionPivot ds) {

        Usuario usuario = ds.getUsuario();
        logger.debug("**guardando alumno visitante by usr {} {} **", usuario.getId(), usuario.getGoogle());
        Persona personaForm = alumnoForm.getPersona();
        this.verificarPersona(personaForm);
        logger.debug("buscar persona  doc {} num  {} ...", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());
        Persona personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());

        if (personaDB == null) {
//            personaDB = this.savePersona(personaForm, usuario, ciclo);
        } else {
            logger.debug("**guardando alumno visitante by usr {} {} **", usuario.getId(), usuario.getGoogle());
            AlumnoVisitante alumnoVisitanteDB = alumnoVisitanteDAO.findByPersona(personaDB);
            if (alumnoVisitanteDB != null) {
                throw new PhobosException("El documento ya pertenece a otro alumno visitante");
            }
            personaDB = this.updatePersona(personaDB, personaForm);
//            this.updateUsuarioAlumno(personaDB, usuario, ciclo);
        }

    }

    @Override
    public void update(Alumno alumno, DataSessionPivot ds) {
        alumnoDAO.update(alumno);
    }

    @Override
    public void delete(Alumno alumno) {
        alumnoDAO.update(alumno);
    }

    @Override
    public Persona validarAlumnoDocumento(Persona personaForm) {

        Persona personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());

        if (personaForm.getId() != null) {
            if (personaDB != null && personaDB.getId() != personaForm.getId().longValue()) {
                throw new PhobosException("Número de documento de identidad ya registrado");
            }
        }

        if (personaDB == null) {
            return new Persona();
        }

        Alumno alumnoDB = alumnoDAO.findFirstByPersona(personaDB);

        if (alumnoDB != null) {
            throw new PhobosException("Número de documento ya pertenece a un alumno");
        }

        return personaDB;

    }

    @Transactional
    private Persona savePersona(Persona persona, Usuario usuario) {

        this.validarEmailEmpresaSinPersona(persona.getEmailCompania());

        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setUserRegistro(usuario);
        persona.setFechaRegistro(new Date());

        personaDAO.save(persona);

        Usuario usuarioVisitante = new Usuario();
        //usuarioVisitante.setGoogle(emailCompania);
        usuarioVisitante.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioVisitante.setFechaRegistro(new Date());
        usuarioVisitante.setPersona(persona);
        usuarioVisitante.setUserRegistro(usuario);
        usuarioDAO.save(usuarioVisitante);

//        Carrera carrera = carreraDAO.findByCodigo(AcademicoConstantine.COD_CARRERA_ALUMNO_VISITANTE);
//        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.VIS);
//        SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");
//        String codigoMatricula = this.generateCodigo(ciclo);
//        String emailCompania = this.generateEmailCompania(codigoMatricula);
//        persona.setEmailCompania(emailCompania);
//        if (StringUtils.isNotBlank(persona.getEmail())) {
//            this.validarEmailsinPersona(persona.getEmail());
//        }
        Alumno alumno = new Alumno();
        alumno.setPersona(persona);

//        alumno.setCarrera(carrera);
//        alumno.setModalidadEstudio(modalidadEstudio);
//        si va en el formulario
//        alumno.setCicloActivo(ciclo);
//        alumno.setCicloIngreso(ciclo);
//        alumno.setSituacionAcademica(situacion);
//        alumno.setCodigo(codigoMatricula);
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
        //x ciclo
        MatriculaResumen mr = new MatriculaResumen();
        mr.setAlumno(alumno);
//        mr.setCicloAcademico(ciclo);
        mr.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        mr.setCreditosMatriculados(0);
        mr.setCreditosRetirados(0);
        mr.setCursosMatriculados(0);
        mr.setCursosRetirados(0);
        mr.setPorcentajeAvance(0);
//        mr.setSituacionInicio(situacion);
        mr.setCreditosTrikaPagados(BigDecimal.ZERO.intValue());
        mr.setCreditosTrikaSeparados(BigDecimal.ZERO.intValue());
        matriculaResumenDAO.save(mr);
        return persona;
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

    @Transactional
    private Persona updatePersona(Persona personaBD, Persona personaForm) {

        if (personaForm.getUbicacionNacer() == null) {
            personaBD.setUbicacionNacer(null);
        }

        personaBD.setPaisNacer(personaForm.getPaisNacer());
        personaBD.setPaisDomicilio(personaForm.getPaisDomicilio());
        personaBD.setUbicacionNacer(personaForm.getUbicacionNacer());
        personaBD.setNacionalidad(personaForm.getNacionalidad());
        personaBD.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
        personaBD.setTipoDocumento(personaForm.getTipoDocumento());

        personaDAO.update(personaBD);

        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, personaForm,
                Arrays.asList("email", "paterno", "materno", "nombres", "sexo", "fechaNacer", "direccion", "celular", "telefono", "numeroDocIdentidad"));

        if (sinCambios) {
            logger.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }

        personaBD.setNombres(personaForm.getNombres());
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setFechaNacer(personaForm.getFechaNacer());
        personaBD.setDireccion(personaForm.getDireccion());
        personaBD.setCelular(personaForm.getCelular());
        personaBD.setTelefono(personaForm.getTelefono());
        personaBD.setEmail(personaForm.getEmail());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());

        this.validarEmailConPersona(personaForm.getEmail(), personaBD);

        personaDAO.update(personaBD);

        return personaBD;
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

}
