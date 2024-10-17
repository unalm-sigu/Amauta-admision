package pe.edu.lamolina.amauta.dao.admision.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaCicloDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.calificacion.TemaCiclo;

@Repository
public class TemaCicloDAOH extends AbstractEasyDAO<TemaCiclo> implements TemaCicloDAO {

    public TemaCicloDAOH() {
        super();
        setClazz(TemaCiclo.class);
    }

    @Override
    public List<TemaCiclo> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(TemaCiclo.class, "eva")
                .join("temaExamen te", "cicloPostula cp", "cp.cicloAcademico ci")
                .filter("ci.id", ciclo)
                .orderBy("eva.orden");

        return all(sql);
    }

}
