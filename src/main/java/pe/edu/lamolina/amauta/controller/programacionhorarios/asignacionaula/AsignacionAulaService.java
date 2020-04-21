package pe.edu.lamolina.amauta.controller.programacionhorarios.asignacionaula;

import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

interface AsignacionAulaService {

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);

    AsignacionAula findAsignacionAulaByCiclo(CicloAcademico cicloAcademico);

    void deleteAsignacion(AsignacionAula asignacionAula);

    AsignacionAula procesarAsignacionAulas(AsignacionAula asignacionAula, DataSessionPivot ds);

    AsignacionAula findAsignacionAula(AsignacionAula asignacionAula);

}
