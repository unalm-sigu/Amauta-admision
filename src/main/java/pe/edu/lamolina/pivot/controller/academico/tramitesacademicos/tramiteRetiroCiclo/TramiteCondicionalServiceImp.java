package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.CAPA_ULTIMO_CICLO;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.MAT_REG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.MAT_VER;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.OrigenTokenEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_TU;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte.AporteAlumnoService;
import pe.edu.lamolina.pivot.controller.matricula.configuracionturno.ConfiguracionMatriculaService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableConnector;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;
import pe.edu.lamolina.pivot.dao.tramite.CambioNotaDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TramiteCondicionalServiceImp implements TramiteCondicionalService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    RetiroCicloDAO retiroCicloDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    MatriculaSimultaneoDAO matriculaSimultaneoDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    TipoResolucionDAO tipoResolucionDAO;
    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;

    @Autowired
    AporteAlumnoService aporteAlumnoService;

    @Autowired
    InfoAcademicoService infoAcademicoService;

    @Autowired
    ResponseRestService responseRestService;

    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;
    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    MatriculableConnector matriculableConector;

    @Autowired
    ConfiguracionMatriculaService configuracionMatriculaService;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    CambioNotaDAO cambioNotaDAO;

    @Autowired
    MatriculableService matriculableService;

    private final static String TOKEN_PROMEDIOS = "-token-promedios";
    private final static String TOKEN_CURRICULA = "-token-curriculas";

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @Override
    public List<CicloAcademico> allCiclos(CicloAcademico academico) {
        return cicloAcademicoDAO.allRegularPre(3, academico);
    }

    @Override
    public List<Tramite> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter) {
        List<Tramite> tramites = tramiteDAO.allReiAndRetByCiclo(cicloAcademico, filter);
        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allByTramites(tramites);
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByTramitesCondicional(tramites);
        List<CambioNota> cambioNotas = cambioNotaDAO.allByTramites(tramites);

        for (Tramite tram : tramites) {
            CicloAcademico academico = null;
            if (tram.getTipoTramite().getCodigo().equals(TipoTramiteEnum.RCI.name())) {
                academico = retiroCiclos.stream().filter(x -> x.getTramite().equals(tram)).map(x -> x.getCicloAcademico()).findAny().orElse(null);
            } else if (tram.getTipoTramite().getCodigo().equals(TipoTramiteEnum.REI.name())) {
                academico = reincorporacions.stream().filter(x -> x.getTramite().equals(tram)).map(x -> x.getCicloReincorporacion()).findAny().orElse(null);
            } else if (tram.getTipoTramite().getCodigo().equals(TipoTramiteEnum.CAM_NOTA.name())) {
                CambioNota cambioNota = cambioNotas.stream().filter(x -> x.getTramite().equals(tram)).findAny().orElse(null);
                academico = cambioNota.getCicloAcademico();
                Curso curso = cambioNota.getCurso();
                tram.setCursoResolucion(curso);
                tram.setNotaResolucion(cambioNota.getNota());
            }

            tram.setCicloAcademicoResolucion(academico);
        }
        return tramites.stream().filter(x -> x.getCicloAcademicoResolucion() != null).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveRetiroCiclo(Tramite tremite, DataSessionPivot dx) {
        CicloAcademico ciclo = cicloAcademicoDAO.find(dx.getCicloAcademico());
        Alumno alumno = tremite.getAlumno();
        alumno = alumnoDAO.find(alumno);
        alumno.setEsMatriculaCondicional(Boolean.TRUE);

        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(tremite.getAlumno());
        AlumnoCiclo alumnoCiclo = alumnoCiclos.stream().filter(x -> Objects.equals(x.getCicloAcademico().getId(), tremite.getCicloAcademicoResolucion().getId())).findAny().orElse(null);
        Assert.isNotNull(alumnoCiclo, "El alumno no tiene actividad en el ciclo " + tremite.getCicloAcademicoResolucion().getDescripcion());

        RetiroCiclo retiro = retiroCicloDAO.findByAlumnoCicloRetiro(alumno, tremite.getCicloAcademicoResolucion());
        Assert.isNull(retiro, "El alumno ya cuenta con un trámite de retiro para el ciclo " + tremite.getCicloAcademicoResolucion().getDescripcion());

        DateTime today = new DateTime();
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), dx.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());
        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(dx.getCompania());
        tramite.setAlumno(alumno);
        tramite.setCicloAcademico(ciclo);
        tramite.setEstadoEnum(TramiteEstadoEnum.PEND);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumno.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(dx.getUsuario());
        tramiteDAO.save(tramite);

        retiro = new RetiroCiclo();
        retiro.setEstadoEnum(TramiteEstadoEnum.PEND);
        retiro.setAlumno(tremite.getAlumno());
        retiro.setCicloAcademico(tremite.getCicloAcademicoResolucion());
        retiro.setCicloRegistro(ciclo);
        retiro.setUsuario(dx.getUsuario());
        retiro.setMotivo(tremite.getMotivoResolucion());
        retiro.setTramite(tramite);
        retiro.setEsCondicional(Boolean.TRUE);
        retiroCicloDAO.save(retiro);

