package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionExistentes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.NREQ;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.PEND;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.INTES;
import pe.edu.lamolina.model.enums.TipoTramiteTrasladoEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.CambioNotaDAO;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteTrasladoDAO;
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
    CursoDirigidoDAO cursoDirigidoDAO;

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
    @Autowired
    CambioNotaDAO cambioNotaDAO;
    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;
    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    TramiteTrasladoDAO tramiteTrasladoDAO;
    @Autowired
    AvanceCurricularService avanceCurricularService;
    @Autowired
    GpoSeccionService gpoSeccionService;
    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;
    @Autowired
    CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;

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
        for (Reincorporacion reincorporacione : resolucionForm.getReincorporaciones()) {

            Alumno alumno = map.get(reincorporacione.getAlumno().getId());
            if (alumno != null) {
                throw new PhobosException("El alumno" + alumno.getCodigo() + " ya cuenta con una resolución para el ciclo activo");
            }
            if (!Objects.equals(reincorporacione.getCicloReincorporacion().getId(), ds.getCicloAcademico().getId())) {
                throw new PhobosException("El alumno debe reincorporarce en el ciclo actual.");
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
            tramite.setEstadoTramite(estadoTramite);
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
            reincorporacione.setEsCondicional(Boolean.FALSE);
            reincorporacione.setTramite(tramite);
            reincorporacionDAO.save(reincorporacione);
            alumnos.add(reincorporacione.getAlumno());
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
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);

        for (RetiroCiclo retiroCicloForm : resolucionForm.getRetiroCiclo()) {
            Alumno alumno = retiroCicloForm.getAlumno();
            Alumno alumnoDB = alumnoDAO.find(alumno);

            RetiroCiclo retiroCiclo = retiroCicloDAO.findByAlumnoCicloRegistro(alumno, retiroCicloForm.getCicloAcademico());
            Assert.isNull(retiroCiclo, "El alumno " + alumnoDB.getPersona().getApellidosNombres() + " cuenta con un trámite retiro ciclo.");

            List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(alumno);
            List<CicloAcademico> ciclo = alumnoCiclos.stream().map(x -> x.getCicloAcademico()).collect(Collectors.toList());
            Boolean exist = false;
            for (CicloAcademico cicloAcademico : ciclo) {
                if (Objects.equals(cicloAcademico.getId(), retiroCicloForm.getCicloAcademico().getId())) {
                    exist = true;
                    break;
                }
            }
            Assert.isTrue(exist, "El alumno " + alumnoDB.getPersona().getApellidosNombres() + " no tiene actividad en el ciclo " + retiroCicloForm.getCicloAcademico().getDescripcion());

            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());

            Tramite tramite = new Tramite();
            tramite.setActivo(true);
            tramite.setCompania(ds.getCompania());
            tramite.setAlumno(alumnoDB);
            tramite.setCicloAcademico(ds.getCicloAcademico());
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setEstadoTramite(estadoTramite);
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
            retiroCiclo.setCicloRegistro(ds.getCicloAcademico());
            retiroCiclo.setUsuario(ds.getUsuario());
            retiroCiclo.setEsCondicional(false);
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
                alumnoCicloCurso.setEstado(EstadoMatriculaEnum.RCI);
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
            }
            alumnos.add(alumnoDB);
        }
        for (Alumno alumno : alumnos) {

            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);

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

    @Override
    @Transactional
    public List<Alumno> saveCambioNota(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        List<Alumno> alumnos = new ArrayList<>();

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.CAM_NOTA);
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

        Assert.isFalse(resolucionForm.getCambioNota().isEmpty(), "Debe Agregar alumnos.");
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        for (CambioNota cambioNota : resolucionForm.getCambioNota()) {

            Tramite tramite = new Tramite();
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CAM_NOTA.name());
            Alumno alumno = alumnoDAO.find(cambioNota.getAlumno());

            tramite.setActivo(true);
            tramite.setCompania(ds.getCompania());
            tramite.setAlumno(alumno);
            tramite.setCicloAcademico(ds.getCicloAcademico());
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setEstadoTramite(estadoTramite);
            tramite.setFechaRegistro(new Date());
            tramite.setPersona(alumno.getPersona());
            tramite.setTipoTramite(tipoTramite);
            tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
            tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
            tramite.setUserRegistro(ds.getUsuario());
            tramiteDAO.save(tramite);

            CambioNota cambioNotaNew = new CambioNota();
            cambioNotaNew.setAlumno(alumno);
            cambioNotaNew.setCicloRegistro(ds.getCicloAcademico());
            cambioNotaNew.setEstado(TramiteEstadoEnum.ACEP);
            cambioNotaNew.setMotivo(cambioNota.getMotivo());
            cambioNotaNew.setTramite(tramite);
            cambioNotaNew.setUsuario(ds.getUsuario());
            cambioNotaNew.setCurso(cambioNota.getCurso());
            cambioNotaNew.setNota(cambioNota.getNota());
            cambioNotaNew.setCicloAcademico(cambioNota.getCicloAcademico());
            cambioNotaNew.setResolucion(resolucion);
            cambioNotaNew.setFechaRegistro(new Date());
            cambioNotaNew.setAceptado(Boolean.TRUE);
            cambioNotaNew.setEsCondicional(Boolean.FALSE);
            cambioNotaDAO.save(cambioNotaNew);

            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cambioNota.getCicloAcademico(), cambioNota.getCurso());

            AlumnoCicloCurso alumnoCicloCursosMod = new AlumnoCicloCurso();
            alumnoCicloCursosMod.setAlumnoCiclo(alumnoCicloCurso.getAlumnoCiclo());
            alumnoCicloCursosMod.setCreditos(alumnoCicloCurso.getCreditos());
            alumnoCicloCursosMod.setCurso(alumnoCicloCurso.getCurso());
            alumnoCicloCursosMod.setCursoEquivalente(alumnoCicloCurso.getCursoEquivalente());
            alumnoCicloCursosMod.setEstaAprobado(evaluateEstaAprobado(cambioNota.getNota(), alumno));
            alumnoCicloCursosMod.setEstado(alumnoCicloCurso.getEstadoEnum());
            alumnoCicloCursosMod.setFechaMigracion(alumnoCicloCurso.getFechaMigracion());
            alumnoCicloCursosMod.setFechaRegistro(new Date());
            alumnoCicloCursosMod.setNota(cambioNota.getNota().toString());
            alumnoCicloCursosMod.setRegistroActivo(1);
            alumnoCicloCursosMod.setTipoCursoCurricula(alumnoCicloCurso.getTipoCursoCurricula());
            alumnoCicloCursosMod.setUsuarioRegistro(ds.getUsuario());
            alumnoCicloCursosMod.setVecesCursado(alumnoCicloCurso.getVecesCursado());
            alumnoCicloCursosMod.setOrigenData(OrigenDataSituacionAcademicaEnum.MOD);
            alumnoCicloCursoDAO.save(alumnoCicloCursosMod);

            alumnoCicloCurso.setEstado(EstadoMatriculaEnum.NMOD);
            alumnoCicloCurso.setFechaModificacion(new Date());
            alumnoCicloCurso.setUserModificacion(ds.getUsuario());
            alumnoCicloCurso.setRegistroActivo(0);
            alumnoCicloCursoDAO.update(alumnoCicloCurso);

            alumnos.add(alumno);
        }
        for (Alumno alumno : alumnos) {
            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
        }
        return alumnos;
    }

    @Override
    public List<CambioNota> allCambioNota(Resolucion resolucionDB) {
        return cambioNotaDAO.allByResolucion(resolucionDB);

    }

    @Override
    public List<CursoDirigido> allCursodirigido(Resolucion resolucionDB) {
        return cursoDirigidoDAO.allByResolucion(resolucionDB);
    }

    private Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (alumno.isPostgrado()) {
            if (nota.compareTo(new BigDecimal(13)) >= 0) {
                aprobado = BigDecimal.ONE.intValue();
            }
        } else if (nota.compareTo(new BigDecimal(11)) >= 0) {
            aprobado = BigDecimal.ONE.intValue();
        }
        return aprobado;
    }

    @Override
    @Transactional
    public List<String> saveCursoDirigido(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        List<String> msg = new ArrayList();
        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.CURDIR);
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

        Assert.isFalse(resolucionForm.getCursoDirigido().isEmpty(), "Debe Agregar alumnos.");
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByCicloAcademicoSol(ds.getCicloAcademico());
        Map<Long, CursoDirigido> map = TypesUtil.convertListToMap("tramite.alumno.id", cursoDirigidos);
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.RES_FAC);
        EstadoTramite estadoTramiteRech = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.RHZ_SOL);

        List<Alumno> alumnos = resolucionForm.getCursoDirigido().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allActivoByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapMatriculaCursos = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", matriculaCursos);
        for (CursoDirigido cursoDirigidoForm : resolucionForm.getCursoDirigido()) {
            String message = "";
            EstadoTramite estado = cursoDirigidoForm.getSeleccionado() ? estadoTramite : estadoTramiteRech;
            TramiteEstadoEnum estadotram = cursoDirigidoForm.getSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ;
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());
            Assert.isNotNull(cursoDirigidoTram, "El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " no cuenta con un tramite de curso dirigido.");
            List<MatriculaCurso> matriculasCursoAlumno = mapMatriculaCursos.get(cursoDirigidoForm.getAlumno().getId());
            if (matriculasCursoAlumno.stream().filter(x -> Objects.equals(x.getCurso().getId(), cursoDirigidoTram.getCurso().getId())).findAny().orElse(null) != null) {
                message = "El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " está matriculado en el curso " + cursoDirigidoTram.getCurso().getNombre();
                msg.add(message);
                continue;
            }

            cursoDirigidoTram.setResolucion(resolucion);
            cursoDirigidoTram.setDocenteAsignado(cursoDirigidoForm.getDocenteAsignado());
            cursoDirigidoTram.setEstado(estado);
            cursoDirigidoDAO.update(cursoDirigidoTram);

            Tramite tramite = cursoDirigidoTram.getTramite();
            tramite.setEstadoEnum(estadotram);
            tramiteDAO.update(tramite);

            if (!cursoDirigidoForm.getSeleccionado()) {
                continue;
            }
            AnexoBoletin anexoBoletin = anexoBoletinDAO.findDepartamento(cursoDirigidoTram.getCurso().getDepartamentoAcademico());
            Assert.isNotNull(anexoBoletin, "No existe el anexo boletín para el departamento " + cursoDirigidoTram.getCurso().getDepartamentoAcademico().getNombre());
            List<GrupoSeccion> grupoSeccions = null;
            GrupoSeccion grupoSeccion = gpoSeccionService.findByCursoAndDocenteDirigido(cursoDirigidoTram.getCurso(), cursoDirigidoTram.getDocenteAsignado(), ds.getCicloAcademico());
            if (grupoSeccion == null) {
                grupoSeccion = new GrupoSeccion();
                grupoSeccion.setCantidad(1);
                grupoSeccion.setCursoDirigido(Boolean.TRUE);
                grupoSeccion.setCurso(cursoDirigidoTram.getCurso());
                grupoSeccion.setDocenteResponsable(cursoDirigidoTram.getDocenteAsignado());
                grupoSeccion.setAnexoBoletin(anexoBoletin);
                grupoSeccions = gpoSeccionService.saveGpoSeccionHeader(grupoSeccion, ds.getCicloAcademico(), ds);
            } else {
                grupoSeccions = new ArrayList<>();
                grupoSeccions.add(grupoSeccion);
            }
            this.matricular(grupoSeccions.get(0), cursoDirigidoTram.getTramite().getAlumno(), cursoDirigidoTram.getCurso(), ds.getUsuario(), ds.getCicloAcademico());
        }

        return msg;
    }

    @Transactional
    private void matricular(GrupoSeccion gpoSeccion, Alumno alumno, Curso curso, Usuario usuario, CicloAcademico academico) {

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, academico);
        List<Seccion> seccions = seccionDAO.allActivosByGpoSeccion(gpoSeccion);
        for (Seccion seccion : seccions) {
            seccion.setVacantes(seccion.getVacantes() + 1);
            seccion.setMatriculados(seccion.getMatriculados() + 1);
            seccionDAO.update(seccion);

            MatriculaSeccion matriculaSeccion = new MatriculaSeccion();
            matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaSeccion.setFechaRegistro(new Date());
            matriculaSeccion.setUserRegistro(usuario);
            matriculaSeccion.setSeccion(seccion);
            matriculaSeccion.setMatriculaResumen(matriculaResumen);
            matriculaSeccion.setVisible(1);
            matriculaSeccion.setFechaMatricula(new Date());
            matriculaSeccion.setUserMatricula(usuario);

            matriculaSeccionDAO.save(matriculaSeccion);
        }
        AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
        if (alumnoCursoCurricula != null) {
            alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.MAT);
            alumnoCursoCurriculaDAO.updateEstado(alumnoCursoCurricula);
        } else {
            TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.ELC);
            CursoOpcionalCurricula opcionalCurricula = cursoOpcionalCurriculaDAO.findByPlanCurricularAndCurso(alumno.getPlanCurricular(), curso);
            alumnoCursoCurricula = new AlumnoCursoCurricula();
            alumnoCursoCurricula.setAlumno(alumno);
            alumnoCursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
            alumnoCursoCurricula.setCurso(curso);
            alumnoCursoCurricula.setCursoOpcional(opcionalCurricula);
            alumnoCursoCurricula.setCursoCurricula(null);
            alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.HAB);
            alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
            alumnoCursoCurricula.setNumeroCiclo(10);
            alumnoCursoCurricula.setValidado(true);
            alumnoCursoCurricula.setVecesCursado(0);
            alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.MAT);
            alumnoCursoCurricula.setCreditos(curso.getCreditos());
            alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
        }

        MatriculaCurso matriculaCurso = new MatriculaCurso();
        matriculaCurso.setCurso(curso);
        matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaCurso.setMatriculaResumen(matriculaResumen);
        matriculaCurso.setNotaAcumulada("0");
        matriculaCurso.setNotaAvance("0");
        matriculaCurso.setNotaFinal("0");
        matriculaCurso.setPorcentajeAvanceNota(0);
        matriculaCurso.setCreditosAprobados(0);
        matriculaCurso.setCreditos(curso.getCreditos());
        matriculaCurso.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());
        matriculaCurso.setInasistencias(0);
        matriculaCurso.setInasistenciasExoneradas(0);
        matriculaCurso.setUserMatricula(usuario);
        matriculaCurso.setFechaMatricula(new Date());
        matriculaCursoDAO.save(matriculaCurso);

        matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaResumen.setCursosMatriculados(matriculaResumen.getCursosMatriculados() + 1);
        matriculaResumen.setCreditosMatriculados(matriculaResumen.getCreditosMatriculados() + curso.getCreditos());
        matriculaResumenDAO.update(matriculaResumen);
    }

    @Override
    @Transactional
    public void saveTramiteTraslado(Resolucion resolucionForm, Usuario usuario, CicloAcademico cicloAcademico, Compania compania) {

        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setUserRegistro(usuario);
        resolucion.setTipoResolucion(resolucionForm.getTipoResolucion());
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        Assert.isFalse(resolucionForm.getTramiteTraslado().isEmpty(), "Debe Agregar alumnos.");
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        for (TramiteTraslado tramiteTraslado : resolucionForm.getTramiteTraslado()) {

            Tramite tramite = new Tramite();
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), usuario);
            TipoTramite tipoTramite = null;
            if (resolucionForm.getTipoResolucion().getCodigo().equals(INTES.name())) {
                tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.INTES.name());
            } else {
                tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.TRAS.name());
            }
            Alumno alumno = alumnoDAO.find(tramiteTraslado.getAlumno());

            tramite.setActivo(true);
            tramite.setCompania(compania);
            tramite.setAlumno(alumno);
            tramite.setCicloAcademico(cicloAcademico);
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setEstadoTramite(estadoTramite);
            tramite.setFechaRegistro(new Date());
            tramite.setPersona(alumno.getPersona());
            tramite.setTipoTramite(tipoTramite);
            tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
            tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
            tramite.setUserRegistro(usuario);
            tramiteDAO.save(tramite);

            tramiteTraslado.setTramite(tramite);
//            tramiteTraslado.setCicloAcademico(cicloAcademico);
            tramiteTraslado.setResolucion(resolucion);
            tramiteTraslado.setFechaRegistro(new Date());
            if (resolucionForm.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.TRAS.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.TRAS);
            } else if (resolucionForm.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.INTES.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.INTES);
            } else if (resolucionForm.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.ING_HIS.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.ING_HIS);
            }
            tramiteTraslado.setUserRegistro(usuario);
            tramiteTraslado.setEstado(EstadoEnum.ACT.name());
            tramiteTrasladoDAO.save(tramiteTraslado);
        }
    }

    @Override
    public void saveIngresoHisto(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setUserRegistro(usuario);
        resolucion.setTipoResolucion(resolucionForm.getTipoResolucion());
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        Assert.isFalse(resolucionForm.getTramiteTraslado().isEmpty(), "Debe Agregar alumnos.");
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
    }

    @Override
    public TramiteTraslado findTramiteTraslado(Resolucion resolucionDB) {
        return tramiteTrasladoDAO.findByResolucion(resolucionDB);
    }

}
