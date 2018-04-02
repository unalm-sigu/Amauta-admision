package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuesta.TipoLikert;

public interface TipoLikertDAO extends EasyDAO<TipoLikert> {

    public List<TipoLikert> allByOpciones(Integer opciones);

}
