package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoReplicaNivelacionDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.nivelacioneegg.CursoReplicaNivelacion;

@Repository
public class CursoReplicaNivelacionDAOH extends AbstractEasyDAO<CursoReplicaNivelacion> implements CursoReplicaNivelacionDAO {

    public CursoReplicaNivelacionDAOH() {
        super();
        setClazz(CursoReplicaNivelacion.class);
    }

    @Override
    public List<CursoReplicaNivelacion> allByParents() {
        Octavia sql = Octavia.query()
                .from(CursoReplicaNivelacion.class, "crn")
                .join("cursoNivelacion cn", "cursoRegular cr");

        return all(sql);
    }

    @Override
    public List<CursoReplicaNivelacion> allByCursoNivelacion(Curso curso) {
        Octavia sql = Octavia.query()
                .from(CursoReplicaNivelacion.class, "crn")
                .join("cursoNivelacion cn", "cursoRegular cr")
                .filter("cn.id", curso);

        return all(sql);
    }

}
