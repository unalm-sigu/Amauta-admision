package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaAsesorDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaAsesor;

@Repository
public class ExperienciaAsesorDAOH extends AbstractEasyDAO<ExperienciaAsesor> implements ExperienciaAsesorDAO {

    public ExperienciaAsesorDAOH() {
        super();
        setClazz(ExperienciaAsesor.class);
    }

    @Override
    public List<ExperienciaAsesor> allByEscalafon(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(ExperienciaAsesor.class, "ee")
                .join("escalafon es", "universidad uni")
                .filter("es.id", escalafon)
                .orderBy("ee.id desc");

        return all(sql);
    }
}
