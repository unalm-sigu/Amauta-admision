package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.dao.academico.ResumenAlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.ResumenAlumnoEvaluacion;

@Repository
public class ResumenAlumnoEvaluacionDAOH extends AbstractDAO<ResumenAlumnoEvaluacion> implements ResumenAlumnoEvaluacionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResumenAlumnoEvaluacionDAOH() {
        super();
        setClazz(ResumenAlumnoEvaluacion.class);
    }

    @Override
    public List<ResumenAlumnoEvaluacion> allByAlumnoGrupoSeccion(Alumno alumno, GrupoSeccion gpoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("rae")
                .parents("tipoEvaluacion", "grupoSeccion gs", "alumno a")
                .filter("a.id", alumno)
                .filter("gs.id", gpoSeccion);
        return all(sqlUtil);
    }

    @Override
    public List<ResumenAlumnoEvaluacion> allByGrupoSeccion(GrupoSeccion gpoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("rae")
                .parents("tipoEvaluacion", "grupoSeccion gs", "alumno a")
                .filter("gs.id", gpoSeccion);
        return all(sqlUtil);
    }
}
