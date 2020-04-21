package pe.edu.lamolina.amauta.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;

public interface ParametroDAO extends EasyDAO<Parametro> {

    Parametro findByAmbienteParametroSistema(AmbienteAplicacionEnum ambiente, ParametrosSistemasEnum parametrosSistemas);

}
