package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.InasistenciaAlumno;
import pe.edu.lamolina.model.academico.TemaLeccion;

public interface InasistenciaAlumnoDAO extends EasyDAO<InasistenciaAlumno> {

    List<InasistenciaAlumno> allByTemaLeccionActives(TemaLeccion temaCiclo);

    void updateEstado(InasistenciaAlumno inasistenciaAlumno);

}
