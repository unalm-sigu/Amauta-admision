package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Egresado;

public interface EgresadoDAO extends EasyDAO<Egresado> {

    Egresado findByAlumno(Alumno alumno);

    
}
