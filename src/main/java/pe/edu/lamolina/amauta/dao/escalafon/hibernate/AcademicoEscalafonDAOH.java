package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.AcademicoEscalafonDAO;
import pe.edu.lamolina.model.escalafon.AcademicoEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

@Repository
public class AcademicoEscalafonDAOH extends AbstractEasyDAO<AcademicoEscalafon> implements AcademicoEscalafonDAO {

    public AcademicoEscalafonDAOH() {
        super();
        setClazz(AcademicoEscalafon.class);
    }

    @Override
    public List<AcademicoEscalafon> allByEscalafon(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(AcademicoEscalafon.class, "ae")
                .join("escalafon es", "pais pa")
                .leftJoin("universidad uni")
                .filter("es.id", escalafon)
                .orderBy("ae.id desc");

        return all(sql);
    }
}
