package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoCuotaMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoCuotaMatriculaDAO;

@Repository
public class AlumnoCuotaMatriculaDAOH extends AbstractEasyDAO<AlumnoCuotaMatricula> implements AlumnoCuotaMatriculaDAO {

    public AlumnoCuotaMatriculaDAOH() {
        super();
        setClazz(AlumnoCuotaMatricula.class);
    }

    @Override
    public List<AlumnoCuotaMatricula> allAlumnoResumenCuotas(AlumnoResumenCuotas alumnoResumenCuotas) {
        Octavia sql = Octavia.query()
                .from(AlumnoCuotaMatricula.class, "acm")
                .join("alumnoResumenCuotas arc")
                .filter("arc.id", alumnoResumenCuotas);
        return all(sql);
    }

}
