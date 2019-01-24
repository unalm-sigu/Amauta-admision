package pe.edu.lamolina.pivot.dao.consejeria.hibernate;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.pivot.dao.consejeria.ConsejeroDAO;

@Service
public class ConsejeroDAOH extends AbstractEasyDAO<Consejero> implements ConsejeroDAO {

    public ConsejeroDAOH() {
        super();
        setClazz(Consejero.class);
    }
    
    @Override
    public List<Consejero> allByCarreraDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Consejero.class, "con")
                .join("carrera car", "colaborador col", "col.persona per", "per.docente doc")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("con.id desc");
        sql.beginRelativeFilters();
        setCondicion(filter, sql);
        return all(sql);
    }

    private void setCondicion(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            String values = (String) queries.get(key);
            sql.filter(key, values);
        }
    }

    @Override
    public List<DepartamentoAcademico> allByIdFacultad(String facultadid) {
        Octavia sql = Octavia.query()
                .from(DepartamentoAcademico.class, "dep")
                .join("dep.facultad fac")
                .filter("fac.id", facultadid);
        return sql.all(getCurrentSession());

    }

    @Override
    public List<Docente> allByNombreAndDeparts(String nombre, List<DepartamentoAcademico> departs) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query().selectDistinct("doc")
                .from(Colaborador.class, "col")
                .join("col.oficina ofi", "col.cargo carg", "ofi.tipoOficina tip", "col.persona per", "per.docente doc")
                .filter("carg.id", 10) //Docente
                .filter("tip.id", 8) //departamentoAcdemico
                .in("ofi.instanciaOficina", departs)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public Consejero finByIdColaborador(Long id) {
        Octavia sql = Octavia.query()
                .from(Consejero.class, "con")
                .join("colaborador col")
                .filter("col.id", id);
          return find(sql);
    }

    @Override
    public List<Carrera> findAllCarreraByIdDocente(long idDocente) {
       Octavia sql = Octavia.query().selectDistinct("carr")
                .from(Docente.class, "doc")
                .join("departamentoAcademico dep", "dep.facultad fac", "fac.carrera carr")
                .filter("doc.id", idDocente);
          return sql.all(getCurrentSession());
    }
}
