package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;

@Repository
public class MatriculaResumenDAOH extends AbstractEasyDAO<MatriculaResumen> implements MatriculaResumenDAO {

    public MatriculaResumenDAOH() {
        super();
        setClazz(MatriculaResumen.class);
    }

    @Override
    public MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .filter("alu.id", alumno)
                .filter("ca.id", ciclo);

        return find(sql);
    }

    @Override
    public List<MatriculaResumen> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaEnum estadoMatriculaCursoEnum) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alu", "cicloAcademico ca");

        if (ciclo != null) {
            sql.filter("ca.id", ciclo);
        }
        if (alumno != null) {
            sql.filter("alu.id", alumno);
        }
        if (estadoMatriculaCursoEnum != null) {
            sql.filter("mr.estado", estadoMatriculaCursoEnum);
        }

        return find(sql);
    }

}
