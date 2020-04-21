package pe.edu.lamolina.amauta.dao.academico.hibernate;

import pe.edu.lamolina.amauta.dao.academico.SistemaNotasDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.SistemaNotas;

@Repository
public class SistemaNotasDAOH extends AbstractEasyDAO<SistemaNotas> implements SistemaNotasDAO {

    public SistemaNotasDAOH() {
        super();
        setClazz(SistemaNotas.class);
    }

    @Override
    public SistemaNotas find(Long id) {
        Octavia sql = Octavia.query()
                .from(SistemaNotas.class, "sn")
                .leftJoin("notaLetra nl")
                .filter("sn.id", id);

        return find(sql);
    }
}
