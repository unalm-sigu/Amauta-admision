package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte.AporteAlumnoService;
import pe.edu.lamolina.pivot.controller.matricula.configuracionturno.ConfiguracionMatriculaService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableConnector;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
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
import pe.edu.lamolina.pivot.dao.general.ParametroDAO;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;
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
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.CAPA_ULTIMO_CICLO;

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
    ParametroDAO parametroDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;

    @Autowired
    DespliegueConfig despliegueConfig;

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
    MatriculableService matriculableService;

    @Override
    public List<CicloAcademico> allCiclos(CicloAcademico academico) {
        return cicloAcademicoDAO.allRegularPre(3, academico);
    }

    @Override
    public List<Tramite> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter) {
        List<Tramite> tramites = tramiteDAO.allReiAndRetByCiclo(cicloAcademico, filter);
        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allByTramites(tramites);
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByTramites(tramites);

        for (Tramite tram : tramites) {
            CicloAcademico academico = null;
            if (tram.getTipoTramite().getCodigo().equals(TipoTramiteEnum.RCI.name())) {
                academico = retiroCiclos.stream().filter(x -> x.getTramite().equals(tram)).map(x -> x.getCicloAcademico()).findAny().orElse(null);
            } else if (tram.getTipoTramite().getCodigo().equals(TipoTramiteEnum.REI.name())) {
                academico = reincorporacions.stream().filter(x -> x.getTramite().equals(tram)).map(x -> x.getCicloReincorporacion()).findAny().orElse(null);
            }
            tram.setCicloAcademicoResolucion(academico);
        }
        return tramites;
    }

    @Override
    @Transactional
    public void saveRetiroCiclo(Tramite tremite, DataSessionPivot ds) {

        Alumno alumno = tremite.getAlumno();
        alumno = alumnoDAO.find(alumno);

        Boolean isCondicional = Arrays.asList(S_6, S_4).contains(alumno.getSituacionAcademica().getCodigoEnum());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(tremite.getAlumno());
        AlumnoCiclo alumnoCiclo = alumnoCiclos.stream().filter(x -> Objects.equals(x.getCicloAcademico().getId(), tremite.getCicloAcademicoResolucion().getId())).findAny().orElse(null);
        Assert.isNotNull(alumnoCiclo, "El alumno no tiene actividad en el ciclo " + tremite.getCicloAcademicoResolucion().getDescripcion());

        RetiroCiclo retiro = retiroCicloDAO.findByAlumnoCicloRetiro(alumno, tremite.getCicloAcademicoResolucion());
        Assert.isNull(retiro, "El alumno ya cuenta con un trámite de retiro para el ciclo " + tremite.getCicloAcademicoResolucion().getDescripcion());

        DateTime today = new DateTime();
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());
        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(alumno);
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setEstadoEnum(TramiteEstadoEnum.PEND);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumno.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(ds.getUsuario());
        tramiteDAO.save(tramite);

        retiro = new RetiroCiclo();
        retiro.setEstado(TramiteEstadoEnum.PEND);
        if (isCondicional) {
            retiro.setTipoEnum(TipoRetiroCicloEnum.EXCEP);
        } else {
            retiro.setTipoEnum(TipoRetiroCicloEnum.REG);
        }
        retiro.setAlumno(tremite.getAlumno());
        retiro.setCicloAcademico(tremite.getCicloAcademicoResolucion());
        retiro.setCicloRegistro(ds.getCicloAcademico());
        retiro.setUsuario(ds.getUsuario());
        retiro.setMotivo(tremite.getMotivoResolucion());
        retiro.setTramite(tramite);
        retiroCicloDAO.save(retiro);

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByFilter(ds.getCicloAcademico(), alumno, EstadoMatriculaEnum.NMAT);
        if (matriculaResumen != null) {
            CicloAcademico ciclo = cicloAcademicoDAO.find(ds.getCicloAcademico());
            if (ciclo.getFechaPrioridades() != null) {
                matriculaResumen.setMotivoMatriculable(tremite.getMotivoResolucion());
                matriculaResumen.setEsCondicional(true);
                matriculaResumen.setFechaCondicional(new Date());
                updateCursoApro(alumno, ds);

                AlumnoCiclo alumnoCicloPenultimo = alumnoCiclos.get(1);
                alumnoCiclo = alumnoCicloDAO.findActivosRegularesByCiclo(alumnoCicloPenultimo.getCicloAcademico(), alumno);
                matriculaResumen = matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo);

                MatriculaResumen matriculaAnt = matriculaResumenDAO.findByAntPrioridad(matriculaResumen, ds.getCicloAcademico(), alumno.getCreditosAprobados() > CAPA_ULTIMO_CICLO ? true : false);
                MatriculaResumen matriculaDes = matriculaResumenDAO.findByDesPrioridad(matriculaResumen, ds.getCicloAcademico(), alumno.getCreditosAprobados() > CAPA_ULTIMO_CICLO ? true : false);
                if (matriculaAnt != null && matriculaDes != null) {

                    BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2));
                    matriculaResumen.setPrioridad(prioridad);
                    if (ciclo.getFechaTurnosAsignados() != null) {
                        TurnoAtencion turnoAlumno = turnoAtencionDAO.findById(matriculaResumen.getTurnoAtencion().getId());
                        TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, ds.getCicloAcademico());
                        if (turnoAlumno.getId() != turnosAtencion.getId()) {
                            BigDecimal numPrioridad = turnosAtencion.getPrioridadFin().add(new BigDecimal("0.01"));
                            Integer cantAlum = turnosAtencion.getAlumnos() + 1;
                            turnosAtencion.setAlumnos(cantAlum);
                            turnosAtencion.setPrioridadFin(numPrioridad);
                            turnoAtencionDAO.update(turnosAtencion);
                        }

                        matriculaResumen.setTurnoAtencion(turnosAtencion);

                    }
                    matriculaResumenDAO.update(matriculaResumen);
                }
            }

        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateCursoApro(Alumno alumno, DataSessionPivot ds) {
        avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
    }

    @Override
    @Transactional
    public void updateRetiroCiclo(Tramite tramiteForm, DataSessionPivot ds) {

        RetiroCiclo retiroCiclobd = retiroCicloDAO.findByTramite(tramiteForm);
        retiroCiclobd.setEstado(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));

        Tramite tramite = tramiteDAO.findById(tramiteForm);
        tramite.setEstadoEnum(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));
        tramiteDAO.update(tramite);
        Alumno alumno = retiroCiclobd.getAlumno();
        MatriculaResumen matriculaResumen = new MatriculaResumen();
        if (retiroCiclobd.getEstadoEnum() == TramiteEstadoEnum.RCHZ) {
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);

            JsonResponse jsonResponse = responseRestService.updateRest(matriculaResumen, ds);

            Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

            matriculaResumenDAO.delete(matriculaResumen);

