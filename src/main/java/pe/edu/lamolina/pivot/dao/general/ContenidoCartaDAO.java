package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface ContenidoCartaDAO extends EasyDAO<ContenidoCarta> {

    List<ContenidoCarta> allByDynaTableBySistema(DynatableFilter filter, List<Sistema> sistemas);

    ContenidoCarta findByCodigo(String mailnocert);

    ContenidoCarta findByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum);

}
