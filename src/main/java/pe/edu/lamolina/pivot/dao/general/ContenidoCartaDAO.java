package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;

public interface ContenidoCartaDAO extends EasyDAO<ContenidoCarta> {

    List<ContenidoCarta> allByDynaTable(DynatableFilter filter);

    ContenidoCarta findByCodigo(String mailnocert);

}
