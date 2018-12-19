package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;

public interface TipoActividadIngresanteDAO extends EasyDAO<TipoActividadIngresante> {

    public TipoActividadIngresante findCodigo(TipoActividadIngresanteEnum tipoActividadIngresanteEnum);

}
