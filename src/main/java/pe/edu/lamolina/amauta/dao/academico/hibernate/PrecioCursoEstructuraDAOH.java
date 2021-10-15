package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.amauta.dao.academico.PrecioCursoEstructuraDAO;

@Repository
public class PrecioCursoEstructuraDAOH extends AbstractEasyDAO<PrecioCursoEstructura> implements PrecioCursoEstructuraDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PrecioCursoEstructuraDAOH() {
        super();
        setClazz(PrecioCursoEstructura.class);
    }

    @Override
    public List<PrecioCursoEstructura> allByCiclo(CicloAcademico cicloDestino) {

        StringBuilder sql = new StringBuilder();

        sql.append(" select new PrecioCursoEstructura ( ");
        sql.append("   pce.id, ");
        sql.append("   pce.tpc, ");
        sql.append("   pce.creditos, ");
        sql.append("   pce.precio, ");
        sql.append("   pce.estado, ");
        sql.append("   ( ");
        sql.append("      select count(*) ");
        sql.append("        from GrupoSeccion as gs ");
        sql.append("       inner join gs.curso cu ");
        sql.append("       inner join gs.cicloAcademico ci ");
        sql.append("       where ci.id = ci2.id ");
        sql.append("         and concat(cu.horasTeoria, '-', cu.horasPractica, '-', ");
        sql.append("             case cu.tipoCredito when 'FIJO' then cu.creditos else concat('[0 a ',cu.creditosVariables,']') end ) ");
        sql.append("             = pce.tpc ");
        sql.append("   ) ");
        sql.append(" ) ");

        sql.append(" from PrecioCursoEstructura as pce ");
        sql.append("      inner join pce.cicloAcademico ci2 ");

        sql.append(" where ci2.id =  :CICLO ");
        sql.append("   and pce.estado = :ESTADO ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("CICLO", cicloDestino.getId());
        query.setParameter("ESTADO", EstadoEnum.ACT.name());

        return query.list();
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();

        sql.append(" DELETE FROM ")
                .append(PrecioCursoEstructura.class.getName()).append(" pce ")
                .append(" WHERE pce.cicloAcademico.id=:CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public List<PrecioCursoEstructura> allByEstructurasCiclo(List<String> tpcs, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(PrecioCursoEstructura.class, "pce")
                .join("cicloAcademico ca")
                .in("tpc", tpcs)
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public PrecioCursoEstructura findByTpcCiclo(String tpc, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(PrecioCursoEstructura.class, "pce")
                .join("cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("tpc", tpc);
        return find(sql);
    }

    @Override
    public int saveList(List<PrecioCursoEstructura> preciosTpc) {
        if (preciosTpc.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(PrecioCursoEstructura.class)
                .columns("tpc", "precio", "estado", "creditos", "fechaPrecio",
                        "cicloAcademico", "userPrecio")
                .values(preciosTpc);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} PrecioCursoEstructura's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

    @Override
    public List<PrecioCursoEstructura> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(PrecioCursoEstructura.class, "pce")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

}
