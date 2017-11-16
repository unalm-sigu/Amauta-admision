package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class SeccionDAOH extends AbstractDAO<Seccion> implements SeccionDAO {

    public SeccionDAOH() {
        super();
        setClazz(Seccion.class);
    }

    @Override
    public List<Seccion> allByCargaAcademica(DynatableFilter filter, Docente docente) {
        filter.setAlias("sec");
        filter.setParents("grupoSeccion gs", "docenteSeccion ds", "aula au",
                "_gs.curso cur", "left _cur.planCalificacion pc", "left _cur.planCalificacionRegular pcr");

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
    public Seccion find(Long idSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("s")
                .parents("grupoSeccion gs", "_gs.curso cur", "left _cur.planCalificacion pc", "left _cur.planCalificacionRegular pcr", "left _gs.planCalificacion pc2")
                .filter("s.id", idSeccion);
        return find(sqlUtil);
    }

    @Override
    public List<Seccion> allByFilter(Long idGrupo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("s")
                .parents("grupoSeccion gs", "_gs.curso cur", "left _cur.planCalificacion pc", "left _cur.planCalificacionRegular pcr", "_gs.planCalificacion pc2")
                .filter("gs.id", idGrupo);
        return all(sqlUtil);
    }

    @Override
    public Seccion findByCodeCiclo(String codigo, CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("s")
                .parents("grupoSeccion gs", "_gs.cicloAcademico ca", "_gs.curso cur")
                .filter("s.codigo", codigo)
                .filter("ca.id", ciclo);
        return find(sqlUtil);
    }

    @Override
    public List<Seccion> allByCiclo(CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("s")
                .parents("grupoSeccion gs", "_gs.cicloAcademico ca", "_gs.curso cur")
                .filter("ca.id", ciclo);
        return all(sqlUtil);
    }

    @Override
    public List<Seccion> allActivosByGposSeccion(List<GrupoSeccion> gruposSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("s")
                .parents("grupoSeccion gs", "_gs.cicloAcademico ca", "_gs.curso cur")
                .parents("left _s.aula", "left _s.grupoHoras")
                .filter("s.estado", EstadoEnum.ACT.name())
                .filterIn("gs.id", gruposSeccion);
        return all(sqlUtil);
    }

}
