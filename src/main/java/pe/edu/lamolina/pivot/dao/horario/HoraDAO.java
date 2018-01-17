package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.horario.Hora;

public interface HoraDAO extends EasyDAO<Hora> {

    List<Hora> allHora();

}
