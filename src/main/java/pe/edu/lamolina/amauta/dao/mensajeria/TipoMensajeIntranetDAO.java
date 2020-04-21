package pe.edu.lamolina.amauta.dao.mensajeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;

public interface TipoMensajeIntranetDAO extends EasyDAO<TipoMensajeIntranet> {

    List<TipoMensajeIntranet> allByDynatable(DynatableFilter filter);

}
