package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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

    @Override
    public List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByDocente(docente, ciclo);

        List<Long> idsGpoSecc = docentesSecciones.stream()
                .map(x -> x.getSeccion().getGrupoSeccion().getId())
                .collect(Collectors.toList());

        List<GrupoSeccion> gruposSeccion = grupoSeccionDAO.allByFilter(idsGpoSecc, ciclo, null, EstadoEnum.ACT);
        List<DocenteSeccion> responsablesgrupo = docenteSeccionDAO.allResponsablesByGpoSecciones(gruposSeccion, ciclo);

        Map<Long, DocenteSeccion> mapResponsables = MapUtil.storeItems("seccion.grupoSeccion.id", responsablesgrupo);

        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            grupoSeccion.setSecciones(new ArrayList());
            DocenteSeccion responsable = mapResponsables.get(grupoSeccion.getId());
            grupoSeccion.setDocenteResponsable(responsable.getDocente());
        }

        Map<Long, GrupoSeccion> mapGposSeccion = MapUtil.storeItems("id", gruposSeccion);

        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gruposSeccion);

        List<DocenteSeccion> responsablesseccion = docenteSeccionDAO.allResponsableBySeccionCiclo(secciones, ciclo);
        Map<Long, DocenteSeccion> mapResponsableSeccion = TypesUtil.convertListToMap("seccion.id", responsablesseccion);

        Map<Long, Seccion> grupoSeccionTcurMap = secciones
                .stream()
                .filter(y -> y.getTipoSeccionEnum() == TipoSeccionEnum.TCUR)
                .collect(Collectors.toMap(x -> x.getGrupoSeccion().getId(), x -> x, (f, s) -> s));

        for (Seccion seccion : secciones) {

            DocenteSeccion docPrincipal = mapResponsableSeccion.get(seccion.getId());
            seccion.setDocentePrincipal(docPrincipal != null ? docPrincipal.getDocente() : null);

            if (seccion.getIsTipoSeccionPCUR()) {
                Seccion seccionSuper = seccion.getSeccionSuperior();
                if (seccionSuper == null) {
                    seccionSuper = grupoSeccionTcurMap.get(seccion.getGrupoSeccion().getId());
                    seccion.setSeccionSuperior(seccionSuper);
                }
            }

            GrupoSeccion gpoSecc = mapGposSeccion.get(seccion.getGrupoSeccion().getId());
            gpoSecc.getSecciones().add(seccion);

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

            MatriculaResumen matricula = matriculasMap.get(alumno.getId());
            alumno.setMotivoMatriculable("No cuenta con registro en matricula para el presente ciclo académico");

            alumno.setSituacion("0");
            if (matricula == null) {
                continue;
            }

            if (!Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matricula.getEstadoEnum())) {
                alumno.setMotivoMatriculable("No matriculable");
                continue;
            }

            MatriculaCurso matriculaCurso = matriculaCursosMap.get(matricula.getId());

            if (matriculaCurso != null && matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                alumno.setMotivoMatriculable("Ya se matriculó");
                continue;
            }

            MatriculaSeccion matriculaSeccion = matriculaSeccionesMap.get(matricula.getId());
            if (matriculaSeccion != null) {
                alumno.setMotivoMatriculable("Ya se matriculó");
                continue;
            }

            /* AlumnoCursoCurricula alumnoCursoCurricula = alumnosCursoCurriculaMap.get(alumno.getId());

            if (alumnoCursoCurricula == null) {
                alumno.setMotivoMatriculable("No cumple requisito");
                continue;
            }

            if (alumnoCursoCurricula.getEstadoEnum() == CursoCurriculaEstadoEnum.APR) {
                alumno.setMotivoMatriculable("Ya aprobó");
                continue;
            }

            if (alumnoCursoCurricula.getEstadoEnum() == CursoCurriculaEstadoEnum.NREQ) {
                alumno.setMotivoMatriculable("No cumple requisito");
                continue;
            }

            if (!Arrays.asList(CursoCurriculaEstadoEnum.HAB, CursoCurriculaEstadoEnum.SIM).contains(alumnoCursoCurricula.getEstadoEnum())) {
                alumno.setMotivoMatriculable("No cumple requisito");
                continue;
            }*/
            alumno.setSituacion("1");
        }

        return alumnos;
    }

    public void solicitarAmpliacion(AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        
    }

    @Override
    @Transactional
    public void matricular(AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        Seccion seccion = seccionDAO.find(ampliacionVacanteForm.getSeccion());
        logger.debug("seccion {} ", seccion.getId());

        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
        docentesSecciones = docentesSecciones.stream().filter(x -> x.getPrincipal() == 1).collect(Collectors.toList());
        Map<Long, DocenteSeccion> mapDocentePrincipalBySeccion = TypesUtil.convertListToMap("seccion.id", docentesSecciones);

        List<Seccion> seccionesByGrupo = seccionDAO.allByGpoSeccion(grupoSeccion);
        Seccion seccionTCUR = null;
        boolean isDocentePrincipalTCUR = false;
        if (curso.isTipoCursoTEOPRA()) {
            seccionTCUR = seccionesByGrupo.stream().filter(x -> x.isTipoSeccionTCUR()).findFirst().orElse(null);
            DocenteSeccion docenteSeccionTCUR = mapDocentePrincipalBySeccion.get(seccionTCUR.getId());
            if (docenteSeccionTCUR != null) {
                seccionTCUR.setDocentePrincipal(docenteSeccionTCUR.getDocente());
                isDocentePrincipalTCUR = true;
            }
        }

        ampliacionVacanteRestService.validarAmpliacionVacante(null, ds);

        DocenteSeccion docenteSeccion = mapDocentePrincipalBySeccion.get(seccion.getId());
        Assert.isTrue(seccionTCUR == null
                && (docenteSeccion != null && ds.getDocente().equals(docenteSeccion.getDocente())), String.format("El docente %s, no es el principal", ds.getDocente().getPersona().getApellidosNombres()));
        seccion.setDocentePrincipal(docenteSeccion.getDocente());

        List<Alumno> alumnos = alumnoDAO.allByAlumnos(ampliacionVacanteForm.getAlumnos());

        StringBuilder sb = null;

        for (Alumno alumno : alumnos) {
            sb = new StringBuilder();
            sb.append("El alumno de código de matricula ");
            sb.append(alumno.getCodigo());

            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);

            Assert.isTrue(matriculaResumen != null,
                    String.format("alumno %S no es matriculable", alumno.getPersona().getApellidosNombres()));

            Assert.isTrue(Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matriculaResumen.getEstadoEnum()),
                    String.format("alumno %S no es matriculable", alumno.getPersona().getApellidosNombres()));

            MatriculaCurso matriculaCurso = matriculaCursoDAO.findByMatriculaCurso(matriculaResumen, curso);

            if (matriculaCurso == null) {
                matriculaCurso = new MatriculaCurso(curso, matriculaResumen, EstadoMatriculaEnum.SOL);
                matriculaCursoDAO.save(matriculaCurso);
            } else {
                Assert.isFalse(matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT,
                        String.format("alumno %S ya se matriculo", alumno.getPersona().getApellidosNombres()));
                Assert.isFalse(matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.SOL,
                        String.format("alumno %S ya solicito matricularse", alumno.getPersona().getApellidosNombres()));
                Assert.isTrue(Arrays.asList(EstadoMatriculaEnum.RET, EstadoMatriculaEnum.NVAC).contains(matriculaCurso.getEstadoEnum()),
                        String.format("alumno %S no es matriculable", alumno.getPersona().getApellidosNombres()));

                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.SOL);
                matriculaCursoDAO.update(matriculaCurso);
            }

            MatriculaSeccion matriculaSeccion = matriculaSeccionDAO.findByMatriculaMatSeccion(matriculaResumen, seccion);
            Assert.isTrue(matriculaSeccion == null, String.format("alumno %S ya se matriculo", alumno.getPersona().getApellidosNombres()));
            matriculaSeccion = new MatriculaSeccion(curso, matriculaResumen, seccion, EstadoMatriculaEnum.SOL, ds.getUsuario(), ds.getFechaAccionAudit());
            if (!seccion.isTipoSeccionTCUR() && seccionTCUR != null) {
                Assert.isTrue(seccionTCUR.getDocentePrincipal() != null,
                        String.format("El docente %s no es el principal de la seccion teoria",
                                seccionTCUR.getDocentePrincipal().getPersona().getApellidosNombres()));
                matriculaSeccion.setEnSolicitud(Boolean.TRUE);
            }
            matriculaSeccionDAO.save(matriculaSeccion);

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                Seccion seccionSuper = seccion.getSeccionSuperior();
                if (seccionSuper == null) {
                    seccionSuper = seccionDAO.findByGpoSeccionTipoSeccion(grupoSeccion, TipoSeccionEnum.TCUR);
                }
                if (seccionSuper == null) {
                    throw new PhobosException("Sección no configurada");
                }
                MatriculaSeccion matriculaSeccionSuper = matriculaSeccionDAO.findByMatriculaMatSeccion(matriculaResumen, seccionSuper);
                Assert.isTrue(matriculaSeccionSuper == null, "Ya se matriculo");

                matriculaSeccionSuper
                        = new MatriculaSeccion(curso, matriculaResumen, seccionSuper, EstadoMatriculaEnum.SOL, ds.getUsuario(), ds.getFechaAccionAudit());
                matriculaSeccionDAO.save(matriculaSeccionSuper);

                // seccionSuper.setMatriculados(seccionSuper.getMatriculados() + 1);
                // seccionSuper.setAmpliacionVacante(seccionSuper.getAmpliacionVacante() + 1);
                seccionDAO.update(seccionSuper);
            }

            //  seccion.setMatriculados(seccion.getMatriculados() + 1);
            //  seccion.setAmpliacionVacante(seccion.getAmpliacionVacante() + 1);
            seccion.setSolicitudesMatricula(seccion.getSolicitudesMatricula() + 1);
            seccionDAO.update(seccion);

            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaResumen.setCreditosMatriculados(matriculaResumen.getCreditosMatriculados() + curso.getCreditos());
            matriculaResumen.setCursosMatriculados(matriculaResumen.getCursosMatriculados() + 1);
            matriculaResumenDAO.update(matriculaResumen);

        }
        throw new PhobosException("no pasaras papu.");
    }

}