//        por ver 
        //        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByFilter(ciclo, alumno, EstadoMatriculaEnum.NMAT);
        //        if (matriculaResumen != null) {
        //
        //            if (ciclo.getFechaPrioridades() != null) {
        //                matriculaResumen.setMotivoMatriculable(tremite.getMotivoResolucion());
        //                matriculaResumen.setEsCondicional(true);
        //                matriculaResumen.setFechaCondicional(new Date());
        //
        //                AlumnoCiclo alumnoCicloPenultimo = alumnoCiclos.get(1);
        //                alumnoCiclo = alumnoCicloDAO.findActivosRegularesByCiclo(alumnoCicloPenultimo.getCicloAcademico(), alumno);
        //                matriculaResumen = matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo);
        //
        //                MatriculaResumen matriculaAnt = matriculaResumenDAO.findByPuntajeMenor(matriculaResumen, ciclo, alumno.getCreditosAprobadosConvalidados() > CAPA_ULTIMO_CICLO ? true : false);
        //                MatriculaResumen matriculaDes = matriculaResumenDAO.findByPuntajeMayor(matriculaResumen, ciclo, alumno.getCreditosAprobadosConvalidados() > CAPA_ULTIMO_CICLO ? true : false);
        //                if (matriculaAnt != null && matriculaDes != null) {
        //
        //                    BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2));
        //                    matriculaResumen.setPrioridad(prioridad);
        //                    if (ciclo.getFechaTurnosAsignados() != null) {
        //                        EventoAcademicoEnum eventoEnum = ciclo.isTipoRegular() ? MAT_REG : MAT_VER;
        //                        TurnoAtencion turnoAlumno = turnoAtencionDAO.findById(matriculaResumen.getTurnoAtencion().getId());
        //                        TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, ciclo, eventoEnum);
        //                        if (turnoAlumno.getId() != turnosAtencion.getId().longValue()) {
        //                            BigDecimal numPrioridad = turnosAtencion.getPrioridadFin().add(new BigDecimal("0.01"));
        //                            Integer cantAlum = turnosAtencion.getAlumnos() + 1;
        //                            turnosAtencion.setAlumnos(cantAlum);
        //                            turnosAtencion.setPrioridadFin(numPrioridad);
        //                            turnoAtencionDAO.update(turnosAtencion);
        //                        }
        //
        //                        matriculaResumen.setTurnoAtencion(turnosAtencion);
        //
        //                    }
        //                    matriculaResumenDAO.update(matriculaResumen);
        //                }
        //            }
        //
        //        } else {
        //
        //            matriculableService.saveMatriculable(alumno, TipoCondicionalEnum.RETIRO_CICLO.name(), dx);
        //        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateCursoApro(Alumno alumno, DataSessionPivot ds) {
        avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
    }

    @Override
    @Transactional
    public String updateRetiroCiclo(Tramite tramiteForm, DataSessionPivot ds) {

        RetiroCiclo retiroCiclobd = retiroCicloDAO.findByTramite(tramiteForm);
        retiroCiclobd.setEstadoEnum(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));

        EstadoTramite estadoTramite = null;
        if (tramiteForm.getEstadoEnum() == TramiteEstadoEnum.ACEP) {
            estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        } else {
            estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.RHZ_SOL);
        }
        Tramite tramite = tramiteDAO.findById(tramiteForm);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setEstadoEnum(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));
        tramiteDAO.update(tramite);

        Alumno alumno = alumnoDAO.find(retiroCiclobd.getAlumno());
        MatriculaResumen matriculaResumen = new MatriculaResumen();
        if (retiroCiclobd.getEstadoEnum() == TramiteEstadoEnum.RCHZ) {
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<SituacionAcademicaEnum> situaciones = Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_3U, S_2U, S_4U, S_6U, S_TU, S_EM);
            matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            if (matriculaResumen != null && !situaciones.contains(matriculaResumen.getAlumno().getSituacionAcademica().getCodigoEnum())) {

                JsonResponse jsonResponse = responseRestService.retirarMatriculaCiclo(matriculaResumen, ds);

                Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

                matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.INH);
                matriculaResumenDAO.update(matriculaResumen);
            }

        } else {
            Resolucion resolucion = createResolucion(tramiteForm.getResolucion(), TipoResolucionEnum.RCI, ds);
            retiroCiclobd.setResolucion(resolucion);
            retiroCicloDAO.update(retiroCiclobd);

            List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnoCicloRegularAct(alumno, retiroCiclobd.getCicloAcademico());
            for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
                alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.NREQ);
                alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);
            }

            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, retiroCiclobd.getCicloAcademico());
            alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.RCI);
            alumnoCicloDAO.update(alumnoCiclo);

            List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCiclo);
            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
                alumnoCicloCurso.setVecesCursado(alumnoCicloCurso.getVecesCursado() - 1);
                alumnoCicloCurso.setEstado(EstadoMatriculaEnum.RCI);
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
            }

