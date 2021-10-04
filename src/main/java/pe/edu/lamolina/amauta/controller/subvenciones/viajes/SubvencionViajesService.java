package pe.edu.lamolina.amauta.controller.subvenciones.viajes;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

public interface SubvencionViajesService {

    List<ViajeCurso> allViajesByDynatble(Docente docente, CicloAcademico ciclo, DynatableFilter filter);

    void saveViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, DataSessionPivot ds);

    void updateViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, DataSessionPivot ds);

    void solicitarAprobarViaje(ViajeCurso viajeCurso, DataSessionPivot ds);

    void aprobarViaje(ViajeCurso viajeCurso, DataSessionPivot ds);

    void aprobarJustificacion(ViajeCurso viajeCurso, DataSessionPivot ds);

}
