package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;

@Repository
public class CursoCicloAcademicoDAOH extends AbstractEasyDAO<CursoCicloAcademico> implements CursoCicloAcademicoDAO {

    public CursoCicloAcademicoDAOH() {
        super();
        this.setClazz(CursoCicloAcademico.class);
    }

    @Override
    public List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino) {
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .filter("ca.id", cicloDestino);

        return all(sql);
    }

    @Override
    public void updatePrecioByTpc(CicloAcademico cicloAcademico, String tpc, BigDecimal precio) {
        StringBuilder sql = new StringBuilder();

        sql.append(" update ").append(CursoCicloAcademico.class.getName()).append(" as cca set precio = :PRECIO ");
        sql.append(" where cca.curso in ( select cu.id from Curso cu where concat( cu.horasTeoria, '-', cu.horasPractica, '-', cu.creditos ) = :TPC ) ");
        sql.append(" and cca.cicloAcademico = :CICLO ");
        sql.append(" and (cca.precioPersonalizado <> 1 or cca.precioPersonalizado is null) ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("TPC", tpc);
        query.setParameter("PRECIO", precio);
        query.setParameter("CICLO", cicloAcademico);

        query.executeUpdate();
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM ")
                .append(CursoCicloAcademico.class.getName()).append(" cca ")
                .append(" WHERE cca.cicloAcademico.id=:CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();

    }

    public List<CursoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoCicloAcademico.class, "pcc")
                .join("curso cu", "cicloAcademico ci")
                .filter("ci.id", ciclo)
                .searchFields("cu.nombre")
                .orderBy("pcc.id desc");
        return all(sql);
    }

    @Override
    public List<CursoCicloAcademico> countGpoSeccByCursosCiclo(List<Curso> cursos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .select("cu.id", "count(*)")
                .into(CursoCicloAcademico.class)
                .from(GrupoSeccion.class, "gs")
                .left("curso cu", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .in("cu.id", cursos)
                .groupBy("cu.id");

        return all(sql);
    }

    @Override
    public List<CursoCicloAcademico> allByLista(List<CursoCicloAcademico> cursosCiclos) {
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .in("cca.id", cursosCiclos);

        return all(sql);
    }

}
