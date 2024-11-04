package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Repository
public class CursoNivelacionDAOH extends AbstractEasyDAO<CursoNivelacion> implements CursoNivelacionDAO {

    public CursoNivelacionDAOH() {
        super();
        setClazz(CursoNivelacion.class);
    }

    @Override
    public List<CursoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoNivelacion.class, "cn")
                .join("docente doc")
                .leftJoin("aula", "doc.persona per")
                .filter("ci.id", ciclo)
                .searchFields("doc.codigo", "cn.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("cn.id DESC");

        return all(sql);
    }

}
