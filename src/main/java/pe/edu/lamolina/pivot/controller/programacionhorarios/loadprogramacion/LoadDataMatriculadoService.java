package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import java.util.Map;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.misc.Acumulador;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface LoadDataMatriculadoService {

    void load(
            Acumulador acumulador,
            ControlMatriCurso control,
            MatriculaSeccion matriSecc,
            Map<String, MatriculaResumen> mapResumenes,
            Map<String, Seccion> mapSecciones,
            Map<Long, CicloAcademico> mapCiclo,
            DataSessionPivot ds);

}
