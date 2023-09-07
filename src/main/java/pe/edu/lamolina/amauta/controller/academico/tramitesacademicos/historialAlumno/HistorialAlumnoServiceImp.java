package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.historialAlumno;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.amauta.dao.general.PaisDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.general.UbicacionDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class HistorialAlumnoServiceImp implements HistorialAlumnoService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        
    private final PersonaDAO personaDAO;
        
    private final TipoDocIdentidadDAO tipoDocIdentidadDAO;    
        
    private final PaisDAO paisDAO;
    
    private final UbicacionDAO ubicacionDAO;
       
    private final ModalidadEstudioDAO modalidadEstudioDAO;
       
    private final FacultadDAO facultadDAO;
        
    private final CarreraDAO carreraDAO;
        
    private final AlumnoDAO alumnoDAO;
    
    private final CicloAcademicoDAO cicloAcademicoDAO;
        
    private final UsuarioDAO usuarioDAO;
    
    private final RolDAO rolDAO;
        
    private final UsuarioRolDAO usuarioRolDAO;
        
    private final SituacionAcademicaDAO situacionAcademicaDAO;
    
    @Override
    public DynatableResponse listAlumnos(DynatableFilter filter, HttpSession httpSession) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean registrarAlumno(PersonaDto personaDto, HttpSession httpSession) {
        
        DataSessionPivot dataSessionPivot = (DataSessionPivot) httpSession.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Usuario usuario = dataSessionPivot.getUsuario();
        validarPersonaDto(personaDto);                                
        
        validarPersona(personaDto, usuario);
        
        return true;
    }

    private void validarPersonaDto(PersonaDto personaDto) {
        LOGGER.info("Validando los datos registros ...");
        if(personaDto == null) {
            throw new PhobosException("Registrar sus datos personales.");
        }
        if(Objects.isNull(personaDto.getNumeroDocIdentidad())) {
            throw new PhobosException("Registrar un número de documento.");
        }
        if(Objects.isNull(personaDto.getIdTipoDocumento())) {
            throw new PhobosException("Registrar un tipo de documento.");
        }
        if(Objects.isNull(personaDto.getFechaNacer())) {
            throw new PhobosException("Registrar la fecha de nacimiento.");
        }
        if(Objects.isNull(personaDto.getSexo()) || personaDto.getSexo().trim().isEmpty()) {
            throw new PhobosException("Registrar un género.");
        }
        if(Objects.isNull(personaDto.getNombres()) || personaDto.getNombres().trim().isEmpty()) {
            throw new PhobosException("Registrar nombre(s).");
        }
        if(Objects.isNull(personaDto.getPaterno()) || personaDto.getPaterno().trim().isEmpty()) {
            throw new PhobosException("Registrar apellido paterno.");
        }
        if(Objects.isNull(personaDto.getMaterno()) || personaDto.getMaterno().trim().isEmpty()) {
            throw new PhobosException("Registrar apellido materno.");
        }
        if(Objects.isNull(personaDto.getEmail()) || personaDto.getEmail().trim().isEmpty()) {
            throw new PhobosException("Registrar correo electrónico.");
        }
        if(Objects.isNull(personaDto.getIdPaisNacer())) {
            throw new PhobosException("Registrar país.");
        }
        if(Objects.isNull(personaDto.getIdUbicacionDomicilio())) {
            throw new PhobosException("Registrar distrito.");
        }
        if(Objects.isNull(personaDto.getDireccion()) || personaDto.getDireccion().trim().isEmpty()) {
            throw new PhobosException("Registrar dirección de domicilio.");
        }        
        LOGGER.info("Datos validados OK ...");
    }

    private void validarPersona(PersonaDto personaDto, Usuario usuario) {
        
        Persona personaDB = personaDAO.findByDocIdentidad(personaDto.getNumeroDocIdentidad());
        
        TipoDocIdentidad tipoDocIdentidad = tipoDocIdentidadDAO.find(personaDto.getIdTipoDocumento());
        
        Pais pais = paisDAO.find(personaDto.getIdPaisNacer());
        
        Ubicacion ubicacion = ubicacionDAO.find(personaDto.getIdNacionalidad());
        
        if(Objects.isNull(tipoDocIdentidad)) {
            throw new PhobosException("Tipo de documento no válido");
        }
        
        if(Objects.isNull(pais)) {
            throw new PhobosException("País no válido");
        }
        
        if(Objects.isNull(ubicacion)) {
            throw new PhobosException("Ubicación no válido");
        }
        
        if(Objects.nonNull(personaDB)) {
            throw new PhobosException("Ya existe un registro con N° de documento %s para la Persona %s", personaDB.getNumeroDocIdentidad(), personaDB.getNombreCompleto());
        }
        
        Persona personaTemp = new Persona();
        personaTemp.setNombres(personaDto.getNombres());
        personaTemp.setPaterno(personaDto.getPaterno());
        personaTemp.setMaterno(personaDto.getMaterno());
        personaTemp.setSexo(personaDto.getSexo());
        personaTemp.setFechaNacer(personaDto.getFechaNacer().toDate());
        personaTemp.setNumeroDocIdentidad(personaDto.getNumeroDocIdentidad());
        personaTemp.setNumeroDocIdentidad2(personaDto.getNumeroDocIdentidad());
        personaTemp.setCelular(personaDto.getTelefono());
        personaTemp.setEmail(personaDto.getEmail());
        personaTemp.setEmailCompania(personaDto.getEmailCompania());
        personaTemp.setDireccion(personaDto.getDireccion());
        personaTemp.setTipoDocumento(tipoDocIdentidad);
        personaTemp.setNacionalidad(pais);
        personaTemp.setPaisNacer(pais);
        personaTemp.setPaisDomicilio(pais);
        personaTemp.setUbicacionDomicilio(ubicacion);        
        personaTemp.setFechaRegistro(new Date());
        personaTemp.setUserRegistro(usuario);
        List<Persona> personasDB = personaDAO.allByApellidos(personaTemp);

        if(personasDB.isEmpty()) {            
            personaDAO.save(personaTemp);
        } else {
            for (Persona persona : personasDB) {            
                if(persona.getNombres().toUpperCase().equals(personaTemp.getNombres())) {
                    LOGGER.info(String.format("Existe persona de nombre completo '%s' y documento %s.", persona.getNombreCompleto(), persona.getNumeroDocIdentidad()));
                }
            }
        }
        
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }
    
    @Override
    public List<ModalidadEstudio> allModalidadEstudioByCodes(List<ModalidadEstudioEnum> codes, Compania compania) {
        return modalidadEstudioDAO.allActivoByCodesCompania(codes, compania);
    }
    
    @Override
    public List<Facultad> allFacultad(String nombre, Compania compania) {
        return facultadDAO.allFacultad(nombre, compania);
    }
    
    @Override
    public List<Carrera> allCarrera(String nombre, Compania compania) {
        return carreraDAO.allByNombre(nombre, compania);
    }
            
    @Override
    public List<CicloAcademico> allCicloAcademico() {
        return cicloAcademicoDAO.allCiclos();
    }

    @Override
    public List<CicloAcademico> allCiclo(String nombre) {
        return cicloAcademicoDAO.allCicloByName(nombre);
    }

    @Override
    public Persona update(Alumno alumno, DataSessionPivot ds) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  
    @Override
    @Transactional
    public void save(Alumno alumno, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Usuario user = ds.getUsuario();
 
        Persona personaForm = alumno.getPersona();
        personaForm.setEstadoEnum(PersonaEstadoEnum.ACT);
        personaForm.setFechaRegistro(today.toDate());
        personaForm.setUserRegistro(user);
        personaDAO.save(personaForm);

        alumno.setPersona(personaForm);
        
        List<Alumno> alumnodDB = alumnoDAO.allByPersona(alumno.getPersona());
        List<Alumno> alumnoPreEpg = alumnodDB.stream()
                .filter(x -> !x.getModalidadEstudio().isPregrado() || !x.getModalidadEstudio().isPostgrado())
                .collect(Collectors.toList());
        LOGGER.debug("existe alumno en db {}", (alumnoPreEpg != null));
        Assert.isTrue(alumnoPreEpg.isEmpty(), "Alumno ya existe");

        LOGGER.debug("guardando docente ...");
        alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);
        alumno.setCodigo(this.getCodigo());
        alumno.setFechaRegistro(today.toDate());
        alumno.setUserRegistro(user);
        
        SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");
        alumno.setSituacionAcademica(situacion);
        alumnoDAO.save(alumno);
        LOGGER.debug("alumno  guardado  {}", alumno.getId());

        this.crearUsuario(alumno.getPersona(), ds);

    }

   
    private String getCodigo() {
        LOGGER.debug("generando codigo");
        String timestamp = TypesUtil.getUnixTime().toString();
        LOGGER.debug("timestamp  {}", timestamp);
        String codigo = timestamp.substring(timestamp.length() - 4, timestamp.length());
        LOGGER.debug("codigo  {}", codigo);
        Alumno alumno = alumnoDAO.findByCodigo(codigo);
        LOGGER.debug("alumno  {}", (alumno != null));
        while (alumno != null) {
            timestamp = TypesUtil.getUnixTime().toString();
            codigo = timestamp.substring(timestamp.length() - 4, timestamp.length());
            alumno = alumnoDAO.findByCodigo(codigo);
        }
        LOGGER.debug("codigo unico  {}", codigo);
        return codigo;
    }
    
    private void crearUsuario(Persona persona, DataSessionPivot ds) {
        Usuario usuario = usuarioDAO.findActivoByPersona(persona);
        LOGGER.debug("existe usuario en db {}", (usuario != null));

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setEstadoEnum(UserEstadoEnum.ACT);
            usuario.setFechaRegistro(new Date());
            usuario.setUserRegistro(ds.getUsuario());
            usuario.setPersona(persona);
            usuario.setGoogle(persona.getEmailCompania());
            usuarioDAO.save(usuario);

        } else {
            LOGGER.debug("actualizando usuario");
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
    
    @Override
    public Persona findPersonaByDocIdentidad(Persona personaTmp) {
        Assert.isNotNull(personaTmp.getTipoDocumento(), "El tipo de documento no debe de ser nulo");
        Assert.isNotNull(personaTmp.getTipoDocumento().getId(), "El tipo de documento no debe de ser nulo");
        return personaDAO.findByDocIdentidad(personaTmp.getTipoDocumento(), personaTmp.getNumeroDocIdentidad());
    }

    @Override
    public Persona findPersona(Persona persona) {
        return personaDAO.find(persona.getId());
    }
    
}