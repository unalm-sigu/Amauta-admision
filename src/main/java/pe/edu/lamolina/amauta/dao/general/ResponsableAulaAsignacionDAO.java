package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.ResponsableAulaAsignacion;

public interface ResponsableAulaAsignacionDAO extends EasyDAO<ResponsableAulaAsignacion> {

    List<ResponsableAulaAsignacion> allByResponsable(List<ResponsableAula> responsables, EstadoEnum... estados);

    List<ResponsableAulaAsignacion> allByAulas(List<Aula> aulas, EstadoEnum... estados);

    List<ResponsableAulaAsignacion> allByEstado(EstadoEnum... estados);

}
