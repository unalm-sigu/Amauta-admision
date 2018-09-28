package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.pivot.dao.academico.ControlMeritoEgresadoDAO;

@Repository
public class ControlMeritoEgresadoDAOH extends AbstractEasyDAO<ControlMeritoEgresado> implements ControlMeritoEgresadoDAO {

    public ControlMeritoEgresadoDAOH() {
        super();
        setClazz(ControlMeritoEgresado.class);
    }

    @Override
    public List<ControlMeritoEgresado> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ControlMeritoEgresado.class, "com")
                .join("cicloAcademico ca")
                .left("carrera car", "facultad fa")
                .filter("ca.id", cicloAcademico)
                .searchFields("car.nombre", "fa.codigo")
                .orderBy("com.id");

        return all(sql);
    }

    @Override
    public List<ControlMeritoEgresado> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(ControlMeritoEgresado.class, "com")
                .join("cicloAcademico ca")
                .left("carrera", "facultad")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public void deleteByCicloAcademico(CicloAcademico cicloAcademico) {
        Query query = getCurrentSession().createQuery("delete from ControlMeritoEgresado where cicloAcademico.id = :CICLO");
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

}
