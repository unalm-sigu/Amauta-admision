package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;

public interface FuncionColaboradorDAO extends EasyDAO<FuncionColaborador> {

    List<FuncionColaborador> allByColaboradores(List<Colaborador> colaboradores);

    List<FuncionColaborador> allByColaborador(Colaborador colaborador);

    List<FuncionColaborador> allColaboradorEditor(DynatableFilter filter);

}
