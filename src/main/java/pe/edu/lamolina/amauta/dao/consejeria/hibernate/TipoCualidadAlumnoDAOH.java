package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoCualidadAlumnoDAO;
import pe.edu.lamolina.model.tutoria.TipoCualidadAlumno;

@Repository
public class TipoCualidadAlumnoDAOH extends AbstractEasyDAO<TipoCualidadAlumno> implements TipoCualidadAlumnoDAO {

    public TipoCualidadAlumnoDAOH() {
        super();
        setClazz(TipoCualidadAlumno.class);
    }

    @Override
    public List<TipoCualidadAlumno> all() {
        Octavia sql = new Octavia()
                .from(TipoCualidadAlumno.class, "tc")
                .orderBy("tc.tipoCualidad", "tc.orden");

        return all(sql);
    }

}
