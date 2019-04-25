package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;

public interface AlumnoAvanceCurricularDAO extends EasyDAO<AlumnoAvanceCurricular> {

    AlumnoAvanceCurricular findByAlumnoTipoCursoCurricula(Alumno alumno, TipoCursoCurricula tipoCursoCurricula);

    List<AlumnoAvanceCurricular> allByAlumno(Alumno alumno);

    void deleteAllByAlumno(Alumno alumnoBD);

    public List<AlumnoAvanceCurricular> allByAlumnos(List<Alumno> alumnos);

}
