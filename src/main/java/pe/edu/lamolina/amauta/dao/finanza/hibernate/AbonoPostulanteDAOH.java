package pe.edu.lamolina.amauta.dao.finanza.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.finanzas.AbonoPostulante;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.amauta.dao.finanza.AbonoPostulanteDAO;

@Repository
public class AbonoPostulanteDAOH extends AbstractEasyDAO<AbonoPostulante> implements AbonoPostulanteDAO {

    public AbonoPostulanteDAOH() {
        super();
        setClazz(AbonoPostulante.class);
    }

    @Override
    public List<AbonoPostulante> allByPostulante(Postulante postul) {
        Octavia sql = Octavia.query()
                .from(AbonoPostulante.class, "ap")
                .join("postulante po")
                .filter("po.id", postul);

        return all(sql);
    }

    @Override
    public AbonoPostulante findByItemCarga(ItemCargaAbono item) {
        Octavia sql = Octavia.query()
                .from(AbonoPostulante.class, "ap")
                .join("postulante po", "abono ab")
                .filter("ab.id", item);

        return find(sql);
    }

    @Override
    public AbonoPostulante findByPostulante(Postulante postulatenBD) {
        Octavia sql = Octavia.query()
                .from(AbonoPostulante.class, "ap")
                .join("postulante po")
                .filter("po.id", postulatenBD);

        return find(sql);
    }
}
