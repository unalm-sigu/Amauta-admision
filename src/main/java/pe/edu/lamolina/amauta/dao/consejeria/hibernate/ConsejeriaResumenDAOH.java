package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import org.springframework.stereotype.Service;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaResumenDAO;

@Service
public class ConsejeriaResumenDAOH extends AbstractEasyDAO<ConsejeriaResumen> implements ConsejeriaResumenDAO {

    public ConsejeriaResumenDAOH() {
        super();
        setClazz(ConsejeriaResumen.class);
    }

    @Override
    public ConsejeriaResumen findByCarreraCiclo(Carrera carrera, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(ConsejeriaResumen.class, "cr")
                .join("carrera ca", "cicloAcademico ci")
                .filter("ci.id", ciclo)
                .filter("ca.id", carrera);

        return find(sql);
    }
}
