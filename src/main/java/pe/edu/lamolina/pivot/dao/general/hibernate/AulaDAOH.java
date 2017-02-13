package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.model.general.Aula;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class AulaDAOH extends AbstractDAO<Aula> implements AulaDAO {

    public AulaDAOH() {
        super();
        setClazz(Aula.class);
    }

    @Override
    public Aula findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("au")
                .filter("au.codigo", codigo);
        return find(sqlUtil);
    }
}
