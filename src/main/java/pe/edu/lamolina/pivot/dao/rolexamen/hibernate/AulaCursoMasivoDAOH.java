package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;

@Repository
public class AulaCursoMasivoDAOH extends AbstractEasyDAO<AulaCursoMasivo> implements AulaCursoMasivoDAO {

    public AulaCursoMasivoDAOH() {
        super();
        setClazz(AulaCursoMasivo.class);
    }

    @Override
    public void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  AulaCursoMasivo acm where acm.cursoMasivoExamen.id = :CURSO_MASIVO ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("CURSO_MASIVO", cursoMasivoExamen.getId());
        query.executeUpdate();
    }

    @Override
    public List<AulaCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo) {
        Octavia sql = Octavia.query()
                .from(AulaCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur", "aula au")
                .filter("cme.id", cursoMasivo);
        return all(sql);
    }

    @Override
    public List<AulaCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamenes) {
        Octavia sql = Octavia.query()
                .from(AulaCursoMasivo.class, "acm")
                .join("cursoMasivoExamen cme", "userRegistro ur", "aula au")
                .in("cme.id", cursosMasivosExamenes);
        return all(sql);
    }
}
