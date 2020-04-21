package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.amauta.dao.academico.CursoEquivalenteElectivoDAO;

@Repository
public class CursoEquivalenteElectivoDAOH extends AbstractEasyDAO<CursoEquivalenteElectivo> implements CursoEquivalenteElectivoDAO {

    public CursoEquivalenteElectivoDAOH() {
        super();
        setClazz(CursoEquivalenteElectivo.class);
    }

    @Override
    public List<CursoEquivalenteElectivo> allActivoByCursoOpcional(CursoOpcionalCurricula cursoOpcional) {
        Octavia sql = Octavia.query()
                .from(CursoEquivalenteElectivo.class, "ce")
                .join("cursoOpcionalCurricula cc", "cursoEquivalente cee", "cc.curso", "cc.planCurricular")
                .filter("cc.id", cursoOpcional)
                .filter("estado", EstadoEnum.ACT.name());

        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoEquivalenteElectivo> allActivoByCursosOpcionales(List<CursoOpcionalCurricula> cursosOpcionales) {
        Octavia sql = Octavia.query()
                .from(CursoEquivalenteElectivo.class, "ce")
                .join("cursoOpcionalCurricula cc", "cursoEquivalente cee", "cc.curso", "cc.planCurricular")
                .in("cc.id", cursosOpcionales)
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

    @Override
    public CursoEquivalenteElectivo findCursoPlanCurricula(Curso curso, PlanCurricular planCurricular) {

        Octavia sql = Octavia.query()
                .from(CursoEquivalenteElectivo.class, "ce")
                .join("cursoOpcionalCurricula cc", "cursoEquivalente cee", "cc.curso", "cc.planCurricular")
                .filter("cc.planCurricular", planCurricular)
                .filter("cee.id", curso)
                .filter("estado", EstadoEnum.ACT.name());

        return find(sql);
    }

    @Override
    public List<CursoEquivalenteElectivo> allCursoPlanCurricula(List<PlanCurricular> planCurricular) {

        Octavia sql = Octavia.query()
                .from(CursoEquivalenteElectivo.class, "ce")
                .left("cursoOpcionalCurricula cc", "cursoEquivalente cee", "cc.curso", "cc.planCurricular pc")
                .in("pc.id", planCurricular)
                .filter("estado", EstadoEnum.ACT.name());

        return all(sql);
    }

}
