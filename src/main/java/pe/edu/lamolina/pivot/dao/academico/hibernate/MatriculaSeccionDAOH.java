package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;
import static pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum.MAT;

@Repository
public class MatriculaSeccionDAOH extends AbstractDAO<MatriculaSeccion> implements MatriculaSeccionDAO {

    public MatriculaSeccionDAOH() {
        super();
        setClazz(MatriculaSeccion.class);
    }

    @Override
    public List<MatriculaSeccion> allBySeccion(Seccion seccion) {
        SqlUtil sqlUtil = new SqlUtil("ms")
                .parents("matriculaResumen mr", "seccion s")
                .parents("_mr.alumno alu", "_s.grupoSeccion gs")
                .parents("_gs.curso cur", "_alu.persona per")
                .filter("ms.estado", EstadoMatriculaCursoEnum.MAT.name())
                .filter("s.id", seccion)
                .orderBy("per.paterno", "per.materno", "per.nombres");
        return this.all(sqlUtil);
    }

    @Override
    public MatriculaSeccion find(Long id) {
        SqlUtil sqlUtil = new SqlUtil("ms")
                .parents("matriculaResumen mr", "seccion s")
                .parents("_mr.alumno alu", "_s.grupoSeccion gs")
                .parents("_gs.curso cur", "_alu.persona per")
                .filter("ms.id", id);
        return this.find(sqlUtil);
    }

    @Override
    public MatriculaSeccion findByAlumnoSeccion(Alumno alumno, Seccion seccion) {
        SqlUtil sqlUtil = new SqlUtil("ms")
                .parents("matriculaResumen mr", "seccion s")
                .parents("_mr.alumno alu", "_s.grupoSeccion gs")
                .parents("_gs.curso cur", "_alu.persona per")
                .filter("s.id", seccion)
                .filter("alu.id", alumno);
        return this.find(sqlUtil);
    }

    @Override
    public List<MatriculaSeccion> allByMatriculaSeccion(MatriculaResumen resumen) {
        SqlUtil sqlUtil = new SqlUtil("ms")
                .parents("matriculaResumen mr", "seccion s")
                .parents("_mr.alumno alu", "_s.grupoSeccion gs")
                .parents("_gs.curso cur", "_alu.persona per")
                .filter("mr.id", resumen);
        return all(sqlUtil);
    }

    @Override
    public List<MatriculaSeccion> allByGpoSeccion(GrupoSeccion grupoSeccion, CicloAcademico ciclo) {
        SqlUtil sqlUtil = new SqlUtil("ms")
                .parents("matriculaResumen mr", "seccion s")
                .parents("_mr.alumno alu", "_s.grupoSeccion gs")
                .parents("_gs.curso cur", "_gs.cicloAcademico ca", "_alu.persona per")
                .parents("left _gs.planCalificacion")
                .filter("ms.estado", MAT.name())
                .filter("gs.id", grupoSeccion)
                .filter("ca.id", ciclo);
        return all(sqlUtil);
    }

    @Override
    public List<MatriculaSeccion> allByCiclo(CicloAcademico ciclo) {
        SqlUtil sqlUtil = new SqlUtil("ms")
                .parents("matriculaResumen mr", "seccion s")
                .parents("_mr.alumno alu", "_s.grupoSeccion gs")
                .parents("_gs.curso cur", "_gs.cicloAcademico ca", "_alu.persona per")
                .parents("left _gs.planCalificacion")
                .filter("ms.estado", MAT.name())
                .filter("ca.id", ciclo);
        return all(sqlUtil);
    }

}
