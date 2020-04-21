package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.AulaReservada;
import pe.edu.lamolina.model.bienestar.ReservaAula;

public interface AulaReservadaDAO extends EasyDAO<AulaReservada> {

    public List<AulaReservada> allByReservaAula(ReservaAula reservaAula);

    public void deleteAllByReservaAula(ReservaAula reservaAula);

   public List<AulaReservada> allByReservaAulas(List<ReservaAula> reservaAulas);

}
