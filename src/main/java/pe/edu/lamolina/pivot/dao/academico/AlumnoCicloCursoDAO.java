package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;

public interface AlumnoCicloCursoDAO extends EasyDAO<AlumnoCicloCurso> {

    List<AlumnoCicloCurso> findHistorial(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumno(Alumno alumno);
    
    List<AlumnoCicloCurso> allByAlumnoOrdeyByCurso(Alumno alumno);
}
