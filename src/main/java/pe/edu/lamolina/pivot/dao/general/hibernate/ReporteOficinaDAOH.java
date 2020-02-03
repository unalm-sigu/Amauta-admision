package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.AmbitoReporteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.ReporteOficina;
import pe.edu.lamolina.pivot.dao.general.ReporteOficinaDAO;

@Repository
public class ReporteOficinaDAOH extends AbstractEasyDAO<ReporteOficina> implements ReporteOficinaDAO {

    public ReporteOficinaDAOH() {
        super();
        setClazz(ReporteOficina.class);
    }

    @Override
    public List<ReporteOficina> allByOficinaAmbito(Oficina oficina, AmbitoReporteEnum ambitoReporteEnum) {
        Octavia sql = Octavia.query()
                .from(ReporteOficina.class, "ro")
                .join("oficina ofi")
                .filter("ofi.id", oficina)
                .filter("ambito", ambitoReporteEnum)
                .orderBy("ro.nombre");

        return all(sql);
    }

}
