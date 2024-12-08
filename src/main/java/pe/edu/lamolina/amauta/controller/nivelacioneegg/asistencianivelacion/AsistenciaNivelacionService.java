package pe.edu.lamolina.amauta.controller.nivelacioneegg.asistencianivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

public interface AsistenciaNivelacionService {

    TemaAsistencia findLeccion(TemaAsistencia temaAsistencia, Docente docente, CicloAcademico ciclo);

    List<AsistenciaNivelacion> allInscritos(DynatableFilter filter, TemaAsistencia leccion);

    void marcarAsistencia(AsistenciaNivelacion asistencia, Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

}
