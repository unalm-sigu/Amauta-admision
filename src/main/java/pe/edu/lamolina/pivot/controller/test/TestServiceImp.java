package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
import pe.edu.lamolina.pivot.controller.academico.promedio.ContadorComponent;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.tramite.SerieDocumentoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class TestServiceImp implements TestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PromedioService promedioService;

    @Autowired
    ContadorComponent contadorComponent;

    @Autowired
    SerieDocumentoDAO serieDocumentoDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;
    @Autowired
    CalculoNotasService calculoNotasService;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Override
    @Transactional
    public void calcularAllResumenEvaluacion(Long seccionId, CicloAcademico ciclo, DataSessionPivot ds) {
        int loop = 1;
        visorCalculoNotas.iniciar();

        Seccion seccionPV = seccionDAO.find(seccionId);

        List<MatriculaSeccion> alumnosSeccion = matriculaSeccionDAO.allMatriculadosByGpoSeccion(seccionPV.getGrupoSeccion());
        for (MatriculaSeccion ms : alumnosSeccion) {
            Seccion seccion = ms.getSeccion();
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            Alumno alumno = ms.getMatriculaResumen().getAlumno();

            if (gpoSecc.getPlanCalificacion() == null) {
                break;
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }

            calculoNotasService.recalcularAllResumenEvalAlumno(alumno, gpoSecc, loop, ds);
            loop++;

        }
    }

    @Override
    @Transactional
    public void trasladarMatriculaCursoForPromedios(DataSessionPivot ds, Long alumnoId) {
        Alumno alumno = alumnoDAO.find(new Alumno(alumnoId));
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2017, "ca.codigo asc", CicloAcademicoEstadoEnum.ACT, CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
        ciclos.removeIf(x -> !x.getModalidadEstudio().equals(alumno.getModalidadEstudio()));
        for (CicloAcademico cicloAcademicoEach : ciclos) {
            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(new Alumno(alumnoId), cicloAcademicoEach);
            if (matriculaResumen == null) {
                continue;
            }
            List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByMatriculaResumenFull(matriculaResumen);
            if (matriculasCurso == null || matriculasCurso.isEmpty()) {
                continue;
            }

            List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allActivesByMatriculaResumen(Arrays.asList(matriculaResumen));
            visorCalculoNotas.iniciar();
            visorCalculoNotas.setCantidadTotal(1);
            logger.debug("##################Ciclo padre {} {} {}", cicloAcademicoEach.getId(), cicloAcademicoEach.getYear(), cicloAcademicoEach.getNumeroCiclo());
            promedioService.trasladarInformcionForHistorial(matriculaResumen, matriculasCurso, matriculaSeccions, ds, false);
        }
    }

    @Override
    @Transactional
    public void trasladarMatriculaCursoForPromedios(DataSessionPivot ds) {
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2017, "ca.codigo asc", CicloAcademicoEstadoEnum.ACT, CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
        //   List<GrupoSeccion> gruposSeccionesByCiclo=gruposecc
        for (CicloAcademico cicloAcademico : ciclos) {
            List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCiclo(cicloAcademico);
            List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByCicloFull(cicloAcademico);
            if (matriculasResumen.isEmpty()) {
                continue;
            }
            List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allActivesByMatriculaResumen(matriculasResumen);
            if (matriculasSeccion.isEmpty()) {
                continue;
            }

            visorCalculoNotas.iniciar();
            visorCalculoNotas.setCantidadTotal(matriculasResumen.size());
            for (MatriculaResumen matriculaResumen : matriculasResumen) {
                promedioService.trasladarInformcionForHistorial(matriculaResumen, matriculasCurso, matriculasSeccion, ds, false);
            }
        }
    }

    @Override
    @Transactional
    public void promediarciclocod(String cicloCod, DataSessionPivot ds) {
        visorCalculoNotas.iniciar();
        List<CicloAcademico> ciclosAll = cicloAcademicoDAO.all();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allByCodigo(cicloCod);
        List<String> ciclosStr = ciclos.stream().map(x -> x.toString()).collect(Collectors.toList());
        logger.info("ciclos encontrados {}", String.join(",", String.join(",", ciclosStr)));

        List<CicloAcademico> ciclosActivos = cicloAcademicoDAO.allActivosAlModalidades();

        for (CicloAcademico cicloAcademico : ciclos) {
            List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCicloFull(cicloAcademico);
            logger.info("matriculas resumen encontradas {}, del ciclo {}", matriculasResumen.size(), cicloAcademico.toString());

            for (MatriculaResumen mResumen : matriculasResumen) {
                Alumno alumno = mResumen.getAlumno();
                CicloAcademico cicloActivoByModalidad = ciclosActivos.stream()
                        .filter(x -> x.getModalidadEstudio().getCodigoEnum().equals(alumno.getModalidadEstudio().getOperativeModalidadEnum()))
                        .findFirst().orElse(null);
                promedioService.promediarAllCicloAsync(alumno, cicloActivoByModalidad, ciclosAll, null, ds);
            }
        }
    }

    @Override
    @Transactional
    public void promediarfull(DataSessionPivot ds, ModalidadEstudioEnum modalidadEstudioEnum) {
        List<String> allYears = alumnoDAO.allYearsCiclos();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
        List<Alumno> alumnosAcumulados = new ArrayList<>();
        for (String year : allYears) {
            List<Alumno> alumnos = alumnoDAO.allPendingPromedioByCicloYearAndModalidadEst(year, ModalidadEstudioEnum.PRE);
            alumnosAcumulados.addAll(alumnos);
            logger.info("Año {}, Alumnos {}, Acumulados {}", year, alumnos.size(), alumnosAcumulados.size());
        }
        contadorComponent.iniciar(alumnosAcumulados.size());
        for (Alumno alumno : alumnosAcumulados) {
            promedioService.promediarAllCicloAsync(alumno, cicloActivo, ciclos, null, ds);
        }
    }

    @Override
    @Transactional
    public void promediarAll(Long cicloId, DataSessionPivot ds) {

        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();
        CicloAcademico cicloAcademico = cicloAcademicoDAO.find(new CicloAcademico(cicloId));
        List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCicloFull(cicloAcademico);
        logger.info("matriculas resumen encontradas {}", matriculasResumen.size());

        visorCalculoNotas.iniciar();
        visorCalculoNotas.setCantidadTotal(matriculasResumen.size());
        for (MatriculaResumen mResumen : matriculasResumen) {
            Alumno alumno = mResumen.getAlumno();
            List<AlumnoCicloCurso> alumnosCicloCursoByAlumno = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
            promedioService.promediarAllCicloAsync(alumno, cicloAcademico, ciclos, alumnosCicloCursoByAlumno, ds);
        }
    }

    @Override
    @Transactional
    public void calcularAllPromediosByCiclo(DataSessionPivot ds) {
        CicloAcademico ciclo = ds.getCicloAcademico();

        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByCiclo(ciclo);
        logger.debug("Catidad de registros a procesar {}", matriculasCurso.size());
        for (MatriculaCurso matriculaCurso : matriculasCurso) {
            //   if (matriculaCurso.getMatriculaResumen().getAlumno().getId().compareTo(54234L) == 0) {

            matriculaCurso.getMatriculaResumen().getAlumno();
            matriculaCurso.getMatriculaResumen().getCicloAcademico();
            matriculaCurso.getCurso();
            promedioService.trasladoPromediosSource(matriculaCurso, ds);

            //  }
        }
    }

    @Override
    public void promediarfullBySituacion(String sit, DataSessionPivot ds, ModalidadEstudioEnum modalidadEstudioEnum) {
        List<String> allYears = alumnoDAO.allYearsCiclos();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(modalidadEstudioEnum);
        List<Alumno> alumnosAcumulados = new ArrayList<>();
        for (String year : allYears) {
            List<Alumno> alumnos = alumnoDAO.allPendingPromedioByCicloYearAndModalidadEst(year, modalidadEstudioEnum);
            alumnosAcumulados.addAll(alumnos);
            logger.info("Año {}, Alumnos {}, Acumulados {}", year, alumnos.size(), alumnosAcumulados.size());
        }
        contadorComponent.iniciar(alumnosAcumulados.size());
        for (Alumno alumno : alumnosAcumulados) {
            if (alumno.getSituacionAcademica().getCodigoEnum() != SituacionAcademicaEnum.get(sit)) {
                continue;
            }
            promedioService.promediarAllCicloAsync(alumno, cicloActivo, ciclos, null, ds);
        }
    }

}