//            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
        }

        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;
        List<Alumno> alumnos = new ArrayList<>();
        alumnos.add(alumno);
        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
    }

    @Override
    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds) {

        return alumnoDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void createToken(DataSessionPivot ds) {
        String valor = RandomStringUtils.randomAlphanumeric(45);
        TokenIngresante token = new TokenIngresante();
        token.setOrigenEnum(OrigenTokenEnum.AMAUTA);
        token.setEstado(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusSeconds(15).toDate());
        token.setPersona(ds.getPersona());
        token.setValor(valor);
        token.setUserRegistro(ds.getUsuario());
        tokenIngresanteDAO.save(token);

    }

    @Override
    public List<TipoTramite> allTipoTramite() {

        return tipoTramiteDAO.all();
    }

    @Override
    @Transactional
    public void saveReincorporacion(Tramite tramite, DataSessionPivot dx) {
        CicloAcademico ciclo = cicloAcademicoDAO.find(dx.getCicloAcademico());
        if (!Objects.equals(tramite.getCicloAcademicoResolucion().getId(), ciclo.getId())) {
            throw new PhobosException("El alumno debe reincorporarce en el ciclo actual.");
        }

        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByCicloReincorporacion(ciclo);
        Map<Long, Alumno> map = TypesUtil.convertListToMap("alumno", reincorporacions);

        Alumno alumno = map.get(tramite.getAlumno().getId());
        if (alumno != null) {
            throw new PhobosException("El alumno" + alumno.getCodigo() + " ya cuenta con una resolución para el ciclo activo");
        }
        DateTime today = new DateTime();
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), dx.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        alumno = alumnoDAO.find(tramite.getAlumno());
        alumno.setEsMatriculaCondicional(Boolean.TRUE);

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_REI);

        tramite.setActivo(true);
        tramite.setCompania(dx.getCompania());
        tramite.setAlumno(alumno);
        tramite.setCicloAcademico(ciclo);
        tramite.setEstadoEnum(TramiteEstadoEnum.PEND);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumno.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(dx.getUsuario());
        tramiteDAO.save(tramite);

        Facultad facultad = tramite.getAlumno().getCarrera().getFacultad();
        Reincorporacion reincorporacione = new Reincorporacion();
        reincorporacione.setAceptado(0);
        reincorporacione.setFechaRegistro(new Date());
        reincorporacione.setEstadoTramite(estadoTramite);
        reincorporacione.setUserRegistro(dx.getUsuario());
        reincorporacione.setAlumno(alumno);
        reincorporacione.setCicloReincorporacion(tramite.getCicloAcademicoResolucion());
        reincorporacione.setMotivoDesercion(tramite.getMotivoResolucion());
        reincorporacione.setFacultad(facultad);
        reincorporacione.setTramite(tramite);
        reincorporacione.setAceptado(0);
        reincorporacione.setEsCondicional(Boolean.TRUE);
        reincorporacionDAO.save(reincorporacione);

