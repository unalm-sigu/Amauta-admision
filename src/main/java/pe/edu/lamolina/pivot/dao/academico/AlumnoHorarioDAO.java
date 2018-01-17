package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.HorarioCachimbos;

public interface AlumnoHorarioDAO extends EasyDAO<AlumnoHorario> {

    public List<AlumnoHorario> allByCicloAcademico(CicloAcademico cicloAcademico);

    public AlumnoHorario findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    public List<AlumnoHorario> allByAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico, Carrera carrera);

    public AlumnoHorario find(AlumnoHorario alumnoHorario);

    public List<AlumnoHorario> allByHorario(HorarioCachimbos horario);

    public List<AlumnoHorario> allByCicloHorarios(CicloAcademico cicloAcademico, List<HorarioCachimbos> horarios);

    public List<AlumnoHorario> allByHorarioCachimbos(HorarioCachimbos horarioCachimbos);

    public List<AlumnoHorario> allByAlumnoHorarioLikeList(AlumnoHorario alumnoHorario);

}
