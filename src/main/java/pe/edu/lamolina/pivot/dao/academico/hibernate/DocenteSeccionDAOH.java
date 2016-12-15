package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.Seccion;

@Repository
public class DocenteSeccionDAOH extends AbstractDAO<DocenteSeccion> implements DocenteSeccionDAO {

    public DocenteSeccionDAOH() {
        super();
        setClazz(DocenteSeccion.class);
    }

    @Override
    public DocenteSeccion find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc");
        sqlUtil.parents("docente doc", "seccion sec", "_sec.grupoSeccion gs", "left _sec.seccionSuperior");
        sqlUtil.filter("dc.id", id);
        return this.find(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico cicloAcademico) {
        filter.setAlias("dc");
        filter.setParents("docente doc", "seccion sec", "_sec.grupoSeccion gs", "left _sec.aula au",
                "_gs.curso cur", "left _cur.planCalificacion pc", "left _gs.planCalificacion pc2", "_gs.cicloAcademico ca");
        filter.filterFix("doc.id", docente.getId());
        filter.filterFix("ca.id", cicloAcademico.getId());

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        if (filtersFix != null) {
            for (Object key : filtersFix.keySet()) {
                this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
            }
        }
        Map filterFixIn = filter.getFiltersInFixed();
        if (filterFixIn != null) {
            for (Object key : filterFixIn.keySet()) {
                this.filterInFixed(sqlUtil, (String) key, (List) filterFixIn.get(key));
            }
        }
        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allByDocente(Docente docente) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc");
        sqlUtil.parents("docente doc", "seccion sec", "_sec.grupoSeccion gs", "left _sec.aula au",
                "_gs.curso cur", "left _cur.planCalificacion pc", "left _gs.planCalificacion pc2");
        sqlUtil.filter("doc.id", docente.getId());
        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allBySeccion(Seccion seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc");
        sqlUtil.parents("docente doc", "seccion sec", "_sec.grupoSeccion gs", "left _sec.aula au",
                "_gs.curso cur", "left _cur.planCalificacion pc", "left _gs.planCalificacion pc2");
        sqlUtil.filter("sec.id", seccion.getId());
        return this.all(sqlUtil);
    }

}
