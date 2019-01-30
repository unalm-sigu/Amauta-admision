package pe.edu.lamolina.pivot.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoConsejeroDAO extends EasyDAO<AlumnoConsejero> {

    void insertAlumnoConsejero(Consejero get, CicloAcademico cicloAcademico, Usuario usuario, Carrera carrera, List<Alumno> alumno);

    void desasignarAlumnosConsejero(List<Consejero> consejeros, Usuario usuario);

    public List<AlumnoConsejero> allByCarrera(DynatableFilter filter);

    public List<AlumnoConsejero> allByPersona(DynatableFilter filter, CicloAcademico cicloAcademico, Persona persona);

}
