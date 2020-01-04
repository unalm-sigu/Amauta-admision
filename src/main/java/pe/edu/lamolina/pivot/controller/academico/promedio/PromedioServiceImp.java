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
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.constantines.EstudiosConstantine;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.CIA;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.NotaLetraEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_7;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_D;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_E;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_T;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_X;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import static pe.edu.lamolina.model.enums.TipoCicloEnum.NIV;
import static pe.edu.lamolina.model.enums.TipoCicloEnum.REG;
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
    EgresadoDAO egresadoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    AuditorService auditorService;
    @Autowired
    AlumnoService alumnoService;
    @Autowired
    InterceptorService interceptorService;
    @Autowired
    VisorCalculoNotas visorCalculoNotas;
    @Autowired
    VisorCalculaSituacion visorCalculaSituacion;
    @Autowired
    SituacionAcademicaService situacionAcademicaService;

    private final Integer VECES_TRIKA = 3;

    private final Integer CICLO_INICIA_TRIKA = 200320;

    private final Integer CICLO_INICIA_SUSPENCION_TRIKA = 201810;

    private final int MAX_INTERCALADOS_NMAT = 6;

    @Async
    @Override
    @Transactional
    public void saveCerrarActaAsync(List<Alumno> alumnos, DataSessionPivot ds) {
        for (Alumno alumno : alumnos) {
            this.calcularSituacionAcademica(new Alumno(alumno.getId()), ds);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void actasNotasHaciaHistorial(
            MatriculaResumen matriculaResumen,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosLlevados,
            DataSessionPivot ds, String token) {

        Alumno alumno = matriculaResumen.getAlumno();
        CicloAcademico cicloAcademico = matriculaResumen.getCicloAcademico();

        String tramaAlumno = "Procesando alumno=" + alumno.getCodigo()
                + " ciclo=" + cicloAcademico.getDescripcion()
                + " cursos-mat=" + cursosMatriculados.size()
                + " cursos-histo=" + cursosLlevados.size() + "\n";

        int cambiosCiclo = 0;
        String tramaCiclo = "";

        for (MatriculaCurso matriculaCurso : cursosMatriculados) {
            String tramaCurso = this.notaCursoHaciaHistorial(matriculaCurso, cursosLlevados, ds);
            cambiosCiclo += tramaCurso.indexOf("Anulando") >= 0 ? 1 : 0;
            cambiosCiclo += tramaCurso.indexOf("Creando") >= 0 ? 1 : 0;
            tramaCiclo += tramaCurso;
        }

        if (cambiosCiclo > 0) {
            tramaAlumno += tramaCiclo;
        }

        System.out.println(tramaAlumno);
        visorCalculoNotas.incrementarToken(token);
    }

    private String notaCursoHaciaHistorial(MatriculaCurso matriculaCurso, List<AlumnoCicloCurso> matriculaCursos, DataSessionPivot ds) {
        Alumno alumno = matriculaCurso.getMatriculaResumen().getAlumno();
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        Curso curso = matriculaCurso.getCurso();
        return generarHistorialNotas(alumno, curso, matriculaCurso, cicloAcademico, matriculaCursos, ds);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void promediarAllCicloAsync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesCicloCurso,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds,
            String token,
            boolean throwError, boolean showError) {

        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }

        this.promediarAllCicloSync(
                alumno,
                cicloActivo,
                egresado,
                ciclos,
                alumnoCiclos,
                allOperativesCicloCurso,
                allAlumnoCicloCurso,
                allReincorporacionesByAlumno, ds,
                throwError, showError);

        visorCalculoNotas.incrementarToken(token);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int promediarAllCicloSync(
            Alumno alumni,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclosAll,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> alumnoCicloCursosActivos,
            List<AlumnoCicloCurso> alumnoCicloCursosAll,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds,
            boolean throwError, boolean showError) {

        long t1 = System.currentTimeMillis();
        Alumno alumno = alumni.clone();
        CicloAcademico cicloIngreso = alumno.getCicloIngreso();

        try {
            List<AlumnoCiclo> alumnoCiclosPrevio = new ArrayList();
            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                AlumnoCiclo acPrev = (AlumnoCiclo) alumnoCiclo.clone();
                alumnoCiclosPrevio.add(acPrev);
            }

            Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("id", alumnoCiclosPrevio);
            Map<String, List<CicloAcademico>> mapCiclo = TypesUtil.convertListToMapList("codigo", ciclosAll);
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", alumnoCicloCursosAll);

            this.promediarAlumno(alumno, egresado, mapCiclo, cicloActivo, alumnoCiclos, alumnoCicloCursosActivos, ds, showError); //cambia situacion academica

            CicloAcademico ultimoCicloRegular = null;
            CicloAcademico ultimoRetiroRegular = null;

            AlumnoCiclo ultimoCicloMatriculadoRegular = null;
            AlumnoCiclo ultimoAlumnoCiclo = null;

            Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloAsc());

            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                if (alumnoCiclo.isRegistroValido()) {
                    ultimoAlumnoCiclo = alumnoCiclo;
                }

                CicloAcademico cicloAlumno = alumnoCiclo.getCicloAcademico();
                if (alumnoCiclo.getEstadoEnum() == MAT) {
                    if (cicloAlumno.getTipoEnum() == REG) {
                        ultimoCicloRegular = cicloAlumno;
                        ultimoCicloMatriculadoRegular = alumnoCiclo;
                    }
                }

                if (alumnoCiclo.getEstadoEnum() == RCI && cicloAlumno.getTipoEnum() == REG) {
                    ultimoRetiroRegular = cicloAlumno;
                }

                if (alumnoCiclo.getId() == null) {
                    if (alumnoCiclo.isRegistroValido()) {
                        ObjectUtil.getParentTree(alumnoCiclo, "alumno.id");
                        ObjectUtil.getParentTree(alumnoCiclo, "cicloAcademico.id");
                        ObjectUtil.getParentTree(alumnoCiclo, "carrera.id");
                        ObjectUtil.getParentTree(alumnoCiclo, "orientacionCarrera.id");
                        ObjectUtil.getParentTree(alumnoCiclo, "situacionInicio.id");
                        ObjectUtil.getParentTree(alumnoCiclo, "situacionFinal.id");
                        alumnoCicloDAO.save(alumnoCiclo);
                        continue;
                    } else {
                        throw new PhobosException("Alumno-Ciclo del " + cicloAlumno.getDescripcion() + " no fue validado para ser guardado");
                    }
                }

                if (!alumnoCiclo.isRegistroValido()
                        && alumnoCiclo.getEstadoEnum() == RCI
                        && cicloIngreso != null
                        && cicloAlumno.getCodigoInt() < cicloIngreso.getCodigoInt()) {
                    alumnoCiclo.setRegistroValido(true);
                    alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_8));
                    alumnoCiclo.setSituacionFinal(new SituacionAcademica(S_8));
                }

                AlumnoCiclo alumnoCicloPrevio = mapAlumnoCiclo.get(alumnoCiclo.getId());
                if (alumnoCicloPrevio == null) {
                    continue;
                }

                boolean sonIguales = ObjectUtil.equalsAttrs(alumnoCicloPrevio, alumnoCiclo,
                        Arrays.asList(
                                "situacionInicio", "situacionFinal", "estado",
                                "creditosCursadosCiclo", "cursosInscritos", "cursosAprobados",
                                "creditosAprobadosAcumulados", "creditosAprobadosCiclo",
                                "creditosAcumulados", "creditosConvalidados",
                                "puntajeCiclo", "puntajeAcumulado",
                                "promedioCiclo", "promedioAcumulado",
                                "ciclosRegularesEstudiados", "ciclosAlternosSinEstudiar", "ciclosConsecutivosSinEstudiar",
                                "estaAprobado"));

                if (!sonIguales) {
                    alumnoCiclo.setUserModificacion(ds.getUsuario());
                    alumnoCiclo.setFechaModificacion(ds.getFechaAccionAudit());
                    alumnoCicloDAO.updateColumns(alumnoCiclo,
                            "situacionInicio", "situacionFinal", "estado",
                            "creditosCursadosCiclo", "cursosInscritos", "cursosAprobados",
                            "creditosAprobadosAcumulados", "creditosAprobadosCiclo",
                            "creditosAcumulados", "creditosConvalidados",
                            "puntajeCiclo", "puntajeAcumulado",
                            "promedioCiclo", "promedioAcumulado",
                            "ciclosRegularesEstudiados", "ciclosAlternosSinEstudiar", "ciclosConsecutivosSinEstudiar",
                            "estaAprobado", "userModificacion", "fechaModificacion");
                }

                if (alumnoCiclo.isRegistroValido() && alumnoCiclo.getEstadoEnum() == INH) {
                    boolean situacionesIguales
                            = ObjectUtil.verificarIgualdad(
                                    alumnoCiclo.getSituacionInicio(),
                                    alumnoCiclo.getSituacionFinal(),
                                    Arrays.asList("id"));
                    if (situacionesIguales && alumnoCiclo.getSituacionFinal().isSeparadoDefinitivo()) {
                        alumnoCiclo.setRegistroValido(false);
                    }
                }
                if (!alumnoCiclo.isRegistroValido()) {
                    CicloAcademico ciclo = alumnoCiclo.getCicloAcademico();
                    this.printSystem("ciclo=" + ciclo.getDescripcion() + " / registro.valido=" + alumnoCiclo.isRegistroValido(), showError);

                    if (alumnoCiclo.getEstadoEnum() == RCI && ciclo.getCodigoInt() == cicloActivo.getCodigoInt()) {
                    } else if (alumnoCiclo.getEstadoEnum() == MAT && ciclo.getCodigoInt() == cicloActivo.getCodigoInt()) {
                    } else {
                        if (!Arrays.asList(INH, NMAT, RCI, CIA).contains(alumnoCiclo.getEstadoEnum())) {
                            throw new PhobosException("No puede eliminarse...");
                        }
                        List<AlumnoCicloCurso> alumnoCicloCursos = TypesUtil.getListNotNull(mapAlumnoCicloCurso.get(alumnoCiclo.getId()));
                        if (alumnoCicloCursos.isEmpty()) {
                            System.out.println("delete id=" + alumnoCiclo.getId() + " ciclo=" + alumnoCiclo.getCicloAcademico().getDescripcion());
                            alumnoCicloDAO.delete(alumnoCiclo);
                        }
                    }
                }
            }

            if (ultimoAlumnoCiclo != null && ultimoAlumnoCiclo.isRegistroValido()) {
                alumno.setSituacionAcademica(ultimoAlumnoCiclo.getSituacionFinal());
            }

            alumno.setUltimoRetiro(ultimoRetiroRegular);
            alumno.setCicloActivoRegular(ultimoCicloRegular);

            alumno.setPromedioUltimoCiclo(null);
            if (ultimoCicloMatriculadoRegular != null) {
                alumno.setPromedioUltimoCiclo(ultimoCicloMatriculadoRegular.getPromedioCiclo());
            }

            alumno.setPromedioProcesado(Boolean.TRUE);
            alumnoDAO.updateColumns(alumno,
                    "cicloActivo", "cicloActivoRegular", "ultimoRetiro", "situacionAcademica",
                    "creditosAprobados", "creditosCursados", "creditosConvalidados",
                    "promedioAcumulado", "promedioUltimoCiclo", "puntaje", "promedioProcesado",
                    "ciclosRegularesEstudiados", "ciclosAlternosSinEstudiar", "ciclosConsecutivosSinEstudiar");

        } catch (Exception e) {
            e.printStackTrace();
            if (throwError) {
                throw new PhobosException(e.getLocalizedMessage());
            }

            String error = "####Error en el hilo alumno " + alumno.getCodigo() + " ciclo activo " + ObjectUtil.getParentTree(alumno, "cicloActivo.codigo");
            logger.error(error);
            if (!throwError) {
                throw new PhobosException();
            }

        } finally {
        }
        long t2 = System.currentTimeMillis();
        if (showError) {
            logger.debug("Calculo de promedios de {} demoro {} mseg", alumni.getCodigo(), (t2 - t1));
        }
        return 1;

    }

    @Override
    @Transactional(readOnly = false)
    public void calcularSituacionAcademica(Alumno alumno, DataSessionPivot ds) { // USO COMO REST
        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }
        alumno = alumnoDAO.findAllInfo(alumno.getId());
        Egresado egresado = egresadoDAO.findPrincipalByAlumno(alumno);
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio().getOperativeModalidadEnum());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(alumno);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        List<AlumnoCicloCurso> alumnoCicloCursosAll = alumnoCicloCursoDAO.allByAlumno(alumno);
        List<Reincorporacion> reincorporacionesByAlumno = reincorporacionDAO.allByEstadoTramiteAndAlumnoRei(
                alumno, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));

        this.promediarAllCicloSync(
                alumno,
                cicloActivo,
                egresado,
                cicloAcademicoDAO.all(),
                alumnoCiclos,
                alumnoCicloCursos,
                alumnoCicloCursosAll,
                reincorporacionesByAlumno, ds, true, true);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void calulcarSituacionAcademicaNewSession(Alumno alumno, Egresado egresado, DataSessionPivot ds) {
        long t1 = System.currentTimeMillis();
        this.calcularSituacionAcademica(alumno, ds);
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
            Egresado egresado,
            Map<String, List<CicloAcademico>> mapCiclo,
            CicloAcademico cicloActivo,
            List<AlumnoCiclo> alumnosCiclosByAlumno,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            DataSessionPivot ds, boolean showError) {

        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", allOperativesByModalidadEstudio);

        Collections.sort(alumnosCiclosByAlumno, new AlumnoCiclo.CompareCicloAsc());
        Integer cicloNumerico = 100;
        if (!alumnosCiclosByAlumno.isEmpty()) {
            cicloNumerico = alumnosCiclosByAlumno.get(0).getCicloAcademico().getCodigoInt() - 100;
        }

        alumno.setCicloActivo(null);
        alumno.setCreditosAprobados(0);
        alumno.setCreditosConvalidados(0);
        alumno.setCreditosCursados(0);
        alumno.setPuntaje(0);
        alumno.setPromedioAcumulado(BigDecimal.ZERO);

        boolean iniciando = true;
        CicloAcademico cicloAnalizar = alumno.getCicloIngreso();
        int ciclosConsecutivosSinEstudiar = 0;
        int ciclosAlternosSinEstudiar = 0;
        ModalidadEstudioEnum modalidadEnum = alumno.getModalidadEstudio().getCodigoEnum();

        for (;;) {
            Collections.sort(alumnosCiclosByAlumno, new AlumnoCiclo.CompareCicloAsc());
            AlumnoCiclo alumnoCicloEach;
            if (iniciando) {
                alumnoCicloEach = this.getAlumnoCicloIngreso(alumnosCiclosByAlumno, alumno, cicloActivo, ds);

            } else {
                CicloAcademico cicloSgte = null;
                if (cicloAnalizar != null) {
                    cicloSgte = findCicloSiguienteRegularActivo(cicloAnalizar, modalidadEnum, mapCiclo);
                }

                if (cicloSgte != null) {
                    this.printSystem("Revisando1 ciclo=" + cicloSgte.getCodigo() + " registros=" + alumnosCiclosByAlumno.size(), showError);
                }

                alumnoCicloEach = getAlumnoCicloSgte(
                        alumno,
                        egresado,
                        alumnosCiclosByAlumno,
                        cicloSgte,
                        cicloNumerico, ds);
            }

            if (alumnoCicloEach == null) {
                break;
            }

            CicloAcademico ciclo = alumnoCicloEach.getCicloAcademico();
            this.printSystem("Revisando2 ciclo=" + ciclo.getCodigo()
                    + ", sit-ini=" + ObjectUtil.getParentTree(alumnoCicloEach, "situacionInicio.codigo")
                    + ", reg-val=" + alumnoCicloEach.isRegistroValido(), showError);

            if (Arrays.asList(PRE, EPG).contains(modalidadEnum) && ciclo.getTipoEnum() == REG) {
                if (alumnoCicloEach.getEstadoEnum() == NMAT) {
                    ciclosConsecutivosSinEstudiar++;
                    ciclosAlternosSinEstudiar++;
                }
            }
            if (Arrays.asList(RCI, MAT).contains(alumnoCicloEach.getEstadoEnum())) {
                ciclosConsecutivosSinEstudiar = 0;
            }

            alumnoCicloEach.setCiclosSinEstudiar(ciclosConsecutivosSinEstudiar, ciclosAlternosSinEstudiar);

            List<AlumnoCicloCurso> alumnoCicloCursoByCiclo = allOperativesByModalidadEstudio.stream().
                    filter(x -> x.getAlumnoCiclo().getCicloAcademico().equals(alumnoCicloEach.getCicloAcademico()))
                    .collect(Collectors.toList());

            List<AlumnoCicloCurso> alumnoCicloCursoAnteriores = allOperativesByModalidadEstudio.stream()
                    .filter(x -> x.getAlumnoCiclo().getCicloAcademico().getCodigoInt() < ciclo.getCodigoInt())
                    .collect(Collectors.toList());

            this.promediarHistorialNotas(
                    alumno,
                    egresado,
                    mapCiclo,
                    ciclo, ds,
                    alumnosCiclosByAlumno,
                    mapAlumnoCicloCurso,
                    alumnoCicloCursoByCiclo,
                    alumnoCicloCursoAnteriores, showError);

            cicloNumerico = ciclo.getCodigoInt();
            cicloAnalizar = ciclo;
            if (iniciando) {
                iniciando = false;
            }

            if (alumnoCicloEach.getEstadoEnum() == INH && alumnoCicloEach.getSituacionFinal().isDesertor()) {
                ciclosConsecutivosSinEstudiar = 0;
                ciclosAlternosSinEstudiar = 0;
                cicloAnalizar = null;
                alumnoCicloEach.setCiclosSinEstudiar(0, 0);

            } else if (alumnoCicloEach.getEstadoEnum() == INH && alumnoCicloEach.getSituacionFinal().isIngresanteSeparado()) {
                ciclosConsecutivosSinEstudiar = 0;
                ciclosAlternosSinEstudiar = 0;
                cicloAnalizar = null;
                alumnoCicloEach.setCiclosSinEstudiar(0, 0);

            } else if (Arrays.asList(S_X, S_7, S_E).contains(alumnoCicloEach.getSituacionFinal().getCodigoEnum())) {
                ciclosConsecutivosSinEstudiar = 0;
                ciclosAlternosSinEstudiar = 0;
                cicloAnalizar = null;
            }
        }
    }

    private AlumnoCiclo getAlumnoCicloSgte(
            Alumno alumno,
            Egresado egresado,
            List<AlumnoCiclo> alumnosCiclosByAlumno,
            CicloAcademico cicloAnalizar,
            Integer cicloNumerico,
            DataSessionPivot ds) {

        AlumnoCiclo alumnoCiclo = findSiguienteAlumnoCiclo(alumnosCiclosByAlumno, cicloNumerico);
        if (cicloAnalizar == null) {
            if (alumnoCiclo != null) {
                if (validarConCicloEgreso(alumnoCiclo, egresado)) {
                    alumnoCiclo.setRegistroValido(true);

                } else {
                    CicloAcademico cicloAlumno = alumnoCiclo.getCicloAcademico();
                    if (cicloAlumno.getTipoEnum() == NIV) {
                        alumnoCiclo.setRegistroValido(true);
                    }
                }
            }
            return alumnoCiclo;
        }

        if (alumnoCiclo != null) {
            CicloAcademico ciclo = alumnoCiclo.getCicloAcademico();
            if (ciclo.getCodigoInt() <= cicloAnalizar.getCodigoInt()) {
                if (validarConCicloEgreso(alumnoCiclo, egresado)) {
                    alumnoCiclo.setRegistroValido(true);
                    return alumnoCiclo;
                } else {
                    if (ciclo.getTipoEnum() == NIV) {
                        alumnoCiclo.setRegistroValido(true);
                        return alumnoCiclo;
                    }
                }
            }
        }

        alumnoCiclo = new AlumnoCiclo();
        alumnoCiclo.defaultValuesToCreate(alumno, cicloAnalizar, ds.getUsuario());
        alumnoCiclo.setCreditosConvalidados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosConvalidadosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        alumnoCiclo.setRegistroValido(true);
        if (!validarConCicloEgreso(alumnoCiclo, egresado)) {
            return null;
        }
        alumnosCiclosByAlumno.add(alumnoCiclo);
        return alumnoCiclo;
    }

    private boolean validarConCicloEgreso(AlumnoCiclo alumnoCiclo, Egresado egresado) {
        if (egresado == null) {
            return true;
        }
        if (egresado.getCicloAcademico() == null) {
            return true;
        }

        CicloAcademico cicloEgreso = egresado.getCicloAcademico();
        CicloAcademico cicloAlumno = alumnoCiclo.getCicloAcademico();
        return cicloEgreso.getCodigoInt() >= cicloAlumno.getCodigoInt();
    }

    private AlumnoCiclo getAlumnoCicloIngreso(List<AlumnoCiclo> alumnosCiclosByAlumno, Alumno alumno, CicloAcademico cicloActivo, DataSessionPivot ds) {
        if (alumno.getSituacionAcademica().isQuintoSecundaria()) {
            return null;
        }

        CicloAcademico cicloIngreso = alumno.getCicloIngreso();
        if (cicloIngreso == null) {
            if (alumnosCiclosByAlumno.isEmpty()) {
                return null;
            } else {
                Collections.sort(alumnosCiclosByAlumno, new AlumnoCiclo.CompareCicloAsc());
                for (AlumnoCiclo alumnoCiclo : alumnosCiclosByAlumno) {
                    if (alumnoCiclo.getEstadoEnum() == MAT) {
                        alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_N));
                        alumnoCiclo.setRegistroValido(true);
                        return alumnoCiclo;
                    }
                }
                return null;
            }
        }

        if (alumno.isPostgrado()) {
            CicloAcademico primerCicloCursado = findPrimerCicloCursado(alumnosCiclosByAlumno);
            if (primerCicloCursado != null && primerCicloCursado.getCodigoInt() < cicloIngreso.getCodigoInt()) {
                cicloIngreso = primerCicloCursado;
            }
        }

        if (cicloIngreso.getCodigoInt() >= cicloActivo.getCodigoInt()) {
            return null;
        }

        AlumnoCiclo alumnoCiclo = findAlumnoCiclo(alumnosCiclosByAlumno, cicloIngreso);
        if (alumnoCiclo != null) {
            if (cicloIngreso.getYear() > EstudiosConstantine.YEAR_ALL_APPROVE && alumno.isPregrado()) {
                alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_8));
            } else {
                alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_N));
            }

            alumnoCiclo.setRegistroValido(true);
            return alumnoCiclo;
        }

        alumnoCiclo = new AlumnoCiclo();
        alumnoCiclo.defaultValuesToCreate(alumno, cicloIngreso, ds.getUsuario());

        if (alumno.getModalidadEstudio().isPregrado()) {
            alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_8));
            alumnoCiclo.setSituacionFinal(new SituacionAcademica(S_9));

        } else {
            alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_N));
            alumnoCiclo.setSituacionFinal(new SituacionAcademica(S_N));
        }

        alumnoCiclo.setCreditosConvalidados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosConvalidadosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        alumnoCiclo.setRegistroValido(true);
        alumnosCiclosByAlumno.add(alumnoCiclo);

        return alumnoCiclo;
    }

    private CicloAcademico findPrimerCicloCursado(List<AlumnoCiclo> alumnosCiclosByAlumno) {
        if (alumnosCiclosByAlumno.isEmpty()) {
            return null;
        }
        Collections.sort(alumnosCiclosByAlumno, new AlumnoCiclo.CompareCicloAsc());
        for (AlumnoCiclo alumnoCiclo : alumnosCiclosByAlumno) {
            if (alumnoCiclo.getEstadoEnum() == MAT) {
                return alumnoCiclo.getCicloAcademico();
            }
        }
        return null;
    }

    private AlumnoCiclo findSiguienteAlumnoCiclo(List<AlumnoCiclo> alumnosCiclosByAlumno, int cicloNumerico) {
        for (AlumnoCiclo ac : alumnosCiclosByAlumno) {
            CicloAcademico ciclo = ac.getCicloAcademico();
            if (ciclo.getCodigoInt() > cicloNumerico) {
                return ac;
            }
        }
        return null;
    }

    private SituacionAcademica calculateSitutacionAcadFinal(
            Alumno alumno,
            CicloAcademico ciclo,
            AlumnoCiclo alumnoCiclo,
            AlumnoCiclo alumnoCicloAnterior,
            SituacionAcademica situacionInicial,
            Integer ciclosEstudiados,
            AlumnoCiclo alumnoCicloInhaAnterior, boolean showLog) {

        List<SituacionAcademicaEnum> basicosRegulares = Arrays.asList(S_N, S_8, S_9, S_7);
        CicloAcademico cicloAcademico = alumnoCiclo.getCicloAcademico();

        if (showLog) {
            logger.debug("ciclo={}, estado-mat={}, sit.inicio={}, consec={}, altern={}, aprobado={}, ingreso={}, ciclosEstudiados={}",
                    cicloAcademico.getCodigo(),
                    alumnoCiclo.getEstado(),
                    alumnoCiclo.getSituacionInicio() == null ? "" : alumnoCiclo.getSituacionInicio().getCodigo(),
                    alumnoCiclo.getCiclosConsecutivosSinEstudiar(),
                    alumnoCiclo.getCiclosAlternosSinEstudiar(),
                    alumnoCiclo.isAprobado(),
                    ObjectUtil.getParentTree(alumno, "cicloIngreso.codigo"),
                    ciclosEstudiados);
        }

        CicloAcademico cicloIngreso = alumno.getCicloIngreso();

        SituacionAcademica situacionAcademicaFinal = null;
        ModalidadEstudioEnum modalidadEnum = alumno.getModalidadEstudio().getOperativeModalidadEnum();
        int MAX_CONSECUTIVOS_NMAT = 3;
        if (modalidadEnum == PRE) {
            MAX_CONSECUTIVOS_NMAT = (cicloAcademico.getCodigoInt() <= 201710 ? 2 : 4);
        }

        int CREDITOS_MINIMO_APR = 0;
        if (alumno.isPostgrado()) {
            CREDITOS_MINIMO_APR = 48;
            if (alumno.getCarrera().isTipoDOC()) {
                CREDITOS_MINIMO_APR = 64;
            }
        }

        if (alumno.isPregrado()) {
            if (alumnoCiclo.getEstadoEnum() == NMAT && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getSituacionInicio().isIngresantePregrado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_9);
                    this.printLogger("Caso 01", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isIngresanteNoMatriculado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_7);
                    this.printLogger("Caso 02", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isIngresanteSeparado()) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_7);
                    this.printLogger("Caso 03", showLog);

                } else if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() == MAX_CONSECUTIVOS_NMAT) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 04", showLog);

                } else if (alumnoCiclo.getCiclosAlternosSinEstudiar() == 6) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 05", showLog);

                } else if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() > MAX_CONSECUTIVOS_NMAT) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 06", showLog);

                } else if (alumnoCiclo.getCiclosAlternosSinEstudiar() > 6) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 07", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_X);
                    this.printLogger("Caso 08", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoTrika()) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_X);
                    this.printLogger("Caso 08.3", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                    this.printLogger("Caso 08.5", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isTrikeado()) {
                    alumnoCiclo.setEstadoEnum(INH);
                    if (alumnoCicloAnterior != null && alumnoCicloAnterior.getSituacionAlterna() != null) {
                        if (alumnoCicloAnterior.getSituacionAlterna().isSuspendido()) {
                            situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                            this.printLogger("Caso 08.6", showLog);
                        } else {
                            situacionAcademicaFinal = alumnoCicloAnterior.getSituacionAlterna();
                            this.printLogger("Caso 08.7", showLog);
                        }

                    } else if (alumnoCicloAnterior != null) {
                        situacionAcademicaFinal = alumnoCicloAnterior.getSituacionInicio();
                        this.printLogger("Caso 08.8", showLog);

                    } else {
                        this.printLogger("Caso 08.9", showLog);
                    }

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 09", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == RCI && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getSituacionInicio().isIngresantePregrado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 10", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isIngresanteNoMatriculado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 11", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_X);
                    this.printLogger("Caso 12", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 12", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == INH && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getSituacionInicio().isIngresanteSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_7);
                    this.printLogger("Caso 13", showLog);

                } else if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() > MAX_CONSECUTIVOS_NMAT) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 14", showLog);

                } else if (alumnoCiclo.getCiclosAlternosSinEstudiar() > 6) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 15", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                    this.printLogger("Caso 16", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isTrikeado()) {
                    if (alumnoCicloAnterior != null && alumnoCicloAnterior.getSituacionAlterna() != null) {
                        if (alumnoCicloAnterior.getSituacionAlterna().isSuspendido()) {
                            situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                            this.printLogger("Caso 16.1", showLog);
                        } else {
                            situacionAcademicaFinal = alumnoCicloAnterior.getSituacionAlterna();
                            this.printLogger("Caso 16.2", showLog);
                        }
                    } else if (alumnoCicloAnterior != null) {
                        situacionAcademicaFinal = alumnoCicloAnterior.getSituacionInicio();
                        this.printLogger("Caso 16.3", showLog);

                    } else {
                        this.printLogger("Caso 16.4", showLog);
                    }

                } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_X);
                    this.printLogger("Caso 16.5", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoTrika()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_X);
                    this.printLogger("Caso 16.5", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 17", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.isTrikaSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_4T);
                    this.printLogger("Caso 18", showLog);

                } else if (!alumnoCiclo.isAprobado() && alumnoCiclo.getSituacionInicio().isSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_X);
                    this.printLogger("Caso 18.4", showLog);

                } else if (alumnoCiclo.isGenerarTrika() && ciclo.getCodigoInt() >= CICLO_INICIA_SUSPENCION_TRIKA) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_T);
                    alumnoCiclo.setSituacionAlterna(getSituacionByTipoAprobado(alumno, alumnoCiclo, showLog));
                    this.printLogger("Caso 18.5", showLog);

                } else if (cicloIngreso != null && ciclosEstudiados <= 1 && cicloIngreso.getCodigoInt() < 201710) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 19", showLog);

                } else if (cicloIngreso != null && ciclosEstudiados <= 2 && cicloIngreso.getCodigoInt() >= 201710) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 20", showLog);

                } else if (alumnoCiclo.getCreditosAprobadosAcumuladosCurricula() > 200) {
                    situacionAcademicaFinal = new SituacionAcademica(S_EM);
                    this.printLogger("Caso 21", showLog);

                } else {
                    situacionAcademicaFinal = getSituacionByTipoAprobado(alumno, alumnoCiclo, showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoNivelacion()) {
                if (alumnoCiclo.isTrikaSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_4T);
                    this.printLogger("Caso 22", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 23", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == RCI && cicloAcademico.isTipoNivelacion()) {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                this.printLogger("Caso 24", showLog);

            } else {
                this.printLogger("Caso 25", showLog);
                situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(
                        alumnoCiclo,
                        alumnoCiclo.getSituacionInicio(),
                        -1,
                        alumnoCiclo.getCreditosAprobadosConvalidadosAcumulados(),
                        alumnoCiclo.getCicloAcademico());
            }

        } else if (alumno.isPostgrado()) {
            if (alumnoCiclo.getEstadoEnum() == NMAT && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() == MAX_CONSECUTIVOS_NMAT) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 26", showLog);

                } else if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() > MAX_CONSECUTIVOS_NMAT) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 27", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 28", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == RCI && cicloAcademico.isTipoRegular()) {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                this.printLogger("Caso 29", showLog);

            } else if (alumnoCiclo.getEstadoEnum() == INH && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() > MAX_CONSECUTIVOS_NMAT) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 30", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                    this.printLogger("Caso 31", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 32", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.isTrikaSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_4T);
                    this.printLogger("Caso 33", showLog);

                } else if (alumnoCiclo.getCreditosAprobadosAcumuladosCurricula() > CREDITOS_MINIMO_APR) {
                    situacionAcademicaFinal = new SituacionAcademica(S_EM);
                    this.printLogger("Caso 34", showLog);

                } else {
                    situacionAcademicaFinal = getSituacionByTipoAprobado(alumno, alumnoCiclo, showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoNivelacion()) {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                this.printLogger("Caso 35", showLog);

            } else {
                this.printLogger("Caso 36", showLog);
                situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(
                        alumnoCiclo,
                        alumnoCiclo.getSituacionInicio(),
                        -1,
                        alumnoCiclo.getCreditosAprobadosConvalidadosAcumulados(),
                        alumnoCiclo.getCicloAcademico());
            }

        } else {
            situacionAcademicaFinal = new SituacionAcademica(S_N);
        }

