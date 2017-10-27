package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import pe.edu.lamolina.pivot.model.general.Oficina;

public interface ColaboradorDAO extends Crud<Colaborador> {

    public List<Colaborador> allColaborador(List<Oficina> oficinas);

}

