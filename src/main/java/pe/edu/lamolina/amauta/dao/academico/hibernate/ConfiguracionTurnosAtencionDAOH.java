package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.amauta.dao.academico.ConfiguracionTurnosAtencionDAO;

@Repository
public class ConfiguracionTurnosAtencionDAOH extends AbstractEasyDAO<ConfiguracionTurnosAtencion> implements ConfiguracionTurnosAtencionDAO {

    public ConfiguracionTurnosAtencionDAOH() {
        super();
        setClazz(ConfiguracionTurnosAtencion.class);
    }

    @Override
    public List<ConfiguracionTurnosAtencion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(ConfiguracionTurnosAtencion.class, "cta")
                .join("cta.eventoCicloAcademico eca", "eca.cicloAcademico ca")
                .join("eca.eventoAcademico ea")
                .filter("ca.id", ciclo);
        return all(sql);
    }

}
