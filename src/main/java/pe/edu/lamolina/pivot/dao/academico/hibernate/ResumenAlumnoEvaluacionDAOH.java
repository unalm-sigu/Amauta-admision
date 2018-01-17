package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ResumenAlumnoEvaluacion;
import pe.edu.lamolina.pivot.dao.academico.ResumenAlumnoEvaluacionDAO;

@Repository
public class ResumenAlumnoEvaluacionDAOH extends AbstractEasyDAO<ResumenAlumnoEvaluacion> implements ResumenAlumnoEvaluacionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResumenAlumnoEvaluacionDAOH() {
        super();
        setClazz(ResumenAlumnoEvaluacion.class);
    }

    @Override
    public List<ResumenAlumnoEvaluacion> allByAlumnoGrupoSeccion(Alumno alumno, GrupoSeccion gpoSeccion) {
        Octavia sql = Octavia.query()
                .from(ResumenAlumnoEvaluacion.class, "rae")
                .join("tipoEvaluacion", "grupoSeccion gs", "alumno a")
                .filter("a.id", alumno)
                .filter("gs.id", gpoSeccion);

        return all(sql);
    }

    @Override
    public List<ResumenAlumnoEvaluacion> allByGrupoSeccion(GrupoSeccion gpoSeccion) {
        Octavia sql = Octavia.query()
                .from(ResumenAlumnoEvaluacion.class, "rae")
                .join("tipoEvaluacion", "grupoSeccion gs", "alumno a")
                .filter("gs.id", gpoSeccion);

        return all(sql);
    }
}
