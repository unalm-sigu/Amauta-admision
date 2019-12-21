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
import pe.edu.lamolina.model.constantines.EstudiosConstantine;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.NotaLetraEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4T;
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

//    @Autowired
//    ContadorComponent contadorComponent;
    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    AlumnoService alumnoService;

    private final Integer VECES_TRIKA = 3;

    private final Integer CICLO_INICIA_TRIKA = 200320;

    private final int MAX_INTERCALADOS_NMAT = 6;

    @Override
    @Async
    @Transactional
    public void saveCerrarActaAsync(List<Alumno> alumnos, DataSessionPivot ds) {
        for (Alumno alumno : alumnos) {
            this.calcularSituacionAcademica(new Alumno(alumno.getId()), ds);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void trasladarInformcionForHistorial(
            MatriculaResumen matriculaResumen,
            List<MatriculaCurso> matriculasCurso,
            List<MatriculaSeccion> matriculasSeccion,
            // List<AlumnoCicloCurso> allAlumnoCicloCurso,
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
            Egresado egresado = egresadoDAO.findPrincipalByAlumno(matriculaResumen.getAlumno());
            List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(matriculaResumen.getAlumno());
            List<AlumnoCicloCurso> alumnoCicloCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumno(matriculaResumen.getAlumno());
            List<AlumnoCicloCurso> alumnoCicloCursosAll = alumnoCicloCursoDAO.allByAlumno(matriculaResumen.getAlumno());
            List<Reincorporacion> reincorporacionesByAlumno = reincorporacionDAO.allByEstadoTramiteAndAlumnoRei(
                    matriculaResumen.getAlumno(), new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));

            this.promediarAllCicloSync(
                    matriculaResumen.getAlumno(),
                    cicloActivo,
                    egresado,
                    cicloAcademicoDAO.all(),
                    alumnoCiclos,
                    alumnoCicloCursosActivos,
                    alumnoCicloCursosAll,
                    reincorporacionesByAlumno, ds, true, true);
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
    public void trasladoPromediosSource(MatriculaCurso matriculaCurso, DataSessionPivot ds, boolean showError) {
        Alumno alumno = alumnoDAO.find(matriculaCurso.getMatriculaResumen().getAlumno());
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        Curso curso = cursoDAO.find(matriculaCurso.getCurso().getId());
        Egresado egresado = egresadoDAO.findPrincipalByAlumno(alumno);

        logger.debug("Trasladar matricula curso alumno {} Ciclo {}, Curso {}",
                alumno.getId(),
                cicloAcademico.getId(),
                matriculaCurso.getCurso().getId());

        DateTime today = new DateTime();

        generarHistorialNotas(alumno, egresado, curso, matriculaCurso, cicloAcademico, ds, showError);

        AlumnoCiclo alumnoCicloSiguiente = alumnoCicloDAO.findActiveSiguienteByAlumno(alumno, cicloAcademico);
        MatriculaCurso matriculaCursoSiguiente = null;
        if (alumnoCicloSiguiente != null) {
            matriculaCursoSiguiente = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, alumnoCicloSiguiente.getCicloAcademico());
        }
        if (alumnoCicloSiguiente != null && matriculaCursoSiguiente != null) {
            this.trasladoPromediosSource(matriculaCursoSiguiente, ds, showError);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void promediarAllCicloAsync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesCicloCurso,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds) {

        //contadorComponent.iniciar(1);
        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }

        this.promediarAllCicloSync(
                alumno,
                cicloActivo,
                egresado, ciclos,
                alumnoCiclos,
                allOperativesCicloCurso,
                allAlumnoCicloCurso,
                allReincorporacionesByAlumno, ds, false, false);
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

            //System.out.println("alumno.situacion.1=" + ObjectUtil.getParentTree(alumno, "situacionAcademica.codigo"));
            //this.analizeAlumnoCiclos(alumno, alumnoCiclos, allOperativesByModalidadEstudio);
            //System.out.println("alumnoCiclos.size.2=" + alumnoCiclos.size());
            this.promediarAlumno(alumno, egresado, mapCiclo, cicloActivo, alumnoCiclos, alumnoCicloCursosActivos, ds, showError); //cambia situacion academica
            //System.out.println("alumnoCiclos.size.3=" + alumnoCiclos.size());
            //System.out.println("alumno.situacion.2=" + ObjectUtil.getParentTree(alumno, "situacionAcademica.codigo"));
            //printCiclos(alumnoCiclos);

            //this.analizarEgresado(alumno, egresado, alumnoCiclos, ds);
            //System.out.println("alumnoCiclos.size.4=" + alumnoCiclos.size());
            //System.out.println("alumno.situacion.3=" + ObjectUtil.getParentTree(alumno, "situacionAcademica.codigo"));
            // printCiclos(alumnoCiclos);
            //this.analizarDesertor(alumno, cicloActivo, alumnoCiclos, ciclosAll, mapCiclo, ds); //slw
            //System.out.println("alumnoCiclos.size.5=" + alumnoCiclos.size());
            //printCiclos(alumnoCiclos);
            //this.analizeReincorporacion(alumno, cicloActivo, allReincorporacionesByAlumno);
            //System.out.println("alumnoCiclos.size.6=" + alumnoCiclos.size());
            // printCiclos(alumnoCiclos);
            //this.analizedCastigados(alumno, alumnoCiclos, cicloActivo, mapCiclo);
            //System.out.println("alumnoCiclos.size.7=" + alumnoCiclos.size());
            // printCiclos(alumnoCiclos);
            //CicloAcademico ultimoCiclo = null;
            CicloAcademico ultimoCicloRegular = null;
            CicloAcademico ultimoRetiroRegular = null;

            AlumnoCiclo ultimoCicloMatriculadoRegular = null;
            //AlumnoCiclo ultimoCicloMatriculado = null;
            AlumnoCiclo ultimoAlumnoCiclo = null;

            int ciclosNorelacionados = 0;
            Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloAsc());

            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                if (alumnoCiclo.isRegistroValido()) {
                    ultimoAlumnoCiclo = alumnoCiclo;
                }

                CicloAcademico cicloAlumno = alumnoCiclo.getCicloAcademico();
                if (alumnoCiclo.getEstadoEnum() == MAT) {
                    //ultimoCiclo = cicloAlumno;
                    //ultimoCicloMatriculado = alumnoCiclo;
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
                    ciclosNorelacionados++;
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
                    //logger.debug("\talumnoCiclos.id {} tiene-cambios", alumnoCiclo.getId());

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

                if (!alumnoCiclo.isRegistroValido()) {
                    CicloAcademico ciclo = alumnoCiclo.getCicloAcademico();
                    this.printSystem("ciclo=" + ciclo.getDescripcion() + " / registro.valido=" + alumnoCiclo.isRegistroValido(), showError);
                    //System.out.println("ciclo=" + ciclo.getDescripcion() + "/registro.valido=" + alumnoCiclo.isRegistroValido());

                    if (alumnoCiclo.getEstadoEnum() == RCI && ciclo.getCodigoInt() == cicloActivo.getCodigoInt()) {
                    } else if (alumnoCiclo.getEstadoEnum() == MAT && ciclo.getCodigoInt() == cicloActivo.getCodigoInt()) {
                    } else {
                        if (!Arrays.asList(INH, NMAT, RCI).contains(alumnoCiclo.getEstadoEnum())) {
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

            //System.out.println("alumno.situacion.4=" + ObjectUtil.getParentTree(alumno, "situacionAcademica.codigo"));
            if (ultimoAlumnoCiclo != null && ultimoAlumnoCiclo.isRegistroValido()) {
                alumno.setSituacionAcademica(ultimoAlumnoCiclo.getSituacionFinal());
            }

            alumno.setUltimoRetiro(ultimoRetiroRegular);
            //alumno.setCicloActivo(ultimoCiclo);
            alumno.setCicloActivoRegular(ultimoCicloRegular);

            alumno.setPromedioUltimoCiclo(null);
            if (ultimoCicloMatriculadoRegular != null) {
                alumno.setPromedioUltimoCiclo(ultimoCicloMatriculadoRegular.getPromedioCiclo());
                //alumno.setCreditosAprobados(ultimoCicloMatriculado.getCreditosAprobadosAcumulados());
            }

            //System.out.println("alumno.situacion.5=" + ObjectUtil.getParentTree(alumno, "situacionAcademica.codigo"));
            alumno.setPromedioProcesado(Boolean.TRUE);
            alumnoDAO.updateColumns(alumno,
                    "cicloActivo", "cicloActivoRegular", "ultimoRetiro", "situacionAcademica",
                    "creditosAprobados", "creditosCursados", "creditosConvalidados",
                    "promedioAcumulado", "promedioUltimoCiclo", "puntaje", "promedioProcesado",
                    "ciclosRegularesEstudiados", "ciclosAlternosSinEstudiar", "ciclosConsecutivosSinEstudiar");

            //logger.debug("\tEl alumno.id {} tiene {} ciclos no relacionados", alumni.getId(), ciclosNorelacionados);
            //contadorComponent.incrementarProcesados();
        } catch (Exception e) {
            e.printStackTrace();
            if (throwError) {
                throw new PhobosException(e.getLocalizedMessage());
            }

            String error = "####Error en el hilo alumno " + alumno.getCodigo() + " ciclo activo " + ObjectUtil.getParentTree(alumno, "cicloActivo.codigo");
            logger.error(error);
            //auditorService.auditPromediarAlumno(alumno, cicloActivo, ds, e);

//            alumno.setConError(Boolean.TRUE);
//            alumnoService.marcarFalla(alumno);
            return 0;

            //throw new PhobosException(error);
        } finally {
            //contadorComponent.reporte();
        }
        long t2 = System.currentTimeMillis();
        logger.debug("Calculo de promedios de {} demoro {} mseg", alumni.getCodigo(), (t2 - t1));
        return 1;

    }

    private void printCiclos(List<AlumnoCiclo> alumnoCiclos) {
        if (1 == 1) {
            return;
        }

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlumno = alumnoCiclo.getCicloAcademico();
            System.out.print("\tciclo=" + cicloAlumno.getCodigo());
            System.out.print(" ::: estado=" + alumnoCiclo.getEstado());
            System.out.print(" ::: consec=" + alumnoCiclo.getCiclosConsecutivosSinEstudiar());
            System.out.print(" ::: altern=" + alumnoCiclo.getCiclosAlternosSinEstudiar());
            if (alumnoCiclo.getSituacionInicio() != null) {
                System.out.print(" ::: inicio=" + alumnoCiclo.getSituacionInicio().getCodigo() + " ::: ");
            }
            if (alumnoCiclo.getSituacionFinal() != null) {
                System.out.print("final=" + alumnoCiclo.getSituacionFinal().getCodigo());
            }
            System.out.println("");
        }
    }

    private void analizedCastigados(Alumno alumno, List<AlumnoCiclo> alumnoCiclos, CicloAcademico cicloActivo, Map<String, List<CicloAcademico>> mapCiclo) {
        if (!(alumno.getSituacionAcademica().isTrikeado() || alumno.getSituacionAcademica().isSuspendido())) {
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
                if (alumnoCiclo.isEstadoRetiradoCiclo()) {
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
                    //alumnoCicloDAO.deleteById(alumnoCiclo);
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
            //alumnoCiclos.remove(eliminar);
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
                if (alumnoCiclo.isEstadoRetiradoCiclo()) {
                    alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
                }
                //alumnoCicloDAO.updateSituacionInicioFinal(alumnoCiclo);
            } else {
                alumnoCiclo.setSituacionInicio(situacion9);
                if (alumnoCiclo.isEstadoRetiradoCiclo()) {
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

    private void analizarEgresado(Alumno alumno, Egresado egresado, List<AlumnoCiclo> alumnoCiclosByAlumno, DataSessionPivot ds) {
        if (egresado == null) {
            return;
        }
        if (egresado.getCicloAcademico() == null) {
            return;
        }

        SituacionAcademica situacionEgresado = new SituacionAcademica(SituacionAcademicaEnum.S_E);

        AlumnoCiclo alumnoCicloEgresado = findAlumnoCiclo(alumnoCiclosByAlumno, egresado.getCicloAcademico());
        if (alumnoCicloEgresado == null) {

            AlumnoCiclo alumnoCicloUltimo = null;
            for (AlumnoCiclo ac : alumnoCiclosByAlumno) {
                if (ac.getEstadoEnum() == MAT) {
                    alumnoCicloUltimo = ac;
                }
            }

            alumnoCicloEgresado = new AlumnoCiclo();
            alumnoCicloEgresado.defaultValuesToCreate(alumno, egresado.getCicloAcademico(), ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
            alumnoCicloEgresado.setCreditosConvalidados(BigDecimal.ZERO.intValue());
            alumnoCicloEgresado.setCreditosConvalidadosAcumulados(BigDecimal.ZERO.intValue());
            alumnoCicloEgresado.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            alumnoCicloEgresado.setSituacionInicio(alumnoCicloUltimo.getSituacionFinal());
            alumnoCiclosByAlumno.add(alumnoCicloEgresado);
        }

        alumnoCicloEgresado.setSituacionFinal(situacionEgresado);
        alumno.setSituacionAcademica(situacionEgresado);
    }

    @Override
    @Transactional(readOnly = false)
    public void calcularSituacionAcademica(Alumno alumno, DataSessionPivot ds) {
//        contadorComponent.iniciar(1);
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
            //List<CicloAcademico> ciclosAll,
            Map<String, List<CicloAcademico>> mapCiclo,
            CicloAcademico cicloActivo,
            List<AlumnoCiclo> alumnosCiclosByAlumno,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            DataSessionPivot ds, boolean showError) {

//        if (alumnosCiclosByAlumno.isEmpty()) {
//            return;
//        }
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

        //System.out.println(">>>>>>>");
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
                    //System.out.println("Revisando1 ciclo=" + cicloSgte.getCodigo() + " registros=" + alumnosCiclosByAlumno.size());
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
            this.printSystem("Revisando2 ciclo=" + ciclo.getCodigo() + " sit.inicio=" + ObjectUtil.getParentTree(alumnoCicloEach, "situacionInicio.codigo"), showError);
            //System.out.println("Revisando2 ciclo=" + ciclo.getCodigo() + " sit.inicii=" + alumnoCicloEach.getSituacionInicio().getCodigo());
            //logger.debug("**** Calc promedios ciclo {} {} {} ****", ciclo.getCodigo(), ciclo.getYear(), ciclo.getNumeroCiclo());

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
                    cicloActivo,
                    ciclo, ds,
                    alumnosCiclosByAlumno,
                    mapAlumnoCicloCurso,
                    alumnoCicloCursoByCiclo,
                    alumnoCicloCursoAnteriores, showError);

            cicloNumerico = ciclo.getCodigoInt();

            if (iniciando) {
                cicloAnalizar = alumno.getCicloIngreso();
                iniciando = false;
            } else {
                cicloAnalizar = ciclo;
            }
//            System.out.print("\t>>ciclo=" + ciclo.getCodigo() + "/estado=" + alumnoCicloEach.getEstado());
//            if (alumnoCicloEach.getSituacionFinal() != null) {
//                System.out.print("/sit.final=" + alumnoCicloEach.getSituacionFinal().getCodigo());
//            }
//            System.out.println("/registros=" + alumnosCiclosByAlumno.size());
            this.printSystem("", showError);

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

            } else if (Arrays.asList(S_X, S_4T, S_7, S_E).contains(alumnoCicloEach.getSituacionFinal().getCodigoEnum())) {
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
                    //if (egresado != null && egresado.getCicloAcademico() != null) {
                    CicloAcademico cicloAlumno = alumnoCiclo.getCicloAcademico();
                    System.out.println("ciclo=" + cicloAlumno.getCodigo());
                    if (cicloAlumno.getTipoEnum() == NIV) {
                        alumnoCiclo.setRegistroValido(true);
                    }
                    //}
                }
            }
            return alumnoCiclo;
        }

        if (alumnoCiclo != null) {
            //System.out.println("alumnoCiclo=" + alumnoCiclo);
            CicloAcademico ciclo = alumnoCiclo.getCicloAcademico();
            //System.out.println("cicloAnalizar=" + cicloAnalizar.getDescripcion());
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

        //int MAX_CONSECUTIVOS_NMAT = cicloAnalizar.getCodigoInt() <= 201710 ? 2 : 4;
        alumnoCiclo = new AlumnoCiclo();
        alumnoCiclo.defaultValuesToCreate(alumno, cicloAnalizar, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
        alumnoCiclo.setCreditosConvalidados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosConvalidadosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        alumnoCiclo.setRegistroValido(true);
        //System.out.println("alumnoCiclo2=" + alumnoCiclo);
        if (!validarConCicloEgreso(alumnoCiclo, egresado)) {
            //System.out.println("return null");
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
        alumnoCiclo.defaultValuesToCreate(alumno, cicloIngreso, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));

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
            AlumnoCiclo alumnoCiclo,
            SituacionAcademica situacionInicial,
            Integer ciclosEstudiados,
            AlumnoCiclo alumnoCicloInhaAnterior, boolean showLog) {

        List<SituacionAcademicaEnum> basicosRegulares = Arrays.asList(S_N, S_8, S_9, S_7);
        List<SituacionAcademicaEnum> basicosSuspendidos = Arrays.asList(S_T, S_6);
        CicloAcademico cicloAcademico = alumnoCiclo.getCicloAcademico();

        if (showLog) {
            logger.debug("ciclo={}, estado-mat={}, sit.inicio={}, consec={}, altern={}, aprobado={}, ingreso={}, ciclosEstudiados={}",
                    cicloAcademico.getCodigo(),
                    alumnoCiclo.getEstado(),
                    alumnoCiclo.getSituacionInicio().getCodigo(),
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

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 08", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == RCI && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getSituacionInicio().isIngresantePregrado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 09", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isIngresanteNoMatriculado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 10", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 11", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == INH && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getSituacionInicio().isIngresanteSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_7);
                    this.printLogger("Caso 12", showLog);

                } else if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() > MAX_CONSECUTIVOS_NMAT) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 13", showLog);

                } else if (alumnoCiclo.getCiclosAlternosSinEstudiar() > 6) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 14", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                    this.printLogger("Caso 14", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 15", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.isTrikaSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_4T);
                    this.printLogger("Caso 16", showLog);

                } else if (cicloIngreso != null && ciclosEstudiados < 2 && cicloIngreso.getCodigoInt() < 201710) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 17", showLog);

                } else if (cicloIngreso != null && ciclosEstudiados < 3 && cicloIngreso.getCodigoInt() >= 201710) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_N);
                    this.printLogger("Caso 18", showLog);

                } else if (alumnoCiclo.getCreditosAprobadosAcumuladosCurricula() > 200) {
                    situacionAcademicaFinal = new SituacionAcademica(S_EM);
                    this.printLogger("Caso 19", showLog);

                } else if (alumnoCiclo.isAprobado()) {
                    if (basicosRegulares.contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
                        situacionAcademicaFinal = new SituacionAcademica(S_N);
                        this.printLogger("Caso 20", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 21", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_N);
                        this.printLogger("Caso 22", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 23", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 24", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isEnPruebaUltimoCiclo()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 25", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 26", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 27", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 28", showLog);
                    }

                } else {
                    if (alumnoCiclo.getSituacionInicio().isNormal()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_1);
                        this.printLogger("Caso 29", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_2);
                        this.printLogger("Caso 30", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isIngresanteSeparado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_1);
                        this.printLogger("Caso 31", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_6);
                        this.printLogger("Caso 32", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_4);
                        this.printLogger("Caso 33", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_4);
                        this.printLogger("Caso 34", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isEnPruebaUltimoCiclo()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_4);
                        this.printLogger("Caso 35", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_4);
                        this.printLogger("Caso 36", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_X);
                        this.printLogger("Caso 37", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_X);
                        this.printLogger("Caso 38", showLog);
                    }

                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoNivelacion()) {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                this.printLogger("Caso 39", showLog);

            } else {
                this.printLogger("Caso 40", showLog);
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
                    this.printLogger("Caso 04", showLog);

                } else if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() > MAX_CONSECUTIVOS_NMAT) {
                    alumnoCiclo.setEstadoEnum(INH);
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 06", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 08", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == RCI && cicloAcademico.isTipoRegular()) {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                this.printLogger("Caso 11", showLog);

            } else if (alumnoCiclo.getEstadoEnum() == INH && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.getCiclosConsecutivosSinEstudiar() > MAX_CONSECUTIVOS_NMAT) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_D);
                    this.printLogger("Caso 13", showLog);

                } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                    this.printLogger("Caso 14", showLog);

                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    this.printLogger("Caso 15", showLog);
                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoRegular()) {
                if (alumnoCiclo.isTrikaSeparado()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_4T);
                    this.printLogger("Caso 16", showLog);

                } else if (alumnoCiclo.getCreditosAprobadosAcumuladosCurricula() > CREDITOS_MINIMO_APR) {
                    situacionAcademicaFinal = new SituacionAcademica(S_EM);
                    this.printLogger("Caso 19", showLog);

                } else if (alumnoCiclo.isAprobado()) {
                    if (basicosRegulares.contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
                        situacionAcademicaFinal = new SituacionAcademica(S_N);
                        this.printLogger("Caso 20", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 21", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_N);
                        this.printLogger("Caso 22", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 23", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 24", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 26", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 27", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_5);
                        this.printLogger("Caso 28", showLog);
                    }

                } else {
                    if (alumnoCiclo.getSituacionInicio().isNormal()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_1);
                        this.printLogger("Caso 29", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isNormalConAntecedente()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_2);
                        this.printLogger("Caso 30", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isIngresanteSeparado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_1);
                        this.printLogger("Caso 31", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_6);
                        this.printLogger("Caso 32", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isObservado2Veces()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_4);
                        this.printLogger("Caso 33", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isEnPrueba()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_4);
                        this.printLogger("Caso 34", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSuspendido()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_4);
                        this.printLogger("Caso 36", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparado()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_X);
                        this.printLogger("Caso 37", showLog);

                    } else if (alumnoCiclo.getSituacionInicio().isSeparadoDefinitivo()) {
                        situacionAcademicaFinal = new SituacionAcademica(S_X);
                        this.printLogger("Caso 38", showLog);
                    }

                }

            } else if (alumnoCiclo.getEstadoEnum() == MAT && cicloAcademico.isTipoNivelacion()) {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                this.printLogger("Caso 39", showLog);

            } else {
                this.printLogger("Caso 40", showLog);
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

        if (alumnoCicloInhaAnterior != null && alumnoCicloInhaAnterior.getSituacionFinal().isTrikeado()) {
            situacionAcademicaFinal = situacionInicial;
            if (alumnoCiclo.isAprobado()) {
                if (situacionAcademicaFinal.isSuspendido()) {
                    situacionAcademicaFinal = new SituacionAcademica(SituacionAcademicaEnum.S_3);
                    this.printLogger("Caso 28", showLog);
                }
            } else {
                SituacionAcademica situacionSeparado = new SituacionAcademica(SituacionAcademicaEnum.S_4);
                situacionAcademicaFinal = situacionSeparado;
                this.printLogger("Caso 29", showLog);
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
    public void generarHistorialNotas(
            Alumno alumno,
            Egresado egresado,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds, boolean showError) {

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
                if (alumnoCiclo.isEstadoRetiradoCiclo() || alumnoCiclo.isNoMatriculado()) {
                    alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
                }
                alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
                alumnoCicloDAO.save(alumnoCiclo);
                alumno.getId();
                alumnoCiclos.add(alumnoCiclo);
                mapAlumnoCicloCurso.put(alumnoCiclo.getId(), alumnoCicloCursosActual);

            } else if (alumnoCiclo.isNoMatriculado() || alumnoCiclo.isEstadoRetiradoCiclo()) {
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
            this.promediarHistorialNotas(alumno, egresado, mapCiclo, cicloActivo, cicloAcademico, ds, alumnoCiclos,
                    mapAlumnoCicloCurso, alumnoCicloCursosActual, alumnoCicloCursoAnteriores, showError);
            //this.promediarHistorialNotas(alumno, ciclosAll, cicloActivo, cicloAcademico, ds);

        } catch (Exception e) {

            String excepcion = this.messageException(e);

            logger.error("####Error en el hilo alumno " + alumno.getCodigo() + " ciclo " + cicloAcademico.getId());//, e 
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
            for (AlumnoCiclo alumnoCiclo : alumnosCiclosByAlumno) {
                //this.analizarDesertorByCiclo(alumno, alumnosCiclos, cicloActivo, alumnoCiclo.getCicloAcademico(), mapCiclo, ds);
            }

        } else if (!alumno.getSituacionAcademica().isIngresanteSeparado()) {
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
                if (alumno.getSituacionAcademica().isIngresantePregrado() || alumno.getSituacionAcademica().isNormal()) {
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
        CicloAcademico siguienteCicloReg = findCicloSiguienteRegularActivo(cicloAcademico, modalidadEstudio, mapCiclo);

        AlumnoCiclo alumnoCiclo = findAlumnoCiclo(alumnoCiclos, cicloAcademico);
//        AlumnoCiclo alumnoCicloAnterior = findAlumnoCicloAnterior(alumnoCiclos, cicloAcademico);
//        AlumnoCiclo alumnoCicloSiguienteActive = findAlumnoCicloActiveSiguiente(alumnoCiclos, cicloAcademico);
//        AlumnoCiclo alumnoCicloLastMat = findAlumnoCicloActiveRegularUltimo(alumnoCiclos);

        AlumnoCiclo alumnoCicloCorrespSgtRegular = findAlumnoCiclo(alumnoCiclos, siguienteCicloReg);

        //System.out.print("ciclo=" + cicloAcademico.getDescripcion());
        //System.out.print(" ciclo-sgte=" + (siguienteCicloReg == null ? "NULL" : siguienteCicloReg.getId()));
        //System.out.print(" alumno-ciclo-sgte=" + (alumnoCicloCorrespSgtRegular == null ? "NULL" : alumnoCicloCorrespSgtRegular.getId()));
        //System.out.print(" alumnoCiclo.estado=" + alumnoCiclo.getEstado());
        //System.out.print(" alumnoCiclo.situacionInicio=" + alumnoCiclo.getSituacionInicio().getCodigo());
        //System.out.print(" alumnoCiclo.situacionFinal=" + alumnoCiclo.getSituacionFinal().getCodigo());
        //System.out.println("");
//        if (alumnoCiclo.isEstadoRetiradoCiclo() || alumnoCiclo.isNoMatriculado()) {
//            if (alumnoCicloAnterior != null) {
//                alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
//                alumnoCiclo.setSituacionFinal(alumnoCicloAnterior.getSituacionFinal());
//            } else {
//                alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
//            }
//        }
        if (alumnoCiclo.getSituacionFinal().isDesertor()
                || alumnoCiclo.getSituacionFinal().isSeparado()
                || alumnoCiclo.getSituacionFinal().isSeparadoUltimoCiclo()
                || alumnoCiclo.getSituacionFinal().isEgresadoMatriculable()
                || alumnoCiclo.getSituacionFinal().isEgresado()) {
            //System.out.println("\tbye.1");
            return;
        }

        if (alumnoCiclo.isNoMatriculado() || alumnoCiclo.isEstadoRetiradoCiclo()) {
//            if (this.evaluarNoMatriculadoOrRetiradoCiclo(alumno, cicloAcademico, alumnoCiclo, alumnoCicloSiguienteActive, alumnoCicloLastMat, ds)) {
//                System.out.print("\talumnoCiclo.estado=" + alumnoCiclo.getEstado());
//                System.out.print(" alumnoCiclo.situacionInicio=" + alumnoCiclo.getSituacionInicio().getCodigo());
//                System.out.print(" alumnoCiclo.situacionFinal=" + alumnoCiclo.getSituacionFinal().getCodigo());
//                System.out.println("");
//                System.out.println("\tbye.2");
//                return;
//            }
//            System.out.print("\talumnoCiclo.estado=" + alumnoCiclo.getEstado());
//            System.out.print(" alumnoCiclo.situacionInicio=" + alumnoCiclo.getSituacionInicio().getCodigo());
//            System.out.print(" alumnoCiclo.situacionFinal=" + alumnoCiclo.getSituacionFinal().getCodigo());
//            System.out.println("");
        }

        if (alumnoCicloCorrespSgtRegular == null && siguienteCicloReg != null) {
            if (siguienteCicloReg.getCodigoInt() < cicloActivo.getCodigoInt()) {
                if (alumnoCicloCorrespSgtRegular == null) {
                    alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                    alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCicloReg, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(alumnoCiclo.getSituacionFinal());
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(alumnoCiclo.getSituacionFinal());
                    alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                    alumnoCicloCorrespSgtRegular.setCreditosConvalidadosAcumulados(BigDecimal.ZERO.intValue());
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                    alumnoCiclos.add(alumnoCicloCorrespSgtRegular);

                    System.out.println("\tanalizarDesertorByCiclo -> " + siguienteCicloReg.getDescripcion());
                    //analizarDesertorByCiclo(alumno, alumnoCiclos, cicloActivo, siguienteCicloReg, mapCiclo, ds);
                }
            }
        }
        System.out.println("\tFIN");
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
            if (cicloAlu.getTipoEnum() == REG && cicloAlu.getCodigoInt() < ciclo.getCodigoInt()) {
                if (alumnoCiclo.getEstadoEnum() == INH && alumnoCiclo.getSituacionInicio().isDesertor()) {
                    continue;
                }
                if (alumnoCiclo.getEstadoEnum() == NMAT && alumnoCiclo.getSituacionFinal().isDesertor()) {
                    return alumnoCiclo;
                }
                if (alumnoCiclo.getEstadoEnum() == NMAT && Arrays.asList(S_8, S_9).contains(alumnoCiclo.getSituacionInicio().getCodigoEnum())) {
                    return alumnoCiclo;
                }
                if (Arrays.asList(MAT, RCI, INH).contains(alumnoCiclo.getEstadoEnum())) {
                    return alumnoCiclo;
                }
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloActiveAnterior(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloDesc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigoInt() < ciclo.getCodigoInt() && alumnoCiclo.getEstadoEnum() == MAT) {
                return alumnoCiclo;
            }
        }
        return null;
    }

    private AlumnoCiclo findAlumnoCicloActiveSiguiente(List<AlumnoCiclo> alumnoCiclos, CicloAcademico ciclo) {
        Collections.sort(alumnoCiclos, new AlumnoCiclo.CompareCicloAsc());
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            CicloAcademico cicloAlu = alumnoCiclo.getCicloAcademico();
            if (cicloAlu.getCodigoInt() > ciclo.getCodigoInt() && alumnoCiclo.getEstadoEnum() == MAT) {
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
            Egresado egresado,
            Map<String, List<CicloAcademico>> mapCiclo,
            CicloAcademico cicloActivo,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds,
            List<AlumnoCiclo> alumnoCiclos,
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso,
            List<AlumnoCicloCurso> alumnosCiclosCursoActual,
            List<AlumnoCicloCurso> alumnosCiclosCursoAnterior, boolean showError) {

        ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(alumno.getModalidadEstudio().getCodigo());
        CicloAcademico siguienteCiclo = findCicloSiguienteRegularActivo(cicloAcademico, modalidadEstudioEnum, mapCiclo);

        AlumnoCiclo alumnoCiclo = findAlumnoCiclo(alumnoCiclos, cicloAcademico);
        //AlumnoCiclo alumnoCicloAnteriorActive = findAlumnoCicloActiveAnterior(alumnoCiclos, cicloAcademico);

        AlumnoCiclo alumnoCicloCorrespSgtRegular = findAlumnoCiclo(alumnoCiclos, siguienteCiclo);

        AlumnoCiclo alumnoCicloAnterior = findAlumnoCicloAnterior(alumnoCiclos, cicloAcademico);
        AlumnoCiclo alumnoCicloAnteriorInha = findAlumnoCicloAnteriorINH(alumnoCiclos, cicloAcademico);

        AlumnoCiclo alumnoCicloSiguienteInha = findAlumnoCicloSiguienteINH(alumnoCiclos, cicloAcademico);

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
            this.printSystem("ciclo.anterior.codigo=" + ObjectUtil.getParentTree(alumnoCicloAnterior, "cicloAcademico.codigo"), showError);
            this.printSystem("ciclo.anterior.sit-ini=" + ObjectUtil.getParentTree(alumnoCicloAnterior, "situacionInicio.codigo"), showError);
            this.printSystem("ciclo.anterior.sit-fin=" + ObjectUtil.getParentTree(alumnoCicloAnterior, "situacionFinal.codigo"), showError);
            this.printSystem("ciclo.estado=" + ObjectUtil.getParentTree(alumnoCiclo, "estado"), showError);
            this.printSystem("ciclo.ciclosConsecutivosSinEstudiar=" + ObjectUtil.getParentTree(alumnoCiclo, "ciclosConsecutivosSinEstudiar"), showError);
            this.printSystem("ciclo.ciclosAlternosSinEstudiar=" + ObjectUtil.getParentTree(alumnoCiclo, "ciclosAlternosSinEstudiar"), showError);

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

        this.printSystem("ciclo.sit-ini=" + ObjectUtil.getParentTree(alumnoCiclo, "situacionInicio.codigo"), showError);
        this.printSystem("ciclo.sit-fin=" + ObjectUtil.getParentTree(alumnoCiclo, "situacionFinal.codigo"), showError);
        this.printSystem("ciclo.estado=" + ObjectUtil.getParentTree(alumnoCiclo, "estado"), showError);
//        this.printSystem("ciclosEstudiados=" + ciclosEstudiados, showError);
//        this.printSystem("ciclosAlternos=" + alumnoCiclo.getCiclosAlternosSinEstudiar(), showError);
//        this.printSystem("ciclosConsecutivos=" + alumnoCiclo.getCiclosConsecutivosSinEstudiar(), showError);
        alumnoCiclo.setCiclosRegularesEstudiados(ciclosEstudiados);

        this.procesarInformacionAlumnoCiclo(//ds,
                alumno,
                alumnoCiclo,
                alumnoCicloSiguienteInha,
                alumnosCiclosCursoActual,
                alumnosCiclosCursoAnterior);

        boolean generarTrika = alumnoCiclo.isGenerarTrika();

        SituacionAcademica situacionAcademicaFinal = null;
        if (cicloEgreso != null && cicloAcademico.getCodigoInt() >= cicloEgreso.getCodigoInt()) {
            situacionAcademicaFinal = new SituacionAcademica(S_E);
        }
        if (situacionAcademicaFinal == null) {
            situacionAcademicaFinal = this.calculateSitutacionAcadFinal(
                    alumno,
                    alumnoCiclo,
                    alumnoCiclo.getSituacionInicio(),
                    ciclosEstudiados,
                    alumnoCicloAnteriorInha, showError);
        }
        alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);

        SituacionAcademica situacionTrika = new SituacionAcademica(SituacionAcademicaEnum.S_T);
        this.printSystem("\tgenerarTrika=" + generarTrika + " - sitFinal=" + situacionAcademicaFinal.getCodigo(), showError);
        if ((generarTrika && alumnoCiclo.getCicloAcademico().getCodigoInt() >= CICLO_INICIA_TRIKA)
                && !(situacionAcademicaFinal.isSeparadoDefinitivo()
                || situacionAcademicaFinal.isSeparado()
                || situacionAcademicaFinal.isSeparadoUltimoCiclo()
                || situacionAcademicaFinal.isSeparadoTrika())) {

            SituacionAcademica situacionFinalForTrika = situacionAcademicaFinal;
            if (situacionAcademicaFinal.isSuspendido()) {
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
                    logger.debug("\ttrika-genero-ciclo-INH = {}", siguienteCiclo.getDescripcion());
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
            logger.debug("\ttrika-resultado sitFinal={}", situacionAcademicaFinal.getCodigo());

        } else if (situacionAcademicaFinal.isSuspendido() && cicloAcademico.isTipoRegular()) {
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
                    logger.debug("\tsuspendido-ciclo-INH = {}", siguienteCiclo.getDescripcion());
                }

            } else {
                List<AlumnoCicloCurso> alusCicloCursos = TypesUtil.getListNotNull(mapAlumnoCicloCurso.get(alumnoCicloCorrespSgtRegular.getId()));
                if (alusCicloCursos.isEmpty()) {
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                }
                alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionAcademicaFinal);
                alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForSuspension);
            }
            logger.debug("\trevision-final sitFinal={}", situacionAcademicaFinal.getCodigo());
        }

        alumno.setCicloActivo(alumnoCiclo.getCicloAcademico());
        alumno.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
        alumno.setCreditosConvalidados(alumnoCiclo.getCreditosConvalidadosAcumulados());
        alumno.setCreditosCursados(alumnoCiclo.getCreditosAcumulados());
        // alumno.setSituacionAcademica(alumnoCiclo.getSituacionFinal());
        alumno.setPromedioAcumulado(alumnoCiclo.getPromedioAcumulado());
        alumno.setPuntaje(alumnoCiclo.getPuntajeAcumulado());
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
            //DataSessionPivot ds,
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
        alumnoCiclo.setCreditosAprobadosAcumuladosCurricula(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosConvalidados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosConvalidadosAcumulados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setPuntajeCiclo(BigDecimal.ZERO.intValue());
        alumnoCiclo.setPuntajeAcumulado(BigDecimal.ZERO.intValue());

        for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursoActual) {
            if (!alumnoCicloCursoEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosCursadosCiclo(alumnoCiclo.getCreditosCursadosCiclo() + alumnoCicloCursoEach.getCreditos());
                alumnoCiclo.setCursosInscritos(alumnoCiclo.getCursosInscritos() + 1);
                alumnoCiclo.setCreditosAcumulados(alumnoCiclo.getCreditosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }
            if (alumnoCicloCursoEach.getIsEstadoMatriculado()) {
                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
            }
            if (alumnoCicloCursoEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosConvalidados(alumnoCiclo.getCreditosConvalidados() + alumnoCicloCursoEach.getCreditos());
                alumnoCiclo.setCreditosConvalidadosAcumulados(alumnoCiclo.getCreditosConvalidadosAcumulados() + alumnoCicloCursoEach.getCreditos());
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

            if (alumnoCicloCursoEach.isAprobado() && !alumnoCicloCursoEach.getNota().equals("TE")) {
                alumnoCiclo.setCreditosAprobadosCiclo(alumnoCiclo.getCreditosAprobadosCiclo() + alumnoCicloCursoEach.getCreditos());
                alumnoCiclo.setCursosAprobados(alumnoCiclo.getCursosAprobados() + 1);
                alumnoCiclo.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }

            if (alumnoCicloCursoEach.isAprobado()) {
                alumnoCiclo.setCreditosAprobadosAcumuladosCurricula(alumnoCiclo.getCreditosAprobadosAcumuladosCurricula() + alumnoCicloCursoEach.getCreditos());
            }

            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);

                Integer nota = TypesUtil.getInt(alumnoCicloCursoEach.getNota());
                Integer creditos = TypesUtil.getInt(alumnoCicloCursoEach.getCreditos());
                alumnoCiclo.setPuntajeCiclo(alumnoCiclo.getPuntajeCiclo() + nota * creditos);
                alumnoCiclo.setPuntajeAcumulado(alumnoCiclo.getPuntajeAcumulado() + nota * creditos);
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

        if (!generarTrika && alumnoCicloSiguienteInha != null && alumnoCicloSiguienteInha.getSituacionFinal().isTrikeado()) {
            List<AlumnoCicloCurso> alumnoCiclosCursos = alumnoCicloCursoDAO.allStateByAlumnoCiclo(alumnoCicloSiguienteInha);
            if (alumnoCiclosCursos.isEmpty()) {
                //alumnoCicloDAO.delete(alumnoCicloSiguienteInha);
            }
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
        CicloAcademico ciclo = alumnoCiclo.getCicloAcademico();
        if (ciclo.getCodigoInt() >= 201810 && alumno.isPregrado()) {
            alumnoCiclo.setGenerarTrika(generarTrika);
            alumnoCiclo.setTrikaSeparado(trikaSeparado);
        }
    }

    private boolean evaluarNoMatriculadoOrRetiradoCiclo(Alumno alumno,
            CicloAcademico cicloAcademico,
            AlumnoCiclo alumnoCiclo,
            AlumnoCiclo alumnoCicloSiguiente,
            AlumnoCiclo alumnoCicloLastMat,
            DataSessionPivot ds) {

        if (Arrays.asList("201620", "201820").contains(cicloAcademico.getCodigo())) {
            //logger.debug("");
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
            //alumnoCicloDAO.updateSituacionFinal(alumnoCiclo);
            return true;
        }

        for (AlumnoCiclo alumnoCicloEach : alumnosCiclosAnteriores) {
            //System.out.println("\t*** Ciclo a evaluar " + alumnoCicloEach.getCicloAcademico().getDescripcion());
            //logger.debug("Ciclo a evaluar alumnocilo {}, ciclo {}", alumnoCicloEach.toString(), alumnoCicloEach.getCicloAcademico().toString());

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
                        //alumnoCicloDAO.updateSituacionFinal(alumnoCiclo);
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

            if (alumnoCicloCursoEach.isAprobado() && !alumnoCicloCursoEach.getNota().equals("TE")) {
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
    public void promedio(MatriculaCurso matriculaCurso, DataSessionPivot ds, boolean calcularSituacionAcadFinal, boolean showError) {

        Alumno alumno = alumnoDAO.find(matriculaCurso.getMatriculaResumen().getAlumno());
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        Curso curso = cursoDAO.find(matriculaCurso.getCurso().getId());
        Egresado egresado = egresadoDAO.findPrincipalByAlumno(alumno);

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
            this.generarHistorialNotas(alumno, egresado, curso, matriculaCurso, cicloAcademico, ds, showError);
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
