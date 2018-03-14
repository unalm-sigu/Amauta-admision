package pe.edu.lamolina.pivot.dao.encuesta;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;

public interface TipoExamenVirtualDAO extends EasyDAO<TipoExamenVirtual> {

    TipoExamenVirtual findByEnum(TipoExamenVirtualEnum tipoExamenVirtualEnum);

}
