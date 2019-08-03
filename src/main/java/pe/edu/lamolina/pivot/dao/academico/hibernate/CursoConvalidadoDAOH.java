package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoConvalidado;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.pivot.dao.academico.CursoConvalidadoDAO;

@Repository
public class CursoConvalidadoDAOH extends AbstractEasyDAO<CursoConvalidado> implements CursoConvalidadoDAO {

    public CursoConvalidadoDAOH() {
        super();
        setClazz(CursoConvalidado.class);
    }

    @Override
    public List<CursoConvalidado> allInTramiteTraslado(List<TramiteTraslado> listTramiteTraslado) {
        Octavia sql = Octavia.query()
                .from(CursoConvalidado.class, "cc")
                .join("curso cur", "tramiteTraslado ttr")
                .in("ttr.id", listTramiteTraslado)
                .orderBy("cc.id desc");

        return all(sql);
    }

}
