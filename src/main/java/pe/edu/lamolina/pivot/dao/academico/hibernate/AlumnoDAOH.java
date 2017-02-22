package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.hibernate.LockOptions;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Alumno findLock(Long id) {
        return (Alumno) getCurrentSession().load(Alumno.class, id, LockOptions.UPGRADE);
    }
}
