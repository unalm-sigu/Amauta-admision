package pe.edu.lamolina.amauta.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoAulaEnum;
import pe.edu.lamolina.model.general.Sede;
import pe.edu.lamolina.model.general.TipoAula;

import java.util.List;

public interface TipoAulaDAO extends EasyDAO<TipoAula> {
    List<TipoAula> allByCodigos(List<String> codigos);
}
