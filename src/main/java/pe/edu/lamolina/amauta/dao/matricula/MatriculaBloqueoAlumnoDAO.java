package pe.edu.lamolina.amauta.dao.matricula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;
import pe.edu.lamolina.model.academico.SituacionAcademica;

public interface MatriculaBloqueoAlumnoDAO extends EasyDAO<MatriculaBloqueoAlumno> {

    public List<MatriculaBloqueoAlumno> allDynatable(DynatableFilter filter);

    public void updateColumns(MatriculaBloqueoAlumno matriculaBloqueoAlumno, String... columns);

    public MatriculaBloqueoAlumno find(MatriculaBloqueoAlumno matriculaBloqueoAlumno);

    public MatriculaBloqueoAlumno findByCicloCarreraSituacion(CicloAcademico cicloAplica, Carrera carrera, SituacionAcademica situacionAcademica);

}
