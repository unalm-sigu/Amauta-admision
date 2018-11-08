package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.SeccionCursoMasivoEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoMasivoDAO;

@Repository
public class SeccionCursoMasivoDAOH extends AbstractEasyDAO<SeccionCursoMasivo> implements SeccionCursoMasivoDAO {

    public SeccionCursoMasivoDAOH() {
        super();
        setClazz(SeccionCursoMasivo.class);
    }

    @Override
    public List<SeccionCursoMasivo> allActiveByCursosMasivos(List<CursoMasivoExamen> cursosCursoMasivoExamens) {
        Octavia sql = Octavia.query()
                .from(SeccionCursoMasivo.class, "scm")
                .join("cursoMasivoExamen cme", "seccion sec")
                .filter("scm.estado", SeccionCursoMasivoEstadoEnum.ACT.name())
                .in("cme.id", cursosCursoMasivoExamens);
        return all(sql);
    }

}
