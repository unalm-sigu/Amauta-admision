package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ExamenCursoNivelacionDAO;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenCursoNivelacion;

@Repository
public class ExamenCursoNivelacionDAOH extends AbstractEasyDAO<ExamenCursoNivelacion> implements ExamenCursoNivelacionDAO {

    public ExamenCursoNivelacionDAOH() {
        super();
        setClazz(ExamenCursoNivelacion.class);
    }

    @Override
    public ExamenCursoNivelacion find(long id) {
        Octavia sql = Octavia.query()
                .from(ExamenCursoNivelacion.class, "excn")
                .join("cursoNivelacion cn", "tipoExamenNivelacion ten")
                .filter("excn.id", id);

        return find(sql);
    }

    @Override
    public List<ExamenCursoNivelacion> allByCursoNivelacion(CursoNivelacion cursoNiv) {
        Octavia sql = Octavia.query()
                .from(ExamenCursoNivelacion.class, "excn")
                .join("cursoNivelacion cn", "tipoExamenNivelacion ten")
                .filter("cn.id", cursoNiv);

        return all(sql);
    }

    @Override
    public List<ExamenCursoNivelacion> allByCursosNivelaciones(List<CursoNivelacion> cursosNiv) {
        Octavia sql = Octavia.query()
                .from(ExamenCursoNivelacion.class, "excn")
                .join("cursoNivelacion cn", "tipoExamenNivelacion ten")
                .in("cn.id", cursosNiv);

        return all(sql);
    }

}
