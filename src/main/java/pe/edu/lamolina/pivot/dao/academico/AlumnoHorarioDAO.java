package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

public interface AlumnoHorarioDAO extends EasyDAO<AlumnoHorario> {

    public List<AlumnoHorario> allByCicloAcademico(CicloAcademico cicloAcademico);

    public AlumnoHorario findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    public List<AlumnoHorario> allByAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico);

    public AlumnoHorario find(AlumnoHorario alumnoHorario);

}
