package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;

public interface FuncionColaboradorDAO extends EasyDAO<FuncionColaborador> {

    List<FuncionColaborador> findFuncionByColaborador(Colaborador colaborador);

   
}
