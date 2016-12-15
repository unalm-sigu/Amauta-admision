package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;

@Repository
public class GrupoSeccionDAOH extends AbstractDAO<GrupoSeccion> implements GrupoSeccionDAO {

    public GrupoSeccionDAOH() {
        super();
        setClazz(GrupoSeccion.class);
    }

    @Override
    public GrupoSeccion find(Long idGrupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "planCalificacion pc", "curso cur")
                .filter("gp.id", idGrupoSeccion);
        return find(sqlUtil);
    }

}
