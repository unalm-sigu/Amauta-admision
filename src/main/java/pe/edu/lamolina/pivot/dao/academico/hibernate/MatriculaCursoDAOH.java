package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;

@Repository
public class MatriculaCursoDAOH extends AbstractDAO<MatriculaCurso> implements MatriculaCursoDAO {

    public MatriculaCursoDAOH() {
        super();
        setClazz(MatriculaCurso.class);
    }

    @Override
    public MatriculaCurso findByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mc")
                .parents("matriculaResumen mr", "_mr.alumno alu", "_mr.cicloAcademico ca", "curso cu")
                .filter("ca.id", ciclo)
                .filter("alu.id", alumno)
                .filter("cu.id", curso);
        return find(sqlUtil);
    }

    @Override
    public List<MatriculaCurso> findByCursoCiclo(Curso curso, CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mc")
                .parents("matriculaResumen mr", "_mr.alumno alu", "_mr.cicloAcademico ca", "curso cu")
                .filter("ca.id", ciclo)
                .filter("cu.id", curso);
        return all(sqlUtil);
    }

    @Override
    public List<MatriculaCurso> allByMatriculaResumen(MatriculaResumen resumen) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mc")
                .parents("matriculaResumen mr", "_mr.alumno alu", "_mr.cicloAcademico ca", "curso cu")
                .filter("mr.id", resumen);
        return all(sqlUtil);
    }
}
