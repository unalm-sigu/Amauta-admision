package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TopeMatricula;
import pe.edu.lamolina.amauta.dao.academico.TopeMatriculaDAO;

@Repository
public class TopeMatriculaDAOH extends AbstractEasyDAO<TopeMatricula> implements TopeMatriculaDAO {

    public TopeMatriculaDAOH() {
        super();
        setClazz(TopeMatricula.class);
    }

    @Override
    public List<TopeMatricula> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TopeMatricula.class, "tm")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .searchFields("ca.descripcion")
                .orderBy("tm.id desc");
        return all(sql);
    }

}
