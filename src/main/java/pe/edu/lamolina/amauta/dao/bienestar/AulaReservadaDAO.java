package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.AulaReservada;
import pe.edu.lamolina.model.tramite.ReservaAula;

public interface AulaReservadaDAO extends EasyDAO<AulaReservada> {

    List<AulaReservada> allByReservaAula(ReservaAula reservaAula);

    void deleteAllByReservaAula(ReservaAula reservaAula);

    List<AulaReservada> allByReservaAulas(List<ReservaAula> reservaAulas);

}
