package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;

public interface FuncionColaboradorDAO extends EasyDAO<FuncionColaborador> {

    List<FuncionColaborador> findFuncionByColaborador();

    public List<FuncionColaborador> findFuncionByColaborador(Colaborador colaborador);

    public List<FuncionColaborador> allByColaborador(Colaborador colaborador);


   
}
