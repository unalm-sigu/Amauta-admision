package pe.edu.lamolina.pivot.dao.vacante.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;

@Repository
public class VacanteAlumnoDAOH extends AbstractEasyDAO<VacanteAlumno> implements VacanteAlumnoDAO {

    public VacanteAlumnoDAOH() {
        super();
        setClazz(VacanteAlumno.class);
    }

    @Override
    public List<VacanteAlumno> allBySeccion(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(VacanteAlumno.class, "va")
                .join("seccion se", "alumno alu")
                .in("se.id", secciones);
        return sql.all(getCurrentSession());
    }
}
