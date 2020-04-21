package pe.edu.lamolina.amauta.dao.migraciones.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.croacia.HistoGradMy;
import pe.edu.lamolina.model.croacia.HistoMy;
import pe.edu.lamolina.amauta.dao.migraciones.HistoMyDAO;

@Repository
public class HistoMyDAOH extends AbstractEasyDAO<HistoMy> implements HistoMyDAO {

    public HistoMyDAOH() {
        super();
        setClazz(HistoMy.class);
    }

    @Override
    public HistoMy findByHisto(HistoGradMy histo) {
        Octavia sql = Octavia.query()
                .from(HistoMy.class, "h")
                .join("histoPK pk")
                .filter("pk.matricula", histo.getMatricula())
                .filter("pk.curCodigo", histo.getCurCodigo())
                .filter("pk.ciclo", histo.getCiclo())
                .filter("pk.mov", histo.getMov());

        return find(sql);
    }

    @Override
    public List<HistoMy> allByMatricula(String matricula) {
        Octavia sql = Octavia.query()
                .from(HistoMy.class, "h")
                .join("histoPK pk")
                .filter("pk.matricula", matricula)
                .orderBy("pk.ciclo", "pk.curCodigo");

        return all(sql);
    }

}
