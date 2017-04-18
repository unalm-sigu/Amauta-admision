package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class CarreraDAOH extends AbstractDAO<Carrera> implements CarreraDAO {

    public CarreraDAOH() {
        super();
        setClazz(Carrera.class);
    }

    @Override
    public Carrera findByCodigo(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca")
                .filter("ca.codigo", codigo);
        return this.find(sqlUtil);
    }
}
