package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.historialAlumno;

//import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
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
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.persona.OrigenValidacionEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.persona.ValidacionEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.ValidacionPersona;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
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
        throw new UnsupportedOperationException("Not supported yet.");
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

    @Override
    @Transactional
    public void save(Alumno alumno, DataSessionPivot ds) {

        Persona personaForm = alumno.getPersona();

        CicloAcademico ciclo = cicloAcademicoDAO.find(alumno.getCicloIngreso().getId());

        Persona personaDB = null;

        if (Objects.isNull(personaForm.getTipoDocumento()) || Objects.isNull(personaForm.getNumeroDocIdentidad()) || personaForm.getNumeroDocIdentidad().isEmpty()) {

            /*String paterno = Normalizer.normalize(personaForm.getPaterno().trim(), Normalizer.Form.NFD);
            String paternoFilter = paterno.replaceAll("[^\\p{ASCII}]", "");

            String materno = Normalizer.normalize(personaForm.getMaterno().trim(), Normalizer.Form.NFD);
            String maternoFilter = materno.replaceAll("[^\\p{ASCII}]", "");

            String nombres = Normalizer.normalize(personaForm.getNombres().trim(), Normalizer.Form.NFD);
            String nombresFilter = nombres.replaceAll("[^\\p{ASCII}]", "");

            personaForm.setPaterno(paternoFilter);
            personaForm.setMaterno(maternoFilter);
            personaForm.setNombres(nombresFilter);*/

            List<Persona> personas = personaDAO.allByApellidosNombres(personaForm);
            if (!personas.isEmpty()) {
                establecerMatriculaAndCorreoEmpresa(personaForm, ciclo, alumno);
            }
            personaForm.setTipoDocumento(tipoDocIdentidadDAO.findBySimbolo("DNI"));
        } else {
            personaDB = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        }

        if (personaDB != null) {
            throw new PhobosException(String.format("Existe una persona con %s %s", personaDB.getTipoDocumento().getNombre(), personaDB.getNumeroDocIdentidad()));
        }

        if (Objects.isNull(personaForm.getEmailCompania()) || personaForm.getEmailCompania().isEmpty()) {
            establecerMatriculaAndCorreoEmpresa(personaForm, ciclo, alumno);
        }
        
        personaForm.setEstadoEnum(PersonaEstadoEnum.ACT);
        personaForm.setUserRegistro(ds.getUsuario());
        personaForm.setFechaRegistro(new Date());
        personaDAO.save(personaForm);
        
        registrarAlumno(personaForm, ciclo, alumno);
        
        String personaFinal = JaneHelper
                    .from(personaForm)
                    .only("id,paterno,materno,nombres,sexo,fechaNacer,numeroDocIdentidad")
                    .join("tipoDocumento", "id,simbolo")
                    .json().toString();
        
        registrarValidacion(personaForm, alumno, null, personaFinal, ds);

    }

    private void establecerMatriculaAndCorreoEmpresa(Persona personaForm, CicloAcademico ciclo, Alumno alumno) {
        
        List<String> codigos = alumnoDAO.allAlumnoByYear(ciclo.getYear()).stream().map(x -> x.getCodigo()).collect(Collectors.toList());
        
        Integer codigo = Integer.valueOf(Collections.max(codigos));
        
        boolean buscarMatricula = true;
        
        do {            
            codigo ++;
            Alumno alumnoDB = alumnoDAO.findByCodigo(String.valueOf(codigo));
            if(alumnoDB == null) {
                alumno.setCodigo(String.valueOf(codigo));
                buscarMatricula = false;
            }
        } while (buscarMatricula);
               
        String emailCompania = String.valueOf(codigo).concat("@lamolina.edu.pe");
        
        personaForm.setEmailCompania(emailCompania);
        
        Persona personaDB = personaDAO.findByEmailCompania(personaForm);        
                
        if(personaDB != null) {
            boolean buscarEmailCompania = true;
            String usuario = "usuariogenerico";
            int cont = 0;
            do {
                cont ++;
                String emailGenerico = usuario.concat(String.valueOf(cont)).concat("@lamolina.edu.pe");
                personaForm.setEmailCompania(emailGenerico);
                personaDB = personaDAO.findByEmailCompania(personaForm);
                if(personaDB == null) {
                    buscarEmailCompania = false;
                }
            } while (buscarEmailCompania);
        }
        
    }
    
    private void registrarAlumno(Persona persona, CicloAcademico ciclo, Alumno alumno) {
        
        SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");                

        Carrera carrera = carreraDAO.find(alumno.getCarrera().getId());        
        
        validarCodigoMatricula(alumno);
        
        alumno.setPersona(persona);
        alumno.setCarrera(carrera);
        alumno.setCicloActivo(ciclo);
        alumno.setCicloIngreso(ciclo);
        alumno.setSituacionAcademica(situacion);
        alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);

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
    
    private void validarCodigoMatricula(Alumno alumno) {
        Alumno alumnoDB = alumnoDAO.findByCodigo(alumno.getCodigo());
        if (alumno.getId() != null && alumnoDB != null && alumnoDB.getId().longValue() != alumno.getId()) {
            throw new PhobosException("El código ingresado ya se encuentra relacionado con otra alumno: " + alumnoDB.getPersona().getApellidosNombres());

        } else if (alumno.getId() == null && alumnoDB != null) {
            throw new PhobosException("El código ingresado ya se encuentra relacionado con otra alumno: " + alumnoDB.getPersona().getApellidosNombres());
        }
    }
    
    private void registrarValidacion(Persona persona, Alumno alumno, String jsonInicio, String jsonFinal, DataSessionPivot ds) {

        persona.setEstadoValidacionEnum(ValidacionEstadoEnum.VALIDADO);
        persona.setOrigenValidacionEnum(OrigenValidacionEnum.ALUMNO_ANTIGUO);
        persona.setUserValidacion(ds.getUsuario());
        persona.setFechaValidacion(new Date());
        persona.setUserModificacion(ds.getUsuario());
        personaDAO.update(persona);

        ValidacionPersona validacion = new ValidacionPersona();
        validacion.setPersona(persona);        
        validacion.setOrigenEnum(OrigenValidacionEnum.ALUMNO_ANTIGUO);
        validacion.setInstanciaOrigen(alumno.getId());
        validacion.setDataInicio(jsonInicio);
        validacion.setDataFinal(jsonFinal);
        validacion.setUserValidacion(ds.getUsuario());
        validacion.setFechaValidacion(new Date());
        validacionPersonaDAO.save(validacion);
        
    }

}
