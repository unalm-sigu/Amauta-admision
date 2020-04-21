package pe.edu.lamolina.amauta.dao.horario.hibernate;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.HorarioFallido;
import pe.edu.lamolina.amauta.dao.horario.HorarioFallidoDAO;

@Repository
public class HorarioFallidoDAOH extends AbstractEasyDAO<HorarioFallido> implements HorarioFallidoDAO {

    public HorarioFallidoDAOH() {
        super();
        setClazz(HorarioFallido.class);
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append("  delete from ").append(HorarioFallido.class.getName()).append(" hf ");
        sql.append("  where hf.cicloAcademico.id = :CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

}
