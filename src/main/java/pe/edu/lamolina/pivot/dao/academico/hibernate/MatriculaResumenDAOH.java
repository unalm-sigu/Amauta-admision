package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

@Repository
public class MatriculaResumenDAOH extends AbstractDAO<MatriculaResumen> implements MatriculaResumenDAO {

    public MatriculaResumenDAOH() {
        super();
        setClazz(MatriculaResumen.class);
    }

    public List<MatriculaResumen> allBy() {
        SqlUtil sqlUtil = new SqlUtil("mr");
        sqlUtil.parents("alumno alu", "matriculaSeccion ms");
        return null;
    }



}
