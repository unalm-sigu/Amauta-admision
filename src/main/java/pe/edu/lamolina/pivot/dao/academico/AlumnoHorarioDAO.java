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

    List<AlumnoHorario> allByCicloAcademico(CicloAcademico cicloAcademico);

    AlumnoHorario findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoHorario> allByAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico, Carrera carrera);

    AlumnoHorario find(AlumnoHorario alumnoHorario);

    List<AlumnoHorario> allByHorario(HorarioCachimbos horario);

    List<AlumnoHorario> allByCicloHorarios(CicloAcademico cicloAcademico, List<HorarioCachimbos> horarios);

    List<AlumnoHorario> allByHorarioCachimbos(HorarioCachimbos horarioCachimbos);

    List<AlumnoHorario> allByAlumnoHorarioLikeList(AlumnoHorario alumnoHorario);

    void allSetHorarioNullByCiclo(CicloAcademico cicloAcademico);

    List<AlumnoHorario> allByCicloAcademicoOrder(CicloAcademico ciclo);

    void updateColumns(AlumnoHorario aluHorario, String... columns);

}
