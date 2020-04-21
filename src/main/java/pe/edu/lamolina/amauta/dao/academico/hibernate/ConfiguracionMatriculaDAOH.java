package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.amauta.dao.academico.ConfiguracionMatriculaDAO;

@Repository
public class ConfiguracionMatriculaDAOH extends AbstractEasyDAO<ConfiguracionTurnosAtencion> implements ConfiguracionMatriculaDAO {

    //@Override
//    public List<ConfiguracionTurnosAtencion> findEventoByConfTurnoAten(CicloAcademico cicloAcademico) {
//        Octavia sql = Octavia.query()
//                .from(ConfiguracionTurnosAtencion.class, "cta")
//                .join("eventoCicloAcademico ea", "ea.evetoAcademico", "ea.cicloAcademico ca")
//                .filter("ca.id", cicloAcademico)
//                .filter("ea.tipo", "MAT");
//
//        return all(sql);
//    }
    @Override
    public List<ConfiguracionTurnosAtencion> allByCiclo(CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .from(ConfiguracionTurnosAtencion.class, "cta")
                .join("eventoCicloAcademico ea", "ea.eventoAcademico", "ea.cicloAcademico ca")
                .filter("ca.id", ciclo);

        return all(sql);

    }

}
