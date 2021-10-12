package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.bienestar.ViajeCursoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

@Repository
public class ViajeCursoDAOH extends AbstractEasyDAO<ViajeCurso> implements ViajeCursoDAO {

    public ViajeCursoDAOH() {
        super();
        setClazz(ViajeCurso.class);
    }

    @Override
    public ViajeCurso find(long id) {
        Octavia sql = Octavia.query()
                .from(ViajeCurso.class, "vc")
                .join("curso", "seccion", "cicloAcademico", "alumnoDelegado", "docenteCreador")
                .filter("id", id);

        return find(sql);
    }

    @Override
    public List<ViajeCurso> allByDocenteDptosCiclo(Docente docente, List<DepartamentoAcademico> dptos, CicloAcademico ciclo, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ViajeCurso.class, "vc")
                .join("curso cu", "seccion sec", "cicloAcademico ci", "alumnoDelegado ald", "docenteCreador doc")
                .join("doc.persona pdoc", "ald.persona palu", "cu.departamentoAcademico dep")
                .filter("ci.id", ciclo)
                .searchFields("cu.nombre", "cu.codigo", "sec.codigo2", "ald.codigo")
                .searchComplexField("concat(coalesce(pdoc.paterno,''),' ',coalesce(pdoc.materno,''),' ',coalesce(pdoc.nombres,''))")
                .searchComplexField("concat(coalesce(pdoc.nombres,''),' ',coalesce(pdoc.paterno,''),' ',coalesce(pdoc.materno,''))")
                .searchComplexField("concat(coalesce(palu.paterno,''),' ',coalesce(palu.materno,''),' ',coalesce(palu.nombres,''))")
                .searchComplexField("concat(coalesce(palu.nombres,''),' ',coalesce(palu.paterno,''),' ',coalesce(palu.materno,''))")
                .orderBy("vc.id desc");

        if (docente != null && dptos.isEmpty()) {
            sql.filter("doc.id", docente);

        } else if (docente == null && !dptos.isEmpty()) {
            sql.in("dep.id", dptos);

        } else if (docente != null && !dptos.isEmpty()) {
            sql.$$$().$$$()
                    .beginBlock()
                    .filter("doc.id", docente)
                    .filter("doc.id", docente)
                    .endBlock();
        }

        return all(sql);
    }
}
