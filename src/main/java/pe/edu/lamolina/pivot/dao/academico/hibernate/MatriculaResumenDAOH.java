package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;

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

    @Override
    public MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaCursoEnum estadoMatriculaCursoEnum) {
        SqlUtil sqlUtil = new SqlUtil("mr")
                .parents("alumno alu", "cicloAcademico ca");
        if (ciclo != null) {
            sqlUtil.filter("ca.id", ciclo);
        }
        if (alumno != null) {
            sqlUtil.filter("alu.id", alumno);
        }
        if (estadoMatriculaCursoEnum != null) {
            sqlUtil.filter("mr.estado", estadoMatriculaCursoEnum.name());
        }
        return find(sqlUtil);
    }

}
