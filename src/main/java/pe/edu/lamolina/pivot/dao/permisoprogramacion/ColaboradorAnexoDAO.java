package pe.edu.lamolina.pivot.dao.permisoprogramacion;

import java.util.ArrayList;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.permisoprogramacion.ColaboradorAnexo;

public interface ColaboradorAnexoDAO extends EasyDAO<ColaboradorAnexo> {

    public List<ColaboradorAnexo> allByColaboradores(ArrayList<Colaborador> colaboradores);
    ColaboradorAnexo findColaborador(Colaborador colaborador,AnexoBoletin anexoBoletin);

}
