package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.LeccionReprogramada;
import pe.edu.lamolina.pivot.dao.horario.LeccionReprogramadaDAO;

@Repository
public class LeccionReprogramadaDAOH extends AbstractEasyDAO<LeccionReprogramada> implements LeccionReprogramadaDAO {

    public LeccionReprogramadaDAOH() {
        super();
        setClazz(LeccionReprogramada.class);
    }

    @Override
    public List<LeccionReprogramada> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(LeccionReprogramada.class, "lr")
                .join("seccion sec")
                .filter("sec.id", seccion);
        return this.all(sql);
    }

}
