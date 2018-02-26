package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.SituacionConfig;

public interface SituacionConfigDAO extends EasyDAO<SituacionConfig> {

    SituacionConfig findForSituacionFinal(SituacionConfig situacionConfig);

    SituacionConfig findsSituacionConfig(SituacionConfig situacionConfig);

}
