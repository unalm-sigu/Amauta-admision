package pe.edu.lamolina.pivot.controller.test;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
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
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2017, "ca.codigo asc", CicloAcademicoEstadoEnum.ACT, CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
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

}
