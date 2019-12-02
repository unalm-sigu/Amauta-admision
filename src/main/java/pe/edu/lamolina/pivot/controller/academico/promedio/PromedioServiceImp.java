package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.NotaLetraEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_T;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoService;
import pe.edu.lamolina.pivot.controller.academico.situacionacademica.SituacionAcademicaService;
import pe.edu.lamolina.pivot.controller.auditor.AuditorService;
import pe.edu.lamolina.pivot.controller.interceptor.InterceptorService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.VisorCalculaSituacion;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PromedioServiceImp implements PromedioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    SituacionAcademicaService situacionAcademicaService;

    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    VisorCalculaSituacion visorCalculaSituacion;

    @Autowired
    InterceptorService interceptorService;

    @Autowired
    AuditorService auditorService;

    @Autowired
    ContadorComponent contadorComponent;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    AlumnoService alumnoService;

    private final Integer VECES_TRIKA = 3;

    private final Integer INI_TRIKA = 200320;

    private final int MAX_INTERCALADOS_NMAT = 6;

    @Override
    @Async
    @Transactional
    public void saveCerrarActaAsync(List<Alumno> alumnos, DataSessionPivot ds) {
        for (Alumno alumno : alumnos) {
            this.calulcarSituacionAcademica(new Alumno(alumno.getId()), ds);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void trasladarInformcionForHistorial(
            MatriculaResumen matriculaResumen,
            List<MatriculaCurso> matriculasCurso,
            List<MatriculaSeccion> matriculasSeccion,
            DataSessionPivot ds, boolean calcularSituacion) {

        visorCalculoNotas.incrementarCantidad();
        List<MatriculaCurso> matriculasCursoByAlumno = matriculasCurso.stream()
                .filter(x -> x.getMatriculaResumen().getAlumno().getId().equals(matriculaResumen.getAlumno().getId()))
                .collect(Collectors.toList());
        List<MatriculaSeccion> matriculasSeccionByAlumno = matriculasSeccion.stream().filter(x -> x.getMatriculaResumen().getId().equals(matriculaResumen.getId())).collect(Collectors.toList());
        for (MatriculaCurso matriculaCurso : matriculasCursoByAlumno) {
            MatriculaSeccion matriculaSeccion = matriculasSeccionByAlumno
                    .stream().filter(x -> x.getSeccion().getGrupoSeccion().getCurso().getId().equals(matriculaCurso.getCurso().getId())).findFirst().orElse(null);
            if (matriculaSeccion != null && matriculaSeccion.getSeccion().getGrupoSeccion().isEstadoGrupoCerrado()) {
                this.trasladoPromediosSource2(matriculaCurso, matriculasCursoByAlumno, ds);
            }
        }
        if (calcularSituacion) {
            CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(matriculaResumen.getAlumno().getModalidadEstudio());
            List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(matriculaResumen.getAlumno());
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio = alumnoCicloCursoDAO.allOperativesByAlumno(matriculaResumen.getAlumno());
            List<Reincorporacion> reincorporacionesByAlumno = reincorporacionDAO.allByEstadoTramiteAndAlumnoRei(
                    matriculaResumen.getAlumno(), new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));

            this.promediarAllCicloSync(
                    matriculaResumen.getAlumno(), cicloActivo, cicloAcademicoDAO.all(), alumnoCiclos,
                    allOperativesByModalidadEstudio, reincorporacionesByAlumno, ds);
        }
        visorCalculoNotas.incrementarProcesados();
        visorCalculoNotas.reporte();
    }

    private void trasladoPromediosSource2(MatriculaCurso matriculaCurso, List<MatriculaCurso> matriculaCursos, DataSessionPivot ds) {
        Alumno alumno = matriculaCurso.getMatriculaResumen().getAlumno();
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        Curso curso = matriculaCurso.getCurso();
        generarHistorialNotas2(alumno, curso, matriculaCurso, cicloAcademico, matriculaCursos, ds);
    }

    @Override
    public void trasladoPromediosSource(MatriculaCurso matriculaCurso, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.find(matriculaCurso.getMatriculaResumen().getAlumno());
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        Curso curso = cursoDAO.find(matriculaCurso.getCurso().getId());

        logger.debug("Trasladar matricula curso alumno {} Ciclo {}, Curso {}",
                alumno.getId(),
                cicloAcademico.getId(),
                matriculaCurso.getCurso().getId());

        DateTime today = new DateTime();

        generarHistorialNotas(alumno, curso, matriculaCurso, cicloAcademico, ds);

        AlumnoCiclo alumnoCicloSiguiente = alumnoCicloDAO.findActiveSiguienteByAlumno(alumno, cicloAcademico);
        MatriculaCurso matriculaCursoSiguiente = null;
        if (alumnoCicloSiguiente != null) {
            matriculaCursoSiguiente = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, alumnoCicloSiguiente.getCicloAcademico());
        }
        if (alumnoCicloSiguiente != null && matriculaCursoSiguiente != null) {
            this.trasladoPromediosSource(matriculaCursoSiguiente, ds);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void promediarAllCicloAsync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds) {

        contadorComponent.iniciar(1);
        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }

        this.promediarAllCicloSync(alumno, cicloActivo, ciclos, alumnoCiclos, allOperativesCicloCurso, allReincorporacionesByAlumno, ds);
    }

    @Override
    @Transactional
    public void promediarAllCicloSync(
            Alumno alumni,
            CicloAcademico cicloActivo,
            List<CicloAcademico> ciclosAll,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds) {

        Alumno alumno = alumni.clone();

        alumni.setConError(Boolean.FALSE);
        alumnoService.marcarFalla(alumni);

        long t1 = System.currentTimeMillis();
        contadorComponent.incrementar();
        logger.info("Promediar Alumno {}", alumno.getCodigo());
        if (alumno.getSituacionAcademica().isCodigoR()) {
            throw new PhobosException("No se puede calcular la situación, en situacion Renunciante.");
        }

        try {
            List<AlumnoCiclo> alumnoCiclosPrevio = new ArrayList();
            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                AlumnoCiclo acPrev = (AlumnoCiclo) alumnoCiclo.clone();
                alumnoCiclosPrevio.add(acPrev);
            }

            Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("id", alumnoCiclosPrevio);
            Map<String, List<CicloAcademico>> mapCiclo = TypesUtil.convertListToMapList("codigo", ciclosAll);

            this.analizeAlumnoCiclos(alumno, alumnoCiclos, allOperativesByModalidadEstudio);

            this.promediarAlumno(alumno, mapCiclo, cicloActivo, alumnoCiclos, allOperativesByModalidadEstudio, ds); //cambia situacion academica

            this.analizarEgresado(alumno, alumnoCiclos, ds);

            this.analizarDesertor(alumno, cicloActivo, alumnoCiclos, ciclosAll, mapCiclo, ds); //slw

            this.analizeReincorporacion(alumno, cicloActivo, allReincorporacionesByAlumno);

            this.analizedCastigados(alumno, alumnoCiclos, cicloActivo, mapCiclo);

            CicloAcademico ultimoCiclo = null;
            CicloAcademico ultimoCicloRegular = null;
            CicloAcademico ultimoRetiroRegular = null;

            AlumnoCiclo ultimoRegular = null;
            AlumnoCiclo ultimoAlumnoCiclo = null;

            int ciclosNorelacionados = 0;
            Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloAsc());

            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                ultimoAlumnoCiclo = alumnoCiclo;
                CicloAcademico cicloAlumno = alumnoCiclo.getCicloAcademico();
                if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT
                        && !cicloActivo.getCodigo().equals(cicloAlumno.getCodigo())) {
                    ultimoCiclo = alumnoCiclo.getCicloAcademico();
                    if (alumnoCiclo.getCicloAcademico().getTipoEnum() == TipoCicloEnum.REG) {
                        ultimoRegular = alumnoCiclo;
                        ultimoCicloRegular = alumnoCiclo.getCicloAcademico();
                    }
                }
                if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.RCI) {
                    if (alumnoCiclo.getCicloAcademico().getTipoEnum() == TipoCicloEnum.REG) {
                        ultimoRetiroRegular = alumnoCiclo.getCicloAcademico();
                    }
                }
                if (alumnoCiclo.getId() == null) {
                    alumnoCicloDAO.save(alumnoCiclo);
                    continue;
                }

                AlumnoCiclo alumnoCicloPrevio = mapAlumnoCiclo.get(alumnoCiclo.getId());
                if (alumnoCicloPrevio == null) {
                    ciclosNorelacionados++;
                    continue;
                }

                boolean sonIguales = ObjectUtil.equalsAttrs(alumnoCicloPrevio, alumnoCiclo,
                        Arrays.asList(
                                "situacionInicio.id", "situacionFinal.id", "estado",
                                "creditosCursadosCiclo", "cursosInscritos", "creditosAcumulados",
                                "cursosAprobados", "creditosAprobadosAcumulados", "creditosAprobadosCiclo",
                                "promedioCiclo", "promedioAcumulado", "estaAprobado"));

                if (!sonIguales) {
                    logger.debug("\talumnoCiclos.id {} tiene-cambios", alumnoCiclo.getId());

                    alumnoCiclo.setUserModificacion(ds.getUsuario());
                    alumnoCiclo.setFechaModificacion(ds.getFechaAccionAudit());
                    alumnoCicloDAO.updateColumns(alumnoCiclo,
                            "situacionInicio", "situacionFinal", "estado",
                            "creditosCursadosCiclo", "cursosInscritos", "creditosAcumulados",
                            "cursosAprobados", "creditosAprobadosAcumulados", "creditosAprobadosCiclo",
                            "promedioCiclo", "promedioAcumulado",
                            "estaAprobado", "userModificacion", "fechaModificacion");
                }

            }

            if (ultimoRegular != null && ultimoRegular.getSituacionFinal() != null) {
                alumno.setSituacionAcademica(ultimoRegular.getSituacionFinal());
            }

            alumno.setUltimoRetiro(ultimoRetiroRegular);
            alumno.setCicloActivo(ultimoCiclo);
            alumno.setCicloActivoRegular(ultimoCicloRegular);

            alumno.setPromedioUltimoCiclo(null);
            if (ultimoCicloRegular != null) {
                alumno.setPromedioUltimoCiclo(ultimoRegular.getPromedioCiclo());
            }

            alumno.setPromedioProcesado(Boolean.TRUE);
            alumnoDAO.updateColumns(alumno,
                    "cicloActivo", "cicloActivoRegular", "ultimoRetiro", "situacionAcademica",
                    "creditosAprobados", "creditosCursados", "promedioAcumulado", "promedioUltimoCiclo");

            logger.debug("\tEl alumno.id {} tiene {} ciclos no relacionados", alumni.getId(), ciclosNorelacionados);
            contadorComponent.incrementarProcesados();

        } catch (Exception e) {
            e.printStackTrace();

            String error = "####Error en el hilo alumno " + alumno.getCodigo() + " ciclo activo " + ObjectUtil.getParentTree(alumno, "cicloActivo.codigo");
            logger.error(error);
            auditorService.auditPromediarAlumno(alumno, cicloActivo, ds, e);

            alumno.setConError(Boolean.TRUE);
            alumnoService.marcarFalla(alumno);

            //throw new PhobosException(error);
        } finally {
            contadorComponent.reporte();
        }
        long t2 = System.currentTimeMillis();
        logger.debug("Calculo de promedios de {} demoro {} mseg", alumni.getCodigo(), (t2 - t1));

    }

    public void analizedCastigados(Alumno alumno, List<AlumnoCiclo> alumnoCiclos, CicloAcademico cicloActivo, Map<String, List<CicloAcademico>> mapCiclo) {
        if (!(alumno.getSituacionAcademica().isTrikeado() || alumno.getSituacionAcademica().isCodigoS6())) {
            return;
        }

        ModalidadEstudioEnum modalidadEnum = alumno.getModalidadEstudio().getOperativeModalidadEnum();

        AlumnoCiclo alumnoCicloSuspendido = alumnoCicloDAO.findLastByAlumnoAndSituacion(alumno, alumno.getSituacionAcademica().getCodigoEnum());
        if (alumnoCicloSuspendido == null) {
            return;
        }

        CicloAcademico cicloSuspendido = alumnoCicloSuspendido.getCicloAcademico();

        //CicloAcademico cicloInha = cicloAcademicoDAO.findSiguienteRegularActivo(cicloSuspendido, modalidadEnum);
        CicloAcademico cicloInha = findCicloSiguienteRegularActivo(cicloSuspendido, modalidadEnum, mapCiclo);
        if (cicloInha == null) {
            return;
        }
        //AlumnoCiclo alumnoCicloInha = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloInha);
        AlumnoCiclo alumnoCicloInha = findAlumnoCiclo(alumnoCiclos, cicloInha);

        if (cicloActivo.getCodigoInt() > cicloInha.getCodigoInt() && alumno.getCicloActivoRegular().getCodigoInt() <= cicloActivo.getCodigoInt()) {
            if (alumno.getSituacionAcademica().getId().longValue() == alumnoCicloInha.getSituacionFinal().getId()) {
                alumno.setSituacionAcademica(alumnoCicloInha.getSituacionFinal());
                // alumnoDAO.updateSituacionAcad(alumno);
            }
        }

    }

    private void analizeReincorporacion(Alumno alumno, CicloAcademico cicloActivo, List<Reincorporacion> reincorporacionesByAlumno) {
        //List<Reincorporacion> reincorporacionesByAlumno = reincorporacionDAO.allByEstadoTramiteAndAlumnoRei(
        //        alumno, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
        if (reincorporacionesByAlumno.isEmpty()) {
            return;
        }

        Collections.sort(reincorporacionesByAlumno,
                (p1, p2) -> Integer.valueOf(p2.getCicloReincorporacion().getCodigo()).
                        compareTo(Integer.valueOf(p1.getCicloReincorporacion().getCodigo())));

        CicloAcademico cicloAcademicoRei = reincorporacionesByAlumno.get(0).getCicloReincorporacion();

        if (cicloActivo.equals(cicloAcademicoRei)) {
            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastNotInSituacion(alumno, SituacionAcademicaEnum.S_D);
            if (alumnoCiclo.getSituacionFinal().isDesertor()) { //to delete
                alumno.setSituacionAcademica(alumnoCiclo.getSituacionInicio());
            } else {
                alumno.setSituacionAcademica(alumnoCiclo.getSituacionFinal());
            }
            //alumnoDAO.updateSituacionAcad(alumno);
        }

    }

    private void analizeAlumnoCiclos(Alumno alumno, List<AlumnoCiclo> alumnoCiclos, List<AlumnoCicloCurso> alumnoCicloCursosAll) {
        //logger.debug("analizeAlumnoCiclos");
        List<String> ciclosStr = alumnoCiclos.stream()
                .map(x -> x.getCicloAcademico().getCodigo())
                .collect(Collectors.toList());
        //logger.debug(String.join(",", ciclosStr));

        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", alumnoCicloCursosAll);

        int idx = 0;
        List<AlumnoCiclo> eliminables = new ArrayList();
        AlumnoCiclo alumnoCicloAnterior = null;

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            if (idx == 0) {
                this.evaluarPrimerCiclo(alumno, alumnoCiclo);
                if (alumno.getModalidadEstudio().isPostgrado()) {
                    alumnoCicloAnterior = (AlumnoCiclo) alumnoCiclo.clone();
                }
            }
            if (idx > 0) {
                if (alumnoCiclo.isEstadoRetiradoCic()) {
                    alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionInicio());
                    alumnoCiclo.setSituacionFinal(alumnoCicloAnterior.getSituacionInicio());
                    //alumnoCicloDAO.updateSituacionInicioFinal(alumnoCiclo);
                }
            }
            List<AlumnoCicloCurso> alumnosCiclosCursosByCiclo = TypesUtil.getListNotNull(mapAlumnoCicloCurso.get(alumnoCiclo.getId()));
            List<AlumnoCicloCurso> alumnosCiclosCursosAnalizados = this.analizedAlumnoCicloCursosByCiclo(alumnosCiclosCursosByCiclo);
            if (alumnosCiclosCursosAnalizados.isEmpty()) {
                //Long count = alumnoCicloCursoDAO.countByAlumnoCiclo(alumnoCiclo);
                int count = alumnosCiclosCursosByCiclo.size();
                if (count == 0 && alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.NMAT) {
                    alumnoCicloDAO.deleteById(alumnoCiclo);
                    logger.debug("Se remueve el alumnoCiclo {}", alumnoCiclo.getId());
                    eliminables.add(alumnoCiclo);
                    //alumnoCiclos.remove(alumnoCiclo);
                    if (idx > 0) {
                        idx++;
                    }
                    continue;
                }
            }

            //logger.debug("Ciclo {}, Cursos {}", alumnoCiclo.getCicloAcademico().getCodigo(), alumnosCiclosCursosAnalizados.size());
            List<AlumnoCicloCurso> rci = alumnosCiclosCursosAnalizados.stream().filter(x -> x.getIsEstadoRCI()).collect(Collectors.toList());
            List<AlumnoCicloCurso> rcu = alumnosCiclosCursosAnalizados.stream().filter(x -> x.getIsEstadoRCU()).collect(Collectors.toList());
            List<AlumnoCicloCurso> mat = alumnosCiclosCursosAnalizados.stream().filter(x -> x.getIsEstadoMatriculado()).collect(Collectors.toList());
            List<AlumnoCicloCurso> ret = alumnosCiclosCursosAnalizados.stream().filter(x -> x.getIsEstadoRET()).collect(Collectors.toList());
            EstadoMatriculaEnum estadoMatriculaEnum = EstadoMatriculaEnum.MAT;
            if (rcu.size() == alumnosCiclosCursosAnalizados.size() || ret.size() == alumnosCiclosCursosAnalizados.size()) {
                estadoMatriculaEnum = EstadoMatriculaEnum.RCI;
            }
            if (!rci.isEmpty()) {
                estadoMatriculaEnum = EstadoMatriculaEnum.RCI;
            }
            if (alumno.isPostgrado()) {
                alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
                alumnoCiclo.setSituacionFinal(alumnoCicloAnterior.getSituacionFinal());
            }
            alumnoCiclo.setEstadoEnum(estadoMatriculaEnum);
            //alumnoCicloDAO.update(alumnoCiclo);

            //logger.debug("Situación Inicio {}", ObjectUtil.getParent(alumnoCiclo, "situacionInicio.codigo"));
            //logger.debug("Situación Final {}", ObjectUtil.getParent(alumnoCiclo, "situacionFinal.codigo"));
            idx++;
            alumnoCicloAnterior = (AlumnoCiclo) alumnoCiclo.clone();
        }

        for (AlumnoCiclo eliminar : eliminables) {
            alumnoCiclos.remove(eliminar);
        }
    }

    private void evaluarPrimerCiclo(Alumno alumno, AlumnoCiclo alumnoCiclo) {
        SituacionAcademica situacionN = new SituacionAcademica(SituacionAcademicaEnum.S_N);
        SituacionAcademica situacion8 = new SituacionAcademica(SituacionAcademicaEnum.S_8);
        SituacionAcademica situacion9 = new SituacionAcademica(SituacionAcademicaEnum.S_9);
        SituacionAcademica situacionQ = new SituacionAcademica(SituacionAcademicaEnum.S_Q);
        SituacionAcademica situacion7 = new SituacionAcademica(SituacionAcademicaEnum.S_7);

        if (alumno.isPregrado()) {
            if (alumno.isQuintoSecundaria()) {
                int yearInicio = alumno.getCicloIngreso().getYear();
                int yearCicloHistorial = alumnoCiclo.getCicloAcademico().getYear();
                int diffYear = yearCicloHistorial - yearInicio;
                if (diffYear == 1) {
                    alumnoCiclo.setSituacionInicio(situacionQ);
                    alumnoCiclo.setSituacionFinal(situacion8);
                }
                if (diffYear == 2) {
                    alumnoCiclo.setSituacionInicio(situacion9);
                    alumnoCiclo.setSituacionFinal(situacionN);
                }
                if (diffYear >= 3) {
                    alumnoCiclo.setSituacionInicio(situacion7);
                    alumnoCiclo.setSituacionFinal(situacionN);
                }

            } else if (alumnoCiclo.getCicloAcademico().equals(alumno.getCicloIngreso())) {
                alumnoCiclo.setSituacionInicio(situacion8);
                if (alumnoCiclo.isEstadoRetiradoCic()) {
                    alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
                }
                //alumnoCicloDAO.updateSituacionInicioFinal(alumnoCiclo);
            } else {
                alumnoCiclo.setSituacionInicio(situacion9);
                if (alumnoCiclo.isEstadoRetiradoCic()) {
                    alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
                }
                //alumnoCicloDAO.updateSituacionInicioFinal(alumnoCiclo);
            }
        } else {
            //EPG
            alumnoCiclo.setSituacionInicio(situacionN);
            alumnoCiclo.setSituacionFinal(situacionN);
            //  alumnoCicloAnterior = (AlumnoCiclo) alumnoCiclo.clone();
            //alumnoCicloDAO.updateSituacionInicioFinal(alumnoCiclo);
        }
    }

    private void analizarEgresado(Alumno alumno, List<AlumnoCiclo> alumnoCiclos, DataSessionPivot ds) {
        Egresado egresado = egresadoDAO.findPrincipalByAlumno(alumno);
        if (egresado != null && egresado.getCicloAcademico() != null) {

            SituacionAcademica situacionAcademicaEM = new SituacionAcademica(SituacionAcademicaEnum.S_E);

            //AlumnoCiclo alumnoCicloEgresado = alumnoCicloDAO.findByAlumnoCiclo(alumno, alumno.getCicloActivo());
            AlumnoCiclo alumnoCicloEgresado = findAlumnoCiclo(alumnoCiclos, alumno.getCicloActivo());
//            AlumnoCiclo alumnoCicloEgresadoPrevio = (AlumnoCiclo) alumnoCicloEgresado.clone();
            alumnoCicloEgresado.setSituacionFinal(situacionAcademicaEM);
//            boolean sonIguales = ObjectUtil.equalsAttrs(alumnoCicloEgresadoPrevio, alumnoCicloEgresado, Arrays.asList("situacionFinal.id"));
//            if (!sonIguales) {
//                alumnoCicloDAO.updateColumns(alumnoCicloEgresado, "situacionFinal");
//            }

            //AlumnoCiclo alumnoCicloActiveAntrior = alumnoCicloDAO.findActiveAnteriorByAlumno(alumno, egresado.getCicloAcademico());
            AlumnoCiclo alumnoCicloActiveAntrior = findAlumnoCicloActiveAnterior(alumnoCiclos, egresado.getCicloAcademico());
//            AlumnoCiclo alumnoCicloActiveAntriorPrevio = (AlumnoCiclo) alumnoCicloActiveAntrior.clone();
            if (alumnoCicloActiveAntrior != null) {
                alumnoCicloActiveAntrior.setSituacionFinal(situacionAcademicaEM);
//                sonIguales = ObjectUtil.equalsAttrs(alumnoCicloActiveAntriorPrevio, alumnoCicloActiveAntrior, Arrays.asList("situacionFinal.id"));
//                if (!sonIguales) {
//                    alumnoCicloDAO.updateColumns(alumnoCicloActiveAntrior, "situacionFinal");
//                }
            }

            alumno.setSituacionAcademica(situacionAcademicaEM);
//            Alumno alumnoPrevio = (Alumno) alumno.clone();
//            sonIguales = ObjectUtil.equalsAttrs(alumnoPrevio, alumno, Arrays.asList("situacionAcademica.id"));
//            if (!sonIguales) {
//                alumnoDAO.updateSituacionAcad(alumno);
//            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void calulcarSituacionAcademica(Alumno alumno, DataSessionPivot ds) {
//        contadorComponent.iniciar(1);
        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }
        alumno = alumnoDAO.findAllInfo(alumno.getId());
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio().getOperativeModalidadEnum());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(alumno);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        List<Reincorporacion> reincorporacionesByAlumno = reincorporacionDAO.allByEstadoTramiteAndAlumnoRei(
                alumno, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
        this.promediarAllCicloSync(alumno, cicloActivo, cicloAcademicoDAO.all(), alumnoCiclos, alumnoCicloCursos, reincorporacionesByAlumno, ds);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void calulcarSituacionAcademicaNewSession(Alumno alumno, DataSessionPivot ds) {
        long t1 = System.currentTimeMillis();
        this.calulcarSituacionAcademica(alumno, ds);
        visorCalculaSituacion.incrementar();
        long t2 = System.currentTimeMillis() - t1;

        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");
        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");
        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");
        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");
        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");
        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");
        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");
        System.out.println(visorCalculaSituacion.reporte() + " en " + t2 + " mseg");

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void promediarAlumno(
            Alumno alumno,
            //List<CicloAcademico> ciclosAll,
            Map<String, List<CicloAcademico>> mapCiclo,
            CicloAcademico cicloActivo,
            List<AlumnoCiclo> alumnosCiclosByAlumno,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            DataSessionPivot ds) {

        if (alumnosCiclosByAlumno.isEmpty()) {
            return;
        }

        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", allOperativesByModalidadEstudio);

        Collections.sort(alumnosCiclosByAlumno, new AlumnoCiclo.CompareCicloAsc());
        Integer cicloInicio = alumnosCiclosByAlumno.get(0).getCicloAcademico().getCodigoInt() - 100;

        for (;;) {
            AlumnoCiclo alumnoCicloEach = findSiguienteAlumnoCiclo(alumnosCiclosByAlumno, cicloInicio);
            if (alumnoCicloEach == null) {
                break;
            }

            CicloAcademico ciclo = alumnoCicloEach.getCicloAcademico();
            logger.debug("\tCalc promedios ciclo {} {} {} ", ciclo.getCodigo(), ciclo.getYear(), ciclo.getNumeroCiclo());

            List<AlumnoCicloCurso> alumnoCicloCursoByCiclo = allOperativesByModalidadEstudio.stream().
                    filter(x -> x.getAlumnoCiclo().getCicloAcademico().equals(alumnoCicloEach.getCicloAcademico()))
                    .collect(Collectors.toList());

            List<AlumnoCicloCurso> alumnoCicloCursoAnteriores = allOperativesByModalidadEstudio.stream()
                    .filter(x -> x.getAlumnoCiclo().getCicloAcademico().getCodigoInt() < ciclo.getCodigoInt())
                    .collect(Collectors.toList());

            this.promediarHistorialNotas(alumno, mapCiclo, cicloActivo, ciclo, ds, alumnosCiclosByAlumno,
                    mapAlumnoCicloCurso, alumnoCicloCursoByCiclo, alumnoCicloCursoAnteriores);

            cicloInicio = ciclo.getCodigoInt();
        }

    }

    private AlumnoCiclo findSiguienteAlumnoCiclo(List<AlumnoCiclo> alumnosCiclosByAlumno, int codigoCicloActual) {
        for (AlumnoCiclo ac : alumnosCiclosByAlumno) {
            CicloAcademico ciclo = ac.getCicloAcademico();
            if (ciclo.getCodigoInt() > codigoCicloActual) {
                return ac;
            }
        }
        return null;
    }

    private SituacionAcademica calculateSitutacionAcadFinal(
            Alumno alumno,
            AlumnoCiclo alumnoCiclo,
            SituacionAcademica situacionInicial,
            Integer ciclosEstudiados,
            AlumnoCiclo alumnoCicloInhaAnterior) {

        List<SituacionAcademicaEnum> basicosRegulares = Arrays.asList(S_N, S_8, S_9);
        List<SituacionAcademicaEnum> basicosSuspendidos = Arrays.asList(S_T, S_6);

        logger.debug("sit.inicio={}, aprobado={}, ingreso={}, ciclosEstudiados={}", alumnoCiclo.getSituacionInicio().getCodigo(), alumnoCiclo.isAprobado(),
                ObjectUtil.getParentTree(alumno, "cicloIngreso.codigo"), ciclosEstudiados);

        SituacionAcademica situacionAcademicaFinal = null;
        if (alumnoCiclo.getCicloAcademico().isTipoNivelacion()) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
            logger.debug("caso 01");

        } else if (alumnoCiclo.isTrikaSeparado()) {
            situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_4T);
            logger.debug("caso 15");

        } else if (basicosRegulares.contains(alumnoCiclo.getSituacionInicio().getCodigoEnum()) && alumnoCiclo.isAprobado()) {
            situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
            logger.debug("caso 02");

        } else if (alumnoCiclo.getSituacionInicio().isCodigoS5() && alumnoCiclo.isAprobado()) {
            situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_5);
            logger.debug("caso 03");

        } else if ((ciclosEstudiados < 2) && alumno.isPregrado()) {
            if (alumnoCiclo.isAprobado()) {
                situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                logger.debug("caso 04");
            } else if (TypesUtil.getInt(alumno.getCicloIngreso().getCodigoInt()) >= 201710) {
                situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                logger.debug("caso 05");
            } else if (ciclosEstudiados == 0) {
                situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                logger.debug("caso 06");
            } else {
                situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_1);
                logger.debug("caso 07");
            }

        } else if (alumnoCiclo.getSituacionInicio().isCodigoS4()) {
            if (alumnoCiclo.isAprobado()) {
                situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_5);
                logger.debug("caso 08");

            } else {
                situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_X);
                logger.debug("caso 09");
            }

        } else if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.INH
                && basicosSuspendidos.contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionFinal();
            logger.debug("caso 10");

        } else if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.INH
                && !basicosSuspendidos.contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
            logger.debug("caso 11");

        } else if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT && !alumnoCiclo.isAprobado()
                && alumnoCiclo.getSituacionInicio().getCodigoEnum() == S_1) {
            situacionAcademicaFinal = new SituacionAcademica(S_6);

        } else if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT && alumnoCiclo.isAprobado()
                && alumnoCiclo.getSituacionInicio().getCodigoEnum() == S_1) {
            situacionAcademicaFinal = new SituacionAcademica(S_N);

        } else if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT && !alumnoCiclo.isAprobado()
                && alumnoCiclo.getSituacionInicio().getCodigoEnum() == S_3) {
            situacionAcademicaFinal = new SituacionAcademica(S_4);

        } else if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT && !alumnoCiclo.isAprobado()
                && alumnoCiclo.getSituacionInicio().getCodigoEnum() == S_6) {
            situacionAcademicaFinal = new SituacionAcademica(S_4);

        } else {
            logger.debug("caso 12");
            situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCiclo, alumnoCiclo.getSituacionInicio(), -1, alumnoCiclo.getCreditosAprobadosAcumulados(), alumnoCiclo.getCicloAcademico());

        }

        if (alumnoCicloInhaAnterior != null && alumnoCicloInhaAnterior.getSituacionFinal().isTrikeado()) {
            situacionAcademicaFinal = situacionInicial;
            if (alumnoCiclo.isAprobado()) {
                if (situacionAcademicaFinal.isCodigoS6()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                    logger.debug("caso 13");
                }
            } else {
                SituacionAcademica situacionSeparado = new SituacionAcademica(SituacionAcademicaEnum.S_4);
                situacionAcademicaFinal = situacionSeparado;
                logger.debug("caso 14");
            }
        }

        if (situacionAcademicaFinal == null) {
            logger.debug("\tProblemas en calculo situacion academica final:");
            logger.debug("\tSituacion inicio:{} aprobado:{}, capa:{}",
                    alumnoCiclo.getSituacionInicio().getCodigo(),
                    alumnoCiclo.isAprobado(),
                    alumnoCiclo.getCreditosAprobadosAcumulados());
        }

        return situacionAcademicaFinal;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void generarHistorialNotas(Alumno alumno,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds) {

        try {
            logger.debug("generar historial notas, alumno {} ciclo {}", alumno.getId(), cicloAcademico.getId());
            List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(alumno);
            AlumnoCiclo alumnoCicloAnterior = findAlumnoCicloActiveAnterior(alumnoCiclos, cicloAcademico);

            List<CicloAcademico> ciclosAll = cicloAcademicoDAO.all();
            CicloAcademico cicloActivo = findCicloActivoByModalidad(alumno.getModalidadEstudio(), ciclosAll);
            AlumnoCiclo alumnoCiclo = findAlumnoCiclo(alumnoCiclos, cicloAcademico);

            List<AlumnoCicloCurso> alumnoCiclosCursosAll = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", alumnoCiclosCursosAll);
            List<AlumnoCicloCurso> alumnoCicloCursosActual = new ArrayList();
            if (alumnoCiclo != null) {
                alumnoCicloCursosActual = TypesUtil.getListNotNull(mapAlumnoCicloCurso.get(alumnoCiclo.getId()));
            }

            AlumnoCicloCurso alumnoCicloCurso = null;
            for (AlumnoCicloCurso acc : alumnoCicloCursosActual) {
                Curso cursoCiclo = acc.getCurso();
                if (cursoCiclo.getId().longValue() == curso.getId()) {
                    alumnoCicloCurso = acc;
                    break;
                }
            }

            if (alumnoCiclo == null) {
                alumnoCiclo = new AlumnoCiclo();
                alumnoCiclo.defaultValuesToCreate(alumno, cicloAcademico, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                alumnoCiclo.setEstadoEnum(matriculaCurso.getMatriculaResumen().getEstadoEnum());
                SituacionAcademica situacionInicio = alumnoCicloAnterior == null ? alumno.getSituacionAcademica() : alumnoCicloAnterior.getSituacionFinal();
                alumnoCiclo.setSituacionInicio(situacionInicio);
                if (alumnoCiclo.isEstadoRetiradoCic() || alumnoCiclo.isNoMatriculado()) {
                    alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
                }
                alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
                alumnoCicloDAO.save(alumnoCiclo);
                alumno.getId();
                alumnoCiclos.add(alumnoCiclo);
                mapAlumnoCicloCurso.put(alumnoCiclo.getId(), alumnoCicloCursosActual);

            } else if (alumnoCiclo.isNoMatriculado() || alumnoCiclo.isEstadoRetiradoCic()) {
                SituacionAcademica situacionInicio = alumnoCicloAnterior == null ? alumno.getSituacionAcademica() : alumnoCicloAnterior.getSituacionFinal();
                alumnoCiclo.setSituacionInicio(situacionInicio);
                alumnoCiclo.setSituacionFinal(situacionInicio);
                //alumnoCicloDAO.update(alumnoCiclo);
                alumno.getId();
            }

            if (alumnoCicloCurso == null) {
                alumnoCicloCurso = new AlumnoCicloCurso();
                alumnoCicloCurso.defaultValuesToCreate(alumnoCiclo, curso, matriculaCurso, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
                alumnoCicloCurso.setEstaAprobado(aprobado);
                alumnoCicloCurso.setVecesCursado(BigDecimal.ONE.intValue());
                alumnoCicloCursoDAO.save(alumnoCicloCurso);
                alumnoCicloCurso.getId();
                alumnoCicloCursosActual.add(alumnoCicloCurso);

            } else {
                // if (!alumnoCicloCurso.getNota().equals(matriculaCurso.getNotaFinal())) {
                alumnoCicloCurso.setFechaModificacion(ds.getFechaAccionAudit());
                alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
                alumnoCicloCurso.setEstado(matriculaCurso.getEstadoEnum());
                alumnoCicloCurso.setUserModificacion(ds.getUsuario());
                Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
                alumnoCicloCurso.setEstaAprobado(aprobado);

                alumnoCicloCursoDAO.update(alumnoCicloCurso);
                alumnoCicloCurso.getId();
                // }
            }

            List<AlumnoCicloCurso> alumnoCicloCursoAnteriores = alumnoCiclosCursosAll.stream()
                    .filter(x -> x.getAlumnoCiclo().getCicloAcademico().getCodigoInt() < cicloAcademico.getCodigoInt())
                    .collect(Collectors.toList());

            Map<String, List<CicloAcademico>> mapCiclo = TypesUtil.convertListToMapList("codigo", ciclosAll);
            this.promediarHistorialNotas(alumno, mapCiclo, cicloActivo, cicloAcademico, ds, alumnoCiclos,
                    mapAlumnoCicloCurso, alumnoCicloCursosActual, alumnoCicloCursoAnteriores);
            //this.promediarHistorialNotas(alumno, ciclosAll, cicloActivo, cicloAcademico, ds);

        } catch (Exception e) {

            String excepcion = this.messageException(e);

            logger.error("####Error en el hilo alumno " + alumno.getCodigo()
                    + " ciclo " + cicloAcademico.getId());//, e 
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    private void generarHistorialNotas2(Alumno alumno,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            List<MatriculaCurso> matriculasCursosByAlumno,
            DataSessionPivot ds) {
        try {
            //  logger.debug("generar historial notas, alumno {} ciclo {}", alumno.getId(), cicloAcademico.getId());
            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);
            DateTime today = new DateTime(ds.getFechaAccionAudit());

            if (alumnoCiclo == null) {
                SituacionAcademica situacionAcademicaComodin = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_00.getValue());
                alumnoCiclo = new AlumnoCiclo();
                alumnoCiclo.defaultValuesToCreate(alumno, cicloAcademico, ds.getUsuario(), today);
                alumnoCiclo.setEstadoEnum(matriculaCurso.getMatriculaResumen().getEstadoEnum());
                alumnoCiclo.setSituacionInicio(situacionAcademicaComodin);
                alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
                alumnoCiclo.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                alumnoCicloDAO.save(alumnoCiclo);
                alumnoCiclo.getId();
            } else {
                AlumnoCiclo alumnoCicloUpd = new AlumnoCiclo(alumnoCiclo.getId());
                alumnoCicloUpd.setEstadoEnum(EstadoMatriculaEnum.MAT);
                alumnoCicloDAO.updateColumns(alumnoCicloUpd, "estado");
            }

            if (alumnoCicloCurso == null) {
                alumnoCicloCurso = new AlumnoCicloCurso();
                alumnoCicloCurso.defaultValuesToCreate(alumnoCiclo, curso, matriculaCurso, ds.getUsuario(), today);
                Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
                alumnoCicloCurso.setEstaAprobado(aprobado);

                //  alumnoCicloCurso.setVecesCursado(alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCiclo(curso, alumno, cicloAcademico).intValue() + 1);
                alumnoCicloCurso.setVecesCursado(this.countVecesAnteriores(matriculasCursosByAlumno, cicloAcademico, curso) + 1);
                alumnoCicloCurso.setVecesCursadoRegular(this.countVecesAnterioresReg(matriculasCursosByAlumno, cicloAcademico, curso) + 1);
                alumnoCicloCursoDAO.save(alumnoCicloCurso);
                alumnoCicloCurso.getId();
            } else {
                alumnoCicloCurso.setFechaModificacion(today.toDate());
                alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
                alumnoCicloCurso.setEstado(matriculaCurso.getEstadoEnum());
                alumnoCicloCurso.setUserModificacion(ds.getUsuario());
                if (curso.isTieneCreditosVariables()) {
                    alumnoCicloCurso.setCreditos(matriculaCurso.getCreditosAprobados());
                } else {
                    alumnoCicloCurso.setCreditos(matriculaCurso.getCreditos());
                }
                Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
                alumnoCicloCurso.setEstaAprobado(aprobado);
                alumnoCicloCurso.setVecesCursado(this.countVecesAnteriores(matriculasCursosByAlumno, cicloAcademico, curso) + 1);
                alumnoCicloCurso.setVecesCursadoRegular(this.countVecesAnterioresReg(matriculasCursosByAlumno, cicloAcademico, curso) + 1);
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
                alumnoCicloCurso.getId();
            }
            //  this.promediarHistorialNotas2(alumno, cicloAcademico, matriculasCursosByAlumno, usuario, today);

        } catch (Exception e) {
            String excepcion = this.messageException(e);
            String error = "####Error en el hilo alumno " + alumno.getId()
                    + " ciclo " + cicloAcademico.getId();
            logger.error(error);//, e 
            visorCalculoNotas.agregarError(error);
            //LoggerAccionEnum.RECAUDA_DEUDA_HANDLE_MESSAGE;

            auditorService.auditTrasladoNotasToHistorial(alumno, curso, cicloAcademico, matriculaCurso, ds, e);
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private Integer countVecesAnteriores(List<MatriculaCurso> matriculasCursosAlumno, CicloAcademico cicloAcademico, Curso curso) {
        List<MatriculaCurso> matriculasCursosAnterioresByCurso = matriculasCursosAlumno.stream().filter(
                x -> (x.getMatriculaResumen().getCicloAcademico().getCodigoInt() < cicloAcademico.getCodigoInt()
                && x.getCurso().equals(curso))
                && x.getMatriculaResumen().getCicloAcademico().isTipoRegular()
                && x.isEstadoMAT())
                .collect(Collectors.toList());
        return matriculasCursosAnterioresByCurso.size();
    }

    private Integer countVecesAnterioresReg(List<MatriculaCurso> matriculasCursosAlumno, CicloAcademico cicloAcademico, Curso curso) {
        List<MatriculaCurso> matriculasCursosAnterioresByCurso = matriculasCursosAlumno.stream().filter(
                x -> (x.getMatriculaResumen().getCicloAcademico().getCodigoInt() < cicloAcademico.getCodigoInt()
                && x.getCurso().equals(curso))
                && x.getMatriculaResumen().getCicloAcademico().isTipoRegular()
                && x.isEstadoMAT())
                .collect(Collectors.toList());
        return matriculasCursosAnterioresByCurso.size();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void analizarDesertor(
            Alumno alumno,
            CicloAcademico cicloActivo,
            List<AlumnoCiclo> alumnosCiclos,
            List<CicloAcademico> ciclosAll,
            Map<String, List<CicloAcademico>> mapCiclo,
            DataSessionPivot ds) {

        List<AlumnoCiclo> alumnosCiclosByAlumno = new ArrayList();
        alumnosCiclosByAlumno.addAll(alumnosCiclos);
        Collections.sort(alumnosCiclosByAlumno, new AlumnoCiclo.CompareCicloAsc());

        List<String> ciclosStr = alumnosCiclosByAlumno.stream().map(x -> x.getCicloAcademico().getCodigo() + " " + x.getEstado()).collect(Collectors.toList());
        logger.debug(String.join(",", ciclosStr));

        List<CicloAcademico> cicloRegularesByModalidad
                = this.allCiclosRegularesByModalidadEstudio(alumno.getModalidadEstudio().getOperativeModalidadEnum(), ciclosAll, cicloActivo);

        if (!alumnosCiclosByAlumno.isEmpty()) {
            //Map<String, List<CicloAcademico>> mapCiclo = TypesUtil.convertListToMapList("codigo", ciclos);
            for (AlumnoCiclo alumnoCiclo : alumnosCiclosByAlumno) {
                this.analizarDesertorByCiclo(alumno, alumnosCiclos, cicloActivo, alumnoCiclo.getCicloAcademico(), mapCiclo, ds);
            }

        } else if (!alumno.getSituacionAcademica().isCodigoS7()) {
            if (alumno.getCicloIngreso() != null) {
                int ciclosNmat = cicloRegularesByModalidad.stream().filter(x -> x.getCodigoInt() >= alumno.getCicloIngreso().getCodigoInt())
                        .collect(Collectors.toList())
                        .size();
                int diffYears = cicloActivo.getYear() - alumno.getCicloIngreso().getYear();
                if (alumno.isQuintoSecundaria()) {
                    if (diffYears >= 1) {
                        if (alumno.getSituacionAcademica().getId().longValue() != SituacionAcademicaEnum.S_8.getId()) {
                            alumno.setSituacionAcademica(new SituacionAcademica(SituacionAcademicaEnum.S_8));
                            //alumnoDAO.updateSituacionAcad(alumno);
                        }
                    }
                    if (diffYears == 1) {
                        return;
                    }
                    ciclosNmat = cicloRegularesByModalidad.stream()
                            .filter(x -> x.getCodigoInt() >= alumno.getCicloIngreso().getCodigoInt())
                            .filter(x -> x.getYear() != (cicloActivo.getYear() + 1))
                            .collect(Collectors.toList())
                            .size();
                }
                if (alumno.getSituacionAcademica().isCodigoS8() || alumno.getSituacionAcademica().isCodigoN()) {
                    SituacionAcademica situacion = null;
                    if (ciclosNmat == 1) {
                        situacion = new SituacionAcademica(SituacionAcademicaEnum.S_9);
                    }
                    if (ciclosNmat > 1) {
                        situacion = new SituacionAcademica(SituacionAcademicaEnum.S_7);
                    }
                    if (situacion != null) {
                        if (alumno.getSituacionAcademica().getId().longValue() != situacion.getId()) {
                            alumno.setSituacionAcademica(situacion);
                            //alumnoDAO.updateSituacionAcad(alumno);
                        }

                    }
                }
            }
        }
    }

    private List<CicloAcademico> allCiclosRegularesByModalidadEstudio(ModalidadEstudioEnum modalidadEstudioEnum, List<CicloAcademico> ciclosAll, CicloAcademico cicloActivo) {
//        ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(modalidadEstudioAlumno.getCodigo());
//        if (modalidadEstudioAlumno.getIsEspecial()) {
//            modalidadEstudioEnum = ModalidadEstudioEnum.EPG;
//        }
//        if (modalidadEstudioAlumno.getIsVisitante()) {
//            modalidadEstudioEnum = ModalidadEstudioEnum.PRE;
//        }
        final ModalidadEstudioEnum fModalidadEstudioEnum = modalidadEstudioEnum;
        List<CicloAcademico> ciclosByModalidad = ciclosAll.stream()
                .filter(x -> x.getModalidadEstudio().getCodigo().equals(fModalidadEstudioEnum.name()))
                .filter(x -> x.isTipoRegular())
                .filter(x -> x.getCodigoInt() < cicloActivo.getCodigoInt())
                .collect(Collectors.toList());
        return ciclosByModalidad;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void analizarDesertorByCiclo(
            Alumno alumno,
            List<AlumnoCiclo> alumnoCiclos,
            CicloAcademico cicloActivo,
            CicloAcademico cicloAcademico,
            Map<String, List<CicloAcademico>> mapCiclo,
            DataSessionPivot ds) {

//        logger.debug("$$$$$$$$$$$$$$ analizar desertor, Alumno {}, Ciclo Code {} Id {}, Ciclo Activo Code {} Id {}",
//                alumno.getId(),
//                cicloAcademico.getCodigo(),
//                cicloAcademico.getId(),
//                cicloActivo.getCodigo(),
//                cicloActivo.getId());
        ModalidadEstudioEnum modalidadEstudio = ModalidadEstudioEnum.valueOf(alumno.getModalidadEstudio().getCodigo());
        //CicloAcademico siguienteCicloReg = cicloAcademicoDAO.findSiguienteRegularActivo(cicloAcademico, modalidadEstudio);
        CicloAcademico siguienteCicloReg = findCicloSiguienteRegularActivo(cicloAcademico, modalidadEstudio, mapCiclo);

        //AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCiclo alumnoCiclo = findAlumnoCiclo(alumnoCiclos, cicloAcademico);
        //AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloAnterior = findAlumnoCicloAnterior(alumnoCiclos, cicloAcademico);
        //AlumnoCiclo alumnoCicloSiguienteActive = alumnoCicloDAO.findActiveSiguienteByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloSiguienteActive = findAlumnoCicloActiveSiguiente(alumnoCiclos, cicloAcademico);
        //AlumnoCiclo alumnoCicloLastMat = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);
        AlumnoCiclo alumnoCicloLastMat = findAlumnoCicloActiveRegularUltimo(alumnoCiclos);

        //AlumnoCiclo alumnoCicloCorrespSgtRegular = alumnoCicloDAO.findByAlumnoCiclo(alumno, siguienteCicloReg);
        AlumnoCiclo alumnoCicloCorrespSgtRegular = findAlumnoCiclo(alumnoCiclos, siguienteCicloReg);

        if (alumnoCiclo.isEstadoRetiradoCic() || alumnoCiclo.isNoMatriculado()) {
            if (alumnoCicloAnterior != null) {
                alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
                alumnoCiclo.setSituacionFinal(alumnoCicloAnterior.getSituacionFinal());
            } else {
                alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
            }
            //alumnoCicloDAO.update(alumnoCiclo);
        }

        if (alumnoCiclo.getSituacionFinal().isDesertor() || alumnoCiclo.getSituacionFinal().isCodigoS4()
                || alumnoCiclo.getSituacionFinal().isCodigoS4U() || alumnoCiclo.getSituacionFinal().isCodigoEM()) {
            return;
        }

        if (alumnoCiclo.isNoMatriculado() || alumnoCiclo.isEstadoRetiradoCic()) {
            if (this.evaluarNoMatriculadoOrRetiradoCic(alumno, cicloAcademico, alumnoCiclo, alumnoCicloSiguienteActive, alumnoCicloLastMat, ds)) {
                return;
            }
        }

        if (alumnoCicloCorrespSgtRegular == null && siguienteCicloReg != null) {
            if (siguienteCicloReg.getCodigoInt() < cicloActivo.getCodigoInt()) {
                if (alumnoCicloCorrespSgtRegular == null) {
                    alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                    alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCicloReg, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                    //       alumnoCicloCorrespSgtRegular.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
                    //     alumnoCicloCorrespSgtRegular.setSituacionFinal(alumnoCicloAnterior.getSituacionFinal());
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(alumnoCiclo.getSituacionFinal());
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(alumnoCiclo.getSituacionFinal());
                    alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                    //alumnoCicloDAO.save(alumnoCicloCorrespSgtRegular);
                    alumnoCiclos.add(alumnoCicloCorrespSgtRegular);

                    // logger.debug("Creado alumno ciclo nmat para el ciclo {}", siguienteCicloReg.getCodigo());
                    analizarDesertorByCiclo(alumno, alumnoCiclos, cicloActivo, siguienteCicloReg, mapCiclo, ds);
                }
            }
        }

    }

    private CicloAcademico findCicloSiguienteRegularActivo(
            CicloAcademico ciclo,
            ModalidadEstudioEnum modalidadEnum,
            Map<String, List<CicloAcademico>> mapCiclo) {

        if (modalidadEnum == ModalidadEstudioEnum.ESP) {
            modalidadEnum = ModalidadEstudioEnum.EPG;
        }
        if (modalidadEnum == ModalidadEstudioEnum.VIS) {
            modalidadEnum = ModalidadEstudioEnum.PRE;
        }

        Integer year = ciclo.getYear();
        String nroCiclo = ciclo.getNumeroCiclo();
        if (nroCiclo.equals("2")) {
            year++;
            nroCiclo = "10";
        } else if (nroCiclo.equals("1.5")) {
            nroCiclo = "20";
        } else {
            nroCiclo = "20";
        }

        List<CicloAcademicoEstadoEnum> validosAnalisis = Arrays.asList(CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);

        String codeSgte = year + nroCiclo;
        //logger.debug("sgte ciclo de {} es {}", ciclo.getCodigo(), codeSgte);
        List<CicloAcademico> ciclosCode = TypesUtil.getListNotNull(mapCiclo.get(codeSgte));
        for (CicloAcademico ca : ciclosCode) {
            if (!validosAnalisis.contains(ca.getEstadoEnum())) {
                continue;
            }
            ModalidadEstudioEnum modaEnum = ca.getModalidadEstudio().getCodigoEnum();
            if (modaEnum == modalidadEnum) {
                return ca;
            }
        }
        return null;
    }

    private CicloAcademico findCicloActivoByModalidad(ModalidadEstudio modalidad, List<CicloAcademico> ciclosAll) {
        for (CicloAcademico ciclo : ciclosAll) {
            if (ciclo.getModalidadEstudio().getId() == modalidad.getId().longValue()
                    && ciclo.getEstadoEnum() == CicloAcademicoEstadoEnum.ACT) {
                return ciclo;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCiclo(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        if (ciclo == null) {
            return null;
        }
        for (AlumnoCiclo ac : alumnoCiclos) {
            if (ac.getCicloAcademico().getId() == ciclo.getId().longValue()) {
                return ac;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloAnterior(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloDesc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigo().compareTo(ciclo.getCodigo()) < 0) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloActiveAnterior(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloDesc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigo().compareTo(ciclo.getCodigo()) < 0 && alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloActiveSiguiente(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloAsc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigo().compareTo(ciclo.getCodigo()) > 0 && alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloActiveRegularUltimo(List<AlumnoCiclo> alumnoCiclos) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloDesc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getTipoEnum() == TipoCicloEnum.REG && alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloAnteriorINH(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloDesc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigo().compareTo(ciclo.getCodigo()) < 0 && alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.INH) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloSiguienteINH(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloAsc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigo().compareTo(ciclo.getCodigo()) > 0 && alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.INH) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void promediarHistorialNotas(
            Alumno alumno,
            Map<String, List<CicloAcademico>> mapCiclo,
            CicloAcademico cicloActivo,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds,
            List<AlumnoCiclo> alumnoCiclos,
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso,
            List<AlumnoCicloCurso> alumnosCiclosCursoActual,
            List<AlumnoCicloCurso> alumnosCiclosCursoAnterior) {

        ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(alumno.getModalidadEstudio().getCodigo());
        CicloAcademico siguienteCiclo = findCicloSiguienteRegularActivo(cicloAcademico, modalidadEstudioEnum, mapCiclo);

        AlumnoCiclo alumnoCiclo = findAlumnoCiclo(alumnoCiclos, cicloAcademico);
        AlumnoCiclo alumnoCicloAnteriorActive = findAlumnoCicloActiveAnterior(alumnoCiclos, cicloAcademico);

        AlumnoCiclo alumnoCicloCorrespSgtRegular = findAlumnoCiclo(alumnoCiclos, siguienteCiclo);

        AlumnoCiclo alumnoCicloAnterior = findAlumnoCicloAnterior(alumnoCiclos, cicloAcademico);
        AlumnoCiclo alumnoCicloAnteriorInha = findAlumnoCicloAnteriorINH(alumnoCiclos, cicloAcademico);

        AlumnoCiclo alumnoCicloSiguienteInha = findAlumnoCicloSiguienteINH(alumnoCiclos, cicloAcademico);
        SituacionAcademica situacionAcademicaFinal = null;

        if (alumnoCicloAnteriorActive != null) {
            alumnoCiclo.setSituacionInicio(alumnoCicloAnteriorActive.getSituacionFinal());
        }
        if (alumnoCicloAnteriorInha != null) {
            if (alumnoCicloAnteriorInha.getCicloAcademico().getCodigoInt() > alumnoCicloAnteriorActive.getCicloAcademico().getCodigoInt()) {
                alumnoCiclo.setSituacionInicio(alumnoCicloAnteriorInha.getSituacionFinal());
            }
        }
        if (alumnoCiclo.getSituacionInicio() == null) {
            alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
        }

        final AlumnoCiclo fAlumnoCiclo = (AlumnoCiclo) alumnoCiclo.clone();
        Integer ciclosEstudiados = 0;
        for (AlumnoCiclo ac : alumnoCiclos) {
            CicloAcademico cic = ac.getCicloAcademico();
            if (cic.getTipoEnum() == TipoCicloEnum.REG) {
                if (ac.getEstadoEnum() == EstadoMatriculaEnum.MAT
                        || ac.getEstadoEnum() == EstadoMatriculaEnum.RCI) {
                    if (ac.getCicloAcademico().getCodigoInt() < alumnoCiclo.getCicloAcademico().getCodigoInt()) {
                        ciclosEstudiados++;
                    }
                }
            }
        }

        this.procesarInformacionAlumnoCiclo(ds,
                alumno,
                alumnoCiclo,
                alumnoCicloSiguienteInha,
                alumnosCiclosCursoActual,
                alumnosCiclosCursoAnterior);

        boolean generarTrika = alumnoCiclo.isGenerarTrika();
        logger.debug("\tgenerarTrika en {}: {}, iniciando con {}", cicloAcademico.getCodigo(), generarTrika,
                alumnoCiclo.getSituacionInicio().getCodigo());

        if (alumnoCiclo.getCicloAcademico().isAmnistiado()) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
        } else {
            situacionAcademicaFinal = calculateSitutacionAcadFinal(alumno, alumnoCiclo, alumnoCiclo.getSituacionInicio(), ciclosEstudiados.intValue(), alumnoCicloAnteriorInha);
        }
        alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);

        if (situacionAcademicaFinal != null) {
            logger.debug("\tSituacion inicial:{} final:{} en {} ", alumnoCiclo.getSituacionInicio().getCodigo(),
                    ObjectUtil.getParentTree(situacionAcademicaFinal, "codigo"), cicloAcademico.getCodigo());
        } else {
            logger.debug("\tSituacion inicial:{} final:NADA en {}", alumnoCiclo.getSituacionInicio().getCodigo(), cicloAcademico.getCodigo());
        }

        SituacionAcademica situacionTrika = new SituacionAcademica(SituacionAcademicaEnum.S_T);
        logger.debug("generarTrika={} - sitFinal={}", generarTrika, situacionAcademicaFinal.getCodigo());
        if ((generarTrika && alumnoCiclo.getCicloAcademico().getCodigoInt() >= INI_TRIKA)
                && !(situacionAcademicaFinal.isCodigoX() || situacionAcademicaFinal.isCodigoS4()
                || situacionAcademicaFinal.isCodigoS4U() || situacionAcademicaFinal.isCodigoS4T())) {

            SituacionAcademica situacionFinalForTrika = null;
            situacionFinalForTrika = situacionAcademicaFinal;
            if (situacionAcademicaFinal.isCodigoS6()) {
                if (alumnoCiclo.isUltimoCiclo()) {
                    situacionFinalForTrika = new SituacionAcademica(SituacionAcademicaEnum.S_3U);
                } else {
                    situacionFinalForTrika = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                }
            }

            //logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionTrika.getId(), situacionTrika.getCodigo(), situacionTrika.getNombre());
            if (alumnoCicloCorrespSgtRegular == null) {
                alumnoCiclo.setSituacionFinal(situacionTrika);

                if (siguienteCiclo != null) {
                    alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                    alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCiclo, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionTrika);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForTrika);
                    alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                    alumnoCiclos.add(alumnoCicloCorrespSgtRegular);
                }

            } else {
                List<AlumnoCicloCurso> alusCicloCursos = TypesUtil.getListNotNull(mapAlumnoCicloCurso.get(alumnoCicloCorrespSgtRegular.getId()));
                if (alusCicloCursos.isEmpty()) {
                    alumnoCiclo.setSituacionFinal(situacionTrika);

                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionTrika);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForTrika);
                } else {
                    //logger.debug("No se podra Generara ciclo alumno fantasma trika, por que tiene cursos matriculados");
                    //logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionAcademicaFinal.getId(), situacionAcademicaFinal.getCodigo(), situacionAcademicaFinal.getNombre());
                    alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
                }
            }

        } else if (situacionAcademicaFinal.isCodigoS6() && cicloAcademico.isTipoRegular()) {
            //logger.debug("Generara registro fantasma prueba codigo situacion 3");
            SituacionAcademica situacionFinalForSuspension = null;
            if (!alumnoCiclo.isAprobado()) {
                if (alumnoCiclo.isUltimoCiclo()) {
                    situacionFinalForSuspension = new SituacionAcademica(SituacionAcademicaEnum.S_3U);
                } else {
                    situacionFinalForSuspension = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                }
            } else {
                situacionFinalForSuspension = new SituacionAcademica(SituacionAcademicaEnum.S_N);
            }

            if (alumnoCicloCorrespSgtRegular == null) {
                if (siguienteCiclo != null) {
                    alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                    alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCiclo, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForSuspension);
                    alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                    alumnoCiclos.add(alumnoCicloCorrespSgtRegular);
                }

            } else {
                List<AlumnoCicloCurso> alusCicloCursos = TypesUtil.getListNotNull(mapAlumnoCicloCurso.get(alumnoCicloCorrespSgtRegular.getId()));
                if (alusCicloCursos.isEmpty()) {
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForSuspension);
                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
                }
            }
        }
        alumno.setCicloActivo(alumnoCiclo.getCicloAcademico());
        alumno.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
        alumno.setCreditosCursados(alumnoCiclo.getCreditosAcumulados());
        alumno.setSituacionAcademica(alumnoCiclo.getSituacionFinal());
        alumno.setPromedioAcumulado(alumnoCiclo.getPromedioAcumulado());

    }

    private List<AlumnoCicloCurso> analizedAlumnoCicloCursosByCiclo(List<AlumnoCicloCurso> alumnoCicloCursoByCiclo) {
        Map<String, List<AlumnoCicloCurso>> mapHistoByCiclo = TypesUtil.convertListToMapList("curso.codigo", alumnoCicloCursoByCiclo);

        for (Map.Entry<String, List<AlumnoCicloCurso>> entry : mapHistoByCiclo.entrySet()) {
            String codigoCurso = entry.getKey();
            List<AlumnoCicloCurso> histoByCicloAndCurso = entry.getValue();
            Collections.sort(histoByCicloAndCurso, (p1, p2) -> p1.getFechaRegistro().compareTo(p2.getFechaRegistro()));
            int idx = 0;
            for (AlumnoCicloCurso histo : histoByCicloAndCurso) {
                //logger.debug("curso {}, fecha {}", codigoCurso, TypesUtil.getStringDate(histo.getFechaRegistro(), "dd/MM/yyyy H:mm:ss"));
                boolean registroActivo2 = ((idx + 1) == histoByCicloAndCurso.size());
                Integer registroActivo3 = histo.getRegistroActivo() == 1 && registroActivo2 ? 1 : 0;
                if (histo.getRegistroActivo() != registroActivo3.intValue()) {
                    histo.setRegistroActivo(registroActivo3);
                    alumnoCicloCursoDAO.updateEstadoRegistroActivo(histo);
                }
                idx++;
            }
        }
        alumnoCicloCursoByCiclo = alumnoCicloCursoByCiclo.stream()
                .filter(x -> x.getRegistroActivo() == 1)
                .collect(Collectors.toList());
        return alumnoCicloCursoByCiclo;
    }

    private void procesarInformacionAlumnoCiclo(
            DataSessionPivot ds,
            Alumno alumno,
            AlumnoCiclo alumnoCiclo,
            AlumnoCiclo alumnoCicloSiguienteInha,
            List<AlumnoCicloCurso> alumnosCicloCursoActual,
            List<AlumnoCicloCurso> alumnosCicloCursoAnteriores) {

        //Alumno alumno = alumnoCiclo.getAlumno();
        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;
        boolean generarTrika = false;
        boolean trikaSeparado = false;

        alumnoCiclo.setCreditosCursadosCiclo(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCursosInscritos(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCursosAprobados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAprobadosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());

        for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursoActual) {
            alumnoCiclo.setCreditosCursadosCiclo(alumnoCiclo.getCreditosCursadosCiclo() + alumnoCicloCursoEach.getCreditos());
            alumnoCiclo.setCursosInscritos(alumnoCiclo.getCursosInscritos() + 1);
            alumnoCiclo.setCreditosAcumulados(alumnoCiclo.getCreditosAcumulados() + alumnoCicloCursoEach.getCreditos());
            if (alumnoCicloCursoEach.getIsEstadoMatriculado()) {
                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
            }

            List<AlumnoCicloCurso> vecesLlevado = alumnosCicloCursoAnteriores.stream().filter(
                    x -> x.getCurso().equals(alumnoCicloCursoEach.getCurso())
                    && x.getEstaActivo()
                    && x.getIsEstadoMatriculado()).collect(Collectors.toList());

            Integer vecesEstudiadoCurso = vecesLlevado.size();
            vecesEstudiadoCurso++;
            alumnoCicloCursoEach.setVecesCursado(vecesEstudiadoCurso);

            List<AlumnoCicloCurso> vecesLlevadoRegular = alumnosCicloCursoAnteriores.stream().filter(
                    x -> x.getCurso().equals(alumnoCicloCursoEach.getCurso())
                    && x.getEstaActivo()
                    && x.getIsEstadoMatriculado()
                    && x.getAlumnoCiclo().getCicloAcademico().isTipoRegular()
            ).collect(Collectors.toList());

            alumnoCicloCursoEach.setVecesCursadoRegular(vecesLlevadoRegular.size());
            if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
                alumnoCicloCursoEach.setVecesCursadoRegular(alumnoCicloCursoEach.getVecesCursadoRegular() + 1);
            }

            if (alumnoCicloCursoEach.isAprobado()) {
                alumnoCiclo.setCreditosAprobadosCiclo(alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCicloCursoEach.getCreditos());
                alumnoCiclo.setCursosAprobados(alumnoCiclo.getCursosAprobados() + 1);
                alumnoCiclo.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);
            }

            if (alumnoCicloCursoEach.getVecesCursadoRegular() >= VECES_TRIKA && !alumnoCicloCursoEach.isAprobado()) {
                if (alumno.getModalidadEstudio().isPregrado()) {
                    generarTrika = true;
                    if (alumnoCicloCursoEach.getVecesCursadoRegular() > VECES_TRIKA) {
                        trikaSeparado = true;
                    }
                }
            }
        }

        if (!generarTrika && (alumnoCicloSiguienteInha != null && alumnoCicloSiguienteInha.getSituacionFinal().isTrikeado())) {
            List<AlumnoCicloCurso> alumnoCiclosCursos = alumnoCicloCursoDAO.allStateByAlumnoCiclo(alumnoCicloSiguienteInha);
            if (alumnoCiclosCursos.isEmpty()) {
                alumnoCicloDAO.delete(alumnoCicloSiguienteInha);
            }
        }

        BigDecimal sumNotasCreditosTotal = sumNotasCreditos;
        BigDecimal sumCreditosTotal = sumCreditos;

        for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursoAnteriores) {
            alumnoCiclo.setCreditosAcumulados(alumnoCiclo.getCreditosAcumulados() + alumnoCicloCursoEach.getCreditos());

            if (alumnoCicloCursoEach.isAprobado()) {
                alumnoCiclo.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditosTotal = sumNotasCreditosTotal.add(notaBig.multiply(creditosBig));
                sumCreditosTotal = sumCreditosTotal.add(creditosBig);
            }
        }

        BigDecimal promedio = BigDecimal.ZERO;
        if (sumNotasCreditos.compareTo(BigDecimal.ZERO) != 0 && sumCreditos.compareTo(BigDecimal.ZERO) != 0) {
            promedio = sumNotasCreditos.divide(sumCreditos, 12, RoundingMode.FLOOR); //DOWN, 
        }

        BigDecimal promedioAcumulado = BigDecimal.ZERO;
        if (sumNotasCreditosTotal.compareTo(BigDecimal.ZERO) != 0 && sumCreditosTotal.compareTo(BigDecimal.ZERO) != 0) {
            promedioAcumulado = sumNotasCreditosTotal.divide(sumCreditosTotal, 12, RoundingMode.FLOOR);
        }

        alumnoCiclo.setPromedioCiclo(promedio);
        alumnoCiclo.setPromedioAcumulado(promedioAcumulado);

        if (alumnosCicloCursoActual.size() == BigDecimal.ONE.intValue()) {
            alumnoCiclo.setEstaAprobado(alumnosCicloCursoActual.get(0).getEstaAprobado());
        } else {
            Integer aprobado = evaluateEstaAprobado(promedio, alumno);
            alumnoCiclo.setEstaAprobado(aprobado);
        }
        CicloAcademico ciclo = alumnoCiclo.getCicloAcademico();
        if (ciclo.getCodigoInt() >= 201810 && alumno.isPregrado()) {
            alumnoCiclo.setGenerarTrika(generarTrika);
            alumnoCiclo.setTrikaSeparado(trikaSeparado);
        }
    }

    private boolean evaluarNoMatriculadoOrRetiradoCic(Alumno alumno,
            CicloAcademico cicloAcademico,
            AlumnoCiclo alumnoCiclo,
            AlumnoCiclo alumnoCicloSiguiente,
            AlumnoCiclo alumnoCicloLastMat,
            DataSessionPivot ds) {

        if (Arrays.asList("201620", "201820").contains(cicloAcademico.getCodigo())) {
            logger.debug("");
        }
        SituacionAcademica situacionDesertor = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_D.getValue());
        int MAX_CONSECUTIVOS_NMAT = cicloAcademico.getCodigoInt() <= 201710 ? 2 : 4;

        List<AlumnoCiclo> alumnosCiclosAnteriores = alumnoCicloDAO.allAnterioresEQByCicloAlumno(alumno, cicloAcademico, 20);
        int contadorIntercalado = 0;

        int consecutivos = TypesUtil.getInt(alumnosCiclosAnteriores.stream().limit(MAX_CONSECUTIVOS_NMAT).filter(x -> x.isNoMatriculado()).count());
        boolean matriculadoCicloFuturo = alumnoCicloLastMat != null && alumnoCicloLastMat.getCicloAcademico().getCodigoInt() > alumnoCiclo.getCicloAcademico().getCodigoInt();
        if (consecutivos == MAX_CONSECUTIVOS_NMAT) {
            if (!matriculadoCicloFuturo) {
                alumno.setSituacionAcademica(situacionDesertor);
                alumnoDAO.updateSituacionAcad(alumno);
            }
            alumnoCiclo.setUserModificacion(ds.getUsuario());
            alumnoCiclo.setSituacionFinal(situacionDesertor);
            alumnoCiclo.setFechaModificacion(ds.getFechaAccionAudit());
            alumnoCicloDAO.updateSituacionFinal(alumnoCiclo);
            return true;
        }

        for (AlumnoCiclo alumnoCicloEach : alumnosCiclosAnteriores) {
            logger.debug("Ciclo a evaluar alumnocilo {}, ciclo {}", alumnoCicloEach.toString(), alumnoCicloEach.getCicloAcademico().toString());

            if (alumnoCicloEach.getSituacionFinal() != null && alumnoCicloEach.getSituacionFinal().isDesertor()) {
                contadorIntercalado = 0;
            }
            if (alumnoCicloEach.isNoMatriculado()) {
                if (contadorIntercalado >= MAX_INTERCALADOS_NMAT) {
                    matriculadoCicloFuturo = alumnoCicloLastMat != null && alumnoCicloLastMat.getCicloAcademico().getCodigoInt() > alumnoCicloEach.getCicloAcademico().getCodigoInt();
                    if (alumnoCicloSiguiente == null) {
                        if (!matriculadoCicloFuturo) {
                            alumno.setSituacionAcademica(situacionDesertor);
                            alumnoDAO.updateSituacionAcad(alumno);
                        }
                        alumnoCiclo.setUserModificacion(ds.getUsuario());
                        alumnoCiclo.setSituacionFinal(situacionDesertor);
                        alumnoCiclo.setFechaModificacion(ds.getFechaAccionAudit());
                        alumnoCicloDAO.updateSituacionFinal(alumnoCiclo);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void promediarHistorialNotas2(Alumno alumno, CicloAcademico cicloAcademico,
            List<MatriculaCurso> matriculasCursosByAlumno, Usuario usuario, DateTime today) {

        //   ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);

        SituacionAcademica situacionAcademicaFinal = null;

        logger.debug("PromediarHistorialNotas Alumno {}, Ciclo Academico {} {} Esto Ciclo Alumno {}, Situacion Inicial Id {} Codigo {} Nombre {}",
                alumno.getId(),
                cicloAcademico.getId(), cicloAcademico.getDescripcion(), alumnoCiclo.getEstado(),
                alumnoCiclo.getSituacionInicio().getId(), alumnoCiclo.getSituacionInicio().getCodigo(),
                alumnoCiclo.getSituacionInicio().getNombre());

        //todos los ciclos anteriores
        Integer credAcumuladosAlumno = BigDecimal.ZERO.intValue();
        Integer credAprAcumuladosAlumno = BigDecimal.ZERO.intValue();

        //por ciclo actual
        Integer credCursadosAlumnoCiclo = BigDecimal.ZERO.intValue();
        Integer credCursadosAproAlumnoCiclo = BigDecimal.ZERO.intValue();

        Integer cursosInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();
        Integer cursosAprInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();

        /*Obtenemos la informacion del ciclo actual*/
        List<AlumnoCicloCurso> alumnosCicloCursoByAlumnoCiclo = alumnoCicloCursoDAO.allOperativesByAlumnoCiclo(alumno, cicloAcademico);

        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;

        //procesamos la informacion del ciclo actual
        for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursoByAlumnoCiclo) {
            credCursadosAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
            cursosInscritosAlumnoCiclo += 1;
            credAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
            //  Integer vecesEstudiadoCurso = alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCiclo(alumnoCicloCursoEach.getCurso(), alumno, cicloAcademico).intValue();
            Integer vecesEstudiadoCurso = this.countVecesAnteriores(matriculasCursosByAlumno, cicloAcademico, alumnoCicloCursoEach.getCurso());
            vecesEstudiadoCurso++;
            alumnoCicloCursoEach.setVecesCursado(vecesEstudiadoCurso);

            if (alumnoCicloCursoEach.isAprobado()) {
                credCursadosAproAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
                cursosAprInscritosAlumnoCiclo += 1;
                credAprAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);
            }
        }

        //obtenemos la informacion de los ciclos anteriores para los acumulados
        List<AlumnoCicloCurso> alumnosCicloCursosCiclosAnteriores = alumnoCicloCursoDAO.allOperativesByAlumnoAnterioresCiclo(alumno, cicloAcademico);
        BigDecimal sumNotasCreditosTotal = sumNotasCreditos;
        BigDecimal sumCreditosTotal = sumCreditos;

        //procesamos la informacion de los ciclos anteriores
        for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursosCiclosAnteriores) {
            credAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();

            if (alumnoCicloCursoEach.isAprobado()) {
                credAprAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditosTotal = sumNotasCreditosTotal.add(notaBig.multiply(creditosBig));
                sumCreditosTotal = sumCreditosTotal.add(creditosBig);
            }
        }

        BigDecimal promedio = BigDecimal.ZERO;
        if (sumNotasCreditos.compareTo(BigDecimal.ZERO) != 0 && sumCreditos.compareTo(BigDecimal.ZERO) != 0) {
            promedio = sumNotasCreditos.divide(sumCreditos, 2, RoundingMode.HALF_UP);
        }

        BigDecimal promedioAcumulado = BigDecimal.ZERO;
        if (sumNotasCreditosTotal.compareTo(BigDecimal.ZERO) != 0 && sumCreditosTotal.compareTo(BigDecimal.ZERO) != 0) {
            promedioAcumulado = sumNotasCreditosTotal.divide(sumCreditosTotal, 2, RoundingMode.HALF_UP);
        }

        alumnoCiclo.setPromedioCiclo(promedio);
        alumnoCiclo.setPromedioAcumulado(promedioAcumulado);

        alumnoCiclo.setCreditosAcumulados(credAcumuladosAlumno);
        alumnoCiclo.setCreditosAprobadosAcumulados(credAprAcumuladosAlumno);

        alumnoCiclo.setCreditosAprobadosCiclo(credCursadosAproAlumnoCiclo);
        alumnoCiclo.setCreditosCursadosCiclo(credCursadosAlumnoCiclo);
        alumnoCiclo.setCursosAprobados(cursosAprInscritosAlumnoCiclo);
        alumnoCiclo.setCursosInscritos(cursosInscritosAlumnoCiclo);

        alumnoCiclo.setUserModificacion(usuario);
        alumnoCiclo.setFechaModificacion(today.toDate());

        //falta evaluar que sucede cuando todos los cursos son de evaluacion letras
        if (alumnosCicloCursoByAlumnoCiclo.size() == BigDecimal.ONE.intValue()) {
            alumnoCiclo.setEstaAprobado(alumnosCicloCursoByAlumnoCiclo.get(0).getEstaAprobado());
        } else {
            Integer aprobado = evaluateEstaAprobado(promedio, alumno);
            alumnoCiclo.setEstaAprobado(aprobado);
        }
        alumnoCicloDAO.update(alumnoCiclo);
        alumnoCiclo.getId();

        logger.debug("Ciclo Academico {} {},Promedio Ciclo {} Aprobado {}, Situacion Inicial Id {} Codigo {} Nombre {}",
                cicloAcademico.getId(), cicloAcademico.getDescripcion(),
                alumnoCiclo.getPromedioCiclo(),
                alumnoCiclo.getEstaAprobado(),
                alumnoCiclo.getSituacionInicio().getId(), alumnoCiclo.getSituacionInicio().getCodigo(), alumnoCiclo.getSituacionInicio().getNombre());

        alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
        alumnoCicloDAO.update(alumnoCiclo);
    }

    //invoked of cargaacademicaservice
    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void promedio(MatriculaCurso matriculaCurso, DataSessionPivot ds, boolean calcularSituacionAcadFinal) {
        Alumno alumno = alumnoDAO.find(matriculaCurso.getMatriculaResumen().getAlumno());
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        Curso curso = cursoDAO.find(matriculaCurso.getCurso().getId());

        CicloAcademico cicloAcademicoAnterior = cicloAcademicoDAO.findAnteriorActivo(cicloAcademico);
        AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademicoAnterior);

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);
        DateTime today = new DateTime(ds.getFechaAccionAudit());

        if (alumnoCiclo == null) {
            alumnoCiclo = new AlumnoCiclo();
            alumnoCiclo.setAlumno(alumno);
            alumnoCiclo.setCarrera(alumno.getCarrera());
            alumnoCiclo.setCicloAcademico(cicloAcademico);
            //todos los ciclos
            alumnoCiclo.setCreditosAcumulados(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCreditosAprobadosAcumulados(BigDecimal.ZERO.intValue());

            //por ciclo
            alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCreditosCursadosCiclo(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCursosAprobados(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCursosInscritos(BigDecimal.ZERO.intValue());
            //
            alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
            alumnoCiclo.setUserRegistro(ds.getUsuario());
            alumnoCiclo.setUserModificacion(ds.getUsuario());
            alumnoCiclo.setFechaModificacion(today.toDate());
            alumnoCiclo.setFechaRegistro(today.toDate());
            alumnoCiclo.setOrientacionCarrera(alumno.getOrientacionCarrera());

            alumnoCiclo.setSituacionInicio(alumno.getSituacionAcademica());
            alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
            // alumnoCiclo.setSituacionFinal(situacionAcademica);

            //calcular
            alumnoCiclo.setPromedioAcumulado(BigDecimal.ZERO);
            alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
            alumnoCicloDAO.save(alumnoCiclo);
            alumno.getId();
        }

        if (alumnoCicloCurso == null) {
            alumnoCicloCurso = new AlumnoCicloCurso();
            alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
            //  alumnoCicloCurso.setAutorizacionRegistro(autorizacionRegistro); wtf
            if (curso.isTieneCreditosVariables()) {

                alumnoCicloCurso.setCreditos(matriculaCurso.getCreditosAprobados());
            } else {
                alumnoCicloCurso.setCreditos(matriculaCurso.getCreditos());
            }
            alumnoCicloCurso.setCurso(curso);

            Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
            alumnoCicloCurso.setEstaAprobado(aprobado);

            alumnoCicloCurso.setEstado(EstadoMatriculaEnum.MAT);
            alumnoCicloCurso.setFechaModificacion(today.toDate());
            alumnoCicloCurso.setFechaRegistro(today.toDate());
            alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
            alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.ACTA);
            alumnoCicloCurso.setRegistroActivo(BigDecimal.ONE.intValue());
            alumnoCicloCurso.setUserModificacion(ds.getUsuario());
            alumnoCicloCurso.setUsuarioRegistro(ds.getUsuario());
            alumnoCicloCurso.setVecesCursado(alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCiclo(curso, alumno, cicloAcademico).intValue() + 1);
            alumnoCicloCurso.setVecesCursadoRegular(alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCicloReg(curso, alumno, cicloAcademico).intValue() + 1);
            alumnoCicloCursoDAO.save(alumnoCicloCurso);
            alumnoCicloCurso.getId();

        } else {
            if (curso.isTieneCreditosVariables()) {
                alumnoCicloCurso.setCreditos(matriculaCurso.getCreditosAprobados());
            } else {
                alumnoCicloCurso.setCreditos(matriculaCurso.getCreditos());
            }
            alumnoCicloCurso.setFechaModificacion(today.toDate());
            alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
            alumnoCicloCurso.setUserModificacion(ds.getUsuario());
            Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
            alumnoCicloCurso.setEstaAprobado(aprobado);

            alumnoCicloCursoDAO.update(alumnoCicloCurso);
            alumnoCicloCurso.getId();
        }

        //todos los ciclos
        Integer credAcumuladosAlumno = BigDecimal.ZERO.intValue();
        Integer credAprAcumuladosAlumno = BigDecimal.ZERO.intValue();

        //por ciclo
        Integer credCursadosAlumnoCiclo = BigDecimal.ZERO.intValue();
        Integer credCursadosAproAlumnoCiclo = BigDecimal.ZERO.intValue();

        Integer cursosInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();
        Integer cursosAprInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();

        List<AlumnoCicloCurso> allByAlumnoCiclo = alumnoCicloCursoDAO.allOperativesByAlumnoCiclo(alumno, cicloAcademico);

        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;
        for (AlumnoCicloCurso alumnoCicloCursoEach : allByAlumnoCiclo) {
            credCursadosAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
            cursosInscritosAlumnoCiclo += 1;
            if (alumnoCicloCursoEach.isAprobado()) {
                credCursadosAproAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
                cursosAprInscritosAlumnoCiclo += 1;
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);
            }
        }

        List<AlumnoCicloCurso> allByAlumno = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        BigDecimal sumNotasCreditosTotal = BigDecimal.ZERO;
        BigDecimal sumCreditosTotal = BigDecimal.ZERO;
        for (AlumnoCicloCurso alumnoCicloCursoEach : allByAlumno) {

            credAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
            if (alumnoCicloCursoEach.isAprobado()) {
                credAprAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditosTotal = sumNotasCreditosTotal.add(notaBig.multiply(creditosBig));
                sumCreditosTotal = sumCreditosTotal.add(creditosBig);
            }
        }

        BigDecimal promedio = BigDecimal.ZERO;
        if (sumNotasCreditos.compareTo(BigDecimal.ZERO) != 0 && sumCreditos.compareTo(BigDecimal.ZERO) != 0) {
            promedio = sumNotasCreditos.divide(sumCreditos, 2, RoundingMode.HALF_UP);
        }

        BigDecimal promedioAcumulado = BigDecimal.ZERO;
        if (sumNotasCreditosTotal.compareTo(BigDecimal.ZERO) != 0 && sumCreditosTotal.compareTo(BigDecimal.ZERO) != 0) {
            promedioAcumulado = sumNotasCreditosTotal.divide(sumCreditosTotal, 2, RoundingMode.HALF_UP);
        }
        alumnoCiclo.setPromedioCiclo(promedio);
        alumnoCiclo.setPromedioAcumulado(promedioAcumulado);

        alumnoCiclo.setCreditosAcumulados(credAcumuladosAlumno);
        alumnoCiclo.setCreditosAprobadosAcumulados(credAprAcumuladosAlumno);

        alumnoCiclo.setCreditosAprobadosCiclo(credCursadosAproAlumnoCiclo);
        alumnoCiclo.setCreditosCursadosCiclo(credCursadosAlumnoCiclo);
        alumnoCiclo.setCursosAprobados(cursosAprInscritosAlumnoCiclo);
        alumnoCiclo.setCursosInscritos(cursosInscritosAlumnoCiclo);

        alumnoCiclo.setUserModificacion(ds.getUsuario());
        alumnoCiclo.setFechaModificacion(today.toDate());

        if (allByAlumnoCiclo.size() == BigDecimal.ONE.intValue()) {
            alumnoCiclo.setEstaAprobado(allByAlumnoCiclo.get(0).getEstaAprobado());
        } else {
            Integer aprobado = evaluateEstaAprobado(promedio, alumno);
            alumnoCiclo.setEstaAprobado(aprobado);
        }
        alumnoCicloDAO.update(alumnoCiclo);
        alumnoCiclo.getId();
        if (calcularSituacionAcadFinal) {
            SituacionAcademica situacionAcademicaIni = alumnoCicloAnterior != null ? alumnoCicloAnterior.getSituacionFinal() : alumno.getSituacionAcademica();
            SituacionAcademica situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCiclo, situacionAcademicaIni, alumno.getCiclosEstudiados(), alumno.getCreditosAprobados(), cicloAcademico);

            alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
            alumnoCicloDAO.update(alumnoCiclo);

            Alumno alumnoUpd = new Alumno();
            alumnoUpd.setId(alumno.getId());
            alumnoUpd.setCicloActivo(cicloAcademico);
            alumnoUpd.setSituacionAcademica(situacionAcademicaFinal);
            alumnoDAO.updateCicloActivoSituacionAcad(alumnoUpd);
        }
        try {
            this.generarHistorialNotas(alumno, curso, matriculaCurso, cicloAcademico, ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (matriculaCurso.getNotaFinal().equals(NotaLetraEnum.APROBADO.getValor1())) {
            aprobado = BigDecimal.ONE.intValue();
        } else if (TypesUtil.getBigDecimal(matriculaCurso.getNotaFinal()) != null) {
            BigDecimal notaBig = TypesUtil.getBigDecimal(matriculaCurso.getNotaFinal());
            aprobado = evaluateEstaAprobado(notaBig, alumno);
        }
        return aprobado;
    }

    @Override
    public Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno) {
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

    private String messageException(Exception e) {
        String exception = "";
        if (e != null) {
            exception = ExceptionHandler.exceptionOnStringMedium(e);
        }
        return exception;
    }

}
