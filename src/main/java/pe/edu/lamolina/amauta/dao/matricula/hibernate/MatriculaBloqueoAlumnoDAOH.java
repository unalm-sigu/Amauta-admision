package pe.edu.lamolina.amauta.dao.matricula.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaBloqueoAlumnoDAO;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;

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

    public MatriculaBloqueoAlumno find(Long idMatriculaBloqueoAlumno) {
        Octavia sql = Octavia.query(MatriculaBloqueoAlumno.class, "mba")
                .join("carrera ca", "situacionAcademica sa", "cicloAplica ci")
                .filter("mba.id", idMatriculaBloqueoAlumno);
        return find(sql);
    }

}
