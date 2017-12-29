package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;

public interface ColaboradorDAO extends EasyDAO<Colaborador> {

    List<Colaborador> allColaborador(List<Oficina> oficinas);

    List<Colaborador> allColaboradorByOficina(Oficina oficina);

}
