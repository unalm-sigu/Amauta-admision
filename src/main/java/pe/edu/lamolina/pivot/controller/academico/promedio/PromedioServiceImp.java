package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.NotaLetraEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_7;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_X;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_XD;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_D;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
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

//    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void trasladarInformcionForHistorial(MatriculaResumen matriculaResumen, List<MatriculaCurso> matriculasCurso, List<MatriculaSeccion> matriculasSeccion, DataSessionPivot ds, boolean calcularSituacion) {
        visorCalculoNotas.incrementarCantidad();
        List<MatriculaCurso> matriculasCursoByAlumno = matriculasCurso.stream()
                .filter(x -> x.getMatriculaResumen().getAlumno().equals(matriculaResumen.getAlumno()))
                .collect(Collectors.toList());
        List<MatriculaSeccion> matriculasSeccionByAlumno = matriculasSeccion.stream().filter(x -> x.getMatriculaResumen().equals(matriculaResumen)).collect(Collectors.toList());
        for (MatriculaCurso matriculaCurso : matriculasCursoByAlumno) {
            MatriculaSeccion matriculaSeccion = matriculasSeccionByAlumno
                    .stream().filter(x -> x.getSeccion().getGrupoSeccion().getCurso().equals(matriculaCurso.getCurso())).findFirst().orElse(null);
            if (matriculaSeccion != null && matriculaSeccion.getSeccion().getGrupoSeccion().isEstadoGrupoCerrado()) {
                this.trasladoPromediosSource2(matriculaCurso, matriculasCursoByAlumno, ds);
            }
        }
        if (calcularSituacion) {
            CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(matriculaResumen.getAlumno().getModalidadEstudio());
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio = alumnoCicloCursoDAO.allOperativesByAlumno(matriculaResumen.getAlumno());
            this.promediarAllCicloSync(matriculaResumen.getAlumno(), cicloActivo, cicloAcademicoDAO.all(), allOperativesByModalidadEstudio, ds);
        }
        visorCalculoNotas.incrementarProcesados();
        visorCalculoNotas.reporte();
    }

    public void trasladoPromediosSource2(MatriculaCurso matriculaCurso, List<MatriculaCurso> matriculaCursos, DataSessionPivot ds) {
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
    public void promediarAllCicloAsync(Alumno alumno, CicloAcademico cicloActivo, List<CicloAcademico> ciclos, List<AlumnoCicloCurso> allOperativesCicloCurso, DataSessionPivot ds) {
        // this.calulcarSituacionAcademica(alumno, ds); 
        contadorComponent.iniciar(1);
        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }
        alumno = alumnoDAO.findAllInfo(alumno.getId());
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        this.promediarAllCicloSync(alumno, cicloActivo, cicloAcademicoDAO.all(), alumnoCicloCursos, ds);
    }

    @Override
    @Transactional
    public void promediarAllCicloSync(Alumno alumno, CicloAcademico cicloActivo, List<CicloAcademico> ciclos, List<AlumnoCicloCurso> allOperativesByModalidadEstudio, DataSessionPivot ds) {
        contadorComponent.incrementar();
        alumno = alumno.clone();
        logger.info("Promediar Alumno {}", alumno.getCodigo());
        try {

            this.analizeAlumnoCiclos(alumno, allOperativesByModalidadEstudio);
            allOperativesByModalidadEstudio = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);

            this.promediarAlumno(alumno, cicloActivo, allOperativesByModalidadEstudio, ds);
            this.analizarEgresado(alumno, ds);
            //Crea alumnociclos con nmat
            this.analizarDesertor(alumno, cicloActivo, ciclos, ds);
            this.analizeReincorporacion(alumno, cicloActivo);
            this.analizedCastigados(alumno, cicloActivo);

            Alumno alumnoUpd = new Alumno(alumno.getId());
            alumnoUpd.setPromedioProcesado(Boolean.TRUE);
            alumnoDAO.updatePromedioProcesado(alumnoUpd);

            contadorComponent.incrementarProcesados();
        } catch (Exception e) {
            String excepcion = this.messageException(e);
            String error = "####Error en el hilo alumno " + alumno.getCodigo() + " ciclo activo " + alumno.getCicloActivo().getCodigo();
            logger.error(error);
            e.printStackTrace();
            auditorService.auditPromediarAlumno(alumno, cicloActivo, ds, e);
            //   TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new PhobosException(error);
        } finally {
            contadorComponent.reporte();
        }

    }

    public void analizedCastigados(Alumno alumno, CicloAcademico cicloActivo) {
        if (alumno.getSituacionAcademica().isTrikeado() || alumno.getSituacionAcademica().isCodigoS6()) {
            ModalidadEstudioEnum MODALIDAD_ESTUDIO_ENUM = alumno.getModalidadEstudio().getOperativeModalidadEnum();

            AlumnoCiclo alumnoCicloTrikeado = alumnoCicloDAO.findLastByAlumnoAndSituacion(alumno, alumno.getSituacionAcademica().getCodigoEnum());
            CicloAcademico cicloTrikeado = alumnoCicloTrikeado.getCicloAcademico();
            //     CicloAcademico ultimoCicloRegular = alumno.getCicloActivoRegular();

            CicloAcademico cicloSuspendido = cicloAcademicoDAO.findSiguienteRegularActivo(cicloTrikeado, MODALIDAD_ESTUDIO_ENUM);
            AlumnoCiclo alumnoCicloSusp = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloSuspendido);

            CicloAcademico siguienteCicloRegularHabil = cicloAcademicoDAO.findSiguienteRegularActivo(cicloSuspendido, MODALIDAD_ESTUDIO_ENUM);
            CicloAcademico siguienteCicloNivHabil = cicloAcademicoDAO.findSiguienteNivelacionActivo(cicloActivo, MODALIDAD_ESTUDIO_ENUM);

            if (alumno.getCicloActivoRegular().getCodigoInt() <= cicloActivo.getCodigoInt()) {
                Alumno alumnoUpd = new Alumno(alumno.getId());
                alumnoUpd.setSituacionAcademica(alumnoCicloSusp.getSituacionFinal());
                alumnoDAO.updateSituacionAcad(alumnoUpd);
            }
//            if (siguienteCicloRegularHabil.equals(cicloActivo)) {
//                Alumno alumnoUpd = new Alumno(alumno.getId());
//                alumnoUpd.setSituacionAcademica(alumnoCicloSusp.getSituacionFinal());
//                alumnoDAO.updateSituacionAcad(alumnoUpd);
//            }
//            if (siguienteCicloNivHabil.getCodigoInt() <= siguienteCicloRegularHabil.getCodigoInt()) {
//                if (siguienteCicloNivHabil.equals(cicloActivo)) {
//                    Alumno alumnoUpd = new Alumno(alumno.getId());
//                    alumnoUpd.setSituacionAcademica(alumnoCicloSusp.getSituacionFinal());
//                    alumnoDAO.updateSituacionAcad(alumnoUpd);
//                }
//            }
        }
    }

    private void analizeReincorporacion(Alumno alumno, CicloAcademico cicloActivo) {
        List<Reincorporacion> reincorporacionesByAlumno = reincorporacionDAO.allByEstadoTramiteAndAlumnoRei(alumno, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
        if (!reincorporacionesByAlumno.isEmpty()) {
            Collections.sort(reincorporacionesByAlumno, (p1, p2) -> Integer.valueOf(p2.getCicloReincorporacion().getCodigo()).compareTo(Integer.valueOf(p1.getCicloReincorporacion().getCodigo())));
            CicloAcademico cicloAcademicoRei = reincorporacionesByAlumno.get(0).getCicloReincorporacion();

            if (cicloActivo.equals(cicloAcademicoRei)) {
                AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastNotInSituacion(alumno, SituacionAcademicaEnum.S_D);
                if (alumnoCiclo.getSituacionFinal().isCodigoD()) { //to delete
                    alumno.setSituacionAcademica(alumnoCiclo.getSituacionInicio());
                } else {
                    alumno.setSituacionAcademica(alumnoCiclo.getSituacionFinal());
                }
                alumnoDAO.updateSituacionAcad(alumno);
            }
        }
    }

    private void analizeAlumnoCiclos(Alumno alumno, List<AlumnoCicloCurso> alumnoCicloCursos) {
        logger.debug("analizeAlumnoCiclos");
        //Todoas los alumnos ciclos
        List<AlumnoCiclo> alumnosCiclosByAlumno = alumnoCicloDAO.allByAlumno(alumno);
        List<String> ciclosStr = alumnosCiclosByAlumno.stream()
                .map(x -> x.getCicloAcademico().getCodigo())
                .collect(Collectors.toList());
        logger.debug(String.join(",", ciclosStr));

        int idx = 0;
        AlumnoCiclo alumnoCicloAnterior = null;
        for (AlumnoCiclo alumnoCiclo : alumnosCiclosByAlumno) {
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
                    alumnoCicloDAO.update(alumnoCiclo);
                }
            }
            List<AlumnoCicloCurso> alumnosCiclosCursosByAluCic = alumnoCicloCursos.stream()
                    .filter(x -> x.getAlumnoCiclo().equals(alumnoCiclo))
                    .collect(Collectors.toList());
            alumnosCiclosCursosByAluCic = this.analizedAlumnoCicloCursosByCiclo(alumnosCiclosCursosByAluCic);
            if (alumnosCiclosCursosByAluCic.isEmpty()) {
                Long count = alumnoCicloCursoDAO.countByAlumnoCiclo(alumnoCiclo);
                if (count == 0) {
                    alumnoCicloDAO.delete(alumnoCiclo);
                    if (idx > 0) {
                        idx++;
                    }
                    continue;
                }
            }

            logger.debug("Ciclo {}, Cursos {}", alumnoCiclo.getCicloAcademico().getCodigo(), alumnosCiclosCursosByAluCic.size());

            List<AlumnoCicloCurso> rci = alumnosCiclosCursosByAluCic.stream().filter(x -> x.getIsEstadoRCI()).collect(Collectors.toList());
            List<AlumnoCicloCurso> rcu = alumnosCiclosCursosByAluCic.stream().filter(x -> x.getIsEstadoRCU()).collect(Collectors.toList());
            List<AlumnoCicloCurso> mat = alumnosCiclosCursosByAluCic.stream().filter(x -> x.getIsEstadoMatriculado()).collect(Collectors.toList());
            List<AlumnoCicloCurso> ret = alumnosCiclosCursosByAluCic.stream().filter(x -> x.getIsEstadoRET()).collect(Collectors.toList());
            EstadoMatriculaEnum estadoMatriculaEnum = EstadoMatriculaEnum.MAT;
            if (rcu.size() == alumnosCiclosCursosByAluCic.size() || ret.size() == alumnosCiclosCursosByAluCic.size()) {
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
            alumnoCicloDAO.update(alumnoCiclo);

            logger.debug("Situación Inicio {}", ObjectUtil.getParent(alumnoCiclo, "situacionInicio.codigo"));
            logger.debug("Situación Final {}", ObjectUtil.getParent(alumnoCiclo, "situacionFinal.codigo"));
            idx++;
            alumnoCicloAnterior = (AlumnoCiclo) alumnoCiclo.clone();
        }
    }

    public void evaluarPrimerCiclo(Alumno alumno, AlumnoCiclo alumnoCiclo) {
        SituacionAcademica situacionN = new SituacionAcademica(SituacionAcademicaEnum.S_N.getId());
        SituacionAcademica situacion8 = new SituacionAcademica(SituacionAcademicaEnum.S_8.getId());
        SituacionAcademica situacion9 = new SituacionAcademica(SituacionAcademicaEnum.S_9.getId());
        SituacionAcademica situacionQ = new SituacionAcademica(SituacionAcademicaEnum.S_Q.getId());
        SituacionAcademica situacion7 = new SituacionAcademica(SituacionAcademicaEnum.S_7.getId());

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

            } else {
                if (alumnoCiclo.getCicloAcademico().equals(alumno.getCicloIngreso())) {
                    alumnoCiclo.setSituacionInicio(situacion8);
                    if (alumnoCiclo.isEstadoRetiradoCic()) {
                        alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
                    }
                    alumnoCicloDAO.update(alumnoCiclo);
                } else {
                    alumnoCiclo.setSituacionInicio(situacion9);
                    if (alumnoCiclo.isEstadoRetiradoCic()) {
                        alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
                    }
                    alumnoCicloDAO.update(alumnoCiclo);
                }
            }
        } else {
            //EPG
            alumnoCiclo.setSituacionInicio(situacionN);
            alumnoCiclo.setSituacionFinal(situacionN);
            //  alumnoCicloAnterior = (AlumnoCiclo) alumnoCiclo.clone();
            alumnoCicloDAO.update(alumnoCiclo);
        }
    }

    private void analizarEgresado(Alumno alumno, DataSessionPivot ds) {
        Egresado egresado = egresadoDAO.findPrincipalByAlumno(alumno);
        if (egresado != null && egresado.getCicloAcademico() != null) {

            SituacionAcademica situacionAcademicaEM = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_EM.getValue());

            AlumnoCiclo alumnoCicloEgresado = alumnoCicloDAO.findByAlumnoCiclo(alumno, alumno.getCicloActivo());
            alumnoCicloEgresado.setSituacionFinal(situacionAcademicaEM);
            alumnoCicloDAO.update(alumnoCicloEgresado);

            AlumnoCiclo alumnoCicloActiveAntrior = alumnoCicloDAO.findActiveAnteriorByAlumno(alumno, egresado.getCicloAcademico());
            if (alumnoCicloActiveAntrior != null) {
                alumnoCicloActiveAntrior.setSituacionFinal(situacionAcademicaEM);
                alumnoCicloDAO.update(alumnoCicloActiveAntrior);
            }
            Alumno alumnoUpd = new Alumno(alumno.getId());
            alumnoUpd.setSituacionAcademica(situacionAcademicaEM);
            alumnoDAO.updateSituacionAcad(alumnoUpd);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void calulcarSituacionAcademica(Alumno alumno, DataSessionPivot ds) {
        contadorComponent.iniciar(1);
        if (ds.getFechaAccionAudit() == null) {
            ds.setFechaAccionAudit(new Date());
        }
        alumno = alumnoDAO.findAllInfo(alumno.getId());
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio().getOperativeModalidadEnum());
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        this.promediarAllCicloSync(alumno, cicloActivo, cicloAcademicoDAO.all(), alumnoCicloCursos, ds);
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
    private void promediarAlumno(Alumno alumno, CicloAcademico cicloActivo, List<AlumnoCicloCurso> allOperativesByModalidadEstudio, DataSessionPivot ds) {

        List<AlumnoCiclo> alumnosCiclosByAlumno = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
        List<String> ciclos = alumnosCiclosByAlumno.stream().map(x -> x.getCicloAcademico().getCodigo()).collect(Collectors.toList());
        logger.debug("Alumno Id {}, Codigo {}", alumno.getId(), alumno.getCodigo());
        logger.debug("Ciclos matriculados del alumno {}", String.join(",", ciclos));

        int idx = 0;
        for (AlumnoCiclo alumnoCicloEach : alumnosCiclosByAlumno) {
            CicloAcademico ciclo = alumnoCicloEach.getCicloAcademico();
            logger.debug("################# Ciclo Padre {} {} {} ", ciclo.getId(), ciclo.getYear(), ciclo.getNumeroCiclo());

            List<AlumnoCicloCurso> alumnoCicloCursoByCiclo = allOperativesByModalidadEstudio.stream().
                    filter(x -> x.getAlumnoCiclo().equals(alumnoCicloEach)).collect(Collectors.toList());

            List<AlumnoCicloCurso> alumnoCicloCursoAnteriores = allOperativesByModalidadEstudio.stream()
                    .filter(x -> x.getAlumnoCiclo().getCicloAcademico().getCodigoInt() < ciclo.getCodigoInt())
                    .collect(Collectors.toList());
            idx++;
            this.promediarHistorialNotasDiciembre(alumno, cicloActivo, ciclo, ds, null, alumnoCicloCursoByCiclo, alumnoCicloCursoAnteriores);
        }

    }

    private SituacionAcademica calculateSitutacionAcadFinal(Alumno alumno,
            AlumnoCiclo alumnoCiclo, SituacionAcademica situacionInicial,
            Integer ciclosEstudiados, AlumnoCiclo alumnoCicloInhaAnterior) {
        SituacionAcademica situacionAcademicaFinal = null;
        if (alumnoCiclo.getCicloAcademico().isTipoNivelacion()) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
        } else if ((ciclosEstudiados.intValue() == 1 || ciclosEstudiados.intValue() == 2) && alumno.isPregrado()) {
            if (TypesUtil.getInt(alumnoCiclo.getCicloAcademico().getCodigo()) >= 201710) {
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
            } else {
                if (alumnoCiclo.isAprobado() || ciclosEstudiados.intValue() == 1) {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
                } else {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_1.getValue());
                }
            }
        } else if (alumnoCiclo.getSituacionInicio().isCodigoS4()) {
            if (alumnoCiclo.isAprobado()) {
                //normal con antecedentes
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_5.getValue());
            } else {
                //separado definitivo
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_X.getValue());
            }
        } else {
            situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCiclo, alumnoCiclo.getSituacionInicio(), -1, alumnoCiclo.getCreditosAprobadosAcumulados(), alumnoCiclo.getCicloAcademico());
        }
        if (alumnoCicloInhaAnterior != null && alumnoCicloInhaAnterior.getSituacionFinal().isTrikeado()) {
            situacionAcademicaFinal = situacionInicial;
            if (alumnoCiclo.isAprobado()) {
                if (situacionAcademicaFinal.isCodigoS6()) {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_3.getValue());
                }
            } else {
                SituacionAcademica situacionSeparado = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_4.getValue());
                situacionAcademicaFinal = situacionSeparado;
            }
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
            AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findActiveAnteriorByAlumno(alumno, cicloAcademico);
            CicloAcademico cicloActivo = cicloAcademicoDAO.findActivoByModalidad(alumno.getModalidadEstudio());
            //    AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCicloEstado(alumno, cicloAcademico, Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.INH, EstadoMatriculaEnum.RCI));
            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            //   alumnoCicloDAO.findLock(alumnoCiclo.getId());

            //     AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCursoEstados(alumno, cicloAcademico, curso, Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.INH, EstadoMatriculaEnum.RCI));        
            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);

            /*
        if (alumnoCiclo != null && (alumnoCiclo.isEstadoInhabilitado() || alumnoCiclo.isEstadoRetiradoCic())) {
            return;
        }
             */
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
            } else {
                if (alumnoCiclo.isNoMatriculado() || alumnoCiclo.isEstadoRetiradoCic()) {
                    SituacionAcademica situacionInicio = alumnoCicloAnterior == null ? alumno.getSituacionAcademica() : alumnoCicloAnterior.getSituacionFinal();
                    alumnoCiclo.setSituacionInicio(situacionInicio);
                    alumnoCiclo.setSituacionFinal(situacionInicio);
                    alumnoCicloDAO.update(alumnoCiclo);
                    alumno.getId();
                }
            }

            if (alumnoCicloCurso == null) {
                alumnoCicloCurso = new AlumnoCicloCurso();
                alumnoCicloCurso.defaultValuesToCreate(alumnoCiclo, curso, matriculaCurso, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
                alumnoCicloCurso.setEstaAprobado(aprobado);
                alumnoCicloCurso.setVecesCursado(BigDecimal.ONE.intValue());
                alumnoCicloCursoDAO.save(alumnoCicloCurso);
                alumnoCicloCurso.getId();
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
            this.promediarHistorialNotas(alumno, cicloActivo, cicloAcademico, ds);

        } catch (Exception e) {

            String excepcion = this.messageException(e);

            logger.error("####Error en el hilo alumno " + alumno.getCodigo()
                    + " ciclo " + cicloAcademico.getId());//, e 
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void generarHistorialNotas2(Alumno alumno,
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

    public Integer countVecesAnteriores(List<MatriculaCurso> matriculasCursosAlumno, CicloAcademico cicloAcademico, Curso curso) {
        List<MatriculaCurso> matriculasCursosAnterioresByCurso = matriculasCursosAlumno.stream().filter(
                x -> (x.getMatriculaResumen().getCicloAcademico().getCodigoInt() < cicloAcademico.getCodigoInt()
                && x.getCurso().equals(curso))
                && x.getMatriculaResumen().getCicloAcademico().isTipoRegular()
                && x.isEstadoMAT())
                .collect(Collectors.toList());
        return matriculasCursosAnterioresByCurso.size();
    }

    public Integer countVecesAnterioresReg(List<MatriculaCurso> matriculasCursosAlumno, CicloAcademico cicloAcademico, Curso curso) {
        List<MatriculaCurso> matriculasCursosAnterioresByCurso = matriculasCursosAlumno.stream().filter(
                x -> (x.getMatriculaResumen().getCicloAcademico().getCodigoInt() < cicloAcademico.getCodigoInt()
                && x.getCurso().equals(curso))
                && x.getMatriculaResumen().getCicloAcademico().isTipoRegular()
                && x.isEstadoMAT())
                .collect(Collectors.toList());
        return matriculasCursosAnterioresByCurso.size();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void promediarHistorialNotas(Alumno alumno, CicloAcademico cicloActivo, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        this.promediarHistorialNotasDiciembre(alumno, cicloActivo, cicloAcademico, ds, null, null, null);
    }

    /*
    @Transactional(propagation = Propagation.SUPPORTS)
    private void promediarHistorialNotas(
            Alumno alumno, CicloAcademico cicloActivo, CicloAcademico cicloAcademico, DataSessionPivot ds,
            List<AlumnoCiclo> alumnoCiclos, List<AlumnoCicloCurso> alumnosCiclosCursoActual, List<AlumnoCicloCurso> alumnosCiclosCursoAnterior) {

        logger.debug("$$$$$$$$$$$$$$ promediarHistorialNotas Ciclo Activo {} {} {}, Ciclo Academico {} {} {}",
                cicloActivo.getId(),
                cicloActivo.getYear(),
                cicloActivo.getNumeroCiclo(),
                cicloAcademico.getId(),
                cicloAcademico.getYear(),
                cicloAcademico.getNumeroCiclo());

        SituacionAcademica situacionTrika = null;
        CicloAcademico siguienteCicloReg = cicloAcademicoDAO.findSiguienteRegularActivo(cicloAcademico);
        CicloAcademico siguienteCiclonNiv = cicloAcademicoDAO.findSiguienteNivelacionActivo(cicloAcademico);

        //  CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio());
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloAnteriorActive = alumnoCicloDAO.findActiveAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloSiguienteActive = alumnoCicloDAO.findActiveSiguienteByAlumno(alumno, cicloAcademico);
        // AlumnoCiclo alumnoCicloAnteriorAllState = alumnoCicloDAO.findAnteriorByAlumno(alumno, cicloAcademico);

        AlumnoCiclo alumnoCicloCorrespSgtNivelacion = alumnoCicloDAO.findByAlumnoCiclo(alumno, siguienteCiclonNiv);
        AlumnoCiclo alumnoCicloCorrespSgtRegular = alumnoCicloDAO.findByAlumnoCiclo(alumno, siguienteCicloReg);
        if (alumnoCicloCorrespSgtNivelacion != null) {
         
          //  Si ciclo nivelacion es superior al ciclo regular, entonces el siguiente ciclo inmediato del alumno es un ciclo regular
     
            if (alumnoCicloCorrespSgtRegular != null && alumnoCicloCorrespSgtNivelacion.getCicloAcademico().getCodigoInt() > alumnoCicloCorrespSgtRegular.getCicloAcademico().getCodigoInt()) {
                alumnoCicloCorrespSgtNivelacion = null;
            }
        }

        AlumnoCiclo alumnoCicloAnteriorInha = alumnoCicloDAO.findInhaAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloSiguienteInha = alumnoCicloDAO.findInhaSiguienteByAlumno(alumno, cicloAcademico);
        SituacionAcademica situacionAcademicaFinal = null;

        if (alumnoCicloAnteriorActive != null) {
            alumnoCiclo.setSituacionInicio(alumnoCicloAnteriorActive.getSituacionFinal());
        }
        if (alumnoCicloAnteriorInha != null) {
            if (alumnoCicloAnteriorInha.getCicloAcademico().getCodigoInt() > alumnoCicloAnteriorActive.getCicloAcademico().getCodigoInt()) {
                alumnoCiclo.setSituacionInicio(alumnoCicloAnteriorInha.getSituacionFinal());
            }
        }

        logger.debug("PromediarHistorialNotas Alumno {},"
                + " Ciclo Academico {} {} Estado Ciclo Alumno {}, "
                + " Situacion Inicial Id {} Codigo {} Nombre {}, ",
                alumno.getId(),
                cicloAcademico.getId(), cicloAcademico.getDescripcion(), alumnoCiclo.getEstado(),
                alumnoCiclo.getSituacionInicio().getId(),
                alumnoCiclo.getSituacionInicio().getCodigo(),
                alumnoCiclo.getSituacionInicio().getNombre());

        if (alumnoCiclo.isNoMatriculado() || alumnoCiclo.isEstadoRetiradoCic()) {
            if (this.evaluarNoMatriculadoOrRetiradoCic(alumno, cicloAcademico, alumnoCiclo, alumnoCicloSiguienteActive, ds)) {
                return;
            }
        } else {
            Long ciclosEstudiados = alumnoCiclos == null
                    ? alumnoCicloDAO.countCiclosEstudiados(alumno, cicloAcademico)
                    : alumnoCiclos.stream().filter(x -> x.getCicloAcademico().getCodigoInt() <= alumnoCiclo.getCicloAcademico().getCodigoInt()).collect(Collectors.toList()).size();

            //Obtenemos la informacion del ciclo actual
            alumnosCiclosCursoActual = alumnosCiclosCursoActual == null ? alumnoCicloCursoDAO.allOperativesByAlumnoCiclo(alumno, cicloAcademico) : alumnosCiclosCursoActual;
            //obtenemos la informacion de los ciclos anteriores para los acumulados
            alumnosCiclosCursoAnterior = alumnosCiclosCursoAnterior == null ? alumnoCicloCursoDAO.allOperativesByAlumnoAnterioresCiclo(alumno, cicloAcademico) : alumnosCiclosCursoAnterior;

            this.procesarInformacionAlumnoCiclo(ds, alumnoCiclo,
                    alumnoCicloSiguienteInha,
                    alumnosCiclosCursoActual,
                    alumnosCiclosCursoAnterior);
            boolean generarTrika = alumnoCiclo.isGenerarTrika();
            
//            logger.debug("Ciclo Academico {} {},Promedio Ciclo {} Aprobado {}, Situacion Inicial Id {} Codigo {} Nombre {}",
//                    cicloAcademico.getId(), cicloAcademico.getDescripcion(),
//                    alumnoCiclo.getPromedioCiclo(),
//                    alumnoCiclo.getEstaAprobado(),
//                    alumnoCiclo.getSituacionInicio().getId(), alumnoCiclo.getSituacionInicio().getCodigo(), alumnoCiclo.getSituacionInicio().getNombre());
//             
            if (alumnoCiclo.getCicloAcademico().isAmnistiado()) {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
            } else {
//                  if (alumnoCicloAnteriorActive != null) {
//                    alumnoCiclo.setSituacionInicio(alumnoCicloAnteriorActive.getSituacionFinal());
//                }
                situacionAcademicaFinal = calculateSitutacionAcadFinal(alumno, alumnoCiclo, alumnoCiclo.getSituacionInicio(), ciclosEstudiados.intValue(), alumnoCicloAnteriorInha);
                if (situacionAcademicaFinal != null) {
                    logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionAcademicaFinal.getId(), situacionAcademicaFinal.getCodigo(), situacionAcademicaFinal.getNombre());
                } else {
                    logger.debug("No se pudo hallar su situacion final, se le pondra la anterior");
                }
            }
            alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
            alumnoCicloDAO.update(alumnoCiclo);
            if (situacionAcademicaFinal == null) {
                logger.debug(">>>>>>>>>>>>>>>>>> el alumno {}", alumnoCiclo.getAlumno().getId());
            }
            if (generarTrika) { //&& situacionAcademicaFinal.isCodigoS6() 
                logger.debug("Generara registro fantasma trika");
                situacionTrika = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_T.getValue());
                SituacionAcademica situacionSuspendidoS6 = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_6.getValue());

                alumnoCiclo.setSituacionFinal(situacionTrika);
                alumnoCicloDAO.update(alumnoCiclo);

                if (alumnoCicloCorrespSgtRegular == null) {
                    alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                    alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCicloReg, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionTrika);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionSuspendidoS6);
                    alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                    alumnoCicloDAO.save(alumnoCicloCorrespSgtRegular);
                } else {
                    List<AlumnoCicloCurso> alusCicloCursos = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCicloCorrespSgtRegular);
                    if (alusCicloCursos.isEmpty()) {
                        alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                        alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionTrika);
                        alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionSuspendidoS6);
                        alumnoCicloDAO.update(alumnoCicloCorrespSgtRegular);
                    }
                }
            } else if (situacionAcademicaFinal.isCodigoS6() && cicloAcademico.isTipoRegular()) {
                logger.debug("Generara registro fantasma prueba codigo situacion 3");
                SituacionAcademica situacionAcademicaS3 = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_6.getValue());

                if (alumnoCicloCorrespSgtRegular == null) {
                    alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                    alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCicloReg, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionTrika);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionAcademicaS3);
                    alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                    alumnoCicloDAO.save(alumnoCicloCorrespSgtRegular);
                } else {
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionAcademicaS3);
                    alumnoCicloDAO.update(alumnoCicloCorrespSgtRegular);
                }
            }

            Alumno alumnoUpd = new Alumno();
            alumnoUpd.setId(alumno.getId());
            alumnoUpd.setCicloActivo(alumnoCiclo.getCicloAcademico());
            alumnoUpd.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
            alumnoUpd.setCreditosCursados(alumnoCiclo.getCreditosAcumulados());
            alumnoUpd.setSituacionAcademica(situacionAcademicaFinal);
            if (generarTrika) {
                alumnoUpd.setSituacionAcademica(situacionTrika);
            }
            alumnoDAO.updateSituacionCicloCapa(alumnoUpd);

            if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
                alumnoUpd.setCicloActivoRegular(alumnoCiclo.getCicloAcademico());
                alumnoDAO.updateCicloActivoRegular(alumnoUpd);
            }

            alumno = alumnoDAO.find(alumno);

//             alumno.setId(alumno.getId());
//            alumno.setCicloActivo(alumnoCiclo.getCicloAcademico());
//            alumno.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
//            alumno.setCreditosCursados(alumnoCiclo.getCreditosAcumulados());
//            alumno.setSituacionAcademica(situacionAcademicaFinal);
//            if (generarTrika) {
//                alumno.setSituacionAcademica(situacionTrika);
//            }
//
//            if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
//                alumno.setCicloActivoRegular(alumnoCiclo.getCicloAcademico());
//            }
//            alumnoDAO.update(alumno);
//            
        }

        if (!Arrays.asList(S_X.getValue(), S_XD.getValue(), S_EM.getValue(), S_7.getValue(), S_D.getValue()).contains(alumnoCiclo.getSituacionFinal().getCodigo())) {
            if ((alumnoCicloCorrespSgtNivelacion == null && alumnoCicloCorrespSgtRegular == null) /) {
//    || (alumnoCicloLast.getId().compareTo(alumnoCiclo.getId()) == 0
//                    && (alumnoCicloLast.isNoMatriculado() || alumnoCicloLast.isEstadoRetiradoCic()))
//    
    
                if (siguienteCicloReg.getCodigoInt() < cicloActivo.getCodigoInt()) {
                    if (alumnoCicloCorrespSgtRegular == null) {
                        alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                        alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCicloReg, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                        alumnoCicloCorrespSgtRegular.setSituacionInicio(alumnoCiclo.getSituacionFinal());
                        alumnoCicloCorrespSgtRegular.setSituacionFinal(alumnoCiclo.getSituacionFinal());
                        alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                        alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                        alumnoCicloDAO.save(alumnoCicloCorrespSgtRegular);
                        logger.debug("Creado alumno ciclo nmat para el ciclo {}", siguienteCicloReg.getCodigo());
                    }
                    promediarHistorialNotas(alumno, cicloActivo, siguienteCicloReg, ds);
                }
            }
        }
    }
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void analizarDesertor(Alumno alumno, CicloAcademico cicloActivo, List<CicloAcademico> ciclos, DataSessionPivot ds) {
        List<AlumnoCiclo> alumnosCiclosByAlumno = alumnoCicloDAO.allByAlumnoAsc(alumno);
        logger.debug("Analizar desertor");
        List<String> ciclosStr = alumnosCiclosByAlumno.stream().map(x -> x.getCicloAcademico().getCodigo() + " " + x.getEstado()).collect(Collectors.toList());
        logger.debug(String.join(",", ciclosStr));

        List<CicloAcademico> cicloRegularesByModalidad = this.allCiclosRegularesByModalidadEstudio(alumno.getModalidadEstudio().getOperativeModalidadEnum(), ciclos, cicloActivo);

        if (!alumnosCiclosByAlumno.isEmpty()) {
            for (AlumnoCiclo alumnoCiclo : alumnosCiclosByAlumno) {
                this.analizarDesertorByCiclo(alumno, cicloActivo, alumnoCiclo.getCicloAcademico(), ds);
            }
        } else {
            if (!alumno.getSituacionAcademica().isCodigoS7()) {
                if (alumno.getCicloIngreso() != null) {
                    int ciclosNmat = cicloRegularesByModalidad.stream().filter(x -> x.getCodigoInt() >= alumno.getCicloIngreso().getCodigoInt())
                            .collect(Collectors.toList())
                            .size();
                    int diffYears = cicloActivo.getYear() - alumno.getCicloIngreso().getYear();
                    if (alumno.isQuintoSecundaria()) {
                        if (diffYears >= 1) {
                            Alumno alumnoUpd = new Alumno(alumno.getId());
                            alumnoUpd.setSituacionAcademica(new SituacionAcademica(SituacionAcademicaEnum.S_8.getId()));
                            alumnoDAO.updateSituacionAcad(alumnoUpd);
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
                            situacion = new SituacionAcademica(SituacionAcademicaEnum.S_9.getId());
                        }
                        if (ciclosNmat > 1) {
                            situacion = new SituacionAcademica(SituacionAcademicaEnum.S_7.getId());
                        }
                        if (situacion != null) {
                            Alumno alumnoUpd = new Alumno(alumno.getId());
                            alumnoUpd.setSituacionAcademica(situacion);
                            alumnoDAO.updateSituacionAcad(alumnoUpd);
                        }
                    }
                }
            }
        }
    }

    private List<CicloAcademico> allCiclosRegularesByModalidadEstudio(ModalidadEstudioEnum modalidadEstudioEnum, List<CicloAcademico> ciclosAcademicos, CicloAcademico cicloActivo) {
//        ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(modalidadEstudioAlumno.getCodigo());
//        if (modalidadEstudioAlumno.getIsEspecial()) {
//            modalidadEstudioEnum = ModalidadEstudioEnum.EPG;
//        }
//        if (modalidadEstudioAlumno.getIsVisitante()) {
//            modalidadEstudioEnum = ModalidadEstudioEnum.PRE;
//        }
        final ModalidadEstudioEnum fModalidadEstudioEnum = modalidadEstudioEnum;
        List<CicloAcademico> ciclosByModalidad = ciclosAcademicos.stream()
                .filter(x -> x.getModalidadEstudio().getCodigo().equals(fModalidadEstudioEnum.name()))
                .filter(x -> x.isTipoRegular())
                .filter(x -> x.getCodigoInt() < cicloActivo.getCodigoInt())
                .collect(Collectors.toList());
        return ciclosByModalidad;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void analizarDesertorByCiclo(
            Alumno alumno, CicloAcademico cicloActivo, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        logger.debug("$$$$$$$$$$$$$$ analizar desertor, Alumno {}, Ciclo Code {} Id {}, Ciclo Activo Code {} Id {}",
                alumno.getId(),
                cicloAcademico.getCodigo(),
                cicloAcademico.getId(),
                cicloActivo.getCodigo(),
                cicloActivo.getId());

        ModalidadEstudioEnum modalidadEstudio = ModalidadEstudioEnum.valueOf(alumno.getModalidadEstudio().getCodigo());
        CicloAcademico siguienteCicloReg = cicloAcademicoDAO.findSiguienteRegularActivo(cicloAcademico, modalidadEstudio);

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloSiguienteActive = alumnoCicloDAO.findActiveSiguienteByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloLastMat = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);

        AlumnoCiclo alumnoCicloCorrespSgtRegular = alumnoCicloDAO.findByAlumnoCiclo(alumno, siguienteCicloReg);

        if (alumnoCiclo.isEstadoRetiradoCic() || alumnoCiclo.isNoMatriculado()) {
            if (alumnoCicloAnterior != null) {
                alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
                alumnoCiclo.setSituacionFinal(alumnoCicloAnterior.getSituacionFinal());
            } else {
                alumnoCiclo.setSituacionFinal(alumnoCiclo.getSituacionInicio());
            }
            alumnoCicloDAO.update(alumnoCiclo);
        }

        if (alumnoCiclo.getSituacionFinal().isCodigoD() || alumnoCiclo.getSituacionFinal().isCodigoS4()
                || alumnoCiclo.getSituacionFinal().isCodigoS4U() || alumnoCiclo.getSituacionFinal().isCodigoEM()) {
            return;
        }

        if (alumnoCiclo.isNoMatriculado() || alumnoCiclo.isEstadoRetiradoCic()) {
            if (this.evaluarNoMatriculadoOrRetiradoCic(alumno, cicloAcademico, alumnoCiclo, alumnoCicloSiguienteActive, alumnoCicloLastMat, ds)) {
                return;
            }
        }

        if (alumnoCicloCorrespSgtRegular == null) {
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
                    alumnoCicloDAO.save(alumnoCicloCorrespSgtRegular);
                    logger.debug("Creado alumno ciclo nmat para el ciclo {}", siguienteCicloReg.getCodigo());
                    analizarDesertorByCiclo(alumno, cicloActivo, siguienteCicloReg, ds);
                }
            }
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void promediarHistorialNotasDiciembre(
            Alumno alumno, CicloAcademico cicloActivo, CicloAcademico cicloAcademico, DataSessionPivot ds,
            List<AlumnoCiclo> alumnoCiclos, List<AlumnoCicloCurso> alumnosCiclosCursoActual, List<AlumnoCicloCurso> alumnosCiclosCursoAnterior) {

        logger.debug("$$$$$$$$$$$$$$ promediarHistorialNotas Ciclo Activo {} , Ciclo Academico {} {} {}",
                cicloActivo.toString(),
                cicloAcademico.toString());

        // SituacionAcademica situacionTrika = null;
        ModalidadEstudioEnum modalidadEstudioEnum = ModalidadEstudioEnum.valueOf(alumno.getModalidadEstudio().getCodigo());
        CicloAcademico siguienteCiclo = cicloAcademicoDAO.findSiguienteRegularActivo(cicloAcademico, modalidadEstudioEnum);

        //  CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio());
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloAnteriorActive = alumnoCicloDAO.findActiveAnteriorByAlumno(alumno, cicloAcademico);

        AlumnoCiclo alumnoCicloCorrespSgtRegular = alumnoCicloDAO.findByAlumnoCiclo(alumno, siguienteCiclo);

        AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloAnteriorInha = alumnoCicloDAO.findInhaAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloSiguienteInha = alumnoCicloDAO.findInhaSiguienteByAlumno(alumno, cicloAcademico);
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

        logger.debug("Situacion Inicial {}",
                alumnoCiclo.getSituacionInicio().toString());

        final AlumnoCiclo fAlumnoCiclo = (AlumnoCiclo) alumnoCiclo.clone();
        Long ciclosEstudiados = alumnoCiclos == null
                ? alumnoCicloDAO.countCiclosEstudiados(alumno, cicloAcademico)
                : alumnoCiclos.stream().filter(x -> x.getCicloAcademico().getCodigoInt() <= fAlumnoCiclo.getCicloAcademico().getCodigoInt()).collect(Collectors.toList()).size();

        /*Obtenemos la informacion del ciclo actual*/
        alumnosCiclosCursoActual = alumnosCiclosCursoActual == null ? alumnoCicloCursoDAO.allOperativesByAlumnoCiclo(alumno, cicloAcademico) : alumnosCiclosCursoActual;
        //obtenemos la informacion de los ciclos anteriores para los acumulados
        alumnosCiclosCursoAnterior = alumnosCiclosCursoAnterior == null ? alumnoCicloCursoDAO.allOperativesByAlumnoAnterioresCiclo(alumno, cicloAcademico) : alumnosCiclosCursoAnterior;

        this.procesarInformacionAlumnoCiclo(ds, alumnoCiclo,
                alumnoCicloSiguienteInha,
                alumnosCiclosCursoActual,
                alumnosCiclosCursoAnterior);
        boolean generarTrika = alumnoCiclo.isGenerarTrika();
        if (alumnoCiclo.getCicloAcademico().isAmnistiado()) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
        } else {
            situacionAcademicaFinal = calculateSitutacionAcadFinal(alumno, alumnoCiclo, alumnoCiclo.getSituacionInicio(), ciclosEstudiados.intValue(), alumnoCicloAnteriorInha);
            if (situacionAcademicaFinal != null) {
                logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionAcademicaFinal.getId(), situacionAcademicaFinal.getCodigo(), situacionAcademicaFinal.getNombre());
            } else {
                situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                logger.debug("No se pudo hallar su situacion final, se le pondra la anterior");
            }
        }
        alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
        alumnoCicloDAO.update(alumnoCiclo);

        if (situacionAcademicaFinal == null) {
            logger.debug(">>>>>>>>>>>>>>>>>> el alumno {}", alumnoCiclo.getAlumno().getId());
        }
        SituacionAcademica situacionTrika = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_T.getValue());
        if ((generarTrika && alumnoCiclo.getCicloAcademico().getCodigoInt() >= INI_TRIKA)
                && (!situacionAcademicaFinal.isCodigoS4() && !situacionAcademicaFinal.isCodigoS4U()))/* && situacionAcademicaFinal.isCodigoS6()*/ {
            logger.debug("Generara registro fantasma trika");

            SituacionAcademica situacionFinalForTrika = null;
            situacionFinalForTrika = situacionAcademicaFinal;
            if (situacionAcademicaFinal.isCodigoS6()) {
                if (alumnoCiclo.isUltimoCiclo()) {
                    situacionFinalForTrika = new SituacionAcademica(SituacionAcademicaEnum.S_3U.getId());
                } else {
                    situacionFinalForTrika = new SituacionAcademica(SituacionAcademicaEnum.S_3.getId());
                }
            }

            // SituacionAcademica situacionSuspendidoS6 = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_6.getValue());
            logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionTrika.getId(), situacionTrika.getCodigo(), situacionTrika.getNombre());

            if (alumnoCicloCorrespSgtRegular == null) {
                alumnoCiclo.setSituacionFinal(situacionTrika);
                alumnoCicloDAO.update(alumnoCiclo);

                alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCiclo, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionTrika);
                alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForTrika);
                alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                alumnoCicloDAO.save(alumnoCicloCorrespSgtRegular);
            } else {
                List<AlumnoCicloCurso> alusCicloCursos = alumnoCicloCursoDAO.allByAlumnoCicloNoFilters(alumnoCicloCorrespSgtRegular);
                if (alusCicloCursos.isEmpty()) {
                    alumnoCiclo.setSituacionFinal(situacionTrika);
                    alumnoCicloDAO.update(alumnoCiclo);

                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionTrika);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForTrika);
                    alumnoCicloDAO.update(alumnoCicloCorrespSgtRegular);
                } else {
                    logger.debug("No se podra Generara ciclo alumno fantasma trika, por que tiene cursos matriculados");
                    logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionAcademicaFinal.getId(), situacionAcademicaFinal.getCodigo(), situacionAcademicaFinal.getNombre());
                    alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
                    alumnoCicloDAO.update(alumnoCiclo);
                }
            }
        } else if (situacionAcademicaFinal.isCodigoS6() && cicloAcademico.isTipoRegular()) {
            logger.debug("Generara registro fantasma prueba codigo situacion 3");
            SituacionAcademica situacionFinalForSuspension = null;
            if (!alumnoCiclo.isAprobado()) {
                if (alumnoCiclo.isUltimoCiclo()) {
                    situacionFinalForSuspension = new SituacionAcademica(SituacionAcademicaEnum.S_3U.getId());
                } else {
                    situacionFinalForSuspension = new SituacionAcademica(SituacionAcademicaEnum.S_3.getId());
                }
            } else {
                situacionFinalForSuspension = new SituacionAcademica(SituacionAcademicaEnum.S_N.getId());
            }

            if (alumnoCicloCorrespSgtRegular == null) {
                alumnoCicloCorrespSgtRegular = new AlumnoCiclo();
                alumnoCicloCorrespSgtRegular.defaultValuesToCreate(alumno, siguienteCiclo, ds.getUsuario(), new DateTime(ds.getFechaAccionAudit()));
                alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionAcademicaFinal);
                alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForSuspension);
                alumnoCicloCorrespSgtRegular.setCreditosConvalidados(BigDecimal.ZERO.intValue());
                alumnoCicloDAO.save(alumnoCicloCorrespSgtRegular);
            } else {
                List<AlumnoCicloCurso> alusCicloCursos = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCicloCorrespSgtRegular);
                ObjectUtil.eliminarAttrSinId(alumnoCicloCorrespSgtRegular);
                if (alusCicloCursos.isEmpty()) {
                    alumnoCicloCorrespSgtRegular.setEstadoEnum(EstadoMatriculaEnum.INH);
                    alumnoCicloCorrespSgtRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloCorrespSgtRegular.setSituacionFinal(situacionFinalForSuspension);
                    alumnoCicloDAO.update(alumnoCicloCorrespSgtRegular);
                } else {
                    situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
                    logger.debug("No se podra Generara ciclo alumno fantasma prueba, por que tiene cursos matriculados");
                    logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionAcademicaFinal.getId(), situacionAcademicaFinal.getCodigo(), situacionAcademicaFinal.getNombre());
                    alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
                    alumnoCicloDAO.update(alumnoCiclo);
                }
            }
        }
        alumno.setCicloActivo(alumnoCiclo.getCicloAcademico());
        alumno.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
        alumno.setCreditosCursados(alumnoCiclo.getCreditosAcumulados());
        alumno.setSituacionAcademica(alumnoCiclo.getSituacionFinal());
        alumno.setPromedioAcumulado(alumnoCiclo.getPromedioAcumulado());

        Alumno alumnoUpd = new Alumno();
        alumnoUpd.setId(alumno.getId());
        alumnoUpd.setCicloActivo(alumnoCiclo.getCicloAcademico());
        alumnoUpd.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
        alumnoUpd.setCreditosCursados(alumnoCiclo.getCreditosAcumulados());
        alumnoUpd.setSituacionAcademica(alumnoCiclo.getSituacionFinal());
        alumnoUpd.setPromedioAcumulado(alumnoCiclo.getPromedioAcumulado());
        alumnoDAO.updateSituacionCicloCapaPPA(alumnoUpd);

        if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
            alumno.setCicloActivoRegular(alumnoCiclo.getCicloAcademico());
            alumnoUpd.setCicloActivoRegular(alumnoCiclo.getCicloAcademico());
            alumnoDAO.updateCicloActivoRegular(alumnoUpd);
        }
    }

    public List<AlumnoCicloCurso> analizedAlumnoCicloCursosByCiclo(List<AlumnoCicloCurso> alumnoCicloCursoByCiclo) {
        Map<String, List<AlumnoCicloCurso>> mapHistoByCiclo = TypesUtil.convertListToMapList("curso.codigo", alumnoCicloCursoByCiclo);

        for (Map.Entry<String, List<AlumnoCicloCurso>> entry : mapHistoByCiclo.entrySet()) {
            String codigoCurso = entry.getKey();
            List<AlumnoCicloCurso> histoByCicloAndCurso = entry.getValue();
            Collections.sort(histoByCicloAndCurso, (p1, p2) -> p1.getFechaRegistro().compareTo(p2.getFechaRegistro()));
            int idx = 0;
            for (AlumnoCicloCurso histo : histoByCicloAndCurso) {
                logger.debug("curso {}, fecha {}",
                        codigoCurso,
                        TypesUtil.getStringDate(histo.getFechaRegistro(), "dd/MM/yyyy H:mm:ss"));
                boolean registroActivo2 = ((idx + 1) == histoByCicloAndCurso.size());
                Integer registroActivo3 = histo.getRegistroActivo() == 1 && registroActivo2 ? 1 : 0;
                histo.setRegistroActivo(registroActivo3);
                alumnoCicloCursoDAO.update(histo);
                idx++;
            }
        }
        alumnoCicloCursoByCiclo = alumnoCicloCursoByCiclo.stream().filter(x -> x.getRegistroActivo() == 1).collect(Collectors.toList());
        return alumnoCicloCursoByCiclo;
    }

    public void procesarInformacionAlumnoCiclo(
            DataSessionPivot ds,
            AlumnoCiclo alumnoCiclo,
            AlumnoCiclo alumnoCicloSiguienteInha,
            List<AlumnoCicloCurso> alumnosCicloCursoActual,
            List<AlumnoCicloCurso> alumnosCicloCursoAnteriores) {

        Alumno alumno = alumnoCiclo.getAlumno();

        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;

        boolean generarTrika = false;

        alumnoCiclo.setCreditosCursadosCiclo(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCursosInscritos(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAcumulados(BigDecimal.ZERO.intValue());

        alumnoCiclo.setCursosAprobados(BigDecimal.ZERO.intValue());
        alumnoCiclo.setCreditosAprobadosAcumulados(BigDecimal.ZERO.intValue());

        alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());

        //procesamos la informacion del ciclo actual
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
            //     Integer vecesEstudiadoCurso = alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCiclo(alumnoCicloCursoEach.getCurso(), alumno, cicloAcademico).intValue();
            Integer vecesEstudiadoCurso = vecesLlevado.size();
            vecesEstudiadoCurso++;
            alumnoCicloCursoEach.setVecesCursado(vecesEstudiadoCurso);

            List<AlumnoCicloCurso> vecesLlevadoRegular = alumnosCicloCursoAnteriores.stream().filter(
                    x -> x.getCurso().equals(alumnoCicloCursoEach.getCurso())
                    && x.getEstaActivo()
                    && x.getIsEstadoMatriculado()
                    && x.getAlumnoCiclo().getCicloAcademico().isTipoRegular()
            ).collect(Collectors.toList());

            //    alumnoCicloCursoEach.setVecesCursadoRegular(alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCicloReg(alumnoCicloCursoEach.getCurso(), alumno, cicloAcademico).intValue());
            alumnoCicloCursoEach.setVecesCursadoRegular(vecesLlevadoRegular.size());
            if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
                alumnoCicloCursoEach.setVecesCursadoRegular(alumnoCicloCursoEach.getVecesCursadoRegular() + 1);
            }

            if (alumnoCicloCursoEach.isAprobado()) {
                alumnoCiclo.setCreditosAprobadosCiclo(alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCicloCursoEach.getCreditos());
                // cursosAprInscritosAlumnoCiclo += 1;
                alumnoCiclo.setCursosAprobados(alumnoCiclo.getCursosAprobados() + 1);
                alumnoCiclo.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados() + alumnoCicloCursoEach.getCreditos());
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);
            }

            if (alumnoCicloCursoEach.getVecesCursadoRegular() == VECES_TRIKA && !alumnoCicloCursoEach.isAprobado()) {
                generarTrika = true;
            }
        }
        //si la nota se modificó y un alumno trikeado deja de serlo
        if (!generarTrika && (alumnoCicloSiguienteInha != null && alumnoCicloSiguienteInha.getSituacionFinal().isTrikeado())) {
            List<AlumnoCicloCurso> alumnoCiclosCursos = alumnoCicloCursoDAO.allStateByAlumnoCiclo(alumnoCicloSiguienteInha);
            if (alumnoCiclosCursos.isEmpty()) {
                alumnoCicloDAO.delete(alumnoCicloSiguienteInha);
            }
        }

        BigDecimal sumNotasCreditosTotal = sumNotasCreditos;
        BigDecimal sumCreditosTotal = sumCreditos;

        //procesamos la informacion de los ciclos anteriores
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

        alumnoCiclo.setUserModificacion(ds.getUsuario());
        alumnoCiclo.setFechaModificacion(ds.getFechaAccionAudit());

        //falta evaluar que sucede cuando todos los cursos son de evaluacion letras
        if (alumnosCicloCursoActual.size() == BigDecimal.ONE.intValue()) {
            alumnoCiclo.setEstaAprobado(alumnosCicloCursoActual.get(0).getEstaAprobado());
        } else {
            Integer aprobado = evaluateEstaAprobado(promedio, alumno);
            alumnoCiclo.setEstaAprobado(aprobado);
        }
        alumnoCicloDAO.update(alumnoCiclo);
        alumnoCiclo.setGenerarTrika(generarTrika);
    }

    public boolean evaluarNoMatriculadoOrRetiradoCic(Alumno alumno,
            CicloAcademico cicloAcademico, AlumnoCiclo alumnoCiclo, AlumnoCiclo alumnoCicloSiguiente, AlumnoCiclo alumnoCicloLastMat,
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
            logger.debug("Ciclo a evaluar alumnocilo {}, cilo {}", alumnoCicloEach.toString(), alumnoCicloEach.getCicloAcademico().toString());
            if (alumnoCicloEach.getSituacionFinal().isCodigoD()) {
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
            alumnoCicloCurso.setCreditos(matriculaCurso.getCreditos());
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
        } else {
            if (nota.compareTo(new BigDecimal(11)) >= 0) {
                aprobado = BigDecimal.ONE.intValue();
            }
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
