package pe.edu.lamolina.pivot.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.AConsejeroEstado;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.ConsejeroEstado;

public interface ConsejeroDAO extends EasyDAO<Consejero> {

    List<Consejero> allByCarreraDynatable(Carrera carrera, DynatableFilter filter);

    Consejero finByIdPersona(Persona persona);

    ConsejeroEstado countConsejerosByCarrera(Carrera carrera);

    List<Consejero> allActivosByCarrera(Carrera carrera);

    List<Consejero> allByNombreAndCarrera(String nombre, Carrera carrera);

    List<Alumno> allAlumnosByConsejero(Consejero consejero);

    List<Alumno> allAlumnosByConsejero(List<Consejero> consejeros);

    Long findByMatriculaActivo(List<Alumno> alumos, Long carrera, CicloAcademico cicloacademico);

    Long findByMatriculaInactivo(List<Alumno> alumos, Long carrera, CicloAcademico cicloacademico);

    AConsejeroEstado findAconsejadosByMatricula(Long carrera, CicloAcademico cicloAcademico);

    Consejero findByColaboradorCarrera(Colaborador colaborador, Carrera carrera);
}
