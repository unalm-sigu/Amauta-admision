package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.NotaLetraDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.NotaLetra;
import pe.edu.lamolina.model.academico.SistemaNotas;

@Repository
public class NotaLetraDAOH extends AbstractEasyDAO<NotaLetra> implements NotaLetraDAO {

    public NotaLetraDAOH() {
        super();
        setClazz(NotaLetra.class);
    }

    @Override
    public List<NotaLetra> allBySistemaNotas(SistemaNotas sistemaNotas) {
        Octavia sql = Octavia.query()
                .from(NotaLetra.class, "nl")
                .join("sistemaNotas sn")
                .filter("sn.id", sistemaNotas);

        return all(sql);
    }

}
