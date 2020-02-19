package pe.edu.lamolina.pivot.dao.migraciones.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.croacia.HistoGradMy;
import pe.edu.lamolina.pivot.dao.migraciones.HistoGradMyDAO;

@Repository
public class HistoGradMyDAOH extends AbstractEasyDAO<HistoGradMy> implements HistoGradMyDAO {

    public HistoGradMyDAOH() {
        super();
        setClazz(HistoGradMy.class);
    }

    @Override
    public List<HistoGradMy> allByMatricula(String matricula) {
        Octavia sql = Octavia.query()
                .from(HistoGradMy.class, "h")
                .filter("matricula", matricula)
                .orderBy("ciclo", "curCodigo");

        return all(sql);
    }

}
