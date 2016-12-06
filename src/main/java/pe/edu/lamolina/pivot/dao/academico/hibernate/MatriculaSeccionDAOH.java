package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Seccion;

@Repository
public class MatriculaSeccionDAOH extends AbstractDAO<MatriculaSeccion> implements MatriculaSeccionDAO {

    public MatriculaSeccionDAOH() {
        super();
        setClazz(MatriculaSeccion.class);
    }

    @Override
    public List<MatriculaSeccion> allByFilters(Seccion seccion) {
        SqlUtil sqlUtil = new SqlUtil("ms");
        sqlUtil.parents("matriculaResumen mr", "seccion s");
        sqlUtil.parents("_mr.alumno alu", "_s.grupoSeccion gs");
        sqlUtil.parents("_gs.curso cur", "_alu.persona per");
        sqlUtil.filter("s.id", seccion.getId());
        sqlUtil.orderBy("per.nombres");
        return this.all(sqlUtil);
    }

}
