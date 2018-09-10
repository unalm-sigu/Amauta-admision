package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.pivot.dao.academico.ConfiguraCargaAdicionalDAO;

@Repository
public class ConfiguraCargaAdicionalDAOH extends AbstractEasyDAO<ConfiguraCargaAdicional> implements ConfiguraCargaAdicionalDAO {

    public ConfiguraCargaAdicionalDAOH() {
        super();
        setClazz(ConfiguraCargaAdicional.class);
    }

    @Override
    public ConfiguraCargaAdicional findByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(ConfiguraCargaAdicional.class, "cca")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        
        return find(sql);
    }

}
