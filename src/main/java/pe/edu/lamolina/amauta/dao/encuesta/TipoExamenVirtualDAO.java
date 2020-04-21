package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;

public interface TipoExamenVirtualDAO extends EasyDAO<TipoExamenVirtual> {

    TipoExamenVirtual findByEnum(TipoExamenVirtualEnum tipoExamenVirtualEnum);

    List<TipoExamenVirtual> allEncuestaEstudiantil();

}
