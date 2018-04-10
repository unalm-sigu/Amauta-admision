package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.OpcionLikert;
import pe.edu.lamolina.model.encuestaestudiantil.TipoLikert;

public interface OpcionLikertDAO extends EasyDAO<OpcionLikert> {

    public List<OpcionLikert> allOpcionLikert();

    public List<OpcionLikert> allByTipoLikert(TipoLikert tipoLikert);

}
