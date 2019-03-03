package pe.edu.lamolina.pivot.controller.programacionhorarios.asignacionaula;

import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

interface AsignacionAulaService {

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);

    AsignacionAula findAsignacionAulaByCiclo(CicloAcademico cicloAcademico);

    void procesarAsignacionAulas(AsignacionAula asignacionAula, DataSessionPivot ds);

}
