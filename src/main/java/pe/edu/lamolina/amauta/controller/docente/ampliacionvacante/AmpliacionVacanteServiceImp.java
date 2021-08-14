package pe.edu.lamolina.amauta.controller.docente.ampliacionvacante;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.PlanCalificacionCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.zelper.misc.MapUtil;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Service
@Transactional(readOnly = true)
public class AmpliacionVacanteServiceImp implements AmpliacionVacanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AmpliacionVacanteRestService ampliacionVacanteRestService;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    PlanCalificacionCursoDAO planCalificacionCursoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Override
    public List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByDocente(docente, ciclo);

        List<Long> idsGpoSecc = docentesSecciones.stream()
                .map(x -> x.getSeccion().getGrupoSeccion().getId())
                .collect(Collectors.toList());

        List<GrupoSeccion> gruposSeccion = grupoSeccionDAO.allByFilter(idsGpoSecc, ciclo, null, EstadoEnum.ACT);
        List<DocenteSeccion> responsablesgrupo = docenteSeccionDAO.allResponsablesByGpoSecciones(gruposSeccion, ciclo);

        Map<Long, DocenteSeccion> mapResponsables = MapUtil.storeItems("seccion.grupoSeccion.id", responsablesgrupo);

        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gruposSeccion);
        List<DocenteSeccion> responsablesseccion = docenteSeccionDAO.allResponsableBySeccionCiclo(secciones, ciclo);
        Map<Long, DocenteSeccion> mapResponsableSeccion = TypesUtil.convertListToMap("seccion.id", responsablesseccion);

        Map<Long, Seccion> grupoSeccionTcurMap = secciones
                .stream()
                .filter(y -> y.getTipoSeccionEnum() == TipoSeccionEnum.TCUR)
                .collect(Collectors.toMap(x -> x.getGrupoSeccion().getId(), x -> x, (f, s) -> s));

        List<MatriculaSeccion> matriculasSecciones = matriculaSeccionDAO.allBySecciones(secciones);
        matriculasSecciones = matriculasSecciones.stream()
                .filter(x -> x.isEstadoSOL())
                .filter(x -> x.getEnSolicitud())
                .collect(Collectors.toList());
        Map<Long, List<MatriculaSeccion>> mapSecciones = TypesUtil.convertListToMapList("seccion.id", matriculasSecciones);
        for (Seccion seccion : secciones) {
            List<MatriculaSeccion> matriculasSeccionBySeccion = mapSecciones.get(seccion.getId());
            if (matriculasSeccionBySeccion == null) {
                matriculasSeccionBySeccion = new ArrayList<>();
            }
            seccion.setSolicitudesMatriculaAlt(matriculasSeccionBySeccion.size());
            DocenteSeccion docPrincipal = mapResponsableSeccion.get(seccion.getId());
            seccion.setDocentePrincipal(docPrincipal != null ? docPrincipal.getDocente() : null);
            seccion.setDocentePrincipalLogeado(ds.getDocente().equals(seccion.getDocentePrincipal()));

            if (seccion.getIsTipoSeccionPCUR()) {
                Seccion seccionSuper = seccion.getSeccionSuperior();
                if (seccionSuper == null) {
                    seccionSuper = grupoSeccionTcurMap.get(seccion.getGrupoSeccion().getId());
                    seccion.setSeccionSuperior(seccionSuper);
                }
                seccion.setDocentePrincipaTcurLogeado(ds.getDocente().equals(seccionSuper.getDocentePrincipal()));
            }
        }
        Map<Long, List<Seccion>> mapSeccionesByGrupoSeccion = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);

        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            grupoSeccion.setSecciones(new ArrayList());
            DocenteSeccion responsable = mapResponsables.get(grupoSeccion.getId());
            grupoSeccion.setDocenteResponsable(responsable.getDocente());
            List<Seccion> seccionesByGrupo = mapSeccionesByGrupoSeccion.get(grupoSeccion.getId());
            grupoSeccion.setSecciones(seccionesByGrupo);
        }

        return gruposSeccion;
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre, CicloAcademico cicloAcademico, Seccion seccionForm) {

        Seccion seccion = seccionDAO.find(seccionForm);
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();

        List<Alumno> alumnos = alumnoDAO.allByName(nombre);

        List<MatriculaResumen> matriculas = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> matriculasMap = TypesUtil.convertListToMap("alumno.id", matriculas);

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByMatriculaResumenCurso(matriculas, curso);
        Map<Long, MatriculaCurso> matriculaCursosMap = TypesUtil.convertListToMap("matriculaResumen.id", matriculaCursos);

        List<MatriculaSeccion> matriculaSecciones = matriculaSeccionDAO.allByMatriculaMatSeccion(matriculas, seccion);
        Map<Long, MatriculaSeccion> matriculaSeccionesMap = TypesUtil.convertListToMap("matriculaResumen.id", matriculaSecciones);

        List<AlumnoCursoCurricula> alumnosCursoCurricula = alumnoCursoCurriculaDAO.allByAlumnosCurso(alumnos, curso);
        Map<Long, AlumnoCursoCurricula> alumnosCursoCurriculaMap = TypesUtil.convertListToMap("alumno.id", alumnosCursoCurricula);

        for (Alumno alumno : alumnos) {

            MatriculaResumen matriculaResumen = matriculasMap.get(alumno.getId());
            alumno.setMotivoMatriculable("No cuenta con registro en matricula para el presente ciclo académico");

            alumno.setSituacion("0");
            if (matriculaResumen == null) {
                continue;
            }

            if (!Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matriculaResumen.getEstadoEnum())) {
                alumno.setMotivoMatriculable("No matriculable");
                continue;
            }

            MatriculaCurso matriculaCurso = matriculaCursosMap.get(matriculaResumen.getId());

            if (matriculaCurso != null && matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                alumno.setMotivoMatriculable("Ya se encuentra matriculado en otro grupo");
                continue;
            }

            MatriculaSeccion matriculaSeccion = matriculaSeccionesMap.get(matriculaResumen.getId());
            if (matriculaSeccion != null && matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                alumno.setMotivoMatriculable("Ya se encuentra matriculado en otro grupo");
                continue;
            }

            AlumnoCursoCurricula alumnoCursoCurricula = alumnosCursoCurriculaMap.get(alumno.getId());

            if (alumnoCursoCurricula == null) {
                alumno.setMotivoMatriculable("Curso no disponible en su curricula en este momento.");
                continue;
            }

            if (!Arrays.asList(
                    CursoCurriculaEstadoEnum.HAB,
                    CursoCurriculaEstadoEnum.SIM,
                    CursoCurriculaEstadoEnum.SUL).contains(alumnoCursoCurricula.getEstadoEnum())) {
                alumno.setMotivoMatriculable("El curso no cumple requisito en su curricula.");
                continue;
            }
            alumno.setSituacion("1");
        }

        return alumnos;
    }

    @Override
    @Transactional
    public void matricular(AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        Seccion seccion = seccionDAO.find(ampliacionVacanteForm.getSeccion());

        logger.debug("seccion {} ", seccion.getId());

        this.validarEventoAcademico(ds.getFechaAccionAudit(), ds.getCicloAcademico());

        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();

        Curso curso = grupoSeccion.getCurso();

        DocenteSeccion docenteSeccionPrincipalDelGrupoSeccion = this.findDocenteSeccionPrincipalDelGrupoSeccionBySeccion(seccion);

        Docente docenteLogueado = ds.getDocente();

        if (!docenteLogueado.equals(docenteSeccionPrincipalDelGrupoSeccion.getDocente())) {

            throw new PhobosException(String.format("%s No es el docente principal", ds.getDocente().getPersona().getApellidosNombres()));

        }

        List<Alumno> alumnos = alumnoDAO.allByAlumnos(ampliacionVacanteForm.getAlumnos());

        this.lazyValidatorAlumno(alumnos, ds.getCicloAcademico(), seccion);

        JsonResponse responseRest = ampliacionVacanteRestService.matricularAmpliacionVacante(seccion, alumnos, ds);
        if (!responseRest.getSuccess()) {
            throw new PhobosException(responseRest.getMessage());
        }

        for (Alumno alumno : alumnos) {

            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);

            this.calcularMatriculaResumenInfoMatriculas(matriculaResumen);
        }

    }

    public Map<Long, DocenteSeccion> getMapDocentesSeccionGroupBySeccion(GrupoSeccion grupoSeccion) {
        List<DocenteSeccion> docentesSeccionesPrincipales = docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
        docentesSeccionesPrincipales = docentesSeccionesPrincipales.stream()
                .filter(x -> x.getPrincipal() == 1 && x.getEstadoEnum() == SeccionEstadoEnum.ACT)
                .collect(Collectors.toList());

        Map<Long, DocenteSeccion> mapDocentePrincipalBySeccion = TypesUtil.convertListToMap("seccion.id", docentesSeccionesPrincipales);
        return mapDocentePrincipalBySeccion;
    }

    public void validarEventoAcademico(Date fecha, CicloAcademico cicloAcademico) {
        Date fechaAudit = this.cleanDate(fecha);
        EventoCicloAcademico eventoCicloAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.MAT_AMP_DOC);
        if (eventoCicloAcademico == null) {
            throw new PhobosException("El evento de ampliación de vacantes no configurado.");
        }
        if (fechaAudit.compareTo(eventoCicloAcademico.getFechaInicio()) < 0) {
            throw new PhobosException("Aún no está en el periodo de matrícula");
        }
        if (fechaAudit.compareTo(eventoCicloAcademico.getFechaFin()) > 0) {
            throw new PhobosException("Está fuera del periodo de matrícula");
        }
    }

    public void calcularSeccionInfoMatriculas(Seccion seccion) {
        this.calcularSeccionInfoMatriculas(seccion, null);
    }

    public void calcularSeccionInfoMatriculas(Seccion seccion, List<MatriculaSeccion> matriculasSeccion) {
        if (matriculasSeccion == null) {
            matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
        }
        List<MatriculaSeccion> solicitudesMatricula = matriculasSeccion.stream().filter(x -> x.isEstadoSOL()).collect(Collectors.toList());
        List<MatriculaSeccion> matriculas = matriculasSeccion.stream().filter(x -> x.isEstadoMAT()).collect(Collectors.toList());
        List<MatriculaSeccion> ampliacionesMatricula = matriculasSeccion.stream()
                .filter(x -> x.isEstadoMAT())
                .filter(x -> x.getEsAmpliacionVacante())
                .collect(Collectors.toList());

        Seccion seccionUpd = new Seccion(seccion.getId());
        seccionUpd.setSolicitudesMatricula(solicitudesMatricula != null ? solicitudesMatricula.size() : BigDecimal.ZERO.intValue());
        seccionUpd.setAmpliacionVacante(ampliacionesMatricula != null ? ampliacionesMatricula.size() : BigDecimal.ZERO.intValue());
        seccionUpd.setMatriculados(matriculas != null ? matriculas.size() : BigDecimal.ZERO.intValue());
        seccionDAO.updateColumns(seccionUpd, "solicitudesMatricula", "ampliacionVacante", "matriculados");
    }

    public void calcularMatriculaResumenInfoMatriculas(MatriculaResumen matriculaResumen) {
        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByMatriculaResumen(matriculaResumen);
        List<MatriculaCurso> matriculados = matriculasCurso.stream()
                .filter(x -> x.isEstadoMAT())
                .collect(Collectors.toList());
        Integer creditosMatriculados = BigDecimal.ZERO.intValue();
        if (matriculados != null) {
            for (MatriculaCurso matriculado : matriculados) {
                creditosMatriculados = creditosMatriculados + matriculado.getCreditos();
            }
        }
        MatriculaResumen matriculaResumenUpd = new MatriculaResumen(matriculaResumen.getId());
        matriculaResumenUpd.setCursosMatriculados(matriculados != null ? matriculados.size() : BigDecimal.ZERO.intValue());
        matriculaResumenUpd.setCreditosMatriculados(creditosMatriculados);
        matriculaResumenUpd.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaResumenDAO.updateColumns(matriculaResumenUpd, "cursosMatriculados", "creditosMatriculados", "estado");
    }

    private Date cleanDate(Date fecha) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaStr = sdf.format(fecha);
            return sdf.parse(fechaStr);

        } catch (Exception e) {
            return null;
        }

    }

    private DocenteSeccion findDocenteSeccionPrincipalDelGrupoSeccionBySeccion(Seccion seccion) {

        if (seccion.isTipoSeccionPCUR()) {

            if (seccion.getSeccionSuperior() == null) {

                throw new PhobosException("La sección teoria no configurada");

            }

            DocenteSeccion docenteSeccionPrincialTCUR = docenteSeccionDAO.findPrincipalBySeccion(seccion.getSeccionSuperior());

            if (docenteSeccionPrincialTCUR == null) {

                throw new PhobosException("Error, No se asignado un docente en la sección teórica.");

            }

            return docenteSeccionPrincialTCUR;

        }

        DocenteSeccion docenteSeccionPrincial = docenteSeccionDAO.findPrincipalBySeccion(seccion);

        if (docenteSeccionPrincial == null) {

            throw new PhobosException("Error, No se asignado un docente en la sección.");

        }

        return docenteSeccionPrincial;

    }

    private void lazyValidatorAlumno(List<Alumno> alumnos, CicloAcademico cicloAcademico, Seccion seccion) {

        List<MatriculaResumen> matriculasResumenes = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> mapMatriculaResumenXalumno = TypesUtil.convertListToMap("alumno.id", matriculasResumenes);

        List<MatriculaCurso> matriculasCursos = matriculaCursoDAO.allByMatriculaResumenCurso(matriculasResumenes, seccion.getGrupoSeccion().getCurso());

        Map<Long, MatriculaCurso> mapMatriculaCurso = TypesUtil.convertListToMap("matriculaResumen.id", matriculasCursos);

        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allByMatriculaMatSeccion(matriculasResumenes, seccion);

        Map<Long, MatriculaSeccion> mapMatriculaSeccion = TypesUtil.convertListToMap("matriculaResumen.id", matriculasSeccion);

        List<AlumnoCursoCurricula> alumnosCursoCurricula = alumnoCursoCurriculaDAO.allByAlumnosCurso(alumnos, seccion.getGrupoSeccion().getCurso());
        Map<Long, AlumnoCursoCurricula> alumnosCursoCurriculaMap = TypesUtil.convertListToMap("alumno.id", alumnosCursoCurricula);

        for (Alumno alumno : alumnos) {

            MatriculaResumen matriculaResumen = mapMatriculaResumenXalumno.get(alumno.getId());

            if (matriculaResumen == null) {
                throw new PhobosException(String.format("alumno %s no es matriculable", alumno.getPersona().getApellidosNombres()));
            }

            if (!Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matriculaResumen.getEstadoEnum())) {
                throw new PhobosException(String.format("alumno %s no es matriculable", alumno.getPersona().getApellidosNombres()));
            }

            MatriculaCurso matriculaCurso = mapMatriculaCurso.get(matriculaResumen.getId());

            if (matriculaCurso != null) {
                if (matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                    throw new PhobosException(String.format("alumno %s ya se matriculo", alumno.getPersona().getApellidosNombres()));
                }
            }

            MatriculaSeccion matriculaSeccion = mapMatriculaSeccion.get(matriculaResumen.getId());

            if (matriculaSeccion != null) {
                if (matriculaSeccion.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                    throw new PhobosException(String.format("alumno %S ya se matriculo", alumno.getPersona().getApellidosNombres()));
                }
            }

            AlumnoCursoCurricula alumnoCursoCurricula = alumnosCursoCurriculaMap.get(alumno.getId());

            if (!Arrays.asList(
                    CursoCurriculaEstadoEnum.HAB,
                    CursoCurriculaEstadoEnum.SIM,
                    CursoCurriculaEstadoEnum.SUL).contains(alumnoCursoCurricula.getEstadoEnum())) {

                throw new PhobosException("El curso no cumple requisito en su curricula.");

            }

            this.validarTrika(matriculaResumen, seccion.getGrupoSeccion().getCurso(),cicloAcademico);

        }

    }

    private void validarTrika(MatriculaResumen matriculaResumen, Curso curso,CicloAcademico cicloAcademico) {

        List<MatriculaCurso> matriculasCursos = matriculaCursoDAO.allMatriculadoByCicloMatricula(matriculaResumen,cicloAcademico);

        List<AlumnoCursoCurricula> alumnoCursosCurricula = alumnoCursoCurriculaDAO.allHabilesByAlumno(matriculaResumen.getAlumno());

        List<AlumnoCursoCurricula> alumnoCursosCurriculaTrikeados = alumnoCursosCurricula.stream()
                .filter(x -> x.getVecesCursado() >= AcademicoConstantine.VECES_TRIKA)
                .collect(Collectors.toList());

        List<AlumnoCursoCurricula> alumnoCursosCurriculaNoTrikeados = alumnoCursosCurricula.stream()
                .filter(x -> x.getVecesCursado() < AcademicoConstantine.VECES_TRIKA)
                .collect(Collectors.toList());

        if (alumnoCursosCurriculaTrikeados.isEmpty()) {
            return;
        }

        if (matriculaResumen.getCicloAcademico().isTipoRegular() && !matriculaResumen.getEsBeneficiadoUltimoCiclo()) {
            AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursosCurriculaTrikeados.stream()
                    .filter(x -> x.getCurso().getId().equals(curso.getId()))
                    .findFirst()
                    .orElse(null);
            if (alumnoCursoCurricula == null || !matriculasCursos.isEmpty()) {
                throw new PhobosException("Solo puede matricularse a un curso trikeado.");
            }

        } else if (matriculaResumen.getCicloAcademico().isTipoNivelacion()) {
            AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursosCurriculaNoTrikeados.stream()
                    .filter(x -> x.getCurso().getId().equals(curso.getId()))
                    .findFirst()
                    .orElse(null);
            if (alumnoCursoCurricula == null) {
                throw new PhobosException("Solo puede matricularse en cursos no trikeados.");
            }
        }

        if (matriculaResumen.getCreditosTrikaPagados() == 0 
                && matriculaResumen.getCicloAcademico().equals(cicloAcademico)) {
            throw new PhobosException("Debe generar un aporte trika.");
        }

    }
}
