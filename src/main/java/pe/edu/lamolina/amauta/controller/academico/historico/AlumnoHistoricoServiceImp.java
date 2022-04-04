package pe.edu.lamolina.amauta.controller.academico.historico;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.amauta.controller.general.persona.PersonaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
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
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoMigracionEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaHistorial;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class AlumnoHistoricoServiceImp implements AlumnoHistoricoService {

    private TipoDocIdentidadDAO tipoDocIdentidadDAO;

    private PersonaDAO personaDAO;

    private AlumnoVisitanteDAO alumnoVisitanteDAO;

    private AlumnoDAO alumnoDAO;

    private UsuarioDAO usuarioDAO;

    private PersonaService personaService;

    private CicloAcademicoDAO cicloAcademicoDAO;

    private CarreraDAO carreraDAO;

    private ModalidadEstudioDAO modalidadEstudioDAO;

    private SituacionAcademicaDAO situacionAcademicaDAO;

    private ContenidoCartaDAO contenidoCartaDAO;

    private MatriculaResumenDAO matriculaResumenDAO;

    private VerificadorService verificadorService;

    private AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    private AlumnoCicloDAO alumnoCicloDAO;

    private PersonaHistorialDAO personaHistorialDAO;

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        int year = new DateTime().getYear();
        int yearinit = year - 30;
        int yearend = year + 5;
        return cicloAcademicoDAO.allPregradoByRange(yearinit, yearend);
    }

    @Override
    public List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        return alumnoDAO.allAlumnoHistoricoByCarrerasDynatable(filter, carreras, todo);
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
        return alumnoDAO.find(new Alumno(idAlumno));
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

    private void verificarDatosPersona(Persona personaForm) {

        personaForm.setNumeroDocIdentidad(limpiarValor(personaForm.getNumeroDocIdentidad()));

        if (personaForm.getTipoDocumento() == null) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (personaForm.getTipoDocumento().getId() == null) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (StringUtils.isBlank(personaForm.getNumeroDocIdentidad())) {
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
    @Transactional
    public void save(Alumno alumnoForm, DataSessionPivot ds) {

        Persona personaForm = alumnoForm.getPersona();

        this.verificarDatosPersona(personaForm);

        log.debug("buscar persona  doc {} num  {} ...", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());

        Persona personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());

        if (personaDB == null) {

            Persona persona = this.savePersona(personaForm, ds);

            this.saveAlumno(persona, alumnoForm, ds);

        } else {

            Alumno alumnoDB = alumnoDAO.findByPersona(personaDB);

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

            Persona persona = this.updatePersona(personaDB, personaForm, ds);

            this.saveAlumno(persona, alumnoForm, ds);

        }

    }

    @Override
    public void update(Alumno alumno, DataSessionPivot ds) {
        alumnoDAO.update(alumno);
    }

    @Override
    @Transactional
    public void deleteAlumnoHistorico(Long idAlumno) {
        Alumno alumno = alumnoDAO.find(new Alumno(idAlumno));
        if (StringUtils.isBlank(alumno.getIngresoAlumnoHistorico())) {
            throw new PhobosException("Esta opción no está permitido para este alumno");
        }
        if (!"SI".equalsIgnoreCase(alumno.getIngresoAlumnoHistorico())) {
            throw new PhobosException("Esta opción no está permitido para este alumno");
        }
        alumnoDAO.delete(idAlumno);
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
    private Persona savePersona(Persona persona, DataSessionPivot ds) {

        this.validarEmailEmpresaSinPersona(persona.getEmailCompania());

        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setUserRegistro(ds.getUsuario());
        persona.setFechaRegistro(new Date());

        personaDAO.save(persona);

        this.validarUsuarioGoogleSinPersona(persona.getEmailCompania());

        Usuario usuarioDB = usuarioDAO.findByGoogleEmail(persona.getEmailCompania());

        if (usuarioDB != null) {
            Persona pEmail = usuarioDB.getPersona();
            throw new PhobosException("El correo UNALM ya pertenece a otro usuario con documento " + pEmail.getNumeroDocIdentidad());
        }

        Usuario usuarioNew = new Usuario();
        usuarioNew.setGoogle(persona.getEmailCompania());
        usuarioNew.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioNew.setFechaRegistro(new Date());
        usuarioNew.setPersona(persona);
        usuarioNew.setUserRegistro(ds.getUsuario());
        usuarioDAO.save(usuarioNew);

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

    private void validarUsuarioGoogleSinPersona(String email) {
        if (email != null) {
            List<Usuario> usuarios = usuarioDAO.allByEmailEmpresaGoogle(email);
            if (!usuarios.isEmpty()) {
                Persona pEmail = usuarios.get(0).getPersona();
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo UNALM ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    @Transactional
    private Persona updatePersona(Persona personaBD, Persona personaForm, DataSessionPivot ds) {

        this.validarEmailEmpresaConPersona(personaForm.getEmailCompania(), personaBD);
        this.validarEmailConPersona(personaForm.getEmail(), personaBD);

        personaBD.setPaisNacer(personaForm.getPaisNacer());
        personaBD.setPaisDomicilio(personaForm.getPaisDomicilio());
        personaBD.setUbicacionNacer(personaForm.getUbicacionNacer());
        personaBD.setNacionalidad(personaForm.getNacionalidad());
        personaBD.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());

        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());

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

        personaDAO.update(personaBD);

        Usuario usuario = usuarioDAO.findActivoByPersona(personaBD);

        if (usuario == null) {

            Usuario usuarioDB = usuarioDAO.findByGoogleEmail(personaForm.getEmailCompania());

            if (usuarioDB != null) {
                Persona pEmail = usuarioDB.getPersona();
                if (pEmail.getId() != personaBD.getId().longValue()) {
                    throw new PhobosException("El correo UNALM ya pertenece a otro usuario con documento " + pEmail.getNumeroDocIdentidad());
                }
            }

            Usuario usuarioNew = new Usuario();
            usuarioNew.setGoogle(personaForm.getEmailCompania());
            usuarioNew.setEstadoEnum(UserEstadoEnum.ACT);
            usuarioNew.setFechaRegistro(new Date());
            usuarioNew.setPersona(personaBD);
            usuarioNew.setUserRegistro(ds.getUsuario());
            usuarioDAO.save(usuarioNew);

        }

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

    @Transactional
    private void saveAlumno(Persona persona, Alumno alumno, DataSessionPivot ds) {

        Alumno alumnoDB = alumnoDAO.findByCodigo(alumno.getCodigo());
        if (alumnoDB != null) {
            throw new PhobosException("El código del alumno ya está registrado");
        }

        alumno.setPersona(persona);
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

        alumno.setUserRegistro(ds.getUsuario());
        alumno.setFechaRegistro(new Date());
        alumno.setIngresoAlumnoHistorico("SI");

        alumnoDAO.save(alumno);

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

    @Override
    public List<ModalidadEstudio> allModalidad() {
        return modalidadEstudioDAO.all();
    }

    @Override
    public List<SituacionAcademica> allSituacionAcademica() {
        return situacionAcademicaDAO.all();
    }

    @Override
    @Transactional
    public void saveCicloAlumno(AlumnoCiclo alumnoCiclo, DataSessionPivot ds) {

        alumnoCiclo.defaultValuesToCreate(alumnoCiclo.getAlumno(), alumnoCiclo.getCicloAcademico(), ds.getUsuario());
        alumnoCiclo.setUserRegistro(ds.getUsuario());
        alumnoCiclo.setFechaRegistro(new Date());
        alumnoCiclo.setCarrera(alumnoCiclo.getAlumno().getCarrera());
        alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
        alumnoCiclo.setEstaAprobado(1);
        alumnoCiclo.setCursosAprobados(0);
        alumnoCiclo.setCursosInscritos(0);
        alumnoCiclo.setPromedioAcumulado(BigDecimal.ZERO);
        alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
        alumnoCiclo.setCreditosConvalidados(0);
        alumnoCiclo.setCreditosAprobadosAcumulados(0);
        alumnoCiclo.setCreditosAprobadosCiclo(0);
        alumnoCiclo.setCreditosCursadosCiclo(0);
        alumnoCiclo.setCreditosAcumulados(0);
        alumnoCiclo.setSituacionInicio(alumnoCiclo.getAlumno().getSituacionAcademica());
        alumnoCiclo.setTipoMigracion(TipoMigracionEnum.AREG);

        alumnoCicloDAO.save(alumnoCiclo);

    }

    @Override
    @Transactional
    public void saveCicloAlumnoCurso(AlumnoCiclo alumnoCiclo, DataSessionPivot ds) {

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCiclo.getAlumnoCicloCurso()) {

            alumnoCicloCurso.setEstaAprobado(1);
            alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
            alumnoCicloCurso.setFechaMigracion(alumnoCicloCurso.getFechaMigracion());
            alumnoCicloCurso.setFechaRegistro(new Date());
            alumnoCicloCurso.setRegistroActivo(1);
            alumnoCicloCurso.setUsuarioRegistro(ds.getUsuario());
            alumnoCicloCurso.setVecesCursado(1);
            alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.MOD);
            alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
            alumnoCicloCurso.setTipoMigracion(TipoMigracionEnum.AREG);

            if (alumnoCicloCurso.getId() == null) {
                alumnoCicloCursoDAO.save(alumnoCicloCurso);
            } else {
                alumnoCicloCursoDAO.updateColumns(alumnoCicloCurso, "curso", "nota", "creditos");
            }

        }

    }

    @Override
    public List<AlumnoCiclo> allAlumnoCiclo(Long idAlumno) {
        return alumnoCicloDAO.allByAlumno(new Alumno(idAlumno));
    }

    @Override
    public List<AlumnoCicloCurso> allAlumnoCicloCurso(Long idAlumnoCiclo) {
        return alumnoCicloCursoDAO.allByAlumnoCiclo(new AlumnoCiclo(idAlumnoCiclo));
    }

    @Override
    @Transactional
    public void deleteAlumnoCiclo(Long idAlumnoCiclo) {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allByAlumnoCiclo(new AlumnoCiclo(idAlumnoCiclo));
        if (alumnoCicloCursos.isEmpty()) {
            alumnoCicloDAO.delete(idAlumnoCiclo);
            return;
        }
        throw new PhobosException(GlobalMessages.FK_ERROR_DELETE);
    }

    @Override
    @Transactional
    public void deleteAlumnoCicloCurso(Long idAlumnoCicloCurso) {
        alumnoCicloCursoDAO.delete(idAlumnoCicloCurso);
    }

}
