package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;

public interface ConsejeriaHistorialDAO extends EasyDAO<ConsejeriaHistorial> {

    public List<ConsejeriaHistorial> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

}
