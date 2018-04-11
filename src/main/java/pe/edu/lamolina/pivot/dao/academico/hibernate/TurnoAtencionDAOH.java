package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;

@Repository
public class TurnoAtencionDAOH extends AbstractEasyDAO<TurnoAtencion> implements TurnoAtencionDAO {

    @Override
    public List<TurnoAtencion> allByConfiguracion(ConfiguracionTurnosAtencion config) {
        Octavia sql = Octavia.query()
                .from(TurnoAtencion.class, "ta")
                .filter("ta.configuracionTurnosAtencion", config)
                .orderBy("ta.turno", "ta.prioridadInicio");

        return all(sql);
    }

    @Override
    public List<TurnoAtencion> allByIdTurno(ConfiguracionTurnosAtencion config, Long id) {
        Octavia sql = Octavia.query()
                .from(TurnoAtencion.class, "ta")
                .filter("ta.configuracionTurnosAtencion", config)
                .filter("ta.id", ">", id)
                .orderBy("ta.prioridadInicio");

        return all(sql);
    }

    @Override
    public TurnoAtencion findById(Long Id) {
        Octavia sql = Octavia.query()
                .from(TurnoAtencion.class, "ta")
                .join("ta.configuracionTurnosAtencion")
                .filter("ta.id", Id);
        return find(sql);
    }

    @Override
    public TurnoAtencion findLastByConfiguracion(ConfiguracionTurnosAtencion config) {
        Octavia sql = Octavia.query()
                .from(TurnoAtencion.class, "ta")
                .join("ta.configuracionTurnosAtencion cta")
                .filter("cta.id", config.getId())
                .orderBy("ta.fechaHoraInicio asc")
                .limit(1);
        return find(sql);
    }

}
