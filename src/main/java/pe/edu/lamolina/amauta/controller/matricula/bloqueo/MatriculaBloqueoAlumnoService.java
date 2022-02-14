package pe.edu.lamolina.amauta.controller.matricula.bloqueo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;

public interface MatriculaBloqueoAlumnoService {

    public void update(MatriculaBloqueoAlumno matriculaBloqueoAlumno);

    public void eliminar(Long idMatriculaBloqueoAlumno);

    public MatriculaBloqueoAlumno find(Long idMatriculaBloqueoAlumno);

    public void save(MatriculaBloqueoAlumno matriculaBloqueoAlumno, DataSessionPivot ds);

    public List<MatriculaBloqueoAlumno> all(DynatableFilter filter);

}
