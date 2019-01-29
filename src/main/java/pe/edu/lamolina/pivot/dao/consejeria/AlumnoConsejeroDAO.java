package pe.edu.lamolina.pivot.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AlumnoConsejeroDAO extends EasyDAO<AlumnoConsejero> {

    void insertAlumnoConsejero(Consejero get, CicloAcademico cicloAcademico, Usuario usuario, Carrera carrera, List<Alumno> alumno);

    void desasignarAlumnosConsejero(List<Consejero> consejeros, Usuario usuario );

}
