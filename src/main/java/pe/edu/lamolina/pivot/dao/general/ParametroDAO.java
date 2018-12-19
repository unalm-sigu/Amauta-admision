package pe.edu.lamolina.pivot.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface ParametroDAO extends EasyDAO<Parametro> {

    Parametro findBySistemaAmbienteParametrosSistemas(Sistema sistema, AmbienteAplicacionEnum ambiente, ParametrosSistemasEnum parametrosSistemas);
    
}
