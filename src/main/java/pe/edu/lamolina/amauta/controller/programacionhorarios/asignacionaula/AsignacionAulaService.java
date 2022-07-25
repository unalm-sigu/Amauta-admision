package pe.edu.lamolina.amauta.controller.programacionhorarios.asignacionaula;

import java.util.List;
import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Seccion;

interface AsignacionAulaService {

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);

    AsignacionAula findAsignacionAulaByCiclo(CicloAcademico cicloAcademico);

    void deleteAsignacion(AsignacionAula asignacionAula, DataSessionPivot ds);

    AsignacionAula procesarAsignacionAulas(AsignacionAula asignacionAula, DataSessionPivot ds);

    AsignacionAula findAsignacionAula(AsignacionAula asignacionAula);
    
    //List<Seccion> findSeccionesForAsignacionAula(DataSessionPivot ds);
    SeccionesResumen findSeccionesForAsignacionAula(DataSessionPivot ds);
    
    void ejecutarAsigacionParcial(List<Seccion> seccionesForm, DataSessionPivot ds);
    //AsignacionAulaParcial ejecutarAsigacionParcial(List<Seccion> seccionesForm, DataSessionPivot ds);
    
    AsignacionAula saveAsignacionAula(FormAsignacionAula asignacionAulaForm, DataSessionPivot ds);

}
