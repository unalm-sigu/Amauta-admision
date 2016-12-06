package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.SistemaNotasDAO;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class SistemaNotasDAOH extends AbstractDAO<SistemaNotas> implements SistemaNotasDAO {

    public SistemaNotasDAOH() {
        super();
        setClazz(SistemaNotas.class);
    }

    @Override
    public SistemaNotas find(Long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("sn")
                .parents("left notaLetra nl");
        sqlUtil.filter("sn.id", id);
        return find(sqlUtil);
    }
}
