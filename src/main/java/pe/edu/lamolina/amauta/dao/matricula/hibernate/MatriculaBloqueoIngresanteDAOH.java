package pe.edu.lamolina.amauta.dao.matricula.hibernate;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaBloqueoIngresanteDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;

@Repository
public class MatriculaBloqueoIngresanteDAOH extends AbstractEasyDAO<MatriculaBloqueoIngresante> implements MatriculaBloqueoIngresanteDAO {

    public MatriculaBloqueoIngresanteDAOH() {
        super();
        setClazz(MatriculaBloqueoIngresante.class);
    }

    @Override
    public List<MatriculaBloqueoIngresante> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(MatriculaBloqueoIngresante.class, "mbi")
                .join("cicloAcademico ca", "ingresante i", "i.postulante p", "p.modalidadIngreso", "p.persona per", "p.cicloPostula cp", "cp.cicloAcademico", "i.carrera car")
                .searchFields("i.codigo", "car.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", cicloAcademico)
                .orderBy("car.nombre","per.paterno","per.materno","per.nombres");
        sql.beginRelativeFilters();
        this.setMatricula(filter, sql);
        return all(sql);
    }

    private void setMatricula(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("matricula")) {
                continue;
            }
            String value = (String) queries.get(key);
            if (value.equals("1")) {
                sql.filter("matricula", Boolean.TRUE);
            } else if (value.equals("0")) {
                sql.filter("matricula", Boolean.FALSE);
            }
        }

    }

    @Override
    public List<MatriculaBloqueoIngresante> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaBloqueoIngresante.class, "mbi")
                .join("cicloAcademico ca", "ingresante i", "i.postulante p", "i.carrera car", "p.modalidadIngreso mi", "p.cicloPostula cp", "cp.cicloAcademico")
                .filter("ca.id", cicloAcademico)
                .orderBy("mi.nombre","car.nombre");
        return sql.all(getCurrentSession());
    }

}
