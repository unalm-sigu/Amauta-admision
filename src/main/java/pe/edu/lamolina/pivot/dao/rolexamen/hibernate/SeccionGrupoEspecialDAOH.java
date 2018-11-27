package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;

@Repository
public class SeccionGrupoEspecialDAOH extends AbstractEasyDAO<SeccionGrupoEspecial> implements SeccionGrupoEspecialDAO {

    public SeccionGrupoEspecialDAOH() {
        super();
        setClazz(SeccionGrupoEspecial.class);
    }

    @Override
    public List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SeccionGrupoEspecial.class, "sge")
                .join("rolExamenes re", "seccion sec", "userRegistro ur")
                .join("ur.persona per")
                .left("aula au")
                .searchFields("sec.codigo", "sec.codigo2");

        return all(sql);
    }

}