//        por ver 
//        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByFilter(ciclo, alumno, EstadoMatriculaEnum.NMAT);
        //        if (matriculaResumen != null) {
        //
        //            if (ciclo.getFechaPrioridades() != null) {
        //                matriculaResumen.setMotivoMatriculable(tramite.getMotivoResolucion());
        //                matriculaResumen.setEsCondicional(true);
        //                matriculaResumen.setFechaCondicional(new Date());
        //
        //                AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findActivoRegularByCicloAlumno(alumno.getCicloActivoRegular(), alumno);
        //                matriculaResumen = matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo);
        //
        //                boolean esUltimoCiclo = alumno.getCreditosAprobadosConvalidados() > CAPA_ULTIMO_CICLO;
        //                MatriculaResumen matriculaAnt = matriculaResumenDAO.findByPuntajeMenor(matriculaResumen, ciclo, esUltimoCiclo);
        //                MatriculaResumen matriculaDes = matriculaResumenDAO.findByPuntajeMayor(matriculaResumen, ciclo, esUltimoCiclo);
        //                if (matriculaAnt != null && matriculaDes != null) {
        //
        //                    BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2));
        //                    matriculaResumen.setPrioridad(prioridad);
        //                    if (ciclo.getFechaTurnosAsignados() != null) {
        //                        EventoAcademicoEnum eventoEnum = ciclo.isTipoRegular() ? MAT_REG : MAT_VER;
        //                        TurnoAtencion turnoAlumno = turnoAtencionDAO.findById(matriculaResumen.getTurnoAtencion().getId());
        //                        TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, ciclo, eventoEnum);
        //                        if (turnoAlumno.getId() != turnosAtencion.getId()) {
        //                            BigDecimal numPrioridad = turnosAtencion.getPrioridadFin().add(new BigDecimal("0.01"));
        //                            Integer cantAlum = turnosAtencion.getAlumnos() + 1;
        //                            turnosAtencion.setAlumnos(cantAlum);
        //                            turnosAtencion.setPrioridadFin(numPrioridad);
        //                            turnoAtencionDAO.update(turnosAtencion);
        //                        }
        //
        //                        matriculaResumen.setTurnoAtencion(turnosAtencion);
        //
        //                    }
        //                    matriculaResumenDAO.update(matriculaResumen);
        //                }
        //            }
        //
        //        } else {
        //            matriculableService.saveMatriculable(alumno, TipoCondicionalEnum.REI.name(), dx);
        //
        //        }
    }

    @Override
    @Transactional
    public String updateReincorporacion(Tramite tramiteForm, DataSessionPivot ds) {
        EstadoTramite estadoTramite = null;
        if (tramiteForm.getEstadoEnum() == TramiteEstadoEnum.ACEP) {
            estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        } else {
            estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.RHZ_SOL);
        }
        Tramite tramite = tramiteDAO.findById(tramiteForm);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setEstadoEnum(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));
        tramiteDAO.update(tramite);

        Reincorporacion reincorporacion = reincorporacionDAO.findByTramiteEstadoTram(tramiteForm, EstadoTramiteEnum.SOL_REI);
        reincorporacion.setEstadoTramite(estadoTramite);

        Alumno alumno = alumnoDAO.find(reincorporacion.getAlumno());
        MatriculaResumen matriculaResumen = new MatriculaResumen();
        if (tramiteForm.getEstadoEnum() != TramiteEstadoEnum.ACEP) {
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            if (matriculaResumen != null) {

                JsonResponse jsonResponse = responseRestService.retirarMatriculaCiclo(matriculaResumen, ds);

                Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

                matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.INH);
                matriculaResumenDAO.update(matriculaResumen);
            }

        } else {
            Resolucion resolucion = createResolucion(tramiteForm.getResolucion(), TipoResolucionEnum.REIC, ds);
            reincorporacion.setResolucion(resolucion);
            reincorporacionDAO.update(reincorporacion);

        }

        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;
        List<Alumno> alumnos = new ArrayList<>();
        alumnos.add(alumno);
        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
    }

    private Resolucion createResolucion(Resolucion resolucionForm, TipoResolucionEnum tipoResolucionEnum, DataSessionPivot dsp) {
        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(tipoResolucionEnum);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(dsp.getUsuario());
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        return resolucion;
    }

    @Override
    @Transactional
    public void saveCambioNota(Tramite tramite, DataSessionPivot dx) {
        DateTime today = new DateTime();
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), dx.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CAM_NOTA.name());
        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        alumno.setEsMatriculaCondicional(Boolean.TRUE);
        CicloAcademico ciclo = cicloAcademicoDAO.find(dx.getCicloAcademico());

        tramite.setActivo(true);
        tramite.setCompania(dx.getCompania());
        tramite.setAlumno(alumno);
        tramite.setCicloAcademico(ciclo);
        tramite.setEstadoEnum(TramiteEstadoEnum.PEND);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumno.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(dx.getUsuario());
        tramiteDAO.save(tramite);

        CambioNota cambioNota = new CambioNota();
        cambioNota.setAlumno(alumno);
        cambioNota.setCicloRegistro(ciclo);
        cambioNota.setEsCondicional(Boolean.TRUE);
        cambioNota.setEstado(TramiteEstadoEnum.PEND);
        cambioNota.setMotivo(tramite.getMotivoResolucion());
        cambioNota.setTramite(tramite);
        cambioNota.setUsuario(dx.getUsuario());
        cambioNota.setCurso(tramite.getCursoResolucion());
        cambioNota.setCicloAcademico(tramite.getCicloAcademicoResolucion());
        cambioNota.setFechaRegistro(new Date());
        cambioNota.setAceptado(Boolean.FALSE);
        cambioNotaDAO.save(cambioNota);

