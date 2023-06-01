package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ParteInformeTutoriaDAO;
import pe.edu.lamolina.model.tutoria.ParteInformeTutoria;

@Repository
public class ParteInformeTutoriaDAOH extends AbstractEasyDAO<ParteInformeTutoria> implements ParteInformeTutoriaDAO {

    public ParteInformeTutoriaDAOH() {
        super();
        setClazz(ParteInformeTutoria.class);
    }

    @Override
    public List<ParteInformeTutoria> all() {
        Octavia sql = Octavia.query()
                .from(ParteInformeTutoria.class, "pa")
                .orderBy("pa.orden");

        return all(sql);
    }

}
