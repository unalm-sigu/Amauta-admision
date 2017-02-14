package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

@Repository
public class MatriculaResumenDAOH extends AbstractDAO<MatriculaResumen> implements MatriculaResumenDAO {

    public MatriculaResumenDAOH() {
        super();
        setClazz(MatriculaResumen.class);
    }

    @Override
    public MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        SqlUtil sqlUtil = new SqlUtil("mr")
                .parents("alumno alu", "cicloAcademico ca")
                .filter("alu.id", alumno)
                .filter("ca.id", ciclo);
        return find(sqlUtil);
    }

    @Override
    public List<MatriculaResumen> allByCiclo(CicloAcademico ciclo) {
        SqlUtil sqlUtil = new SqlUtil("mr")
                .parents("alumno alu", "cicloAcademico ca")
                .filter("ca.id", ciclo);
        return all(sqlUtil);
    }

}
