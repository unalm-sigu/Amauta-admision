package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.amauta.dao.academico.TipoRepitenciaDAO;

@Repository
public class TipoRepitenciaDAOH extends AbstractEasyDAO<TipoRepitencia> implements TipoRepitenciaDAO {

    public TipoRepitenciaDAOH() {
        super();
        setClazz(TipoRepitencia.class);
    }

    @Override
    public List<TipoRepitencia> allByCode(List<String> codigos) {
        Octavia sql = Octavia.query()
                .from(TipoRepitencia.class, "ttrr")
                .in("ttrr.codigo", codigos);
        return all(sql);
    }

}
