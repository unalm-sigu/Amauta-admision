package pe.edu.lamolina.amauta.controller.matricula.bloqueo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;
import pe.edu.lamolina.model.academico.SituacionAcademica;

public interface MatriculaBloqueoAlumnoService {

    public void update(MatriculaBloqueoAlumno matriculaBloqueoAlumno);

    public void eliminar(Long idMatriculaBloqueoAlumno);

    public MatriculaBloqueoAlumno find(Long idMatriculaBloqueoAlumno);

    public void save(MatriculaBloqueoAlumno matriculaBloqueoAlumno, DataSessionPivot ds);

    public List<MatriculaBloqueoAlumno> all(DynatableFilter filter);

    public List<Carrera> allCarrera();

    public List<SituacionAcademica> allSituacionAcademica();

    public List<CicloAcademico> allCicloAcademico(DataSessionPivot ds);

}
