package pe.edu.lamolina.pivot.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.Aconsejado;

public interface AlumnoConsejeroDAO extends EasyDAO<AlumnoConsejero> {

    void insertAlumnoConsejero(Consejero consejero, CicloAcademico cicloAcademico, Usuario usuario, Carrera carrera, List<Alumno> alumno);

    List<AlumnoConsejero> allByDynatableCarrera(Carrera carrera, DynatableFilter filter, CicloAcademico cicloAcademico);

    List<AlumnoConsejero> allByPersonaTutor(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor);

    List<AlumnoConsejero> allByConsejeroCiclo(Consejero consejero, CicloAcademico ciclo);

    List<AlumnoConsejero> allActivosByConsejeroCarreraCiclo(Consejero consejero, Carrera carrera, CicloAcademico ciclo);

    List<AlumnoConsejero> allActivosByCarreraCiclo(Carrera carrera, CicloAcademico ciclo);

    Aconsejado countAconsejadosMatriculables(Carrera carrera, CicloAcademico ciclo);

    Aconsejado countAconsejadosNoMatriculables(Carrera carrera, CicloAcademico ciclo);

    List<AlumnoConsejero> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    List<AlumnoConsejero> allByConsejerosAndCiclo(List<Consejero> consejeros, CicloAcademico ciclo, EstadoEnum... estados);

}
