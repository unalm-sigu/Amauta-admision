package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.InasistenciaAlumno;
import pe.edu.lamolina.model.academico.TemaLeccion;

public interface InasistenciaAlumnoDAO extends EasyDAO<InasistenciaAlumno> {

    List<InasistenciaAlumno> allActivosByTemaLeccion(TemaLeccion temaCiclo);

    List<InasistenciaAlumno> allByTemaLeccion(TemaLeccion temaCiclo);

    void updateEstado(InasistenciaAlumno inasistenciaAlumno);

}
