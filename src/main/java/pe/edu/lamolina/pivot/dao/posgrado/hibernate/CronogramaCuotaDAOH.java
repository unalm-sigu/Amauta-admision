package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.CronogramaCuota;
import pe.edu.lamolina.pivot.dao.posgrado.CronogramaCuotaDAO;

@Repository
public class CronogramaCuotaDAOH extends AbstractEasyDAO<CronogramaCuota> implements CronogramaCuotaDAO {

    public CronogramaCuotaDAOH() {
        super();
        setClazz(CronogramaCuota.class);
    }

    @Override
    public List<CronogramaCuota> allByCiclo(CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .from(CronogramaCuota.class, "crono")
                .join("cicloAcademico ciclo")
                .filter("ciclo.id", ciclo)
                .orderBy("crono.numeroCuota");

        return all(sql);

    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();
        sql.append("  delete from ").append(CronogramaCuota.class.getName()).append(" crono ");
        sql.append("  where crono.cicloAcademico.id = :CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public CronogramaCuota find(CronogramaCuota cronograma) {

        Octavia sql = Octavia.query()
                .from(CronogramaCuota.class, "crono")
                .join("cicloAcademico ciclo")
                .filter("crono.id", cronograma);

        return find(sql);

    }

}
