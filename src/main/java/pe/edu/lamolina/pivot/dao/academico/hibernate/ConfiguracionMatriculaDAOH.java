package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionMatriculaDAO;

@Repository
public class ConfiguracionMatriculaDAOH extends AbstractEasyDAO<ConfiguracionTurnosAtencion> implements ConfiguracionMatriculaDAO {

    @Override
    public List<ConfiguracionTurnosAtencion> findEventoByConfTurnoAten(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "cta")
                .join("eventoAcademico ea")
                .filter("cta.cicloAcademico", cicloAcademico) 
                .filter("ea.tipo", "MAT");

        return all(sql);
    }

    @Override
    public List<ConfiguracionTurnosAtencion> allByCicloAcad(CicloAcademico cicloAcademico) {
        
           Octavia sql = Octavia.query()
                .from(ConfiguracionTurnosAtencion.class, "cta")
                .join("eventoCicloAcademico eca")
                .filter("eca.cicloAcademico", cicloAcademico);

        return all(sql);
        
    }
    
}
