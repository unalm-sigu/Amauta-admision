package pe.edu.lamolina.pivot.dao.mensajeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.MensajeIntranet;

public interface MensajeIntranetDAO extends EasyDAO<MensajeIntranet> {

    List<MensajeIntranet> allByDynatble(DynatableFilter filter);

    MensajeIntranet find(MensajeIntranet mensajeriaForm);

}
