package pe.edu.lamolina.pivot.controller.test;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.tramite.SerieDocumentoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class TestServiceImp implements TestService {

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

}
