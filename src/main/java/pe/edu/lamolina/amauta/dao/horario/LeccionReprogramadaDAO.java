package pe.edu.lamolina.amauta.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.LeccionReprogramada;

public interface LeccionReprogramadaDAO extends EasyDAO<LeccionReprogramada> {

    List<LeccionReprogramada> allBySeccion(Seccion seccion);

}
