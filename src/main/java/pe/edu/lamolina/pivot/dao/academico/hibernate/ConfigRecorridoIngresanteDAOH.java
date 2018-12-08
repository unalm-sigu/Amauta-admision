package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfigRecorridoIngresante;
import pe.edu.lamolina.pivot.dao.academico.ConfigRecorridoIngresanteDAO;

@Repository
public class ConfigRecorridoIngresanteDAOH extends AbstractEasyDAO<ConfigRecorridoIngresante> implements ConfigRecorridoIngresanteDAO {

    public ConfigRecorridoIngresanteDAOH() {
        super();
        setClazz(ConfigRecorridoIngresante.class);
    }

    @Override
    public List<ConfigRecorridoIngresante> allByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query(ConfigRecorridoIngresante.class, "cri")
                .join("cicloAcademico ci", "tipoActividadIngresante ta")
                .filter("ci.id", ciclo)
                .orderBy("cri.numero");
        return all(sql);
    }

}
