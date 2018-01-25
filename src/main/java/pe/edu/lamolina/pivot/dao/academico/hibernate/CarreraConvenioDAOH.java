package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CarreraConvenio;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.pivot.dao.academico.CarreraConvenioDAO;

@Repository
public class CarreraConvenioDAOH extends AbstractEasyDAO<CarreraConvenio> implements CarreraConvenioDAO {

    public CarreraConvenioDAOH() {
        super();
        setClazz(CarreraConvenio.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void deleteByConvenioBeca(ConvenioBeca convenioBeca) {
        StringBuilder strb = new StringBuilder();
        strb.append("delete CarreraConvenio ds   where ds.convenioBeca.id=:CONVENIO ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setLong("CONVENIO", convenioBeca.getId());
        query.executeUpdate();
    }

    @Override
    public List<CarreraConvenio> allByCarreraConvenio(List<ConvenioBeca> convenios) {
        Octavia sql = Octavia.query()
                .from(CarreraConvenio.class, "alu")
                .join("convenioBeca cb", "carrera car")
                .in("cb.id", convenios);
        return all(sql);
    }

    @Override
    public List<CarreraConvenio> allByConvenioBeca(ConvenioBeca convenioBeca) {
        Octavia sql = Octavia.query()
                .from(CarreraConvenio.class, "alu")
                .join("convenioBeca cb", "carrera car")
                .filter("cb.id", convenioBeca);
        return all(sql);
    }

}
