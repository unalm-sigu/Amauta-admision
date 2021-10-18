package pe.edu.lamolina.amauta.controller.academico.historico;

import java.math.BigDecimal;
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
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
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
    public void save(Alumno alumnoForm, DataSessionPivot ds) {

        Persona personaForm = alumnoForm.getPersona();

        this.verificarDatosPersona(personaForm);

        logger.debug("buscar persona  doc {} num  {} ...", personaForm.getTipoDocumento().getId(), personaForm.getNumeroDocIdentidad());

        Persona personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());

        if (personaDB == null) {

            Persona persona = this.savePersona(personaForm, ds);

            this.saveAlumno(persona, alumnoForm, ds);

        } else {

            Alumno alumnoDB = alumnoDAO.findByPersona(personaDB);

            if (alumnoDB != null) {

                throw new PhobosException("El documento ya pertenece a otro alumno");

            }

            Persona persona = this.updatePersona(personaDB, personaForm, ds);

            this.saveAlumno(persona, alumnoForm, ds);

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

}
