package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.horario.Hora;

public interface HoraDAO extends EasyDAO<Hora> {

    Hora findByNumeroHora(Integer numero);

    List<Hora> allHoraInitOcho();

    List<Hora> allByInicioFin(Hora inicio, Hora fin);

    List<Hora> allHorasByRango(int min, int max);

    List<Hora> allHoras();
}
