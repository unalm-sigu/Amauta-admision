package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class AlumnoDAOH extends AbstractDAO<Alumno> implements AlumnoDAO {

    public AlumnoDAOH() {
        super();
        setClazz(Alumno.class);
    }

    @Override
    public Alumno findByCodigo(String codigoAlumno) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("alu")
                .parents("persona")
                .filter("alu.codigo", codigoAlumno);
        return find(sqlUtil);
    }
}
