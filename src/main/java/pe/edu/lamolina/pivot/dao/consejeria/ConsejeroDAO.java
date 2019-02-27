package pe.edu.lamolina.pivot.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.consejeria.consejeria.AConsejeroEstado;
import pe.edu.lamolina.pivot.controller.consejeria.consejeria.ConsejeriaEstado;

public interface ConsejeroDAO extends EasyDAO<Consejero> {

    List<Consejero> allByCarreraDynatable(Carrera carrera, DynatableFilter filter);

    Consejero finByIdPersona(Persona persona);

    ConsejeriaEstado findByStateAndCarrera(Long carrera);

    List<Consejero> findConsejeroByEstado(Long carrera);

    List<Consejero> allByNombreAndCarrera(String nombre, Carrera carrera);

    List<Alumno> allAlumnosByConsejero(Consejero consejero);

    public Long findByMatriculaActivo(List<Alumno> alumos, Long carrera, CicloAcademico cicloacademico);

    public Long findByMatriculaInactivo(List<Alumno> alumos, Long carrera, CicloAcademico cicloacademico);

    public AConsejeroEstado findAconsejadosByMatricula(Long carrera, CicloAcademico cicloAcademico);
}
