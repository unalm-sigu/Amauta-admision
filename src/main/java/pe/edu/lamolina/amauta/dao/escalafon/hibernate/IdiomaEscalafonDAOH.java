package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.IdiomaEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.IdiomaEscalafon;

@Repository
public class IdiomaEscalafonDAOH extends AbstractEasyDAO<IdiomaEscalafon> implements IdiomaEscalafonDAO {

    public IdiomaEscalafonDAOH() {
        super();
        setClazz(IdiomaEscalafon.class);
    }

    @Override
    public List<IdiomaEscalafon> allByEscalafon(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(IdiomaEscalafon.class, "ie")
                .join("escalafon es", "idioma idi")
                .filter("es.id", escalafon)
                .orderBy("ie.id desc");

        return all(sql);
    }
}
