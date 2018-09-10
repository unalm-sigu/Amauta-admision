package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;

public interface ConfiguraCargaAdicionalDAO extends EasyDAO<ConfiguraCargaAdicional> {

    public ConfiguraCargaAdicional findByCicloAcademico(CicloAcademico cicloAcademico);

}