//        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByFilter(ciclo, alumno, EstadoMatriculaEnum.NMAT);
//        if (matriculaResumen != null) {
//
//            if (ciclo.getFechaPrioridades() != null) {
//                matriculaResumen.setMotivoMatriculable(tramite.getMotivoResolucion());
//                matriculaResumen.setEsCondicional(true);
//                matriculaResumen.setFechaCondicional(new Date());
//
//                AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findActivoRegularByCicloAlumno(alumno.getCicloActivoRegular(), alumno);
//                matriculaResumen = matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo);
//
//                MatriculaResumen matriculaAnt = matriculaResumenDAO.findByPuntajeMenor(matriculaResumen, ciclo, alumno.getCreditosAprobadosConvalidados() > CAPA_ULTIMO_CICLO ? true : false);
//                MatriculaResumen matriculaDes = matriculaResumenDAO.findByPuntajeMayor(matriculaResumen, ciclo, alumno.getCreditosAprobadosConvalidados() > CAPA_ULTIMO_CICLO ? true : false);
//                if (matriculaAnt != null && matriculaDes != null) {
//
//                    BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2));
//                    matriculaResumen.setPrioridad(prioridad);
//                    if (ciclo.getFechaTurnosAsignados() != null) {
//                        EventoAcademicoEnum eventoEnum = ciclo.isTipoRegular() ? MAT_REG : MAT_VER;
//                        TurnoAtencion turnoAlumno = turnoAtencionDAO.findById(matriculaResumen.getTurnoAtencion().getId());
//                        TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, ciclo, eventoEnum);
//                        if (turnoAlumno.getId() != turnosAtencion.getId()) {
//                            BigDecimal numPrioridad = turnosAtencion.getPrioridadFin().add(new BigDecimal("0.01"));
//                            Integer cantAlum = turnosAtencion.getAlumnos() + 1;
//                            turnosAtencion.setAlumnos(cantAlum);
//                            turnosAtencion.setPrioridadFin(numPrioridad);
//                            turnoAtencionDAO.update(turnosAtencion);
//                        }
//
//                        matriculaResumen.setTurnoAtencion(turnosAtencion);
//
//                    }
//                    matriculaResumenDAO.update(matriculaResumen);
//                }
//            }
//
//        } else {
//            matriculableService.saveMatriculable(alumno, TipoCondicionalEnum.REI.name(), dx);
//
//        }
    }

    @Override
    @Transactional
    public String updateCambioNota(Tramite tramiteForm, DataSessionPivot ds) {

        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        CambioNota cambioNota = cambioNotaDAO.findByTramite(tramiteForm);
        cambioNota.setEstado(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));

        Tramite tramite = tramiteDAO.findById(tramiteForm);
        Alumno alumno = alumnoDAO.findAllInfo(cambioNota.getAlumno().getId());

        List<SituacionAcademicaEnum> situaciones = Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_3U, S_2U, S_4U, S_6U, S_TU, S_EM);
        MatriculaResumen matriculaResumen = new MatriculaResumen();
        if (tramiteForm.getEstadoEnum() != TramiteEstadoEnum.ACEP) {
            EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.RHZ_SOL);
            tramite.setEstadoEnum(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));
            tramite.setEstadoTramite(estadoTramite);
            tramiteDAO.update(tramite);

            matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            if (matriculaResumen != null && !situaciones.contains(matriculaResumen.getAlumno().getSituacionAcademica().getCodigoEnum())) {

                JsonResponse jsonResponse = responseRestService.retirarMatriculaCiclo(matriculaResumen, ds);

                Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

                matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.INH);
                matriculaResumenDAO.update(matriculaResumen);
            }

        } else {
            EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);

            tramite.setEstadoEnum(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));
            tramite.setEstadoTramite(estadoTramite);
            tramiteDAO.update(tramite);

            Resolucion resolucion = createResolucion(tramiteForm.getResolucion(), TipoResolucionEnum.CAM_NOTA, ds);
            cambioNota.setNota(tramiteForm.getNotaResolucion());
            cambioNota.setResolucion(resolucion);
            cambioNotaDAO.update(cambioNota);

            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cambioNota.getCicloAcademico(), cambioNota.getCurso());

            AlumnoCicloCurso alumnoCicloCursosMod = new AlumnoCicloCurso();
            alumnoCicloCursosMod.setAlumnoCiclo(alumnoCicloCurso.getAlumnoCiclo());
            alumnoCicloCursosMod.setCreditos(alumnoCicloCurso.getCreditos());
            alumnoCicloCursosMod.setCurso(alumnoCicloCurso.getCurso());
            alumnoCicloCursosMod.setCursoEquivalente(alumnoCicloCurso.getCursoEquivalente());
            alumnoCicloCursosMod.setEstaAprobado(evaluateEstaAprobado(tramiteForm.getNotaResolucion(), alumno));
            alumnoCicloCursosMod.setEstado(alumnoCicloCurso.getEstadoEnum());
            alumnoCicloCursosMod.setFechaMigracion(alumnoCicloCurso.getFechaMigracion());
            alumnoCicloCursosMod.setFechaRegistro(new Date());
            alumnoCicloCursosMod.setNota(tramiteForm.getNotaResolucion().toString());
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

            logger.debug("Situación academica {}", alumno.getSituacionAcademica().getCodigo());
            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);

        }
        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;
        List<Alumno> alumnos = new ArrayList<>();
        alumnos.add(alumno);
        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
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
    public void evaluarEliminarMatriculable(Alumno alumno, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        List<SituacionAcademicaEnum> situaciones = Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_3U, S_2U, S_4U, S_6U, S_TU, S_EM);
        alumno = alumnoDAO.findAllInfo(alumno.getId());
        logger.debug("Situación academica {}", alumno.getSituacionAcademica().getCodigo());
        if (!situaciones.contains(alumno.getSituacionAcademica().getCodigoEnum())) {
            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            if (matriculaResumen != null) {

                JsonResponse jsonResponse = responseRestService.retirarMatriculaCiclo(matriculaResumen, ds);

                Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

                matriculaResumenDAO.delete(matriculaResumen);
            }
        }
    }

    @Override
    public List<Curso> allCursosByName(String nombre, Alumno alumno, CicloAcademico academico, DataSessionPivot ds) {
        List<AlumnoCicloCurso> alumnoCicloCurso = alumnoCicloCursoDAO.allByNombre(alumno, academico, nombre);
        return alumnoCicloCurso.stream().map(x -> x.getCurso()).collect(Collectors.toList());
    }
}
