package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionMatriculaDAO;

@Repository
public class ConfiguracionMatriculaDAOH extends AbstractEasyDAO<ConfiguracionTurnosAtencion> implements ConfiguracionMatriculaDAO {

    @Override
    public ConfiguracionTurnosAtencion findConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion) {
        Octavia sql = Octavia.query()
                .from(ConfiguracionTurnosAtencion.class, "cta")
                .join("EventoCicloAcademico ec", "ec.EventoAcademico ea")
                .filter("id", configuracionTurnosAtencion);

        return find(sql);
    }

}
