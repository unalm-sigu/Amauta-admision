package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EvaluacionExpandidaService {

    void recalcularNivel(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void analizarLogCarga();

}
