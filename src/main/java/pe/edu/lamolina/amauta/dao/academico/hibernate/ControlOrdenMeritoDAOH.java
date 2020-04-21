package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.amauta.dao.academico.ControlOrdenMeritoDAO;

@Repository
public class ControlOrdenMeritoDAOH extends AbstractEasyDAO<ControlOrdenMerito> implements ControlOrdenMeritoDAO {

    public ControlOrdenMeritoDAOH() {
        super();
        setClazz(ControlOrdenMerito.class);
    }

    @Override
    public List<ControlOrdenMerito> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ControlOrdenMerito.class, "com")
                .join("cicloAcademico ca")
                .left("carrera car", "facultad fa")
                .filter("ca.id", cicloAcademico)
                .searchFields("car.nombre", "fa.codigo")
                .orderBy("com.id");

        return all(sql);
    }

    @Override
    public List<ControlOrdenMerito> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(ControlOrdenMerito.class, "com")
                .join("cicloAcademico ca")
                .left("carrera", "facultad")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public void deleteByCicloAcademico(CicloAcademico cicloAcademico) {
        Query query = getCurrentSession().createQuery("delete from ControlOrdenMerito where cicloAcademico.id = :CICLO");
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public ControlOrdenMerito findByFac(Facultad facultad, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(ControlOrdenMerito.class, "com")
                .join("cicloAcademico ca")
                .left("carrera", "facultad fac")
                .filter("ca.id", cicloAcademico)
                .filter("fac.id", facultad);

        return find(sql);
    }

}
