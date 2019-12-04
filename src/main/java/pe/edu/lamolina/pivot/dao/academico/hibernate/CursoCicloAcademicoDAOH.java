package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;

@Repository
public class CursoCicloAcademicoDAOH extends AbstractEasyDAO<CursoCicloAcademico> implements CursoCicloAcademicoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CursoCicloAcademicoDAOH() {
        super();
        this.setClazz(CursoCicloAcademico.class);
    }

    @Override
    public List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino) {
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .left("tipoCursoCurricula")
                .filter("ca.id", cicloDestino);
        return all(sql);
    }

    @Override
    public List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino, CicloAcademicoEstadoEnum... estadoEnum) {
        List<CicloAcademicoEstadoEnum> estadosEnum = Arrays.asList(estadoEnum);
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .filter("ca.id", cicloDestino)
                .in("cca.estado", estadosEnum);
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
                .leftJoin("tipoCursoCurricula tipo", "tipoCarpetaTeoria tipocteo", "tipoCarpetaPractica tipocpra")
                .filter("ci.id", ciclo)
                .searchFields("cu.nombre", "cu.codigo")
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

    @Override
    public List<CursoCicloAcademico> allByCursosCiclo(List<Curso> cursos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .in("c.id", cursos);

        return all(sql);
    }

    @Override
    public CursoCicloAcademico findByCursoCiclo(Curso curso, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("c.id", curso);
        return find(sql);
    }

    @Override
    public void updateColumns(CursoCicloAcademico cursoCicloAcademico, String... columns) {
        Octavia octavia = Octavia.update(CursoCicloAcademico.class);
        for (String column : columns) {
            octavia.set(cursoCicloAcademico, column);
        }
        this.update(octavia);
    }

    @Override
    public List<CursoCicloAcademico> allByCicloAndNombre(CicloAcademico cicloAcademico, String nombre) {
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .leftJoin("tipoCursoCurricula")
                .leftJoin("c.departamentoAcademico")
                .beginBlock()
                .__().filter("c.codigo", "like", nombre)
                .__().filter("c.nombre", "like", nombre)
                .__().filter("c.tipoCurso", "like", nombre)
                .endBlock()
                .filter("ca.id", cicloAcademico)
                .limit(20);
        return all(sql);
    }

    @Override
    public int updateList(List<CursoCicloAcademico> cursosCiclos, String... columnas) {
        if (cursosCiclos.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createUpdate(CursoCicloAcademico.class)
                .set(columnas)
                .with(cursosCiclos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} CursoCicloAcademico's actualizados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
