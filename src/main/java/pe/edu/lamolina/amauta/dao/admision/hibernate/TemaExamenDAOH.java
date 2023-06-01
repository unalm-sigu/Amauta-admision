package pe.edu.lamolina.amauta.dao.admision.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaCicloDAO;
import pe.edu.lamolina.model.calificacion.TemaCiclo;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class TemaExamenDAOH extends AbstractEasyDAO<TemaCiclo> implements TemaCicloDAO {

    public TemaExamenDAOH() {
        super();
        setClazz(TemaCiclo.class);
    }

    @Override
    public List<TemaCiclo> allByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(TemaCiclo.class, "eva")
                .join("temaExamen te", "cicloPostula cp")
                .filter("cp.id", ciclo)
                .orderBy("eva.orden");

        return all(sql);
    }

}
