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
public class TurnoAtencionDAOH extends AbstractEasyDAO<TurnoAtencion> implements TurnoAtencionDAO{

    @Override
    public List<TurnoAtencion> findConfiguracion(ConfiguracionTurnosAtencion config) {
        Octavia sql = Octavia.query()
                .from(TurnoAtencion.class, "ta")
                .filter("ta.configuracionTurnosAtencion", config)
                .orderBy("ta.turno");

        return all(sql);
    }
    
}
