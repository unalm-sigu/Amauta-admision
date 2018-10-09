package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaConceptoDAO;

@Repository
public class TarifaConceptoDAOH extends AbstractEasyDAO<TarifaConcepto> implements TarifaConceptoDAO {

    public TarifaConceptoDAOH() {
        super();
        setClazz(TarifaConcepto.class);
    }

    @Override
    public List<TarifaConcepto> allByTarifaCarrera(TarifaCarrera tarifaCarrera) {
        Octavia sql = Octavia.query(TarifaConcepto.class, "tc")
                .join("tarifaCarrera tcar", "conceptoPosgrado cp")
                .filter("tcar.id", tarifaCarrera)
                .orderBy("cp.nombre asc");

        return all(sql);
    }

    @Override
    public void deleteAllByTarifaCarrera(TarifaCarrera tarifaCarrera) {
        Query query = getCurrentSession().createQuery("delete from TarifaConcepto where tarifaCarrera = :TARIFACARRERA");
        query.setParameter("TARIFACARRERA", tarifaCarrera);
        query.executeUpdate();
    }

    @Override
    public TarifaConcepto findByConceptoPosgrado(ConceptoPosgrado conceptoPosgrado) {
        Octavia sql = Octavia.query(TarifaConcepto.class, "tc")
                .join("tarifaCarrera tcar", "conceptoPosgrado cp")
                .filter("cp.id", conceptoPosgrado)
                .orderBy("cp.nombre asc");
        return find(sql);
    }

}
