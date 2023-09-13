package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.historialAlumno;

import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.amauta.dao.general.PaisDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaHistorialDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.general.UbicacionDAO;
import pe.edu.lamolina.amauta.dao.general.ValidacionPersonaDAO;
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
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.enums.persona.OrigenValidacionEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.persona.ValidacionEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaHistorial;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.ValidacionPersona;
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
    
    private final PersonaHistorialDAO personaHistorialDAO;
                            
    private final ValidacionPersonaDAO validacionPersonaDAO;
    
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
    public List<Carrera> allCarrera(String nombre) {
        return carreraDAO.allCarreras(nombre);        
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
        
        Persona personaForm = alumno.getPersona();
        
        Persona personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        
        if(personaDB != null) {
            throw new PhobosException(String.format("Existe una persona con %s %s", personaDB.getTipoDocumento().getNombre(), personaDB.getNumeroDocIdentidad()));
        }
                
        this.clearAlumnoPersonaForm(alumno, personaForm);

        Assert.isNotNull(alumno.getModalidadEstudio(), "Debe especificar la modalidad de estudio");
        
        Assert.isNotNull(alumno.getCarrera(), "Debe especificar la carrera");
        
        Assert.isNotNull(alumno.getCicloIngreso(), "Debe especificar el ciclo de ingreso");

        //this.validarPersona(personaForm);
        
        if (Objects.isNull(personaForm) || Objects.isNull(personaForm.getTipoDocumento()) ) {
            throw new PhobosException("Registrar el tipo de documento");
        }
        if (Objects.isNull(personaForm.getNumeroDocIdentidad())) {
            throw new PhobosException("Registrar el número del documento de identidad");
        }
        
        if (personaForm.getNumeroDocIdentidad().equals(AcademicoConstantine.CODE_POSTULANTE_DUMMY)) {
            throw new PhobosException("Este número de documento de identidad no está permitido");
        }
        
        personaForm.setNumeroDocIdentidad(limpiarValor(personaForm.getNumeroDocIdentidad()));        
        
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
        
        CicloAcademico ciclo = cicloAcademicoDAO.find(alumno.getCicloIngreso().getId());

        String codigoMatricula = StringUtils.isBlank(alumno.getCodigo()) ? 
                this.generateCodigo(ciclo) : 
                alumno.getCodigo();       
        
        String emailCompania = StringUtils.isBlank(alumno.getPersona().getEmailCompania()) ? 
                this.generateEmailCompania(codigoMatricula) : 
                alumno.getPersona().getEmailCompania();

        if (personaDB == null) {

            personaForm.setEmailCompania(emailCompania);            
            personaForm.setEstadoEnum(PersonaEstadoEnum.ACT);            
            personaForm.setUserRegistro(ds.getUsuario());            
            personaForm.setFechaRegistro(new Date());
            
            this.validarEmailsinPersona(personaForm.getEmail());            
            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            this.validarDNI(personaForm);
            
            personaDAO.save(personaForm);

            this.crearUsuarioAlumno(emailCompania, personaForm, ds);
            
            this.saveAlumno(alumno, personaForm, ciclo, codigoMatricula);
            
            //this.enviarNotificacionUsuarioCreacion(personaForm);            
            //this.updateCicloSgteMatricula(ciclo);

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
        //this.enviarNotificacionUsuarioCreacion(personaForm);
        //this.updateCicloSgteMatricula(ciclo);

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
        /*DateTime today = new DateTime();
        Usuario user = ds.getUsuario();
 
        Persona personaForm = alumno.getPersona();
        validarDNI(personaForm);
        validarPersona(personaForm);
        validarEmailEmpresaConPersona(personaForm);
        
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

        this.crearUsuario(alumno.getPersona(), ds);*/

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
    
    private String generateEmailCompania(String codigoMatricula) {
        return codigoMatricula + "@lamolina.edu.pe";
    }
        
    private String generateCodigo(CicloAcademico ciclo) {
        if (ciclo.getMatriculaSiguiente() == null || ciclo.getMatriculaInicio() == null) {
            StringBuilder ssb = new StringBuilder();
            ssb.append("Configuración del ciclo académico UNALM  ");
            ssb.append(ciclo.getDescripcion());
            ssb.append("  no esta completa");
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
        
        LOGGER.debug("guardando docente ...");
                
        SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");
        
        alumno.setPersona(persona);       
        alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);        
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
        
        LOGGER.debug("alumno  guardado  {}", alumno.getId());
        
    }
    
    private void registrarValidacion(Persona persona, Alumno alumno, String jsonInicio, String jsonFinal, DataSessionPivot ds) {
        
        persona.setEstadoValidacionEnum(ValidacionEstadoEnum.VALIDADO);
        persona.setOrigenValidacionEnum(OrigenValidacionEnum.ALUMNO_AMAUTA);
        persona.setUserValidacion(ds.getUsuario());
        persona.setFechaValidacion(new Date());
        persona.setUserModificacion(ds.getUsuario());
        personaDAO.update(persona);

        ValidacionPersona validacion = new ValidacionPersona();
        validacion.setPersona(persona);
        validacion.setOrigenEnum(OrigenValidacionEnum.ALUMNO_AMAUTA);
        validacion.setInstanciaOrigen(alumno.getId());
        validacion.setDataInicio(jsonInicio);
        validacion.setDataFinal(jsonFinal);
        validacion.setUserValidacion(ds.getUsuario());
        validacion.setFechaValidacion(new Date());
        validacionPersonaDAO.save(validacion);
        
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
        
    /*private String getCodigo() {
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
    }*/
    
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
    
    private void validarPersona(Persona personaForm) {

        if (Objects.isNull(personaForm) || Objects.isNull(personaForm.getTipoDocumento()) ) {
            throw new PhobosException("Registrar el tipo de documento");
        }
        if (Objects.isNull(personaForm.getNumeroDocIdentidad())) {
            throw new PhobosException("Registrar el número del documento de identidad");
        }
        
        if (personaForm.getNumeroDocIdentidad().equals(AcademicoConstantine.CODE_POSTULANTE_DUMMY)) {
            throw new PhobosException("Este número de documento de identidad no está permitido");
        }

        personaForm.setNumeroDocIdentidad(limpiarValor(personaForm.getNumeroDocIdentidad()));
        
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
        
        if(Objects.isNull(personaForm) || Objects.isNull(personaForm.getTipoDocumento())) {
            throw new PhobosException("Registrar el tipo de documento");
        }
        
        TipoDocIdentidad tipoDocIdentidad = personaForm.getTipoDocumento();

        Persona personaBD = personaDAO.findByDocIdentidad(tipoDocIdentidad, personaForm.getNumeroDocIdentidad());
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
        if (!StringUtils.isEmpty(email)) {
            List<Persona> personas = personaDAO.allByEmailEmpresaWithoutPersona(persona);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo UNALM ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }
    
    private void validarEmailEmpresaConPersona(Persona persona) {
        if (!StringUtils.isEmpty(persona.getEmail())) {
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
    
}