package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;

@Repository
public class PrecioCursoEstructuraDAOH extends AbstractEasyDAO<PrecioCursoEstructura> implements PrecioCursoEstructuraDAO {

    public PrecioCursoEstructuraDAOH() {
        super();
        setClazz(PrecioCursoEstructura.class);
    }

    @Override
    public List<PrecioCursoEstructura> allByCiclo(CicloAcademico cicloDestino) {

        StringBuilder sql = new StringBuilder();

        sql.append(" select new PrecioCursoEstructura ( ");
        sql.append(" pce.id, ");
        sql.append(" pce.tpc, ");
        sql.append(" pce.creditos, ");
        sql.append(" pce.precio, ");
        sql.append(" pce.estado, ");
        sql.append(" count(*) )");

        sql.append(" from GrupoSeccion as gs ");
        sql.append("        inner join gs.curso cur ");
        sql.append("        inner join gs.cicloAcademico ca, ");
        sql.append("      PrecioCursoEstructura as pce ");
        sql.append("        inner join pce.cicloAcademico ca2 ");

        sql.append(" where ca.id = :CICLO and ");
        sql.append("       ca2.id =  :CICLO and ");
        sql.append("       pce.estado = :ESTADO and ");
        sql.append("       concat(cur.horasTeoria, '-', cur.horasPractica, '-', cur.creditos) = pce.tpc ");

        sql.append(" group by pce.id ");

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

}
