package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface AlumnadoService {

    CursoNivelacion findSeccion(CursoNivelacion cursoNiv, Docente docente, CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allMatriculadosDynatable(DynatableFilter filter, CursoNivelacion seccion);

    List<NotaAlumnoNivelacion> allAlumnadoBySeccion(CursoNivelacion seccion);

}
