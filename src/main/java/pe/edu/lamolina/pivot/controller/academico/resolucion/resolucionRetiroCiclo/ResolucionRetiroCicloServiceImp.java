package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionRetiroCiclo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ResolucionRetiroCicloServiceImp implements ResolucionRetiroCicloService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    AlumnoDAO alumnoDAO;
    
    @Autowired
    TipoResolucionDAO tipoResolucionDAO;
    
    @Autowired
    ResolucionDAO resolucionDAO;
    
    @Autowired
    RetiroCicloDAO retiroCicloDAO;
    
    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;
    @Autowired
    TramiteDAO tramiteDAO;
    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;
    @Autowired
    TipoTramiteDAO tipoTramiteDAO;
    
    @Autowired
    MatriculableService matriculableService;
    @Autowired
    SerieDocumentoService serieDocumentoService;
    
    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    
    @Override
    public List<Alumno> allAlumnoDesertorByNombre(String nombre, Long instanciaOficina) {
        return alumnoDAO.allDesertorByName(nombre, instanciaOficina);
    }
    
    @Override
    @Transactional
    public Alumno save(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        // Aceptado en su totalidad
        List<Alumno> alumnos = new ArrayList<>();
        
        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.RCI);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(usuario);
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);
        
        Assert.isFalse(resolucionForm.getRetiroCiclo() == null, "Debe Agregar alumnos.");
        
        RetiroCiclo retiroCiclo = retiroCicloDAO.findByAlumnoCicloRegistro(resolucionForm.getRetiroCiclo().getAlumno(), resolucionForm.getRetiroCiclo().getCicloAcademico());
        
        Assert.isNull(retiroCiclo, "El alumno cuenta con un trámite retiro ciclo.");
        
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        System.out.println("Estado" + estadoTramite.getId());
        
        DateTime today = new DateTime();
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());
        Alumno alumno = alumnoDAO.find(resolucionForm.getRetiroCiclo().getAlumno());
        
        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(alumno);
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumno.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(ds.getUsuario());
        tramiteDAO.save(tramite);
        
        retiroCiclo = resolucionForm.getRetiroCiclo();
        retiroCiclo.setEstado(TramiteEstadoEnum.ACEP);
        retiroCiclo.setTipoEnum(TipoRetiroCicloEnum.EXCEP);
        retiroCiclo.setAlumno(retiroCiclo.getAlumno());
        retiroCiclo.setCicloAcademico(retiroCiclo.getCicloAcademico());
        retiroCiclo.setCicloRegistro(ds.getCicloAcademico());
        retiroCiclo.setUsuario(ds.getUsuario());
        retiroCiclo.setMotivo(retiroCiclo.getMotivo());
        retiroCiclo.setTramite(tramite);
        retiroCicloDAO.save(retiroCiclo);
        
        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnoCicloRegularAct(alumno, retiroCiclo.getCicloAcademico());
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
            alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.NREQ);
            alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);
            
            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, retiroCiclo.getCicloAcademico());
            List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCiclo);
            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
                alumnoCicloCurso.setVecesCursado(alumnoCicloCurso.getVecesCursado() - 1);
                alumnoCicloCurso.setEstado(EstadoMatriculaEnum.RCI);
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
            }
        }
        
        return alumno;
    }
    
    @Override
    public List<Alumno> allAlumno(String nombre, Long instanciaOficina) {
        
        return alumnoDAO.allAlumnoByOficina(nombre, instanciaOficina);
    }
    
    @Override
    public List<CicloAcademico> ciclosAnteriores(int i) {
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivoPregrado();
        return cicloAcademicoDAO.allAnteriores(i, cicloAcademico);
    }
    
}