//        if (alumnoCicloInhaAnterior != null && alumnoCicloInhaAnterior.getSituacionFinal().isTrikeado()) {
//            situacionAcademicaFinal = situacionInicial;
//            if (alumnoCiclo.isAprobado()) {
//                if (situacionAcademicaFinal.isSuspendido()) {
//                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
//                    this.printLogger("Caso 37", showLog);
//                }
//            } else {
//                SituacionAcademica situacionSeparado = new SituacionAcademica(SituacionAcademicaEnum.S_4);
//                situacionAcademicaFinal = situacionSeparado;
//                this.printLogger("Caso 38", showLog);
//            }
//        }
        if (situacionAcademicaFinal == null) {
            if (showLog) {
                logger.debug("\tProblemas en calculo situacion academica final:");
                logger.debug("\tSituacion inicio:{} aprobado:{}, capa:{}",
                        alumnoCiclo.getSituacionInicio() == null ? "" : alumnoCiclo.getSituacionInicio().getCodigo(),
                        alumnoCiclo.isAprobado(),
                        alumnoCiclo.getCreditosAprobadosAcumulados());
            }
        }

        return situacionAcademicaFinal;
    }

    private SituacionAcademica getSituacionByTipoAprobado(Alumno alumno, AlumnoCiclo alumnoCiclo, boolean showLog) {
        SituacionAcademica situacionAcademicaFinal = null;
        List<SituacionAcademicaEnum> basicosRegulares = Arrays.asList(S_N, S_8, S_9, S_7);

        if (alumno.isPregrado()) {
            if (alumnoCiclo.isAprobado()) {
                if (basicosRegulares.contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
                    situacionAcademicaFinal = new SituacionAcademica(S_N);
                    this.printLogger("Caso 39", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 40", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_N);
                    this.printLogger("Caso 41", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 42", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 43", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isEnPruebaUltimoCiclo()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 44", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 45", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 46", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoTrika()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 47", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 48", showLog);
                }

            } else {
                if (alumnoCiclo.getSituacionInicio().isNormal()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_1);
                    this.printLogger("Caso 49", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_2);
                    this.printLogger("Caso 50", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isIngresanteSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_1);
                    this.printLogger("Caso 51", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_6);
                    this.printLogger("Caso 52", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_4);
                    this.printLogger("Caso 53", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_4);
                    this.printLogger("Caso 54", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isEnPruebaUltimoCiclo()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_4);
                    this.printLogger("Caso 55", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_4);
                    this.printLogger("Caso 56", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoTrika()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_X);
                    this.printLogger("Caso 57", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_X);
                    this.printLogger("Caso 58", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_X);
                    this.printLogger("Caso 59", showLog);
                }

            }
        }

        if (alumno.isPostgrado()) {
            if (alumnoCiclo.isAprobado()) {
                if (basicosRegulares.contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
                    situacionAcademicaFinal = new SituacionAcademica(S_N);
                    this.printLogger("Caso 60", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 61", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_N);
                    this.printLogger("Caso 62", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 63", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 64", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 65", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 66", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_5);
                    this.printLogger("Caso 67", showLog);
                }

            } else {
                if (alumnoCiclo.getSituacionInicio().isNormal()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_1);
                    this.printLogger("Caso 68", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_2);
                    this.printLogger("Caso 69", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isIngresanteSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_1);
                    this.printLogger("Caso 70", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_6);
                    this.printLogger("Caso 71", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_4);
                    this.printLogger("Caso 72", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_4);
                    this.printLogger("Caso 73", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_4);
                    this.printLogger("Caso 74", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_X);
                    this.printLogger("Caso 75", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                    situacionAcademicaFinal = new SituacionAcademica(S_X);
                    this.printLogger("Caso 76", showLog);
                }

            }
        }

        return situacionAcademicaFinal;
    }

    private String generarHistorialNotas(
            Alumno alumno,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico ciclo,
            List<AlumnoCicloCurso> matriculasCursosByAlumno,
            DataSessionPivot ds) {

        String trama = "";
        try {
            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, ciclo);
            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, ciclo, curso);

            Integer vecesCursadoRegular = this.countVecesAnterioresReg(matriculasCursosByAlumno, ciclo, curso) + 1;
            Integer vecesCursado = this.countVecesAnteriores(matriculasCursosByAlumno, ciclo, curso) + 1;
            Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);

            if (alumnoCiclo == null) {
                SituacionAcademica situacionAcademicaComodin = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_00.getValue());
                alumnoCiclo = new AlumnoCiclo();
                alumnoCiclo.defaultValuesToCreate(alumno, ciclo, ds.getUsuario());
                alumnoCiclo.setEstadoEnum(matriculaCurso.getMatriculaResumen().getEstadoEnum());
                alumnoCiclo.setSituacionInicio(situacionAcademicaComodin);
                alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
                alumnoCicloDAO.save(alumnoCiclo);
                trama += "\tCreando alumno-ciclo\n";

            } else {
                if (alumnoCiclo.getEstadoEnum() != MAT) {
                    AlumnoCiclo alumnoCicloUpd = new AlumnoCiclo(alumnoCiclo.getId());
                    alumnoCicloUpd.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    alumnoCicloUpd.setUserModificacion(ds.getUsuario());
                    alumnoCicloUpd.setFechaModificacion(new Date());
                    alumnoCicloDAO.updateColumns(alumnoCicloUpd, "estado", "userModificacion", "fechaModificacion");
                    trama += "\tActualizando alumno-ciclo\n";
                }
            }

            if (alumnoCicloCurso == null) {
                alumnoCicloCurso = new AlumnoCicloCurso();
                alumnoCicloCurso.defaultValuesToCreate(alumnoCiclo, curso, matriculaCurso, ds.getUsuario());
                alumnoCicloCurso.setEstaAprobado(aprobado);
                alumnoCicloCurso.setVecesCursado(vecesCursado);
                alumnoCicloCurso.setVecesCursadoRegular(vecesCursadoRegular);
                alumnoCicloCursoDAO.save(alumnoCicloCurso);
                trama += "\tCreando alumno-ciclo-curso " + curso.getCodigo() + "\n";

            } else {
                Integer creditos = matriculaCurso.getCreditos();
                if (curso.isTieneCreditosVariables()) {
                    creditos = matriculaCurso.getCreditosAprobados();
                }

                AlumnoCicloCurso alumnoCicloCursoTmp = new AlumnoCicloCurso();
                alumnoCicloCursoTmp.setNota(matriculaCurso.getNotaFinal());
                alumnoCicloCursoTmp.setEstado(matriculaCurso.getEstadoEnum());
                alumnoCicloCursoTmp.setEstaAprobado(aprobado);
                alumnoCicloCursoTmp.setCreditos(creditos);

                boolean noSonIguales = !ObjectUtil.equalsAttrs(alumnoCicloCursoTmp, alumnoCicloCurso, Arrays.asList("nota", "estado", "creditos", "estaAprobado"));

                if (noSonIguales) {
                    AlumnoCicloCurso alumnoCicloCursoUpd = new AlumnoCicloCurso(alumnoCicloCurso.getId());
                    alumnoCicloCursoUpd.setRegistroActivo(0);
                    alumnoCicloCursoUpd.setUserModificacion(ds.getUsuario());
                    alumnoCicloCursoUpd.setFechaModificacion(new Date());
                    alumnoCicloCursoDAO.updateColumns(alumnoCicloCursoUpd, "registroActivo", "userModificacion", "fechaModificacion");

                    trama += "\tAnulando alumno-ciclo-curso " + alumnoCicloCurso.getId()
                            + " nota=" + alumnoCicloCurso.getNota()
                            + " estado=" + alumnoCicloCurso.getEstado()
                            + " creditos=" + alumnoCicloCurso.getCreditos()
                            + " aprobado=" + alumnoCicloCurso.getEstaAprobado() + "\n";

                    AlumnoCicloCurso alumnoCicloCursoNew = new AlumnoCicloCurso();
                    alumnoCicloCursoNew.defaultValuesToCreate(alumnoCiclo, curso, matriculaCurso, ds.getUsuario());
                    alumnoCicloCursoNew.setEstaAprobado(aprobado);
                    alumnoCicloCursoNew.setVecesCursado(vecesCursado);
                    alumnoCicloCursoNew.setVecesCursadoRegular(vecesCursadoRegular);
                    alumnoCicloCursoDAO.save(alumnoCicloCursoNew);

                    trama += "\tReemplazando alumno-ciclo-curso " + curso.getCodigo()
                            + " nota=" + matriculaCurso.getNotaFinal()
                            + " estado=" + matriculaCurso.getEstado()
                            + " creditos=" + creditos
                            + " aprobado=" + aprobado + "\n";

                } else {
                    trama += "\tInmutable alumno-ciclo-curso " + curso.getCodigo()
                            + " nota=" + matriculaCurso.getNotaFinal()
                            + " creditos=" + creditos + "\n";
                }
            }

        } catch (Exception e) {
            String excepcion = this.messageException(e);
            String error = "####Error en el hilo alumno " + alumno.getId()
                    + " ciclo " + ciclo.getId();
            logger.error(error);//, e 

            auditorService.auditTrasladoNotasToHistorial(alumno, curso, ciclo, matriculaCurso, ds, e);
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return trama;
    }

    private Integer countVecesAnteriores(List<AlumnoCicloCurso> matriculasCursosAlumno, CicloAcademico cicloAcademico, Curso curso) {
        List<AlumnoCicloCurso> matriculasCursosAnterioresByCurso = matriculasCursosAlumno.stream().filter(
                x -> (x.getAlumnoCiclo().getCicloAcademico().getCodigoInt() < cicloAcademico.getCodigoInt()
                && x.getCurso().getId() == curso.getId().longValue()
                && x.getEstadoEnum() == MAT
                && x.isBooleanRegistroActivo())).collect(Collectors.toList());
        return matriculasCursosAnterioresByCurso.size();
    }

    private Integer countVecesAnterioresReg(List<AlumnoCicloCurso> matriculasCursosAlumno, CicloAcademico cicloAcademico, Curso curso) {
        List<AlumnoCicloCurso> matriculasCursosAnterioresByCurso = matriculasCursosAlumno.stream().filter(
                x -> (x.getAlumnoCiclo().getCicloAcademico().getCodigoInt() < cicloAcademico.getCodigoInt()
                && x.getCurso().getId() == curso.getId().longValue()
                && x.getAlumnoCiclo().getCicloAcademico().isTipoRegular()
                && x.getEstadoEnum() == MAT
                && x.isBooleanRegistroActivo())).collect(Collectors.toList());
        return matriculasCursosAnterioresByCurso.size();
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
        } else if (nroCiclo.equals("0")) {
            nroCiclo = "10";
        } else if (nroCiclo.equals("1.5")) {
            nroCiclo = "20";
        } else {
            nroCiclo = "20";
        }

        List<CicloAcademicoEstadoEnum> validosAnalisis = Arrays.asList(CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);

        String codeSgte = year + nroCiclo;
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

    private AlumnoCiclo findAlumnoCicloAnterior(Alumno alumno, List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        AlumnoCiclo alumnoCicloTempo = null;

        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloDesc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigoInt() < ciclo.getCodigoInt()) {
                boolean diferentesSituaciones
                        = !ObjectUtil.verificarIgualdad(
                                alumnoCiclo.getSituacionInicio(),
                                alumnoCiclo.getSituacionFinal(),
                                Arrays.asList("id"));
                if (cicloAlu.getTipoEnum() == NIV && diferentesSituaciones) {
                    return alumnoCiclo;
                }
                if (cicloAlu.getTipoEnum() == REG) {
                    if (alumnoCiclo.getEstadoEnum() == INH && alumnoCiclo.getSituacionInicio().isDesertor()) {
                        continue;
                    }
                    if (alumnoCiclo.getEstadoEnum() == NMAT && alumnoCiclo.getSituacionFinal().isDesertor()) {
                        return alumnoCiclo;
                    }
                    if (alumnoCiclo.getEstadoEnum() == NMAT && alumnoCiclo.getSituacionInicio() != null && Arrays.asList(S_8, S_9).contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
                        return alumnoCiclo;
                    }
                    if (alumnoCiclo.getEstadoEnum() == NMAT && alumno.isPostgrado()) {
                        alumnoCicloTempo = alumnoCiclo;
                    }
                    if (Arrays.asList(MAT, RCI, INH).contains(alumnoCiclo.getEstadoEnum())) {
                        return alumnoCiclo;
                    }
                }
            }
        }
        if (alumno.isPostgrado()) {
            return alumnoCicloTempo;
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloAnteriorINH(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloDesc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigoInt() >= ciclo.getCodigoInt()) {
                continue;
            }
            if (cicloAlu.isTipoNivelacion()) {
                continue;
            }
            if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                return alumnoCiclo;
            }
            if (alumnoCiclo.getEstadoEnum() == EstadoMatriculaEnum.INH) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void promediarHistorialNotas(
            Alumno alumno,
            Egresado egresado,
            Map<String, List<CicloAcademico>> mapCiclo,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds,
            List<AlumnoCiclo> alumnoCiclos,
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso,
            List<AlumnoCicloCurso> alumnosCiclosCursoActual,
            List<AlumnoCicloCurso> alumnosCiclosCursoAnterior, boolean showError) {

        ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(alumno.getModalidadEstudio().getCodigo());
        CicloAcademico siguienteCiclo = findCicloSiguienteRegularActivo(cicloAcademico, modalidadEstudioEnum, mapCiclo);

        AlumnoCiclo alumnoCiclo = findAlumnoCiclo(alumnoCiclos, cicloAcademico);
        this.printSystem("ciclo.estado.00=" + ObjectUtil.getParentTree(alumnoCiclo, "estado"), showError);

        AlumnoCiclo alumnoCicloAnterior = findAlumnoCicloAnterior(alumno, alumnoCiclos, cicloAcademico);
        AlumnoCiclo alumnoCicloAnteriorInha = findAlumnoCicloAnteriorINH(alumnoCiclos, cicloAcademico);

        CicloAcademico cicloEgreso = null;
        if (egresado != null && egresado.getCicloAcademico() != null) {
            cicloEgreso = egresado.getCicloAcademico();
        }

        Integer ciclosEstudiados = 0;
        for (AlumnoCiclo ac : alumnoCiclos) {
            CicloAcademico cicloAnterior = ac.getCicloAcademico();
            if (cicloAnterior.getTipoEnum() == TipoCicloEnum.REG) {
                if (Arrays.asList(MAT, RCI).contains(ac.getEstadoEnum())) {
                    if (cicloAnterior.getCodigoInt() <= cicloAcademico.getCodigoInt()) {
                        ciclosEstudiados++;
                    }
                }
            }
        }

        boolean esCachimbo = false;
        if (ciclosEstudiados <= 1) {
            esCachimbo = (alumnoCiclo.getSituacionInicio() != null && alumnoCiclo.getSituacionInicio().isIngresantePregrado());
        }

        int MAX_CONSECUTIVOS_NMAT = 3;
        ModalidadEstudioEnum modalidadEnum = alumno.getModalidadEstudio().getOperativeModalidadEnum();
        if (modalidadEnum == PRE) {
            MAX_CONSECUTIVOS_NMAT = (cicloAcademico.getCodigoInt() <= 201710 ? 2 : 4);
        }

        if (alumnoCicloAnterior != null && !esCachimbo) {
            alumnoCiclo.setSituacionInicio(null);
            String traza = "alumnoCiclo-anterior {codigo:" + ObjectUtil.getParentTree(alumnoCicloAnterior, "cicloAcademico.codigo");
            traza += " - sit-ini=" + ObjectUtil.getParentTree(alumnoCicloAnterior, "situacionInicio.codigo");
            traza += " - sit-fin=" + ObjectUtil.getParentTree(alumnoCicloAnterior, "situacionFinal.codigo") + " }";
            this.printSystem(traza, showError);

            traza = "alumnoCiclo {estado=" + ObjectUtil.getParentTree(alumnoCiclo, "estado");
            traza += " - consecutivos=" + ObjectUtil.getParentTree(alumnoCiclo, "ciclosConsecutivosSinEstudiar");
            traza += " - alternos=" + ObjectUtil.getParentTree(alumnoCiclo, "ciclosAlternosSinEstudiar") + "}";
            this.printSystem(traza, showError);

            if (cicloEgreso != null && cicloAcademico.getCodigoInt() > cicloEgreso.getCodigoInt()) {
                alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_E));
                this.printSystem("egresado", showError);

            } else if (alumnoCicloAnterior.getSituacionFinal().isDesertor()) {
                this.printSystem("desertor", showError);
                if (alumnoCiclo.getEstadoEnum() == MAT) {
                    alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionInicio());
                } else if (alumnoCiclo.getEstadoEnum() == RCI) {
                    alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionInicio());
                } else {
                    alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_D));
                }
                if (alumnoCiclo.getEstadoEnum() == NMAT) {
                    if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() < MAX_CONSECUTIVOS_NMAT
                            && alumnoCiclo.getCiclosAlternosSinEstudiar() < 6) {
                        alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionInicio());

                    } else {
                        alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_D));
                        alumnoCiclo.setEstadoEnum(INH);
                    }
                }

            } else if (alumnoCicloAnterior.getSituacionFinal().isTrikeado()) {
                this.printSystem("trikeado", showError);
                if (cicloAcademico.getTipoEnum() == NIV) {
                    alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_T));
                } else if (alumnoCiclo.getEstadoEnum() == INH) {
                    alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_T));
                } else if (alumnoCiclo.getEstadoEnum() == NMAT) {
                    alumnoCiclo.setSituacionInicio(new SituacionAcademica(S_T));
                } else {
                    alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionInicio());
                }

            } else {
                this.printSystem("anterior-default", showError);
                alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
            }
        }

        String traza = "alumnoCiclo {codigo:" + ObjectUtil.getParentTree(alumnoCiclo, "cicloAcademico.codigo");
        traza += " - estado=" + ObjectUtil.getParentTree(alumnoCiclo, "estado");
        traza += " - sit-ini=" + ObjectUtil.getParentTree(alumnoCiclo, "situacionInicio.codigo");
        traza += " - sit-fin=" + ObjectUtil.getParentTree(alumnoCiclo, "situacionFinal.codigo") + " }";
        this.printSystem(traza, showError);

        alumnoCiclo.setCiclosRegularesEstudiados(ciclosEstudiados);

        this.procesarInformacionAlumnoCiclo(
                alumno,
                cicloAcademico,
                alumnoCiclo,
                alumnosCiclosCursoActual,
                alumnosCiclosCursoAnterior, showError);

        boolean generarTrika = alumnoCiclo.isGenerarTrika();
        boolean separadoTrika = alumnoCiclo.isTrikaSeparado();

        SituacionAcademica situacionAcademicaFinal = null;
        if (cicloEgreso != null && cicloAcademico.getCodigoInt() >= cicloEgreso.getCodigoInt()) {
            situacionAcademicaFinal = new SituacionAcademica(S_E);
        }
        if (situacionAcademicaFinal == null) {
            situacionAcademicaFinal = this.calculateSitutacionAcadFinal(
                    alumno,
                    cicloAcademico,
                    alumnoCiclo,
                    alumnoCicloAnterior,
                    alumnoCiclo.getSituacionInicio(),
                    ciclosEstudiados,
                    alumnoCicloAnteriorInha, showError);
        }
        alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);

        String sitFinal = situacionAcademicaFinal == null ? "" : situacionAcademicaFinal.getCodigo();
        this.printSystem("generarTrika=" + generarTrika + " - separadoTrika=" + separadoTrika + " - sitFinal=" + sitFinal, showError);

        alumno.setCicloActivo(alumnoCiclo.getCicloAcademico());
        alumno.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
        alumno.setCreditosConvalidados(alumnoCiclo.getCreditosConvalidadosAcumulados());
        alumno.setCreditosCursados(alumnoCiclo.getCreditosAcumulados());
        alumno.setPromedioAcumulado(alumnoCiclo.getPromedioAcumulado());
        alumno.setPuntaje(alumnoCiclo.getPuntajeAcumulado());
    }

    private void procesarInformacionAlumnoCiclo(
            Alumno alumno,
            CicloAcademico cicloAcademico,
            AlumnoCiclo alumnoCiclo,
            List<AlumnoCicloCurso> alumnosCicloCursoActual,
            List<AlumnoCicloCurso> alumnosCicloCursoAnteriores, boolean showError) {

        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;
        boolean generarTrika = false;
        boolean trikaSeparado = false;

        alumnoCiclo.setCreditosCursadosCiclo(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCursosInscritos(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCursosAprobados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAprobadosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAprobadosAcumuladosCurricula(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosConvalidados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosConvalidadosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setPuntajeCiclo(BigDecimal.ZERO.intValue());
        alumnoCiclo.setPuntajeAcumulado(BigDecimal.ZERO.intValue());

        for (AlumnoCicloCurso cursoAluCicloEach : alumnosCicloCursoActual) {
            if (!cursoAluCicloEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosCursadosCiclo(alumnoCiclo.getCreditosCursadosCiclo() + cursoAluCicloEach.getCreditos());
                alumnoCiclo.setCursosInscritos(alumnoCiclo.getCursosInscritos() + 1);
                alumnoCiclo.setCreditosAcumulados(alumnoCiclo.getCreditosAcumulados() + cursoAluCicloEach.getCreditos());
            }
            if (cursoAluCicloEach.getIsEstadoMatriculado()) {
                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
            }
            if (cursoAluCicloEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosConvalidados(alumnoCiclo.getCreditosConvalidados() + cursoAluCicloEach.getCreditos());
                alumnoCiclo.setCreditosConvalidadosAcumulados(alumnoCiclo.getCreditosConvalidadosAcumulados() + cursoAluCicloEach.getCreditos());
            }

            List<AlumnoCicloCurso> vecesLlevado = alumnosCicloCursoAnteriores.stream().filter(
                    x -> x.getCurso().equals(cursoAluCicloEach.getCurso())
                    && x.getEstaActivo()
                    && x.getIsEstadoMatriculado()).collect(Collectors.toList());

            Integer vecesEstudiadoCurso = vecesLlevado.size();
            vecesEstudiadoCurso++;
            cursoAluCicloEach.setVecesCursado(vecesEstudiadoCurso);

            List<AlumnoCicloCurso> vecesLlevadoRegular = alumnosCicloCursoAnteriores.stream().filter(
                    x -> x.getCurso().equals(cursoAluCicloEach.getCurso())
                    && x.getEstaActivo()
                    && x.getIsEstadoMatriculado()
                    && x.getAlumnoCiclo().getCicloAcademico().isTipoRegular()
            ).collect(Collectors.toList());

            cursoAluCicloEach.setVecesCursadoRegular(vecesLlevadoRegular.size());
            if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
                cursoAluCicloEach.setVecesCursadoRegular(cursoAluCicloEach.getVecesCursadoRegular() + 1);
            }

            if (cursoAluCicloEach.isAprobado() && !cursoAluCicloEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosAprobadosCiclo(alumnoCiclo.getCreditosAprobadosCiclo() + cursoAluCicloEach.getCreditos());
                alumnoCiclo.setCursosAprobados(alumnoCiclo.getCursosAprobados() + 1);
                alumnoCiclo.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados() + cursoAluCicloEach.getCreditos());
            }

            if (cursoAluCicloEach.isAprobado()) {
                alumnoCiclo.setCreditosAprobadosAcumuladosCurricula(alumnoCiclo.getCreditosAprobadosAcumuladosCurricula() + cursoAluCicloEach.getCreditos());
            }

            BigDecimal notaBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);

                Integer nota = TypesUtil.getInt(cursoAluCicloEach.getNota());
                Integer creditos = TypesUtil.getInt(cursoAluCicloEach.getCreditos());
                alumnoCiclo.setPuntajeCiclo(alumnoCiclo.getPuntajeCiclo() + nota * creditos);
                alumnoCiclo.setPuntajeAcumulado(alumnoCiclo.getPuntajeAcumulado() + nota * creditos);
            }

            if (cursoAluCicloEach.getVecesCursadoRegular() >= VECES_TRIKA && !cursoAluCicloEach.isAprobado()) {
                if (alumno.getModalidadEstudio().isPregrado()) {
                    generarTrika = true;
                    if (cursoAluCicloEach.getVecesCursadoRegular() > VECES_TRIKA && cicloAcademico.isTipoRegular()) {
                        trikaSeparado = true;
                    } else if (cursoAluCicloEach.getVecesCursadoRegular() >= VECES_TRIKA && cicloAcademico.isTipoNivelacion()) {
                        trikaSeparado = true;
                    }
                }
            }
            this.printSystem("\tcurso {id=" + cursoAluCicloEach.getId()
                    + ", aprobo=" + cursoAluCicloEach.isAprobado()
                    + ", veces=" + cursoAluCicloEach.getVecesCursadoRegular()
                    + ", generarTrika=" + generarTrika
                    + ", trikaSeparado=" + trikaSeparado + "}", showError);
        }

        BigDecimal sumNotasCreditosTotal = sumNotasCreditos;
        BigDecimal sumCreditosTotal = sumCreditos;

        for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursoAnteriores) {
            if (!alumnoCicloCursoEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosAcumulados(alumnoCiclo.getCreditosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }
            if (alumnoCicloCursoEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosConvalidadosAcumulados(alumnoCiclo.getCreditosConvalidadosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }

            if (alumnoCicloCursoEach.isAprobado() && !alumnoCicloCursoEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }
            if (alumnoCicloCursoEach.isAprobado()) {
                alumnoCiclo.setCreditosAprobadosAcumuladosCurricula(alumnoCiclo.getCreditosAprobadosAcumuladosCurricula() + alumnoCicloCursoEach.getCreditos());
            }
            if (alumnoCiclo.getPuntajeAcumulado() == null) {
                alumnoCiclo.setPuntajeAcumulado(0);
            }

            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditosTotal = sumNotasCreditosTotal.add(notaBig.multiply(creditosBig));
                sumCreditosTotal = sumCreditosTotal.add(creditosBig);
                Integer nota = TypesUtil.getInt(alumnoCicloCursoEach.getNota());
                Integer creditos = TypesUtil.getInt(alumnoCicloCursoEach.getCreditos());
                alumnoCiclo.setPuntajeAcumulado(alumnoCiclo.getPuntajeAcumulado() + nota * creditos);
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

        if (cicloAcademico.getCodigoInt() >= CICLO_INICIA_TRIKA && alumno.isPregrado()) {
            alumnoCiclo.setGenerarTrika(generarTrika);
            alumnoCiclo.setTrikaSeparado(trikaSeparado);
        }
    }

    @Override
    public Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (matriculaCurso.getNotaFinal().equals(NotaLetraEnum.APROBADO.getValor1())) {
            aprobado = BigDecimal.ONE.intValue();
        } else if (TypesUtil.getBigDecimal(matriculaCurso.getNotaFinal()) != null) {
            BigDecimal notaBig = TypesUtil.getBigDecimal(matriculaCurso.getNotaFinal());
            aprobado = evaluateEstaAprobado(notaBig, alumno, matriculaCurso.getCurso());
        }
        return aprobado;
    }

    @Override
    public Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (alumno.isPostgrado() || alumno.isEspecial()) {
            if (nota.compareTo(new BigDecimal(13)) >= 0) {
                aprobado = BigDecimal.ONE.intValue();
            }
        } else if (nota.compareTo(new BigDecimal(11)) >= 0) {
            aprobado = BigDecimal.ONE.intValue();
        }
        return aprobado;
    }

    @Override
    public Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno, Curso curso) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (alumno.isPostgrado() || alumno.isEspecial()) {
            if (curso.isPostgrado()) {
                if (nota.compareTo(new BigDecimal(13)) >= 0) {
                    aprobado = BigDecimal.ONE.intValue();
                }
            } else {
                if (nota.compareTo(new BigDecimal(11)) >= 0) {
                    aprobado = BigDecimal.ONE.intValue();
                }
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

    private void printLogger(String msg, boolean show) {
        if (!show) {
            return;
        }
        logger.debug(msg);
    }

    private void printSystem(String msg, boolean show) {
        if (!show) {
            return;
        }
        System.out.println(msg);
    }

}
