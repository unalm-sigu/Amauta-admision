package pe.edu.lamolina.pivot.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.general.AusenciaJefe;

public interface AusenciaJefeDAO extends EasyDAO<AusenciaJefe> {

    AusenciaJefe findSinCerrar(AusenciaJefe ausencia);

}
