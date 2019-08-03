package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.posgrado.CursoHabilEscuela;
import pe.edu.lamolina.model.enums.CursoHabilEstadoEnum;
import pe.edu.lamolina.pivot.dao.posgrado.CursoHabilEscuelaDAO;

@Repository
public class CursoHabilEscuelaDAOH extends AbstractEasyDAO<CursoHabilEscuela> implements CursoHabilEscuelaDAO {

    public CursoHabilEscuelaDAOH() {
        super();
        setClazz(CursoHabilEscuela.class);
    }

    @Override
    public List<CursoHabilEscuela> allAlumnos(List<Alumno> alumnos) {
        Octavia sql = Octavia.query()
                .from(CursoHabilEscuela.class, "che")
                .join("alumno al", "curso cu")
                .in("al.id", alumnos)
                .in("estado", Arrays.asList(CursoHabilEstadoEnum.HAB));
        return sql.all(getCurrentSession());
    }
}
