package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteElectivoDAO;

@Repository
public class CursoEquivalenteElectivoDAOH extends AbstractEasyDAO<CursoEquivalenteElectivo> implements CursoEquivalenteElectivoDAO {

    public CursoEquivalenteElectivoDAOH() {
        super();
        setClazz(CursoEquivalenteElectivo.class);
    }

    @Override
    public List<CursoEquivalenteElectivo> allActivoByCursoOpcionalCurricula(CursoOpcionalCurricula cursoOpcionalCurricula) {
        Octavia sql = Octavia.query()
                .from(CursoEquivalenteElectivo.class, "ce")
                .filter("cursoOpcionalCurricula", cursoOpcionalCurricula)
                .filter("estado", EstadoEnum.ACT.name());

        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoEquivalenteElectivo> allActivoByPlanCurricular(PlanCurricular planCurricular) {
        Octavia sql = Octavia.query()
                .from(CursoEquivalenteElectivo.class, "ce")
                .join("cursoOpcionalCurricula cc", "cursoEquivalente cee", "cc.curso", "cc.planCurricular")
                .filter("cc.planCurricular", planCurricular)
                .filter("estado", EstadoEnum.ACT.name());

        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteByGrupoCursoOpcionalCurricula(Integer grupo, CursoOpcionalCurricula curso) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete CursoEquivalenteElectivo where cursoOpcionalCurricula = :CURSO and grupo = :GRUPO ");
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CURSO", curso);
        query.setParameter("GRUPO", grupo);
        query.executeUpdate();
    }

    @Override
    public Integer findMaxGrupoByCursoOpcionalCurricula(CursoOpcionalCurricula curso) {
        Octavia sql = Octavia.query()
                .from(CursoEquivalenteElectivo.class, "ce")
                .orderBy("grupo desc")
                .filter("cursoOpcionalCurricula", curso)
                .limit(1);

        CursoEquivalenteElectivo cursoEquivalente = (CursoEquivalenteElectivo) sql.find(getCurrentSession());

        if (cursoEquivalente == null) {
            return 0;
        }

        return cursoEquivalente.getGrupo();
    }

}
