package pe.edu.lamolina.amauta.dao.matricula.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaBloqueoAlumnoDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;
import pe.edu.lamolina.model.academico.SituacionAcademica;

@Repository
public class MatriculaBloqueoAlumnoDAOH extends AbstractEasyDAO<MatriculaBloqueoAlumno> implements MatriculaBloqueoAlumnoDAO {

    public MatriculaBloqueoAlumnoDAOH() {
        super();
        setClazz(MatriculaBloqueoAlumno.class);
    }

    @Override
    public List<MatriculaBloqueoAlumno> allDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(MatriculaBloqueoAlumno.class, "mba")
                .join("carrera ca", "situacionAcademica sa", "cicloAplica ci")
                .searchFields("ca.nombre", "sa.nombre", "sa.descripcion", "sa.codigo", "ci.codigo", "ci.descripcion")
                .orderBy("ca.nombre");
        return all(sql);
    }

    @Override
    public MatriculaBloqueoAlumno find(MatriculaBloqueoAlumno matriculaBloqueoAlumno) {
        Octavia sql = Octavia.query()
                .from(MatriculaBloqueoAlumno.class, "mba")
                .join("mba.situacionAcademica sa", "mba.carrera ca", "mba.cicloAplica ci")
                .filter("mba.id", matriculaBloqueoAlumno);
        return find(sql);
    }

    @Override
    public void updateColumns(MatriculaBloqueoAlumno matriculaBloqueoAlumno, String... columns) {
        Octavia octavia = Octavia.update(MatriculaBloqueoAlumno.class);
        for (String column : columns) {
            octavia.set(matriculaBloqueoAlumno, column);
        }
        this.update(octavia);
    }

    @Override
    public MatriculaBloqueoAlumno findByCicloCarreraSituacion(CicloAcademico cicloAplica, Carrera carrera, SituacionAcademica situacionAcademica) {
        Octavia sql = Octavia.query()
                .from(MatriculaBloqueoAlumno.class, "mba")
                .join("mba.situacionAcademica sa", "mba.carrera ca", "mba.cicloAplica ci")
                .filter("ci.id", cicloAplica)
                .filter("ca.id", carrera)
                .filter("sa.id", situacionAcademica);
        return find(sql);
    }

}
