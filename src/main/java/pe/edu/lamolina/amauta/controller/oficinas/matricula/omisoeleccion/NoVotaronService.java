package pe.edu.lamolina.amauta.controller.oficinas.matricula.omisoeleccion;

import java.util.List;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;

public interface NoVotaronService {

    List<AporteAlumnoCiclo> anularOmisosSeleccionados(List<AlumnoOmisoEleccion> omisosElecciones, DataSessionPivot ds);

    void anularAportesAfectados(List<AporteAlumnoCiclo> afectados, DataSessionPivot ds);

    void deshacerAnuladosOmisosSeleccionados(List<AlumnoOmisoEleccion> omisionesForm, DataSessionPivot ds);

}
