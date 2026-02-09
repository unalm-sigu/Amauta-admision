package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.notificarcambio;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

public interface NotificarCambioService {

    void notificaCambioAula(CursoNivelacion cursoNiv, DataSessionPivot ds);

}
