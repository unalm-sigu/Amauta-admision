package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EvaluacionExpandidaService {

    void recalcularNivel(CicloAcademico cicloAcademico, DataSessionPivot ds);

}
