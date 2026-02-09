package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.clonar;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface ClonarProgramacionNivelacionService {

    int clonar(CicloAcademico ciclo, DataSessionPivot ds);
}
