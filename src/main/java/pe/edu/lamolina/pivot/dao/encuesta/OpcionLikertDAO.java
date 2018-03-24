package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuesta.OpcionLikert;
import pe.edu.lamolina.model.encuesta.TipoLikert;

public interface OpcionLikertDAO extends EasyDAO<OpcionLikert> {

    public List<OpcionLikert> allOpcionLikert();

    public List<OpcionLikert> allByTipoLikert(TipoLikert tipoLikert);

}
