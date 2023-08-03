package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaResumenDAO;

@Repository
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

    @Override
    public List<ConsejeriaResumen> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(ConsejeriaResumen.class, "cr")
                .join("carrera ca", "cicloAcademico ci")
                .filter("ci.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public void deleteByCiclo(CicloAcademico cicloAcademico) {
        
        String strQuery = "delete from ConsejeriaResumen cr where cr.cicloAcademico.id=:CICLO_ACADEMICO";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("CICLO_ACADEMICO", cicloAcademico.getId());
        query.executeUpdate();
        
    }
}
