package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.AlumnoEvaluacionElim;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionEliminada;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionPlan;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.NotaLetra;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.academico.ReclamoNota;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.model.academico.TipoEvaluacion;
import pe.edu.lamolina.model.enums.AlumnoEvaluacionEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.MotivoAnulacionEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEvalEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionEliminadaDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SistemaNotasDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.NotaLetraDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.ReclamoNotaDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenAlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;

@Service
@Transactional(readOnly = true)
public class CargaAcademicaServiceImp implements CargaAcademicaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    PlanCalificacionDAO planCalificacionDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    TipoEvaluacionDAO tipoEvaluacionDAO;

    @Autowired
    EvaluacionPlanDAO evaluacionPlanDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    EvaluacionSeccionDAO evaluacionSeccionDAO;

    @Autowired
    EvaluacionDAO evaluacionDAO;

    @Autowired
    SistemaNotasDAO sistemaNotasDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;

    @Autowired
    ReclamoNotaDAO reclamoNotaDAO;

    @Autowired
    ResumenAlumnoEvaluacionDAO resumenAlumnoEvaluacionDAO;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @Autowired
    PlanCalificacionCursoDAO planCalificacionCursoDAO;

    @Autowired
    EvaluacionEliminadaDAO evaluacionEliminadaDAO;

    @Autowired
    CalculoNotasService calculoNotasService;

    @Autowired
    NotaLetraDAO notaLetraDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    PromedioService promedioService;

    @Override
    public List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByDocente(docente, ciclo);
        Map<Long, DocenteSeccion> mapDocentesSeccion = MapUtil.storeItems("seccion.id", docentesSecciones);

        logger.debug("Cantidad docente seccion {}", docentesSecciones.size());
        List<Long> lstIds = new ArrayList<>();
        for (DocenteSeccion docenteSeccion : docentesSecciones) {
            lstIds.add(docenteSeccion.getSeccion().getGrupoSeccion().getId());
            logger.debug("la seccion {}, grupo {}", docenteSeccion.getSeccion().getId(), docenteSeccion.getSeccion().getGrupoSeccion().getId());
        }

        logger.debug("Lista de grupos para el filtro {}", StringUtils.join(lstIds, ","));
        List<GrupoSeccion> gruposSeccion = grupoSeccionDAO.allByFilter(lstIds, ciclo, null, EstadoEnum.ACT);
        logger.debug("Lista grupo seccion tamaño {}", gruposSeccion.size());
        List<DocenteSeccion> responsables = docenteSeccionDAO.allResponsablesByGpoSecciones(gruposSeccion, ciclo);
        Map<Long, DocenteSeccion> mapResponsables = MapUtil.storeItems("seccion.grupoSeccion.id", responsables);
        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            grupoSeccion.setSecciones(new ArrayList());
            DocenteSeccion responsable = mapResponsables.get(grupoSeccion.getId());
            grupoSeccion.setDocenteResponsable(responsable.getDocente());
        }

        Map<Long, GrupoSeccion> mapGposSeccion = MapUtil.storeItems("id", gruposSeccion);

        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gruposSeccion);
        Map<Long, Seccion> mapSecciones = MapUtil.storeItems("id", secciones);
        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(new ArrayList());
            GrupoSeccion gpoSecc = mapGposSeccion.get(seccion.getGrupoSeccion().getId());
            seccion.setGrupoSeccion(gpoSecc);
            gpoSecc.getSecciones().add(seccion);

            DocenteSeccion profeSeccion = mapDocentesSeccion.get(seccion.getId());
            Docente responsable = gpoSecc.getDocenteResponsable();
            if (profeSeccion != null) {
                seccion.setVerInformacion(true);
            } else if (responsable != null && responsable.getId() == docente.getId().longValue()) {
                seccion.setVerInformacion(true);
            }
        }

        for (DocenteSeccion profeSecc : docentesSecciones) {
            Seccion secc = mapSecciones.get(profeSecc.getSeccion().getId());
            if (secc == null) {
                continue;
            }
            profeSecc.setSeccion(secc);
            secc.getDocenteSeccion().add(profeSecc);
        }

        for (GrupoSeccion gpoSecc : gruposSeccion) {
            List<PlanCalificacionCurso> planCalificacionCursos = planCalificacionCursoDAO.allByFilter(null, ds.getCicloAcademico().getTipoCicloEnum(), gpoSecc.getCurso(), EstadoEnum.ACT);
            gpoSecc.getCurso().setPlanesCalificacionCursos(planCalificacionCursos);
            logger.debug("PlanCalificacionCurso del curso {}, con tipo de ciclo {}, cantidad {}",
                    gpoSecc.getCurso().getId(), ds.getCicloAcademico().getTipoCicloEnum().name(), planCalificacionCursos.size());

            List<Seccion> seccion = gpoSecc.getSecciones();
            logger.debug("GrupoSecc {}-{} tiene {} secciones", gpoSecc.getId(), gpoSecc.getCodigo(), gpoSecc.getSecciones().size());
            for (Seccion secc : seccion) {
                List<DocenteSeccion> docSeccs = secc.getDocenteSeccion();
                logger.debug("\tSeccion {}-{} hay {} docentes", secc.getCodigo(), secc.getCodigo2(), docSeccs.size());
            }
        }

        return gruposSeccion;
    }

    public List<DocenteSeccion> allDocenteSeccion(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByDocente(docente, ciclo);
        logger.debug("Cantidad docente seccion {}", docentesSecciones.size());
        List<Long> lstIds = new ArrayList<>();
        for (DocenteSeccion docenteSeccion : docentesSecciones) {
            lstIds.add(docenteSeccion.getSeccion().getGrupoSeccion().getId());
            logger.debug("la seccion {}, grupo {}", docenteSeccion.getSeccion().getId(), docenteSeccion.getSeccion().getGrupoSeccion().getId());
        }

        logger.debug("Lista de grupos para el filtro {}", StringUtils.join(lstIds, ","));
        List<GrupoSeccion> gruposSeccion = grupoSeccionDAO.allByFilter(lstIds, ciclo, null, EstadoEnum.ACT);
        logger.debug("Lista grupo seccion tamaño {}", gruposSeccion.size());
        gruposSeccion.forEach((grupoSeccion) -> {
            grupoSeccion.setSecciones(new ArrayList());
        });
        Map<Long, GrupoSeccion> mapGposSeccion = MapUtil.storeItems("id", gruposSeccion);

        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gruposSeccion);
        Map<Long, Seccion> mapSecciones = MapUtil.storeItems("id", secciones);
        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(new ArrayList());
            GrupoSeccion gpoSecc = mapGposSeccion.get(seccion.getGrupoSeccion().getId());
            seccion.setGrupoSeccion(gpoSecc);
            gpoSecc.getSecciones().add(seccion);
        }

        for (DocenteSeccion profeSecc : docentesSecciones) {
            Seccion secc = mapSecciones.get(profeSecc.getSeccion().getId());
            if (secc == null) {
                continue;
            }
            profeSecc.setSeccion(secc);
            secc.getDocenteSeccion().add(profeSecc);
        }

        for (GrupoSeccion gpoSecc : gruposSeccion) {
            List<Seccion> seccion = gpoSecc.getSecciones();
            logger.debug("GrupoSecc {}-{} tiene {} secciones", gpoSecc.getId(), gpoSecc.getCodigo(), gpoSecc.getSecciones().size());
            for (Seccion secc : seccion) {
                List<DocenteSeccion> docSeccs = secc.getDocenteSeccion();
                logger.debug("\tSeccion {}-{} hay {} docentes", secc.getCodigo(), secc.getCodigo2(), docSeccs.size());
            }
        }

        return docentesSecciones;
    }

    @Override
    public List<TipoEvaluacion> allTipoEvaluacion() {
        return tipoEvaluacionDAO.all();
    }

    @Override
    public List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico ciclo) {
        return docenteSeccionDAO.allByCargaAcademica(filter, docente, ciclo);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionByDocente(Docente docente, CicloAcademico ciclo) {
        return docenteSeccionDAO.allByDocente(docente, ciclo);
    }

    @Override
    public PlanCalificacion findPlanCalificacion(Long idPlanCalificacion) {
        return planCalificacionDAO.find(idPlanCalificacion);
    }

    @Override
    public DocenteSeccion findDocenteSeccionByFilter(Docente docente, Seccion seccion) {
        return docenteSeccionDAO.findByFilter(docente, seccion);
    }

    @Override
    public Curso findCurso(Long idCurso) {
        return cursoDAO.find(idCurso);
    }

    @Override
    public Seccion findSeccion(Long idSeccion) {
        return seccionDAO.find(idSeccion);
    }

    @Override
    public GrupoSeccion findGrupo(Long idGrupoSeccion) {
        GrupoSeccion gpoSecc = grupoSeccionDAO.find(idGrupoSeccion);
        List<EvaluacionSeccion> evalSeccs = evaluacionSeccionDAO.allByGrupoSeccion(gpoSecc);
        gpoSecc.setEvaluacionSecciones(evalSeccs);

        return gpoSecc;
    }

    @Override
    public List<EvaluacionPlan> allEvaluacionPlanByDynatable(DynatableFilter filter, Long idPlanCalificacion) {
        return evaluacionPlanDAO.allByDynatable(filter, idPlanCalificacion);
    }

    @Override
    public List<EvaluacionPlan> allEvaluacionPlanByPlanCalifica(Long idPlanCalificacion) {
        return evaluacionPlanDAO.allByFilter(idPlanCalificacion);
    }

    @Override
    public EvaluacionPlan findEvaluacionPlan(Long idEvaluacionPlan) {
        return evaluacionPlanDAO.find(idEvaluacionPlan);
    }

    @Override
    public Evaluacion findEvaluacion(Long idEvaluacion) {
        return evaluacionDAO.find(idEvaluacion);
    }

    @Override
    @Transactional
    public List<MatriculaSeccion> eliminarNotas(Evaluacion evaluacion, DataSessionPivot ds) {

        evaluacion = evaluacionDAO.find(evaluacion.getId());
        EvaluacionExpandida evalExp = new EvaluacionExpandida(evaluacion.getEvaluacionExpandida().getId());
        List<AlumnoEvaluacion> alumnoEvaluaciones = alumnoEvaluacionDAO.allByFilter(null, null, null, evaluacion.getId());
        DateTime today = new DateTime();

        EvaluacionEliminada evaluacionEliminada = new EvaluacionEliminada();
        evaluacionEliminada.create(evaluacion);
        evaluacionEliminada.setUsuarioRegistro(ds.getUsuario());
        evaluacionEliminada.setFechaRegistro(today.toDate());

        evaluacionEliminada.setAlumnoEvaluacionElims(new ArrayList<>());

        List<MatriculaSeccion> marticulasSeccion = new ArrayList();
        for (AlumnoEvaluacion alumnoEvaluacion : alumnoEvaluaciones) {
            AlumnoEvaluacionElim alumnoEvaluacionElim = new AlumnoEvaluacionElim();
            alumnoEvaluacionElim.create(alumnoEvaluacion);
            alumnoEvaluacionElim.setEvaluacionEliminada(evaluacionEliminada);
            evaluacionEliminada.getAlumnoEvaluacionElims().add(alumnoEvaluacionElim);

            Alumno alumno = alumnoEvaluacion.getAlumno();
            MatriculaSeccion matSecc = new MatriculaSeccion();
            matSecc.setMatriculaResumen(new MatriculaResumen());
            matSecc.getMatriculaResumen().setAlumno(alumno);
            matSecc.setSeccion(evaluacion.getSeccionResponsable());

            marticulasSeccion.add(matSecc);

            MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno,
                    evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso(),
                    ds.getCicloAcademico());
            matriculaCurso.setCreditosAprobados(null);
            matriculaCursoDAO.update(matriculaCurso);
        }

        evaluacionEliminadaDAO.save(evaluacionEliminada);

        alumnoEvaluacionDAO.deleteByEvaluacion(evaluacion);
        reclamoNotaDAO.deleteByEvaluacion(evaluacion);

        evaluacion.setFechaIngresoNota(null);
        evaluacion.setFechaRealizada(null);
        evaluacionDAO.update(evaluacion);

        List<AlumnoEvaluacion> listaAluEvas = alumnoEvaluacionDAO.allByEvaluacionExp(evaluacion.getEvaluacionExpandida().getId());
        if (listaAluEvas == null || listaAluEvas.isEmpty()) {
            evaluacion.getEvaluacionExpandida().setNotasIngresadas(BigDecimal.ZERO.intValue());
            evaluacionExpandidaDAO.update(evaluacion.getEvaluacionExpandida());
        }

        List<MatriculaSeccion> matriculasSeccion = this.allMatriculaSeccionByFilter(evalExp, ds.getCicloAcademico());
        for (MatriculaSeccion ms : matriculasSeccion) {
            Seccion seccion = ms.getSeccion();
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            Alumno alumno = ms.getMatriculaResumen().getAlumno();

            if (gpoSecc.getPlanCalificacion() == null) {
                break;
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }

            calculoNotasService.calcularNotasAlumno(alumno, gpoSecc, gpoSecc.getCurso(), gpoSecc.getCicloAcademico(), ds);

        }

        return marticulasSeccion;
    }

    @Override
    @Transactional
    public void createEvaluacionSeccionPorDocente(Docente docente, CicloAcademico ciclo) {

        List<DocenteSeccion> lstDocenteSeccion = docenteSeccionDAO.allByDocente(docente, ciclo);
        logger.debug("Lista de secciones por docente {}", lstDocenteSeccion.size());
        for (DocenteSeccion docenteSeccion : lstDocenteSeccion) {

            GrupoSeccion grupoSeccion = docenteSeccion.getSeccion().getGrupoSeccion();
            Curso curso = docenteSeccion.getSeccion().getGrupoSeccion().getCurso();

            String objectToEvaluate = "";
            if (ciclo.isTipoNivelacion()) {
                logger.debug("El ciclo es nivelacion");
                objectToEvaluate = "planCalificacion.id";
            } else if (ciclo.isTipoRegular()) {
                logger.debug("El ciclo es regular");
                objectToEvaluate = "planCalificacionRegular.id";
            }
            if (ObjectUtil.getParentTree(curso, objectToEvaluate) == null) {
                logger.debug("el curso {} no cuenta con plan calificacion", curso.getId());
                continue;
            }

            Long idGrupoSeccion = grupoSeccion.getId();
            //   Long idPlanCalificacion = curso.getPlanCalificacion().getId();
            Long idPlanCalificacion = (Long) ObjectUtil.getParentTree(curso, objectToEvaluate);
            logger.debug("El Docente Seccion {}, Seccion {}, Grupo seccion {}, plan calificacion {}", docenteSeccion.getId(),
                    docenteSeccion.getSeccion().getId(), idGrupoSeccion, idPlanCalificacion);
            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, idGrupoSeccion, null);
            PlanCalificacion planCalificacion = planCalificacionDAO.find(idPlanCalificacion);

            if (evaluacionSeccion != null) {
                logger.debug("el grupo ya cuenta con evaluacion seccion");
                if (evaluacionSeccion.isEstadoPro()) {
                    evaluacionSeccion.setPlanCalificacion(planCalificacion);
                    evaluacionSeccion.setSistemaNotas(planCalificacion.getSistemaNotas());
                    evaluacionSeccion.setGrupoSeccion(new GrupoSeccion(idGrupoSeccion));
                    evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
                    evaluacionSeccionDAO.update(evaluacionSeccion);

                    grupoSeccion.setPlanCalificacion(planCalificacion);
                    grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PRO);
                    grupoSeccionDAO.update(grupoSeccion);
                }
            } else {

                logger.debug("se le creara una evaluacion seccion al grupo");
                EvaluacionSeccion evaluacionSeccionCreate = new EvaluacionSeccion();
                evaluacionSeccionCreate.setPlanCalificacion(planCalificacion);
                evaluacionSeccionCreate.setSistemaNotas(planCalificacion.getSistemaNotas());
                evaluacionSeccionCreate.setGrupoSeccion(new GrupoSeccion(idGrupoSeccion));
                evaluacionSeccionCreate.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
                evaluacionSeccionDAO.save(evaluacionSeccionCreate);

                grupoSeccion.setPlanCalificacion(planCalificacion);
                grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PRO);
                grupoSeccionDAO.update(grupoSeccion);
            }
        }
    }

    @Override
    public void createEvaluacionExpPorEvalSeccion(EvaluacionSeccion evaluacionSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();

        List<EvaluacionExpandida> evaluacionesExpandidas = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);
        logger.debug("Evaluacion seccion {}, cantidad de evaluaciones expandidadas {}", evaluacionSeccion.getId(), evaluacionesExpandidas.size());
        if (evaluacionesExpandidas.isEmpty()) {
            logger.debug("no tiene evaluaciones, se creara las evaluaciones en base al plan calificacion {}", evaluacionSeccion.getPlanCalificacion().getId());

            List<EvaluacionPlan> evaluacionesPlanes = this.allEvaluacionPlanByPlanCalifica(evaluacionSeccion.getPlanCalificacion().getId());
            logger.debug("Plan Calificacion {}, Cantidad de evaluaciones para el plan {} ", evaluacionSeccion.getPlanCalificacion().getId(), evaluacionesPlanes.size());

            for (EvaluacionPlan evaluacionPlan : evaluacionesPlanes) {
                logger.debug("createEvaluacionExpPorEvalSeccion ##################################");
                logger.debug("Evaluacion PLan {}, Codigo {}", evaluacionPlan.getId(), evaluacionPlan.getTipoEvaluacion().getCodigo());
                BigDecimal peso = BigDecimal.ZERO;
                for (int i = 1; i <= evaluacionPlan.getCantidadEvaluaciones(); i++) {
                    EvaluacionExpandida evaluacion = new EvaluacionExpandida();
                    evaluacion.setAlumnoEvaluacion(null);
                    evaluacion.create(evaluacionSeccion, evaluacionPlan, i);
                    evaluacion.setTipoSeccionEvalEnum(grupoSeccion.getCurso().getTipoCursoEnum().getTipoSeccionEvalEnum());
                    evaluacion.setNivel(BigDecimal.ONE.intValue());

                    if (i == evaluacionPlan.getCantidadEvaluaciones() //                            && (evaluacionPlan.getNotaMinimaAnulable() == null || evaluacionPlan.getNotaMinimaAnulable() == 0)
                            ) {
                        BigDecimal pesoFinal = evaluacionPlan.getPesoTotal().subtract(peso);
                        evaluacion.setPeso(pesoFinal);
                    }
                    peso = peso.add(evaluacionPlan.getPesoEvaluacion());
                    logger.debug("Se guardara la evaluacion expandida  {} {}", evaluacion.getTipoEvaluacion().getCodigo(), i);
                    evaluacionExpandidaDAO.save(evaluacion);
                }
            }

            grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
            grupoSeccion.setPlanCalificacion(evaluacionSeccion.getPlanCalificacion());
            grupoSeccionDAO.update(grupoSeccion);

            this.aceptarExpansion(evaluacionSeccion.getId(), null);
        }

        grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
        grupoSeccion.setPlanCalificacion(evaluacionSeccion.getPlanCalificacion());
        grupoSeccionDAO.update(grupoSeccion);
    }

    @Override
    @Transactional
    public void saveExpansionEvaluacion(EvaluacionExpandida evaluacionExpandidaForm, DataSessionPivot ds) {

        EvaluacionExpandida evaluacionPadreBD = evaluacionExpandidaDAO.find(evaluacionExpandidaForm.getId());

        EvaluacionSeccion evaluacionSeccion = evaluacionPadreBD.getEvaluacionSeccion();
        validarEvaluacionesExpandidas(evaluacionExpandidaForm, evaluacionPadreBD);

        List<EvaluacionExpandida> evaluacionesHijasForm = evaluacionExpandidaForm.getEvaluacionesExpandidas();
        List<EvaluacionExpandida> evaluacionesHijasBD = evaluacionPadreBD.getEvaluacionesExpandidas();

        Map<Long, EvaluacionExpandida> mapEvaluaciones = MapUtil.storeItems("id", evaluacionesHijasBD);

        if (evaluacionesHijasForm.isEmpty()) {
            evaluacionPadreBD.setEstaDesagregado(BigDecimal.ZERO.intValue());
            evaluacionPadreBD.setFechaDesagregar(null);
            evaluacionPadreBD.setUsuarioDesagregar(null);

            for (Evaluacion evaluacion : evaluacionPadreBD.getEvaluaciones()) {
                evaluacion.setEstaDesagregado(BigDecimal.ZERO.intValue());
            }
        } else {
            if (evaluacionExpandidaForm.getNotaMinimaAnulable() > evaluacionesHijasForm.size() - 1) {
                throw new PhobosException("Error, notas minimas anulables incorrectas.");
            }

            evaluacionPadreBD.setEstaDesagregado(BigDecimal.ONE.intValue());
            evaluacionPadreBD.setFechaDesagregar(new Date());
            evaluacionPadreBD.setUsuarioDesagregar(ds.getUsuario());
        }

        for (EvaluacionExpandida eval : evaluacionesHijasForm) {
            if (eval.getId() == null) {
                continue;
            }
            if (existeEvaluacion(eval, evaluacionesHijasBD)) {
                continue;
            }
            EvaluacionExpandida evalBD = mapEvaluaciones.get(eval.getId());
            if (evalBD == null) {
                throw new PhobosException("Está intentando modificar una modalidad de evaluación inexistente");
            } //   if (evalBD.isNotasIngresadas()) {
            else {
                // logger.debug("Eval expandida {}, Peso {}", eval.getId(), eval.getPeso());
                //throw new PhobosException("Está intentando modificar una evaluación que contiene notas");

                for (Evaluacion ev : evalBD.getEvaluaciones()) {
                    ev.setPeso(eval.getPeso());
                    ev.setNumero(eval.getNumero());
                    evaluacionDAO.update(ev);
                }
                evalBD.setNumero(eval.getNumero());
                evalBD.setPeso(eval.getPeso());
                evaluacionExpandidaDAO.update(evalBD);
                //     continue;
            }
            /*
            evaluacionDAO.deleteByEvaluacionExpandida(evalBD.getId());
            evaluacionExpandidaDAO.delete(evalBD);
            evaluacionesHijasBD.remove(evalBD);
            eval.setId(null);*/
        }

        List<EvaluacionExpandida> eliminados = new ArrayList();
        for (EvaluacionExpandida eval : evaluacionesHijasBD) {
            if (existeEvaluacion(eval, evaluacionesHijasForm)) {
                continue;
            }
            if (eval.isNotasIngresadas()) {
                continue;
                // throw new PhobosException("Está intentando modificar una evaluación que contiene notas");
            }
            eliminados.add(eval);
            evaluacionDAO.deleteByEvaluacionExpandida(eval.getId());
            evaluacionExpandidaDAO.delete(eval);

        }

        for (EvaluacionExpandida eliminado : eliminados) {
            evaluacionesHijasBD.remove(eliminado);
        }

        Date today = new Date();
        List<Seccion> secciones = seccionDAO.allByFilter(evaluacionSeccion.getGrupoSeccion().getId());
        logger.debug("Cantidad de secciones del grupo {}", secciones.size());
        for (Seccion seccion : secciones) {
            if (ObjectUtil.getParentTree(seccion, "id") != null) {
                logger.debug("el id de la seccion {}", seccion.getId());
            }
        }

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allPersonasActivasBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapDocentesSeccion = MapUtil.storeLists("seccion.id", docentesSecciones);

        List<Evaluacion> evaluacionesSuperiores = evaluacionDAO.allByEvaluacionExpandidaSecciones(evaluacionPadreBD, secciones);
        Map<String, Evaluacion> mapEvaluacionesDelPadre = new LinkedHashMap();
        for (Evaluacion evalSuperior : evaluacionesSuperiores) {
            Seccion seccion = evalSuperior.getSeccionResponsable();
            EvaluacionExpandida evalExpandidaSup = evalSuperior.getEvaluacionExpandida();
            mapEvaluacionesDelPadre.put(seccion.getId() + "-" + evalExpandidaSup.getId(), evalSuperior);
        }

        if (evaluacionesHijasForm.isEmpty()) {
            //crear evaluaciones apra el padre??????????/
            evaluacionExpandidaDAO.update(evaluacionPadreBD);
            return;
        }

        for (EvaluacionExpandida evalForm : evaluacionesHijasForm) {
            if (evalForm.getId() != null) {
                continue;
            }

            evalForm.setEstaDesagregado(BigDecimal.ZERO.intValue());
            evalForm.setEvaluacionSeccion(evaluacionPadreBD.getEvaluacionSeccion());
            evalForm.setEvaluacionSuperior(evaluacionPadreBD);
//            evalForm.setEvaluados(BigDecimal.ZERO.intValue());
//            evalForm.setExtemporaneos(BigDecimal.ZERO.intValue());
            evalForm.setTipoSeccion(evaluacionPadreBD.getTipoSeccion());
            evalForm.setPorcentajeVariable(evaluacionPadreBD.getPorcentajeVariable());
            evalForm.setNotaMinimaAnulable(BigDecimal.ZERO.intValue());
            evalForm.setNivel(evaluacionPadreBD.getNivel().intValue() + 1);

            evalForm.setEvaluaciones(new ArrayList());
            for (Seccion seccion : secciones) {
                if (seccion.getTipoSeccionEnum().getTipoSeccionEvalEnum() != evalForm.getTipoSeccionEvalEnum()) {
                    continue;
                }

                Evaluacion evalPadre = mapEvaluacionesDelPadre.get(seccion.getId() + "-" + evaluacionPadreBD.getId());
                if (evalPadre == null) {
                    // throw new PhobosException("No se encontro la evaluación del padre");
                    continue;
                }
                if (!evalPadre.isDesagregado()) {
                    evalPadre.setEstaDesagregado(BigDecimal.ONE.intValue());
                    evalPadre.setUsuarioDesagregar(ds.getUsuario());
                    evalPadre.setFechaDesagregar(today);
                }

                Evaluacion eval = new Evaluacion();
                eval.create(evaluacionSeccion, seccion, evalForm);
                eval.setEvaluacionSuperior(evalPadre);
                eval.setEstaDesagregado(BigDecimal.ZERO.intValue());
                evalForm.getEvaluaciones().add(eval);

                List<DocenteSeccion> docentesSecc = mapDocentesSeccion.get(seccion.getId());
                if (docentesSecc != null && docentesSecc.size() == 1) {
                    Docente profe = docentesSecc.get(0).getDocente();
                    eval.setDocenteEvaluador(profe);
                }
                //   evaluacionDAO.save(eval);
                evalForm.getEvaluaciones().add(eval);
            }

            if (evalForm.getEvaluaciones().isEmpty()) {
                //   evalForm.setEvaluaciones(null);
                throw new PhobosException("Error. No se puedieron generar las evaluaciones.");
            }
            evaluacionExpandidaDAO.save(evalForm);

        }
        /*
        for (Evaluacion evalSuperior : evaluacionesSuperiores) {
            evaluacionDAO.update(evalSuperior);
        }*//*
        evaluacionExpandidaDAO.update(evaluacionPadreBD);*/
        EvaluacionExpandida evaluacionExpBD = evaluacionExpandidaDAO.find(evaluacionExpandidaForm.getId());
        evaluacionExpBD.setNotaMinimaAnulable(evaluacionExpandidaForm.getNotaMinimaAnulable());
        evaluacionExpandidaDAO.update(evaluacionExpBD);

        List<MatriculaSeccion> matriculasSeccion = this.allMatriculaSeccionByFilter(evaluacionExpBD, ds.getCicloAcademico());

        for (MatriculaSeccion ms : matriculasSeccion) {
            Seccion seccion = ms.getSeccion();
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            Alumno alumno = ms.getMatriculaResumen().getAlumno();

            if (gpoSecc.getPlanCalificacion() == null) {
                break;
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }

            calculoNotasService.calcularNotasAlumno(alumno, gpoSecc, gpoSecc.getCurso(), gpoSecc.getCicloAcademico(), ds);

        }
    }

    private void validarEvaluacionesExpandidas(EvaluacionExpandida evaluacionForm, EvaluacionExpandida evaluacionPadreBD) {
        List<EvaluacionExpandida> evaluacionesHijasForm = evaluacionForm.getEvaluacionesExpandidas();

        if (evaluacionesHijasForm == null) {
            evaluacionesHijasForm = new ArrayList();
            evaluacionForm.setEvaluacionesExpandidas(evaluacionesHijasForm);
        }

        List<EvaluacionExpandida> evaluacionesHijasBD = evaluacionPadreBD.getEvaluacionesExpandidas();
        if (evaluacionesHijasForm.size() == evaluacionesHijasBD.size() && evaluacionesHijasForm.isEmpty()) {
            throw new PhobosException("No ha ingresado las evaluaciones disgregadas ");
        }

        boolean isOkEvaluacionesBD = true;
        for (EvaluacionExpandida evalExp : evaluacionesHijasBD) {
            if (!existeEvaluacion(evalExp, evaluacionesHijasForm)) {
                isOkEvaluacionesBD = false;
            }
        }

        boolean isOkEvaluacionesForm = true;
        for (EvaluacionExpandida evalExp : evaluacionesHijasForm) {
            if (!existeEvaluacion(evalExp, evaluacionesHijasBD)) {
                isOkEvaluacionesForm = false;
            }
        }

        if ((isOkEvaluacionesBD && isOkEvaluacionesForm) && evaluacionForm.getNotaMinimaAnulable().equals(evaluacionPadreBD.getNotaMinimaAnulable())) {
            throw new PhobosException("No ha ingresado ningún cambio");
        }
        /*
        BigDecimal newPesoTotal = BigDecimal.ZERO;
        for (EvaluacionExpandida evaluacionHija : evaluacionesHijasForm) {
            newPesoTotal = newPesoTotal.add(evaluacionHija.getPeso());
        }*/
        //   if (evaluacionForm.getNotaMinimaAnulable().equals(BigDecimal.ZERO.intValue())) {
        /*
        if (newPesoTotal.compareTo(BigDecimal.valueOf(100)) > 0 && !evaluacionesHijasForm.isEmpty()) {
            throw new PhobosException("El peso de las evaluaciones expandidas no debe ser mayor que 100, verifique ");
        }
         */
        //  }
    }

    private boolean existeEvaluacion(EvaluacionExpandida evaluacion, List<EvaluacionExpandida> evaluaciones) {
        for (EvaluacionExpandida eval : evaluaciones) {
            if (ObjectUtil.verificarIgualdad(evaluacion, eval, Arrays.asList("id", "peso", "numero", "tipoEvaluacion.id"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void saveAsignacionDocentes(EvaluacionExpandida evaluacionExpandida, DataSessionPivot ds) {
        for (Evaluacion evaluacion : evaluacionExpandida.getEvaluaciones()) {
            if (!evaluacion.isNotasIngresadas()) {
                evaluacionDAO.updateDocenteEvaluador(evaluacion, evaluacion.getDocenteEvaluador());
            }
        }
    }

    @Override
    @Transactional
    public void saveSistemaCalifica(PlanCalificacion planCalificacion, Long grupoSeccionId, DataSessionPivot ds) {

        DateTime today = new DateTime();

        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(grupoSeccionId);
        logger.debug("Grupo Seccion Id {}", grupoSeccion.getId());

        DepartamentoAcademico departamentoAcademico = departamentoAcademicoDAO.find(planCalificacion.getDepartamentoAcademico().getId());

        planCalificacion.setEstadoEnum(EstadoPlanCalificaEnum.SOL);
        planCalificacion.setFechaRegistro(new Date());
        planCalificacion.setDepartamentoAcademico(departamentoAcademico);
        //     planCalificacion.setTipo(TipoPlanCalificacionEnum.PLANT.name());
        planCalificacion.setTipoCiclo(ds.getCicloAcademico().getTipo());

        BigDecimal totalWeight = BigDecimal.ZERO;

        SistemaNotas sistemaNotas = sistemaNotasDAO.find(planCalificacion.getSistemaNotas().getId());
        planCalificacion.setSistemaNotas(sistemaNotas);

        if (planCalificacion.getSistemaNotas().isLetras()) {
            if (planCalificacion.getEvaluacionPlan().size() == 1) {
                TipoEvaluacion tipoEvaluacion = tipoEvaluacionDAO.find(planCalificacion.getEvaluacionPlan().get(0).getTipoEvaluacion().getId());
                if (!tipoEvaluacion.isTipoEvaluacionNF()) {
                    throw new PhobosException("El sistema de notas seleccionado, solo debe tener una evaluacion del tipo Nota Final.");
                }
            } else {
                throw new PhobosException("El sistema de notas seleccionado, solo debe tener una evaluacion del tipo Nota Final.");
            }
        }

        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {
//            logger.debug("nota minima anulable {}", evaluacionPlan.getNotaMinimaAnulable());
            evaluacionPlan.setPlanCalificacion(planCalificacion);
            evaluacionPlan.setCantidadEvaluaciones(BigDecimal.ONE.intValue());
            evaluacionPlan.setPesoEvaluacion(evaluacionPlan.getPesoTotal());
            /*
            if (evaluacionPlan.getPesoEvaluacion() == null || evaluacionPlan.getPesoEvaluacion().compareTo(BigDecimal.ZERO) == 0) {
                throw new PhobosException("Peso evaluacion incorrecto..");
            }
             */
            if (evaluacionPlan.getEvaluacionesObligatorias() == null) {
                evaluacionPlan.setEvaluacionesObligatorias(BigDecimal.ZERO.intValue());
            }

            totalWeight = totalWeight.add(evaluacionPlan.getPesoTotal());
        }

        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new PhobosException("Pesos total (" + totalWeight.toString() + ") de las evaluaciones incorrecto.");
        }

        Long maxNumeroCorrelativo = planCalificacionDAO.maxNumeroCorrelativoPlanCalifica(planCalificacion.getDepartamentoAcademico().getId());
        maxNumeroCorrelativo = maxNumeroCorrelativo + 1;
        planCalificacion.setNumero(maxNumeroCorrelativo);

        planCalificacion.generateCodigo();

        planCalificacionDAO.save(planCalificacion);

        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.ACEP);
        grupoSeccion.setPlanCalificacion(planCalificacion);
        grupoSeccionDAO.update(grupoSeccion);
        /*
        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.ACEP);
        evaluacionSeccion.setPlanCalificacion(planCalificacion);
        evaluacionSeccionDAO.update(evaluacionSeccion);*/

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        if (evaluacionSeccion == null) {
            evaluacionSeccion = new EvaluacionSeccion();
            evaluacionSeccion.setPlanCalificacion(planCalificacion);
            evaluacionSeccion.setSistemaNotas(planCalificacion.getSistemaNotas());
            evaluacionSeccion.setGrupoSeccion(grupoSeccion);
            evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.ACEP);
            evaluacionSeccion.setUserAceptacion(ds.getUsuario());
            evaluacionSeccion.setFechaAceptacion(today.toDate());
            evaluacionSeccionDAO.save(evaluacionSeccion);
        } else {
            evaluacionSeccion.setPlanCalificacion(planCalificacion);
            evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.ACEP);
            evaluacionSeccion.setUserAceptacion(ds.getUsuario());
            evaluacionSeccion.setFechaAceptacion(today.toDate());
            evaluacionSeccionDAO.update(evaluacionSeccion);
        }

        planCalificacion.getId();

        PlanCalificacionCurso planCalificacionCurso = new PlanCalificacionCurso();
        planCalificacionCurso.setCurso(grupoSeccion.getCurso());
        planCalificacionCurso.setPlanCalificacion(planCalificacion);
        planCalificacionCurso.setEstadoEnum(EstadoEnum.ACT);
        planCalificacionCurso.setFechaActualizacion(today.toDate());
        planCalificacionCurso.setFechaCreacion(today.toDate());
        planCalificacionCursoDAO.save(planCalificacionCurso);
        this.aceptarPropuestaSolicitud(planCalificacion);
    }

    public void aceptarPropuestaSolicitud(PlanCalificacion planCalificacion) {
        EstadoPlanCalificaEnum estadoPlanCalificaEnum = EstadoPlanCalificaEnum.ACEP;

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(planCalificacion.getId(), null, null);
        evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(evaluacionSeccion.getGrupoSeccion().getId());
        grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
        grupoSeccion.setPlanCalificacion(planCalificacion);
        grupoSeccionDAO.update(grupoSeccion);

        this.createEvaluacionExpPorEvalSeccion(evaluacionSeccion, estadoPlanCalificaEnum);
        /*
        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("Cantidad de secciones para el grupo {}", secciones.size());
        List<EvaluacionExpandida> planEvaluacionesExpandidas = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", planCalificacion.getId(), planEvaluacionesExpandidas.size());
        for (Seccion seccionEach : secciones) {
            logger.debug("aceptarPropuestaSolicitud #############################");
            logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEvalEnum().name());
            for (EvaluacionExpandida evaluacionExpandida : planEvaluacionesExpandidas) {

                logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEvalEnum().getTipoSeccionEvalEnum().name());
                logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEvalEnum().name());
                if (seccionEach.getTipoSeccionEvalEnum().getTipoSeccionEvalEnum().equals(
                        evaluacionExpandida.getTipoSeccionEvalEnum())) {

                    Evaluacion evaluacion = new Evaluacion();
                    evaluacion.create(evaluacionSeccion, seccionEach, evaluacionExpandida);
                    if (evaluacionExpandida.getEvaluacionesExpandidas() != null && !evaluacionExpandida.getEvaluacionesExpandidas().isEmpty()) {
                        evaluacion.setEvaluaciones(new ArrayList<>());
                        for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluacionesExpandidas()) {
                            Evaluacion evaluacionChild = new Evaluacion();
                            evaluacionChild.create(evaluacionSeccion, seccionEach, evalExp);
                            evaluacionChild.setEvaluacionSuperior(evaluacion);
                            evaluacion.getEvaluaciones().add(evaluacionChild);
                        }
                    }
                    evaluacionDAO.save(evaluacion);
                }
            }
        }*/
        planCalificacion = planCalificacionDAO.find(planCalificacion.getId());
        planCalificacion.setEstadoEnum(estadoPlanCalificaEnum);
        planCalificacionDAO.update(planCalificacion);
    }

    @Override
    public EvaluacionSeccion findEvalSeccByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        return evaluacionSeccionDAO.findByPlanCalGrupoSec(idPlanCalificacion, idGrupoSeccion, estadoPlanCalificaEnum);
    }

    @Override
    public EvaluacionSeccion findEvaluacionSeccion(Long id) {
        return evaluacionSeccionDAO.find(id);
    }

    @Override
    public List<Evaluacion> allEvaluacionesByEvalSeccion(EvaluacionSeccion evaluacionSeccion) {
        return evaluacionDAO.allByFilter(evaluacionSeccion.getId(), null, null, null);
    }

    @Override
    public List<Evaluacion> allEvaluacionesByEvalExpandida(EvaluacionExpandida evaluacionExpandida) {
        return evaluacionDAO.allByFilter(null, null, null, evaluacionExpandida.getId());
    }

    @Override
    public List<EvaluacionExpandida> allEvaluacionesExpByEvalSeccion(EvaluacionSeccion evaluacionSeccion) {
        Map<Long, EvaluacionExpandida> mapEvaluacionesExp = new LinkedHashMap();
        Map<Long, EvaluacionExpandida> mapEvaluacionesExpHijas = new LinkedHashMap();
        Map<Long, EvaluacionExpandida> mapEvaluacionesExpNietas = new LinkedHashMap();

        List<EvaluacionExpandida> evaluacionesExp = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);
        for (EvaluacionExpandida evalExp : evaluacionesExp) {
            evalExp.setEvaluaciones(new ArrayList());
            mapEvaluacionesExp.put(evalExp.getId(), evalExp);

            List<EvaluacionExpandida> evalExpansHijas = evalExp.getEvaluacionesExpandidas();
            for (EvaluacionExpandida evalExpHija : evalExpansHijas) {
                evalExpHija.setEvaluaciones(new ArrayList());
                mapEvaluacionesExpHijas.put(evalExpHija.getId(), evalExpHija);

                if (evalExpHija.getEvaluacionesExpandidas() != null) {
                    for (EvaluacionExpandida evalExpansNieta : evalExpHija.getEvaluacionesExpandidas()) {
                        evalExpansNieta.setEvaluaciones(new ArrayList());
                        mapEvaluacionesExpNietas.put(evalExpansNieta.getId(), evalExpansNieta);
                    }
                }

            }
        }

        List<EvaluacionExpandida> evalsExpsQuery = new ArrayList();
        evalsExpsQuery.addAll(mapEvaluacionesExpNietas.values());
        evalsExpsQuery.addAll(mapEvaluacionesExpHijas.values());
        evalsExpsQuery.addAll(evaluacionesExp);

        List<Evaluacion> evals = evaluacionDAO.allByEvaluacionesByExpandidas(evalsExpsQuery);
        for (Evaluacion eval : evals) {
            EvaluacionExpandida evalExp = mapEvaluacionesExp.get(eval.getEvaluacionExpandida().getId());
            if (evalExp == null) {
                if (mapEvaluacionesExpHijas.get(eval.getEvaluacionExpandida().getId()) != null) {
                    evalExp = mapEvaluacionesExpHijas.get(eval.getEvaluacionExpandida().getId());
                } else {
                    evalExp = mapEvaluacionesExpNietas.get(eval.getEvaluacionExpandida().getId());
                }
            }
            evalExp.getEvaluaciones().add(eval);
        }

        return evaluacionesExp;
    }

    @Override
    public List<SistemaNotas> allSistemasNotas() {
        return sistemaNotasDAO.all();
    }

    @Override
    @Transactional
    public void aceptarRechazo(Long cursoId, Long grupoId, DataSessionPivot ds) {
        logger.debug("CursoId {}, GrupoId {}", cursoId, grupoId);

        Curso curso = cursoDAO.find(cursoId);
        GrupoSeccion grupo = grupoSeccionDAO.find(grupoId);

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupo.getId(), null);
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
        evaluacionSeccion.setPlanCalificacion(curso.getPlanCalificacion());
        evaluacionSeccionDAO.update(evaluacionSeccion);

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PRO);
        grupoSeccion.setPlanCalificacion(curso.getPlanCalificacion());
        grupoSeccionDAO.update(grupoSeccion);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aceptarPlanCalificacionSession(PlanCalificacion planCalificacion, Long cursoId, Long grupoId, DataSessionPivot ds) {
        aceptarPlanCalificacion(planCalificacion, cursoId, grupoId, ds);
    }

    @Override
    @Transactional
    public void aceptarPlanCalificacion(PlanCalificacion planCalificacion, Long cursoId, Long grupoId, DataSessionPivot ds) {
        logger.debug("CursoId {}, grupoId {}", cursoId, grupoId);

        Date today = new Date();

        Curso curso = cursoDAO.find(cursoId);
        GrupoSeccion grupo = grupoSeccionDAO.find(grupoId);
        planCalificacion = planCalificacionDAO.find(planCalificacion.getId());

        logger.debug("Plan Calificacion {}, Codigo {}", planCalificacion.getId(), planCalificacion.getCodigo());

        BigDecimal totalWeight = BigDecimal.ZERO;

        SistemaNotas sistemaNotas = sistemaNotasDAO.find(planCalificacion.getSistemaNotas().getId());
        planCalificacion.setSistemaNotas(sistemaNotas);

        if (planCalificacion.getSistemaNotas().isLetras()) {
            if (planCalificacion.getEvaluacionPlan().size() == 1) {
                TipoEvaluacion tipoEvaluacion = tipoEvaluacionDAO.find(planCalificacion.getEvaluacionPlan().get(0).getTipoEvaluacion().getId());
                if (!tipoEvaluacion.isTipoEvaluacionNF()) {
                    throw new PhobosException("El sistema de notas seleccionado, solo debe tener una evaluacion del tipo Nota Final.");
                }
            } else {
                throw new PhobosException("El sistema de notas seleccionado, solo debe tener una evaluacion del tipo Nota Final.");
            }
        }

        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {
//            logger.debug("nota minima anulable {}", evaluacionPlan.getNotaMinimaAnulable());
            evaluacionPlan.setPlanCalificacion(planCalificacion);
            evaluacionPlan.setCantidadEvaluaciones(BigDecimal.ONE.intValue());
            evaluacionPlan.setPesoEvaluacion(evaluacionPlan.getPesoTotal());
            if (evaluacionPlan.getEvaluacionesObligatorias() == null) {
                evaluacionPlan.setEvaluacionesObligatorias(BigDecimal.ZERO.intValue());
            }

            totalWeight = totalWeight.add(evaluacionPlan.getPesoTotal());
        }
        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new PhobosException("Pesos total (" + totalWeight.toString() + ") de las evaluaciones incorrecto.");
        }

        grupo.setPlanCalificacion(planCalificacion);

        logger.debug("Buscara la evaluacion seccion del grupo {}", grupo.getId());
        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupo.getId(), null);
        if (evaluacionSeccion == null) {
            evaluacionSeccion = new EvaluacionSeccion();
            evaluacionSeccion.setPlanCalificacion(planCalificacion);
            evaluacionSeccion.setSistemaNotas(planCalificacion.getSistemaNotas());
            evaluacionSeccion.setGrupoSeccion(grupo);
            evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.ACEP);
            evaluacionSeccion.setUserAceptacion(ds.getUsuario());
            evaluacionSeccion.setFechaAceptacion(today);
            evaluacionSeccionDAO.save(evaluacionSeccion);
        } else {
            evaluacionSeccion.setPlanCalificacion(planCalificacion);
            evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.ACEP);
            evaluacionSeccion.setUserAceptacion(ds.getUsuario());
            evaluacionSeccion.setFechaAceptacion(today);
            evaluacionSeccionDAO.update(evaluacionSeccion);
        }

        this.createEvaluacionExpPorEvalSeccion(evaluacionSeccion, EstadoPlanCalificaEnum.ACEP);

    }

    @Override
    @Transactional
    public void aceptarExpansion(Long evaluacionSeccionId, DataSessionPivot ds) {
        logger.debug("La evaluacionSeccionId es {}", evaluacionSeccionId);

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.find(evaluacionSeccionId);
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.EXPR);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();

        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.EXPR);
        grupoSeccionDAO.update(grupoSeccion);

        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("la cantidad de secciones para el grupo {}, es {}", grupoSeccion.getId(), secciones.size());
        List<EvaluacionExpandida> planEvaluacionesExpandidas = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", grupoSeccion.getPlanCalificacion().getId(), planEvaluacionesExpandidas.size());

        for (EvaluacionExpandida evaluacionExpandida : planEvaluacionesExpandidas) {
            BigDecimal pesoTotal = evaluacionExpandida.getPeso();
            BigDecimal pesoAcum = BigDecimal.ZERO;
            if (evaluacionExpandida.getEvaluacionesExpandidas() != null && !evaluacionExpandida.getEvaluacionesExpandidas().isEmpty()) {

                for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluacionesExpandidas()) {
                    pesoAcum = pesoAcum.add(evalExp.getPeso());
                }

                if (pesoTotal.compareTo(pesoAcum) != 0) {
                    String msg = "Pesos de las subevaluaciones de la evaluación {1} {2} incorrectos, verifique";
                    msg = msg.replace("{1}", evaluacionExpandida.getTipoEvaluacion().getNombre());
                    msg = msg.replace("{2}", evaluacionExpandida.getNumero().toString());
                    throw new PhobosException(msg);
                }
            }
        }
        grupoSeccion.getCurso().getTipoCurso();
        for (Seccion seccionEach : secciones) {
            if (!seccionEach.isEstadoActivo()) {
                continue;
            }
            logger.debug("aceptarExpansion ############################################");
            logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
            for (EvaluacionExpandida evaluacionExpandida : planEvaluacionesExpandidas) {

                logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
                logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEvalEnum().name());

                if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                        evaluacionExpandida.getTipoSeccionEvalEnum())) {
                    /*
                if (seccionEach.getTipoSeccionEnum().getTipoCursoEum().equals(curso.getTipoCursoEnum())) {
                     */
                    Evaluacion evaluacion = new Evaluacion();
                    evaluacion.create(evaluacionSeccion, seccionEach, evaluacionExpandida);
                    if (evaluacionExpandida.getEvaluacionesExpandidas() != null && !evaluacionExpandida.getEvaluacionesExpandidas().isEmpty()) {
                        evaluacion.setEvaluaciones(new ArrayList<>());
                        for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluacionesExpandidas()) {
                            Evaluacion evaluacionChild = new Evaluacion();
                            evaluacionChild.create(evaluacionSeccion, seccionEach, evalExp);
                            evaluacionChild.setEvaluacionSuperior(evaluacion);
                            evaluacion.getEvaluaciones().add(evaluacionChild);
                        }
                    }
                    if (evaluacion.getDocenteEvaluador() == null) {
                        List<DocenteSeccion> docentesSecc = docenteSeccionDAO.allPersonasActivasBySeccion(evaluacion.getSeccionResponsable());
                        if (docentesSecc.size() == 1) {
                            Docente profe = docentesSecc.get(0).getDocente();
                            evaluacion.setDocenteEvaluador(profe);
                        }
                    }
                    evaluacionDAO.save(evaluacion);
                }
            }
        }

    }

    @Override
    public DocenteSeccion findDocenteSeccion(Long idDocenteSeccion) {
        return docenteSeccionDAO.find(idDocenteSeccion);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionByGrupo(GrupoSeccion grupoSeccion) {
        return docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
    }

    @Override
    public List<Evaluacion> allEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion) {
        return evaluacionDAO.allByFilter(null, idGrupoSeccion, idSeccion, null);
    }

    @Override
    public List<AlumnoEvaluacion> allAlumnoEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacion) {
        return alumnoEvaluacionDAO.allByFilter(idEvaluacionSeccion, idGrupoSeccion, idSeccion, idEvaluacion);
    }

    @Override
    public List<Evaluacion> findBySeccion(Long idSeccion) {
        return evaluacionDAO.allByFilter(null, null, idSeccion, null);
    }

    @Override
    public EvaluacionExpandida findEvaluacionExpandida(Long idEvaluacionPlan) {
        return evaluacionExpandidaDAO.find(idEvaluacionPlan);
    }

    @Override
    @Transactional
    public void deleteEvaluacionExpandida(Long id) {

        EvaluacionExpandida evaluacion = evaluacionExpandidaDAO.find(id);
        EvaluacionExpandida evalSuperior = evaluacion.getEvaluacionSuperior();
        logger.debug("Evaluacion expandida a eliminar {}, Evaluacion Padre {}", id, evalSuperior.getId());
        evaluacionExpandidaDAO.delete(evaluacion);

        if (evalSuperior != null) {
            evalSuperior = evaluacionExpandidaDAO.find(evalSuperior.getId());
            if (evalSuperior.getEvaluacionesExpandidas() == null || evalSuperior.getEvaluacionesExpandidas().isEmpty()) {
                evalSuperior.setEstaDesagregado(BigDecimal.ZERO.intValue());
                evaluacionExpandidaDAO.update(evalSuperior);
            } else {
                logger.debug("Cantidad de evaluaciones hijas del padre {}", evalSuperior.getEvaluacionesExpandidas().size());
                if (evalSuperior.getEvaluacionesExpandidas().size() == 1) {
                    for (EvaluacionExpandida eva : evalSuperior.getEvaluacionesExpandidas()) {
                        if (eva.getId().equals(id)) {
                            evalSuperior.setEstaDesagregado(BigDecimal.ZERO.intValue());
                            evaluacionExpandidaDAO.update(evalSuperior);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion) {
        return matriculaSeccionDAO.allBySeccion(seccion);
    }

    @Override
    @Transactional
    public void updateEvaluacion(Evaluacion evaluacion) {
        evaluacionDAO.update(evaluacion);
    }

    @Override
    @Transactional
    public Evaluacion activarEvaluacion(Long evaluacionId, Date fechaRealizada, DataSessionPivot ds) {
        Evaluacion evaluacion = evaluacionDAO.find(evaluacionId);
        logger.debug("evaluacion param {}, {}", evaluacionId, evaluacion == null ? "no encontro" : "si encontro");

        if (evaluacion.getDocenteEvaluador() == null) {
            throw new PhobosException("La evaluación no cuenta con evaluador, verifique");
        }
        logger.debug("Evaluacion {}, Docente Aignado {}, Docente Logueado {}", evaluacion.getId(), evaluacion.getDocenteEvaluador().getId(), ds.getDocente().getId());
        if (!evaluacion.getDocenteEvaluador().getId().equals(ds.getDocente().getId())) {
            throw new PhobosException("Docente evaluador incorrecto, verifique");
        }

        evaluacion.setFechaRealizada(fechaRealizada);
        evaluacionDAO.update(evaluacion);
        return evaluacion;
    }

    @Override
    @Transactional
    public List<MatriculaSeccion> saveIngresoNotas(Evaluacion evaluacionParam, AlumnoEvaluacion[] alumnosEvaluaciones, DataSessionPivot ds) {
        Date today = new Date();

        Evaluacion evaluacion = evaluacionDAO.find(evaluacionParam.getId());

        EvaluacionExpandida evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacion.getEvaluacionExpandida().getId());
        evaluacionExpandida.setNotasIngresadas(BigDecimal.ONE.intValue());
        evaluacionExpandidaDAO.update(evaluacionExpandida);

        PlanCalificacion planCalificacion = evaluacion.getEvaluacionSeccion().getPlanCalificacion();
        CicloAcademico ciclo = evaluacion.getSeccionResponsable().getGrupoSeccion().getCicloAcademico();
        SistemaNotas sistemaNotas = sistemaNotasDAO.find(evaluacion.getEvaluacionSeccion().getSistemaNotas().getId());

        Seccion seccion = seccionDAO.find(evaluacion.getSeccionResponsable().getId());
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();

        evaluacion.setFechaIngresoNota(today);
        evaluacion.setEvaluados(alumnosEvaluaciones.length);
        evaluacionDAO.update(evaluacion);

        Map<Long, Alumno> mapAlumno = new LinkedHashMap();

        Map<String, NotaLetra> mapNotaLetra = new HashMap<>();
        List<NotaLetra> notasLetras = notaLetraDAO.all();
        for (NotaLetra notasLetra : notasLetras) {
            mapNotaLetra.put(notasLetra.getLetra(), notasLetra);
        }

        for (AlumnoEvaluacion alumnoEvaluacionEach : alumnosEvaluaciones) {
            Alumno alumnoEach = alumnoEvaluacionEach.getAlumno();
            Evaluacion evaluacionEach = alumnoEvaluacionEach.getEvaluacion();

            AlumnoEvaluacion alumnoEvaluacion = new AlumnoEvaluacion();
            alumnoEvaluacion.setAlumno(alumnoEach);
            alumnoEvaluacion.setEvaluacion(evaluacionEach);
            alumnoEvaluacion.setFechaIngresoNota(today);
            alumnoEvaluacion.setNota(alumnoEvaluacionEach.getNota());
            alumnoEvaluacion.setValorLetra(alumnoEvaluacionEach.getValorLetra());
            alumnoEvaluacion.setEsIngresoRegular(BigDecimal.ONE.intValue());
            alumnoEvaluacion.setEstadoEnum(AlumnoEvaluacionEstadoEnum.ACT);
            mapAlumno.put(alumnoEach.getId(), alumnoEach);

            if (alumnoEvaluacion.getNota().equals(AlumnoEvaluacion.NSP)) {
                alumnoEvaluacion.setValorNumerico(BigDecimal.ZERO);
            } else if (alumnoEvaluacion.isNCV()) {
                alumnoEvaluacion.setEstadoEnum(AlumnoEvaluacionEstadoEnum.ANC);
                alumnoEvaluacion.setValorNumerico(BigDecimal.ZERO);
                alumnoEvaluacion.setMotivoAnulacion(MotivoAnulacionEnum.NOTA_NCV.name());
            } else if (sistemaNotas.isNumerico()) {
                alumnoEvaluacion.setValorNumerico(new BigDecimal(alumnoEvaluacion.getNota()));
                String notax = NumberFormat.notaDecimal(alumnoEvaluacion.getValorNumerico());
                alumnoEvaluacion.setNota(notax);
            } else {
                MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(alumnoEach, curso, ciclo);
                matriculaCurso.setNotaFinal(alumnoEvaluacion.getValorLetra());
                if (curso.isCreditosZero()) {
                    NotaLetra notaLetra = (NotaLetra) mapNotaLetra.get(alumnoEvaluacionEach.getValorLetra());
                    alumnoEvaluacion.setNota(notaLetra.getValor().toString());
                } else if (curso.isTieneCreditosVariables()) {
                    matriculaCurso.setCreditosAprobados(Integer.valueOf(alumnoEvaluacionEach.getNota()));
                }
                matriculaCursoDAO.update(matriculaCurso);

                alumnoEvaluacion.setValorNumerico(new BigDecimal(alumnoEvaluacion.getNota()));
                String notax = NumberFormat.notaDecimal(alumnoEvaluacion.getValorNumerico());
                alumnoEvaluacion.setNota(notax);
            }

            alumnoEvaluacion.setUsuarioIngresoNota(ds.getUsuario());

            alumnoEvaluacionDAO.save(alumnoEvaluacion);
        }

        //List<EvaluacionPlan> evaluacionesPlan = evaluacionPlanDAO.allByPlan(planCalificacion);
        //     BigDecimal bd100 = new BigDecimal("100");
        List<MatriculaSeccion> marticulasSeccion = new ArrayList();
        for (AlumnoEvaluacion alumnoEvaluacionEach : alumnosEvaluaciones) {
            Alumno alumno = alumnoEvaluacionEach.getAlumno();
            MatriculaSeccion matSecc = new MatriculaSeccion();
            matSecc.setMatriculaResumen(new MatriculaResumen());
            matSecc.getMatriculaResumen().setAlumno(alumno);
            matSecc.setSeccion(evaluacion.getSeccionResponsable());
            evaluacion.getSeccionResponsable().getGrupoSeccion();
            evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso();
            evaluacion.getSeccionResponsable().getGrupoSeccion().getPlanCalificacion();
            evaluacion.getSeccionResponsable().getGrupoSeccion().getCicloAcademico();

            marticulasSeccion.add(matSecc);

            /*
            GrupoSeccion gpoSeccion = evaluacion.getSeccionResponsable().getGrupoSeccion();
            Curso curso = gpoSeccion.getCurso();
            List<AlumnoEvaluacion> evaluacionesAlumno = alumnoEvaluacionDAO.allByAlumnoCursoCiclo(alumno, curso, ciclo);
            
            calcularNotasAlumno(alumno, evaluacion, grupoSeccion, curso, ciclo, evaluacionesPlan);
            //*/
        }

        this.calcularNotasLista(marticulasSeccion, ds);

        return marticulasSeccion;
        /*
        if (evaluacionDAO.countEvaluacionesFaltantesByGrupo(grupoSeccion.getId()).intValue() == 0) {
            grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.CER);
            grupoSeccionDAO.update(seccion.getGrupoSeccion());
        }*/
    }

    @Override
    @Transactional
    public void calcularNotasLista(List<MatriculaSeccion> matriculasSeccion, DataSessionPivot ds) {
        for (MatriculaSeccion matSecc : matriculasSeccion) {
            GrupoSeccion gpoSeccion = matSecc.getSeccion().getGrupoSeccion();
            Curso curso = gpoSeccion.getCurso();
            CicloAcademico ciclo = gpoSeccion.getCicloAcademico();
            //PlanCalificacion plan = gpoSeccion.getPlanCalificacion();
            Alumno alumno = matSecc.getMatriculaResumen().getAlumno();

            //List<EvaluacionExpandida> evaluasExpan = evaluacionExpandidaDAO.allByGpoSeccionPlan(gpoSeccion, plan);
            calculoNotasService.calcularNotasAlumno(alumno, gpoSeccion, curso, ciclo, ds);
        }
    }

    @Override
    public SistemaNotas findSistemaNotaById(Long id) {
        return sistemaNotasDAO.find(id);
    }

    @Override
    public ObjectNode getDetalleEvaluacion(Long idEvaluacion, Long idSeccion) {
        Evaluacion evaluacion = this.findEvaluacion(idEvaluacion);
        logger.debug("evaluacion param {}, {}", idEvaluacion, evaluacion == null ? "no encontro" : "si encontro");

        Seccion seccion = seccionDAO.find(idSeccion);
        GrupoSeccion grupoSeccion = this.findGrupo(seccion.getGrupoSeccion().getId());
        List<AlumnoEvaluacion> alumnosEvaluaciones = this.allAlumnoEvaluacionByFilter(null, null, seccion.getId(), null);

        EvaluacionSeccion evaluacionSeccion = this.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        SistemaNotas sistemaNotas = evaluacionSeccion.getSistemaNotas();

        BigDecimal notaminima = BigDecimal.valueOf(1000L);
        BigDecimal notaMaxima = BigDecimal.ZERO;
        BigDecimal sumatoriaNotas = BigDecimal.ZERO;
        int cantidadNsp = 0;
        int cantidadEvaluados = 0;

        for (AlumnoEvaluacion alumnosEvaluacionEach : alumnosEvaluaciones) {
            if (!alumnosEvaluacionEach.getEvaluacion().getId().equals(evaluacion.getId())) {
                continue;
            }
            //  if (sistemaNotas.isNumerico()) {
            if (alumnosEvaluacionEach.getValorNumerico().compareTo(notaminima) < 0) {
                notaminima = alumnosEvaluacionEach.getValorNumerico();
            }
            if (alumnosEvaluacionEach.getValorNumerico().compareTo(notaMaxima) > 0) {
                notaMaxima = alumnosEvaluacionEach.getValorNumerico();
            }
            if (alumnosEvaluacionEach.getNota().equalsIgnoreCase(AlumnoEvaluacion.NSP)) {
                cantidadNsp++;
            } else {
                cantidadEvaluados++;
                sumatoriaNotas = sumatoriaNotas.add(alumnosEvaluacionEach.getValorNumerico());
            }

            //  }
        }

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        if (evaluacion != null) {
            node.put("evaluacionId", evaluacion.getId());
            //      node.put("estado", evaluacion.getFechaIngresoNota() == null ? "CERRADA" : "ABIERTA");
            node.put("estaAbierto", grupoSeccion.isEstadoGrupoAbierto());
            node.put("estaCerrado", grupoSeccion.isEstadoGrupoCerrado());
            node.put("estaReabierto", grupoSeccion.isEstadoGrupoReabierto());

            node.put("tEvaluacionNombre", evaluacion.getTipoEvaluacion().getNombre());
            node.put("tEvaluacionCodigo", evaluacion.getTipoEvaluacion().getCodigo());
            node.put("numero", evaluacion.getNumero());
            node.put("evaFechaIngresoNota", evaluacion.getFechaIngresoNota() != null ? new DateTime(evaluacion.getFechaIngresoNota()).toString("dd/MM/yyyy") : "");
            node.put("evaFechaRealizada", evaluacion.getFechaRealizada() != null ? new DateTime(evaluacion.getFechaRealizada()).toString("dd/MM/yyyy") : "");
            node.put("notaminima", 0);
            if (BigDecimal.valueOf(1000L).compareTo(notaminima) != 0) {
                node.put("notaminima", notaminima);
            }

            node.put("notaMaxima", notaMaxima);
            node.put("cantidadEvaluados", cantidadEvaluados);
            node.put("cantidadNsp", cantidadNsp);
            node.put("promedioNotas", 0);
            if (sumatoriaNotas.compareTo(BigDecimal.ZERO) != 0) {
                node.put("promedioNotas", sumatoriaNotas.divide(new BigDecimal(cantidadEvaluados), 2, RoundingMode.CEILING));
            }
        }

        return node;
    }

    @Override
    public List<Evaluacion> allEvaluacionBySecciones(List<Seccion> secciones) {
        return evaluacionDAO.allBySecciones(secciones);
    }

    @Override
    public List<Evaluacion> allEvaluacionByEvaluacionSeccion(Seccion seccion) {
        return evaluacionDAO.allBySeccion(seccion);
    }

    @Override
    public Map<String, AlumnoEvaluacion> allAlumnoEvaluacionBySeccion(Long idSeccion) {
        List<AlumnoEvaluacion> alumnosEvaluaciones = alumnoEvaluacionDAO.allBySeccion(idSeccion);
        Map<String, AlumnoEvaluacion> mapNotas = new HashMap();
        for (AlumnoEvaluacion alumnosEvaluacion : alumnosEvaluaciones) {
            mapNotas.put(alumnosEvaluacion.getAlumno().getId() + "-" + alumnosEvaluacion.getEvaluacion().getId(), alumnosEvaluacion);
        }
        return mapNotas;
    }

    @Override
    public MatriculaSeccion findMatriculaSeccion(Long id) {
        return matriculaSeccionDAO.find(id);
    }

    @Override
    public List<AlumnoEvaluacion> allEvaluacionsByFilter(Alumno alumno, Curso curso, CicloAcademico cicloAcademico) {
        return alumnoEvaluacionDAO.allByAlumnoCursoCiclo(alumno, curso, cicloAcademico);
    }

    @Override
    public AlumnoEvaluacion findAlumnoEvaluacion(Long id, Long idEvaluacion, Long idAlumno) {
        return alumnoEvaluacionDAO.findByFilter(id, idEvaluacion, idAlumno);
    }

    @Override
    @Transactional
    public void saveReclamoNota(ReclamoNota reclamoNota, DataSessionPivot ds) {
        //Evaluacion evaluacion = evaluacionDAO.find(reclamoNota.getEvaluacion().getId());
        /*
        if (!AlumnoEvaluacion.NSP.equals(reclamoNota.getNotaInicial())) {
            DateTime fechaRealizada = new DateTime(evaluacion.getFechaRealizada());
            DateTime fechaVencimiento = fechaRealizada.plusDays(ReclamoNota.MAXIMO_DIAS_RECLAMO);
            logger.debug("Fecha evaluacion {}", fechaRealizada.toString("dd/MM/yyyy"));
            logger.debug("Fecha limite camio de nota {}", fechaVencimiento.toString("dd/MM/yyyy"));
            if (fechaVencimiento.toLocalDate().isBefore(new DateTime().toLocalDate())) {
                throw new PhobosException("Error, Superó la fecha limite para cambiar la nota.");
            }
        }*/
        reclamoNota.setEstado(EstadoEnum.CRE.name());
        reclamoNota.setFechaReclamo(new Date());
        reclamoNota.setUserReclamo(ds.getUsuario());
        reclamoNotaDAO.save(reclamoNota);

        Map<String, NotaLetra> mapNotaLetra = new HashMap<>();
        List<NotaLetra> notasLetras = notaLetraDAO.all();
        for (NotaLetra notasLetra : notasLetras) {
            mapNotaLetra.put(notasLetra.getLetra(), notasLetra);
        }

        AlumnoEvaluacion alumnoEvaluacion = alumnoEvaluacionDAO.findByFilter(null, reclamoNota.getEvaluacion().getId(), reclamoNota.getAlumno().getId());
        Evaluacion evaluacion = evaluacionDAO.find(alumnoEvaluacion.getEvaluacion().getId());
        alumnoEvaluacion.setNota(reclamoNota.getNotaFinal());
        if (evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso().isTieneCreditosVariables()) {
            alumnoEvaluacion.setValorLetra(reclamoNota.getLetraFinal());
        }
        SistemaNotas sistemaNotas = alumnoEvaluacion.getEvaluacion().getEvaluacionSeccion().getSistemaNotas();

        if (alumnoEvaluacion.getNota().equals(AlumnoEvaluacion.NSP)) {
            if (evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso().isTieneCreditosVariables()) {
                throw new PhobosException("No se permite " + AlumnoEvaluacion.NSP);
            }
            alumnoEvaluacion.setValorNumerico(BigDecimal.ZERO);
        } else if (alumnoEvaluacion.isNCV()) {
            alumnoEvaluacion.setEstadoEnum(AlumnoEvaluacionEstadoEnum.ANC);
            alumnoEvaluacion.setValorNumerico(BigDecimal.ZERO);
            alumnoEvaluacion.setMotivoAnulacion(MotivoAnulacionEnum.NOTA_NCV.name());
        } else if (sistemaNotas.isNumerico()) {
            alumnoEvaluacion.setValorNumerico(new BigDecimal(alumnoEvaluacion.getNota()));
            String notax = NumberFormat.notaDecimal(alumnoEvaluacion.getValorNumerico());
            alumnoEvaluacion.setNota(notax);
        } else {


            /*
                NotaLetra notaLetra = sistemaNotas.getNotaLetra(alumnoEvaluacion.getNota());
                alumnoEvaluacion.setValorNumerico(new BigDecimal(notaLetra.getValor()));
             */
            if (evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso().isCreditosZero()) {
                NotaLetra notaLetra = (NotaLetra) mapNotaLetra.get(alumnoEvaluacion.getValorLetra());
                alumnoEvaluacion.setNota(notaLetra.getValor().toString());
            } else {
                //  MatriculaResumen matriculaResumen = matriculaResumenDAO.findByFilter(ciclo, alumnoEach, EstadoMatriculaCursoEnum.MAT);
                Curso curso = evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso();
                MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(alumnoEvaluacion.getAlumno(), curso, ds.getCicloAcademico());
                matriculaCurso.setCreditosAprobados(Integer.valueOf(alumnoEvaluacion.getNota()));
                matriculaCurso.setNotaFinal(alumnoEvaluacion.getValorLetra());
                matriculaCursoDAO.update(matriculaCurso);
            }
            alumnoEvaluacion.setValorNumerico(new BigDecimal(alumnoEvaluacion.getNota()));
            String notax = NumberFormat.notaDecimal(alumnoEvaluacion.getValorNumerico());
            alumnoEvaluacion.setNota(notax);
        }

        alumnoEvaluacionDAO.update(alumnoEvaluacion);

        //evaluacion.getEvaluacionSeccion().getPlanCalificacion()
        //List<EvaluacionPlan> evaluacionesPlan = evaluacionPlanDAO.allByPlan(evaluacion.getEvaluacionSeccion().getPlanCalificacion());
        //logger.debug("Plan calificacion, id {} codigo {}",
        //evaluacion.getEvaluacionSeccion().getPlanCalificacion().getId().toString(), evaluacion.getEvaluacionSeccion().getPlanCalificacion().getCodigo().toString()
        //);
        calculoNotasService.calcularNotasAlumno(reclamoNota.getAlumno(), //evaluacion,
                alumnoEvaluacion.getEvaluacion().getSeccionResponsable().getGrupoSeccion(),
                alumnoEvaluacion.getEvaluacion().getSeccionResponsable().getGrupoSeccion().getCurso(),
                alumnoEvaluacion.getEvaluacion().getSeccionResponsable().getGrupoSeccion().getCicloAcademico(),
                ds);
    }

    @Override
    public Map<Long, MatriculaCurso> getMapMatriculasCursoByCicloCurso(CicloAcademico ciclo, Curso curso) {
        List<MatriculaCurso> lstMatriculaCurso = matriculaCursoDAO.findByCursoCiclo(curso, ciclo);
        Map<Long, MatriculaCurso> resultMap = new HashMap<>();
        for (MatriculaCurso matriculaCurso : lstMatriculaCurso) {
            resultMap.put(matriculaCurso.getMatriculaResumen().getAlumno().getId(), matriculaCurso);
        }
        return resultMap;
    }

    @Override
    public List<Evaluacion> allEvaluacionesByTipoSeccion(Seccion seccion) {

        List<Evaluacion> evaluacionesBySeccion = evaluacionDAO.allBySeccion(seccion);
        List<Evaluacion> evaluacionesBySeccionFinal = new ArrayList<>();
        for (Evaluacion eva : evaluacionesBySeccion) {
            if (!eva.isDesagregado() && !eva.getEvaluacionExpandida().isEstadoAnulado()) { // && eva.getEvaluacionSuperior() == null
                eva.setNombreCorto(eva.getTipoEvaluacion().getCodigo() + eva.getNumero());
                eva.setNombreLargo(eva.getTipoEvaluacion().getNombre() + " " + eva.getNumero());
                evaluacionesBySeccionFinal.add(eva);
            }
            if (eva.isDesagregado() && !eva.getEvaluacionExpandida().isEstadoAnulado()) {
                logger.debug("esta desagregado");
                if (eva.getEvaluaciones() == null || eva.getEvaluaciones().isEmpty()) {
                    continue;
                }

                logger.debug("hijos {}", eva.getEvaluaciones().size());
                for (Evaluacion evaChild : eva.getEvaluaciones()) {
                    if (!evaChild.isDesagregado() && !evaChild.getEvaluacionExpandida().isEstadoAnulado()) {
                        evaChild.setNombreCorto(evaChild.getTipoEvaluacion().getCodigo() + evaChild.getNumero());
                        evaChild.setNombreLargo(evaChild.getTipoEvaluacion().getNombre() + " " + evaChild.getNumero());
                        evaluacionesBySeccionFinal.add(evaChild);
                    }
                    if (eva.isDesagregado() && !evaChild.getEvaluacionExpandida().isEstadoAnulado()) {
                        if (evaChild.getEvaluaciones() == null || evaChild.getEvaluaciones().isEmpty() || evaChild.getEvaluacionExpandida().isEstadoAnulado()) {
                            continue;
                        }

                        for (Evaluacion evaGrandChild : evaChild.getEvaluaciones()) {
                            StringBuilder codigoPadre = new StringBuilder();
                            StringBuilder codigoHijo = new StringBuilder();
                            StringBuilder nombreHijo = new StringBuilder();
                            StringBuilder nombrePadre = new StringBuilder();

                            codigoPadre.append(evaChild.getTipoEvaluacion().getCodigo()).append(evaChild.getNumero());
                            nombrePadre.append(evaChild.getTipoEvaluacion().getNombre()).append(" ").append(evaChild.getNumero());

                            codigoHijo.append(evaGrandChild.getTipoEvaluacion().getCodigo()).append(evaGrandChild.getNumero());
                            nombreHijo.append(evaGrandChild.getTipoEvaluacion().getNombre()).append(" ").append(evaGrandChild.getNumero());

                            evaGrandChild.setNombreCorto("(" + codigoPadre + ")" + codigoHijo);
                            evaGrandChild.setNombreLargo(String.format("%s expandido de %s", nombreHijo, nombrePadre));

                            evaluacionesBySeccionFinal.add(evaGrandChild);
                        }

                    }
                }
            }
        }
        return evaluacionesBySeccionFinal;
    }

    @Override
    @Transactional
    public void saveEvaluacion(Evaluacion evaluacion) {
        if (evaluacion.getDocenteEvaluador() == null) {
            List<DocenteSeccion> docentesSecc = docenteSeccionDAO.allPersonasActivasBySeccion(evaluacion.getSeccionResponsable());
            if (docentesSecc.size() == 1) {
                Docente profe = docentesSecc.get(0).getDocente();
                evaluacion.setDocenteEvaluador(profe);
            }
        }
        evaluacionDAO.save(evaluacion);
    }

    @Override
    @Transactional
    public void cambiarTipoSeccionEvaluacion(EvaluacionExpandida evaluacionExpandida, TipoSeccionEvalEnum tipoSeccionEvalEnum) {
        logger.debug("Evaluacion Exp {}, Tipo Seccion {}", evaluacionExpandida.getId(), tipoSeccionEvalEnum.name());
        List<Evaluacion> evaluaciones = evaluacionDAO.allByFilter(null, null, null, evaluacionExpandida.getId());
        logger.debug("cantidad de evaluaciones {}", evaluaciones.size());
        for (Evaluacion eva : evaluaciones) {
            if (eva.getFechaIngresoNota() != null) {
                throw new PhobosException("No se puede cambiar el tipo de sección, ya que cuenta con evaluaciones realizadas.");
            }
        }

        evaluacionDAO.deleteByEvaluacionExpandida(evaluacionExpandida.getId());

        evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacionExpandida.getId());
        evaluacionExpandida.setTipoSeccionEnum(tipoSeccionEvalEnum);
        evaluacionExpandidaDAO.update(evaluacionExpandida);
        logger.debug("Evaluacion expandida {}", evaluacionExpandida.getId());

        GrupoSeccion grupoSeccion = evaluacionExpandida.getEvaluacionSeccion().getGrupoSeccion();

        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("Cantidad de secciones para el grupo {}", secciones.size());

        for (Seccion seccionEach : secciones) {
            logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
            logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
            logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEvalEnum().name());

            if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                    evaluacionExpandida.getTipoSeccionEvalEnum())) {

                Evaluacion evaluacion = new Evaluacion();
                evaluacion.create(evaluacionExpandida.getEvaluacionSeccion(), seccionEach, evaluacionExpandida);
                this.saveEvaluacion(evaluacion);
            }

        }

    }

    @Transactional
    @Override
    public void deletePlanCalificacion(Long idPlanCalifica, DataSessionPivot ds) {
        PlanCalificacion plan = planCalificacionDAO.find(idPlanCalifica);

        List<EvaluacionSeccion> evalSeccs = evaluacionSeccionDAO.allByPlan(plan);
        for (EvaluacionSeccion evalSecc : evalSeccs) {
            List<Evaluacion> evaluaciones = evaluacionDAO.allByEvaluacionSeccion(evalSecc);
            for (Evaluacion eval : evaluaciones) {
                evaluacionDAO.delete(eval);
            }

            List<EvaluacionExpandida> evalExpans = evaluacionExpandidaDAO.allByEvaluacionSeccion(evalSecc);
            for (EvaluacionExpandida evalExpan : evalExpans) {
                evaluacionExpandidaDAO.delete(evalExpan);
            }
            evaluacionSeccionDAO.delete(evalSecc);
        }

        List<GrupoSeccion> gpoSeccs = grupoSeccionDAO.allByPlan(plan);
        for (GrupoSeccion gpoSecc : gpoSeccs) {
            gpoSecc.setPlanCalificacion(null);
            gpoSecc.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
            grupoSeccionDAO.update(gpoSecc);
        }

        List<Curso> cursos = cursoDAO.allByPlan(plan);
        for (Curso curso : cursos) {
            curso.setPlanCalificacion(null);
            cursoDAO.update(curso);
        }

        cursos = cursoDAO.allByPlanRegular(plan);
        for (Curso curso : cursos) {
            curso.setPlanCalificacionRegular(null);
            cursoDAO.update(curso);
        }

        List<EvaluacionPlan> evalPlans = evaluacionPlanDAO.allByPlan(plan);
        for (EvaluacionPlan evalPlan : evalPlans) {
            evaluacionPlanDAO.delete(evalPlan);
        }
        planCalificacionDAO.delete(plan);

    }

    @Transactional
    @Override
    public void saveAceptarExpandir(EvaluacionExpandida[] evaluacionesExpandidas) {
        EvaluacionSeccion evaluacionSeccion = evaluacionesExpandidas[0].getEvaluacionSeccion();
        evaluacionSeccion = evaluacionSeccionDAO.find(evaluacionSeccion.getId());
        List<EvaluacionPlan> evaluacionesPlan = evaluacionPlanDAO.allByFilter(evaluacionSeccion.getPlanCalificacion().getId());
        List<EvaluacionExpandida> evaluacionExpandidasDB = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);

        for (EvaluacionExpandida evaluacionExpandida : evaluacionesExpandidas) {
            List<Evaluacion> evaluacionesPorExp = evaluacionDAO.allByFilter(null, null, null, evaluacionExpandida.getId());
            for (Evaluacion evaluacion : evaluacionesPorExp) {
                if (evaluacion.getFechaIngresoNota() != null) {
                    throw new PhobosException(String.format("Error, la evaluación %s %s ya cuenta con notas ingresadas.",
                            evaluacion.getTipoEvaluacion().getCodigo(),
                            evaluacion.getNumero()));
                }
            }
        }

        for (EvaluacionPlan evaluacionPlan : evaluacionesPlan) {
            if (evaluacionPlan.getEvaluacionesExpandidas() == null) {
                evaluacionPlan.setEvaluacionesExpandidas(new ArrayList<>());
            }
            for (EvaluacionExpandida evaluacionExpandida : evaluacionExpandidasDB) {
                if (evaluacionPlan.getTipoEvaluacion().getId().equals(
                        evaluacionExpandida.getTipoEvaluacion().getId())) {
                    evaluacionPlan.getEvaluacionesExpandidas().add(evaluacionExpandida);
                }
            }
        }

        for (EvaluacionPlan evaluacionPlan : evaluacionesPlan) {
            for (EvaluacionExpandida evaluacionExpPlan : evaluacionPlan.getEvaluacionesExpandidas()) {
                for (EvaluacionExpandida evalExp : evaluacionesExpandidas) {
                    if (evaluacionExpPlan.getId().equals(evalExp.getId())) {
                        evaluacionExpPlan.setPeso(evalExp.getPeso());
                        evaluacionPlan.setValidarPesoTotal(true);
                        break;
                    }
                }
            }
        }
        List<String> errores = new ArrayList<>();
        for (EvaluacionPlan evaluacionPlan : evaluacionesPlan) {
            if (evaluacionPlan.isValidarPesoTotal()) {

                BigDecimal pesoTotal = evaluacionPlan.getPesoTotal();
                BigDecimal pesoEvals = BigDecimal.ZERO;
                for (EvaluacionExpandida evaluacionExpandida : evaluacionPlan.getEvaluacionesExpandidas()) {
                    pesoEvals = pesoEvals.add(evaluacionExpandida.getPeso());
                }
                if (pesoTotal.compareTo(pesoEvals) != 0) {
                    errores.add(evaluacionPlan.getTipoEvaluacion().getNombre());
                }
            }
        }
        if (!errores.isEmpty()) {
            throw new PhobosException("Error en el porcentaje total de las siguientes evaluaciones : " + StringUtils.join(errores, ", "));
        }

        logger.debug("Evaluacion Seccion {}", evaluacionSeccion.getId());
        for (EvaluacionExpandida evaluacionesExpandida : evaluacionesExpandidas) {
            logger.debug("Id {}, Peso {}", evaluacionesExpandida.getId(), evaluacionesExpandida.getPeso());
            EvaluacionExpandida evaluacionesExpandidaDB = evaluacionExpandidaDAO.find(evaluacionesExpandida.getId());
            evaluacionesExpandidaDB.setPeso(evaluacionesExpandida.getPeso());
            evaluacionExpandidaDAO.update(evaluacionesExpandidaDB);
        }

    }

    @Override
    public List<Curso> allActiveCursosByPlan(PlanCalificacion planCalificacion) {
        return cursoDAO.allActiveByPlan(planCalificacion);
    }

    @Override
    @Transactional
    public void saveCerrarActa(GrupoSeccion grupoSeccion, Usuario usuario) {
        grupoSeccion = this.findGrupo(grupoSeccion.getId());
        DateTime today = new DateTime();
        if (grupoSeccion.isEstadoGrupoCerrado()) {
            throw new PhobosException("No se puede cerrar el acta debido a que el acta ya se encuentra cerrada.");
        }

        boolean evaluactionsComplete = true;
        List<String> lstSeccionesIncompletes = new ArrayList<String>();
        for (Seccion seccion : grupoSeccion.getSecciones()) {
            List<Evaluacion> evaluacionesBySeccion = this.allEvaluacionByFilter(null, null, seccion.getId());

            for (Evaluacion evaluacion : evaluacionesBySeccion) {
                List<MatriculaSeccion> matriculasSeccionByFilter = matriculaSeccionDAO.allBySeccion(seccion);
                if (matriculasSeccionByFilter.isEmpty()) {
                    continue;
                }
                if (!evaluacion.isDesagregado()) {
                    DocenteSeccion docenteSeccion = docenteSeccionDAO.findByDocenteSeccion(evaluacion.getDocenteEvaluador(), evaluacion.getSeccionResponsable());
                    if (docenteSeccion != null) {
                        if (docenteSeccion.isEstadoActivado()) {
                            if (evaluacion.getFechaIngresoNota() == null) {
                                logger.debug("falta eva {}, sec {}", evaluacion.getId(), evaluacion.getSeccionResponsable().getId());
                                evaluactionsComplete = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (!evaluactionsComplete) {
                lstSeccionesIncompletes.add(seccion.getCodigo2() + " - " + seccion.getGrupoHoras().getCodigo());
            }
        }
        if (!evaluactionsComplete) {
            throw new PhobosException(String.format("Faltan ingresar notas en las secciones %s", String.join(", ", lstSeccionesIncompletes)));
        }
        grupoSeccion.setUsuarioCierraActa(usuario);
        grupoSeccion.setFechaCierreActa(today.toDate());
        grupoSeccion.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.CER);
        grupoSeccionDAO.update(grupoSeccion);

        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allByGpoSeccion(grupoSeccion, grupoSeccion.getCicloAcademico());
        List<MatriculaResumen> matriculasResumen = matriculasSeccion.stream().map(x -> x.getMatriculaResumen()).collect(Collectors.toList());
        List<String> idsMatsResumen = matriculasResumen.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        logger.debug(String.join(",", idsMatsResumen));

        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByMatriculaResumenCurso(matriculasResumen, grupoSeccion.getCurso());//falta enviar el curso
        for (MatriculaCurso matriculaCurso : matriculasCurso) {
            Alumno alumno = matriculaCurso.getMatriculaResumen().getAlumno();
            matriculaCurso.getMatriculaResumen().getCicloAcademico();
            Curso curso = matriculaCurso.getCurso();
            // promedioService.promedio(matriculaCurso, usuario, true);
            promedioService.generarHistorialNotas(alumno, curso, matriculaCurso, grupoSeccion.getCicloAcademico(), usuario, today);
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void desvincularPlanCalificacion(GrupoSeccion grupo) {
        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(grupo.getId());

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        logger.debug("La evaluacion seccion es {}", evaluacionSeccion.getId());
        List<AlumnoEvaluacion> evaluacionsByEvalSec = alumnoEvaluacionDAO.allByFilter(evaluacionSeccion.getId(), null, null, null);
        logger.debug("Cantidad de alumno evaluaciones {}", evaluacionsByEvalSec.size());

        List<AlumnoEvaluacion> evaluacionesCalc = new ArrayList<>();

        for (AlumnoEvaluacion alumnoEvaluacion : evaluacionsByEvalSec) {
            if (alumnoEvaluacion.isEstadoCalc()) {
                evaluacionesCalc.add(alumnoEvaluacion);
            }
        }
        if (evaluacionsByEvalSec.size() == evaluacionesCalc.size()) {
            for (AlumnoEvaluacion alumnoEvaluacionEach : evaluacionesCalc) {
                alumnoEvaluacionDAO.delete(alumnoEvaluacionEach);
            }
            evaluacionsByEvalSec = alumnoEvaluacionDAO.allByFilter(evaluacionSeccion.getId(), null, null, null);
        }

        if (evaluacionsByEvalSec.isEmpty()) {
            evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
            evaluacionSeccionDAO.update(evaluacionSeccion);

            evaluacionDAO.deleteEvaluacionesByEvaluacionSeccion(evaluacionSeccion);

            evaluacionSeccionDAO.delete(evaluacionSeccion);

            grupoSeccion.setPlanCalificacion(null);
            grupoSeccionDAO.update(grupoSeccion);
        } else {
            throw new PhobosException("No se puede desvincular el sistema porque ya cuenta con evaluaciones ingresadas.");
        }
    }

    @Override
    public List<PlanCalificacionCurso> findAllActivePlanCalificacionCursos(Curso curso, TipoCicloEnum tipoCicloEnum) {
        return planCalificacionCursoDAO.allByFilter(null, tipoCicloEnum, curso, EstadoEnum.ACT);
    }

    @Override
    public List<AlumnoEvaluacion> allAlumnosEvaluacionesPorEvaluacionExpandida(Long idEvaluacionExpandida) {
        List<Evaluacion> evaluacionesPorExp = evaluacionDAO.allByFilter(null, null, null, idEvaluacionExpandida);
        List<AlumnoEvaluacion> alumnosEvaluaciones = new ArrayList<>();

        for (Evaluacion evals : evaluacionesPorExp) {
            alumnosEvaluaciones.addAll(alumnoEvaluacionDAO.allByFilter(null, null, null, evals.getId()));
        }

        return alumnosEvaluaciones;
    }

    @Override
    @Transactional(readOnly = false)
    public void cambiarAnularNotaminima(EvaluacionExpandida evaluacionExpandida, Integer notaMinimaAnulable) {
        evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacionExpandida.getId());
        evaluacionExpandida.setNotaMinimaAnulable(notaMinimaAnulable);
        evaluacionExpandidaDAO.update(evaluacionExpandida);
    }

    @Override
    @Transactional(readOnly = false)
    public void anularEvaluacionExp(EvaluacionExpandida evaluacionExpandidaAnul) {
        evaluacionExpandidaAnul = evaluacionExpandidaDAO.find(evaluacionExpandidaAnul.getId());

        List<AlumnoEvaluacion> alumnosEvaluaciones = this.allAlumnosEvaluacionesPorEvaluacionExpandida(evaluacionExpandidaAnul.getId());
        if (!alumnosEvaluaciones.isEmpty()) {
            throw new PhobosException("Error, no se puede anular la evaluación por que ya cuenta con notas ingresadas.");
        }

        EvaluacionExpandida evaluacionExpPadre = evaluacionExpandidaAnul.getEvaluacionSuperior();
        List<EvaluacionExpandida> evaluacionesHijas = evaluacionExpandidaDAO.allByFilter(null, null, evaluacionExpPadre.getId(), EstadoEnum.ACT);
        logger.debug("Cantidad de evaluaciones del mismo nivel {}", evaluacionesHijas.size());
        if (evaluacionesHijas.size() == 1) {
            throw new PhobosException("Error, no se puede anular la evaluación por que es la unica del mismo nivel.");
        }

        BigDecimal pesoEvaluacionAnul = evaluacionExpandidaAnul.getPeso();
        BigDecimal pesoTotalEvalPadre = evaluacionExpPadre.getPeso();

        BigDecimal pesoProrrateado = pesoEvaluacionAnul.divide(BigDecimal.valueOf(evaluacionesHijas.size() - 1), 4, RoundingMode.HALF_DOWN);

        int indx = 0;
        BigDecimal sumatoriaProrra = BigDecimal.ZERO;
        for (EvaluacionExpandida evalHija : evaluacionesHijas) {
            if (!evalHija.getId().equals(evaluacionExpandidaAnul.getId())) {
                indx++;
                sumatoriaProrra = sumatoriaProrra.add(pesoProrrateado);
                evalHija.setPeso(evalHija.getPeso().add(pesoProrrateado));
                if (indx == evaluacionesHijas.size()) {
                    if (sumatoriaProrra.compareTo(pesoTotalEvalPadre) != 0) {
                        BigDecimal diferencia = pesoTotalEvalPadre.subtract(sumatoriaProrra);
                        evalHija.setPeso(evalHija.getPeso().add(diferencia));
                    }
                }
                evaluacionExpandidaDAO.update(evalHija);
            }
        }

        evaluacionExpandidaAnul.setEstadoEnum(EstadoEnum.ANU);
        evaluacionExpandidaDAO.update(evaluacionExpandidaAnul);

        for (EvaluacionExpandida evaluacionesIter : evaluacionExpandidaAnul.getEvaluacionesExpandidas()) {
            evaluacionesIter.setEstadoEnum(EstadoEnum.ANU);
            evaluacionExpandidaDAO.update(evaluacionesIter);
            for (EvaluacionExpandida evaluacionesIter2 : evaluacionesIter.getEvaluacionesExpandidas()) {
                evaluacionesIter2.setEstadoEnum(EstadoEnum.ANU);
                evaluacionExpandidaDAO.update(evaluacionesIter2);
            }
        }

    }

    @Override
    public List<MatriculaSeccion> allMatriculaSeccionByFilter(EvaluacionExpandida evaluacionExpandida, CicloAcademico ciclo) {
        evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacionExpandida.getId());
        return matriculaSeccionDAO.allByGpoSeccion(evaluacionExpandida.getEvaluacionSeccion().getGrupoSeccion(), ciclo);
    }

    @Override
    public List<MatriculaCurso> allMatriculaCursoCiclo(Curso curso, CicloAcademico cicloAcademico) {
        return matriculaCursoDAO.findByCursoCiclo(curso, cicloAcademico);
    }

}
