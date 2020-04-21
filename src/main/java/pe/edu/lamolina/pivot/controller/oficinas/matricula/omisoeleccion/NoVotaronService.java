package pe.edu.lamolina.pivot.controller.oficinas.matricula.omisoeleccion;

import java.util.List;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface NoVotaronService {

    void anularOmisosSeleccionados(List<AlumnoOmisoEleccion> omisosElecciones, DataSessionPivot ds);

    void deshacerAnuladosOmisosSeleccionados(List<AlumnoOmisoEleccion> omisionesForm, DataSessionPivot ds);

}
