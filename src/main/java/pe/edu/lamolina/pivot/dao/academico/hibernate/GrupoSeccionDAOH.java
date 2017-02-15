package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

@Repository
public class GrupoSeccionDAOH extends AbstractDAO<GrupoSeccion> implements GrupoSeccionDAO {

    public GrupoSeccionDAOH() {
        super();
        setClazz(GrupoSeccion.class);
    }

    @Override
    public GrupoSeccion find(Long idGrupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "planCalificacion pc", "curso cur")
                .filter("gp.id", idGrupoSeccion);
        return find(sqlUtil);
    }

    @Override
    public List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("left planCalificacion pc", "curso cur", "cicloAcademico ca", "left _cur.planCalificacion pcc")
                .parents("_cur.departamentoAcademico da")
                .filter("ca.id", cicloAcademico);
        if (departamentoAcademico != null) {
            sqlUtil.filter("da.id", departamentoAcademico.getId());
        }
        if (ids != null) {
            sqlUtil.filterIn("gp.id", ids);
        }
        return all(sqlUtil);
    }

    @Override
    public List<GrupoSeccion> allByPlan(PlanCalificacion plan) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("planCalificacion pc")
                .filter("pc.id", plan);

        return all(sqlUtil);
    }

    @Override
    public GrupoSeccion findByCodeCiclo(String codigo, CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "left planCalificacion pc", "curso cur", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("gp.codigo", codigo);
        return find(sqlUtil);
    }

    @Override
    public List<GrupoSeccion> allByCiclo(CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "left planCalificacion pc", "curso cur", "cicloAcademico ca")
                .filter("ca.id", ciclo);
        return all(sqlUtil);
    }

}