//            alumno = alumnoDAO.find(alumno.getId());
//            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
            /*List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByMatriculaResumen(matriculaResumen);
            List<Curso> cursos = matriculaCursos.stream().map(x -> x.getCurso()).collect(Collectors.toList());
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.RET);
                matriculaCursoDAO.update(matriculaCurso);
            }

            List<MatriculaSimultaneo> matriculaSimultaneos = matriculaSimultaneoDAO.allByMatriculaCurso(matriculaCursos);
            for (MatriculaSimultaneo matriculaSimultaneo : matriculaSimultaneos) {
                matriculaSimultaneoDAO.delete(matriculaSimultaneo);
            }

            List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByMatriculaResumen(matriculaResumen);
            for (MatriculaSeccion matriculaSeccion : matriculaSeccions) {
                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.RET);
                matriculaSeccionDAO.update(matriculaSeccion);

                Seccion seccion = matriculaSeccion.getSeccion();
                if (cicloAcademico.getTipoEnum() == TipoCicloEnum.REG) {
                    seccion.setPrematriculados(seccion.getPrematriculados() - 1);
                }
                seccion.setMatriculados(seccion.getMatriculados() - 1);
                seccionDAO.update(seccion);

                VacanteAlumno vacanteAlumno = vacanteAlumnoDAO.allByAlumnoAndSeccion(alumno, seccion);
                vacanteAlumno.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                vacanteAlumno.setUserModificacion(null);
                vacanteAlumno.setFechaModificacion(null);
                vacanteAlumno.setAlumno(null);
                vacanteAlumnoDAO.update(vacanteAlumno);

            }
            for (Curso curso : cursos) {

                AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
                alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.RET);
                alumnoCursoCurriculaDAO.delete(alumnoCursoCurricula);
            }*/
            // Consultar si existe algun pago al matricularse.
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
            List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCiclo);
            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
                alumnoCicloCurso.setVecesCursado(alumnoCicloCurso.getVecesCursado() - 1);
                alumnoCicloCurso.setEstado(EstadoMatriculaEnum.RCI);
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
            }
            matriculableService.revisarSituacionAcademica(alumno, ds);
            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
        }
    }

    @Override
    public Parametro findParametro() {

        return parametroDAO.findBySistemaAmbienteParametrosSistemas(new Sistema(despliegueConfig.getSistema()),
                AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase()),
                ParametrosSistemasEnum.SALTO_PIVOT_MATRICULA);
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
        token.setEstado(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusSeconds(5).toDate());
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
    public void saveReincorporacion(Tramite tramite, DataSessionPivot ds) {
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByCicloReincorporacion(ds.getCicloAcademico());
        Map<Long, Alumno> map = TypesUtil.convertListToMap("alumno", reincorporacions);

        Alumno alumno = map.get(tramite.getAlumno().getId());
        if (alumno != null) {
            throw new PhobosException("El alumno" + alumno.getCodigo() + " ya cuenta con una resolución para el ciclo activo");
        }
        DateTime today = new DateTime();
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        alumno = alumnoDAO.find(tramite.getAlumno());

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_REI);

        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(alumno);
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setEstadoEnum(TramiteEstadoEnum.PEND);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumno.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(ds.getUsuario());
        tramiteDAO.save(tramite);

        Facultad facultad = tramite.getAlumno().getCarrera().getFacultad();
        Reincorporacion reincorporacione = new Reincorporacion();
        reincorporacione.setAceptado(0);
        reincorporacione.setFechaRegistro(new Date());
        reincorporacione.setEstadoTramite(estadoTramite);
        reincorporacione.setUserRegistro(ds.getUsuario());
        reincorporacione.setAlumno(alumno);
        reincorporacione.setCicloReincorporacion(tramite.getCicloAcademicoResolucion());
        reincorporacione.setMotivoDesercion(tramite.getMotivoResolucion());
        reincorporacione.setFacultad(facultad);
        reincorporacione.setTramite(tramite);
        reincorporacionDAO.save(reincorporacione);

    }

    @Override
    @Transactional
    public void updateReincorporacion(Tramite tramiteForm, DataSessionPivot ds) {
        EstadoTramite estadoTramite = null;
        if (tramiteForm.getEstadoEnum() == TramiteEstadoEnum.ACEP) {
            estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        } else {
            estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.RHZ_SOL);
        }
        Tramite tramite = tramiteDAO.findById(tramiteForm);
        tramite.setEstadoEnum(TramiteEstadoEnum.valueOf(tramiteForm.getEstado()));
        tramiteDAO.update(tramite);

        Reincorporacion reincorporacion = reincorporacionDAO.findByTramiteEstadoTram(tramiteForm, EstadoTramiteEnum.SOL_REI);
        reincorporacion.setEstadoTramite(estadoTramite);

        Alumno alumno = reincorporacion.getAlumno();
        MatriculaResumen matriculaResumen = new MatriculaResumen();
        if (tramiteForm.getEstadoEnum() != TramiteEstadoEnum.ACEP) {
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);

            JsonResponse jsonResponse = responseRestService.updateRest(matriculaResumen, ds);

            Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

            matriculaResumenDAO.delete(matriculaResumen);

        } else {
            Resolucion resolucion = createResolucion(tramiteForm.getResolucion(), TipoResolucionEnum.REIC, ds);
            reincorporacion.setResolucion(resolucion);
            reincorporacionDAO.update(reincorporacion);
            matriculableService.revisarSituacionAcademica(alumno, ds);
        }
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
        resolucion.setAplicacionDirecta(0l);
        resolucionDAO.save(resolucion);

        return resolucion;
    }
}
