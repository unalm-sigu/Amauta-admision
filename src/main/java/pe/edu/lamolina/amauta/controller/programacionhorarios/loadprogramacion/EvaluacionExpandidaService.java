package pe.edu.lamolina.amauta.controller.programacionhorarios.loadprogramacion;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface EvaluacionExpandidaService {

    void recalcularNivel(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void analizarLogCarga();

}
