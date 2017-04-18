package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.model.academico.SituacionAcademica;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class SituacionAcademicaDAOH extends AbstractDAO<SituacionAcademica> implements SituacionAcademicaDAO {

    public SituacionAcademicaDAOH() {
        super();
        setClazz(SituacionAcademica.class);
    }

    @Override
    public SituacionAcademica findByCodigo(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("sa")
                .filter("sa.codigo", codigo);
        return this.find(sqlUtil);
    }
}
