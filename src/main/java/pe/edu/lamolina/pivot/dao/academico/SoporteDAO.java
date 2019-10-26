package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Soporte;

public interface SoporteDAO extends EasyDAO<Soporte> {

    public void updateColumns(Soporte matriculaResumen, String... params);

    public List<Soporte> allDyanatable(DynatableFilter filter);

}
