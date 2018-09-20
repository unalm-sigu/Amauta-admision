package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;

public interface FuncionColaboradorDAO extends EasyDAO<FuncionColaborador> {

    List<FuncionColaborador> findFuncionByColaborador();

    List<FuncionColaborador> findFuncionByColaborador(Colaborador colaborador);

    List<FuncionColaborador> allByColaborador(Colaborador colaborador);

    List<FuncionColaborador> allColaboradorEditor(DynatableFilter filter);


   
}
