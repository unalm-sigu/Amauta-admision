package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Dia;

public interface DiaDAO extends EasyDAO<Dia> {

    List<Dia> allDia();

    Dia findByNumeroDia(Integer numero);

    List<Dia> allDiaForPrinter();

    public List<Dia> allOrderDias();
}
