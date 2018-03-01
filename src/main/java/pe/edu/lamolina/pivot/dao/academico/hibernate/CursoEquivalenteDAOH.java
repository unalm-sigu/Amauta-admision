package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteDAO;

@Repository
public class CursoEquivalenteDAOH extends AbstractEasyDAO<CursoEquivalente> implements CursoEquivalenteDAO {

    public CursoEquivalenteDAOH() {
        super();
        setClazz(CursoEquivalente.class);
    }

    @Override
    public void deleteByGrupoCursoCurricula(Integer grupo, CursoCurricula curso) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete CursoEquivalente where cursoCurricula = :CURSO and grupo = :GRUPO ");
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CURSO", curso);
        query.setParameter("GRUPO", grupo);
        query.executeUpdate();
    }

    @Override
    public Integer findMaxGrupoByCursoCurricula(CursoCurricula curso) {
        Octavia sql = Octavia.query()
                .from(CursoEquivalente.class, "ce")
                .orderBy("grupo desc")
                .filter("cursoCurricula", curso)
                .limit(1);

        CursoEquivalente cursoEquivalente = (CursoEquivalente) sql.find(getCurrentSession());

        if (cursoEquivalente == null) {
            return 0;
        }

        return cursoEquivalente.getGrupo();
    }

}
