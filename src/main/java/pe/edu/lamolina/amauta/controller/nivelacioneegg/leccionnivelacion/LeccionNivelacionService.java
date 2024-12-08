package pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion.dto.ControlAsistenciaDTO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

public interface LeccionNivelacionService {

    CursoNivelacion findSeccion(CursoNivelacion cursoNivelacion, Docente docente, CicloAcademico ciclo);

    List<TemaAsistencia> allLecciones(DynatableFilter filter, CursoNivelacion cursoNivelacion);

    List<ControlAsistenciaDTO> allFechasLecciones(CursoNivelacion seccion);

    TemaAsistencia crearLeccion(TemaAsistencia temaAsistencia, Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

}
