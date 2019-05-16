package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionExistentes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.REIC;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
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
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ResolucionExistentesServiceImp implements ResolucionExistenteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TipoResolucionDAO tipoResolucionDAO;

    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;
    @Autowired
    TramiteDAO tramiteDAO;
    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;
    @Autowired
    TipoTramiteDAO tipoTramiteDAO;
    @Autowired
    RetiroCicloDAO retiroCicloDAO;
    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    MatriculableService matriculableService;
    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Override
    public List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina) {
        return alumnoDAO.allAlumnoByOficina(nombre, instanciaOficina);
    }

    @Override
    @Transactional
    public List<Alumno> saveReincorporacion(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {

        List<Alumno> alumnos = new ArrayList<>();

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.REIC);
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

        Assert.isFalse(resolucionForm.getReincorporaciones().isEmpty(), "Debe Agregar alumnos.");

        Map<Long, Long> couterMap = resolucionForm.getReincorporaciones().stream().collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));
        for (Long count : couterMap.values()) {
            Assert.isFalse(count > 1, "Está repitiendo alumno");
        }
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByCicloReincorporacion(ds.getCicloAcademico());
        Map<Long, Alumno> map = TypesUtil.convertListToMap("alumno", reincorporacions);

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        System.out.println("Estado" + estadoTramite.getId());
        for (Reincorporacion reincorporacione : resolucionForm.getReincorporaciones()) {

            Alumno alumno = map.get(reincorporacione.getAlumno().getId());
            if (alumno != null) {
                throw new PhobosException("El alumno" + alumno.getCodigo() + " ya cuenta con una resolución para el ciclo activo");
            }
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
            alumno = alumnoDAO.find(reincorporacione.getAlumno());

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

            Facultad facultad = reincorporacione.getAlumno().getCarrera().getFacultad();
            reincorporacione.setAceptado(1);
            reincorporacione.setFechaRegistro(new Date());
            reincorporacione.setResolucion(resolucion);
            reincorporacione.setEstadoTramite(estadoTramite);
            reincorporacione.setUserRegistro(usuario);
            reincorporacione.setFacultad(facultad);
            reincorporacione.setTramite(tramite);
            reincorporacionDAO.save(reincorporacione);
            alumnos.add(reincorporacione.getAlumno());
        }
        for (Alumno alumno : alumnos) {
            matriculableService.revisarSituacionAcademica(alumno, ds);
        }
        return alumnos;
    }

    @Override
    public Resolucion findByResolucion(Long resolucionId, DataSessionPivot ds) {
        Resolucion resolucion = resolucionDAO.findById(resolucionId);

        return resolucion;
    }

    @Override
    public List<TipoResolucion> allTipoResolucion() {

        return tipoResolucionDAO.all();
    }

    @Override
    @Transactional
    public List<Alumno> saveRetiroCiclo(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
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

        Assert.isFalse(resolucionForm.getRetiroCiclo().isEmpty(), "Debe Agregar alumnos.");

        for (RetiroCiclo retiroCicloForm : resolucionForm.getRetiroCiclo()) {
            Alumno alumno = retiroCicloForm.getAlumno();
            RetiroCiclo retiroCiclo = retiroCicloDAO.findByAlumnoCicloRegistro(alumno, retiroCicloForm.getCicloAcademico());

            Assert.isNull(retiroCiclo, "El alumno cuenta con un trámite retiro ciclo.");

            EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
            System.out.println("Estado" + estadoTramite.getId());

            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());
            Alumno alumnoDB = alumnoDAO.find(alumno);

            Tramite tramite = new Tramite();
            tramite.setActivo(true);
            tramite.setCompania(ds.getCompania());
            tramite.setAlumno(alumnoDB);
            tramite.setCicloAcademico(ds.getCicloAcademico());
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setFechaRegistro(new Date());
            tramite.setPersona(alumnoDB.getPersona());
            tramite.setTipoTramite(tipoTramite);
            tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
            tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
            tramite.setUserRegistro(ds.getUsuario());
            tramiteDAO.save(tramite);

            retiroCiclo = retiroCicloForm;
            retiroCiclo.setEstado(TramiteEstadoEnum.ACEP);
            retiroCiclo.setTipoEnum(TipoRetiroCicloEnum.EXCEP);
            retiroCiclo.setAlumno(retiroCiclo.getAlumno());
            retiroCiclo.setCicloAcademico(retiroCiclo.getCicloAcademico());
            retiroCiclo.setCicloRegistro(ds.getCicloAcademico());
            retiroCiclo.setUsuario(ds.getUsuario());
            retiroCiclo.setMotivo(retiroCiclo.getMotivo());
            retiroCiclo.setTramite(tramite);
            retiroCiclo.setResolucion(resolucion);
            retiroCicloDAO.save(retiroCiclo);

            List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnoCicloRegularAct(alumnoDB, retiroCiclo.getCicloAcademico());
            for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
                alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.NREQ);
                alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);
            }
            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, retiroCiclo.getCicloAcademico());
            alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.RCI);
            alumnoCicloDAO.update(alumnoCiclo);
            
            List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCiclo);
            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
                Integer count = alumnoCicloCurso.getVecesCursado() - 1;
                alumnoCicloCurso.setVecesCursado(count);
                if (alumnoCicloCurso.isAprobado()) {
                    alumnoCicloCurso.setEstado(EstadoMatriculaEnum.RCI);
                }
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
            }
            alumnos.add(alumnoDB);
        }

        return alumnos;
    }

    @Override
    public List<CicloAcademico> ciclosAnteriores(int i) {
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivoPregrado();
        return cicloAcademicoDAO.allAnteriores(i, cicloAcademico);
    }

    @Override
    public List<Reincorporacion> allReincorporacionByResolucion(Resolucion resolucionDB) {
        return reincorporacionDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<RetiroCiclo> allRetiroCicloByResolucion(Resolucion resolucionDB) {
        return retiroCicloDAO.allByResolucion(resolucionDB);
    }
}
