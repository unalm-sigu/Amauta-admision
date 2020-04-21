package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;

@Repository
public class TipoCursoCurriculaDAOH extends AbstractEasyDAO<TipoCursoCurricula> implements TipoCursoCurriculaDAO {

    public TipoCursoCurriculaDAOH() {
        super();
        setClazz(TipoCursoCurricula.class);
    }

    @Override
    public TipoCursoCurricula findByCodigo(TipoCursoCurriculaEnum tipoCursoCurriculaEnum) {
        Octavia sql = Octavia.query()
                .from(TipoCursoCurricula.class, "t")
                .filter("t.codigo", tipoCursoCurriculaEnum);
        return find(sql);
    }

    @Override
    public List<TipoCursoCurricula> allByCodigos(List<String> list) {
        Octavia sql = Octavia.query()
                .from(TipoCursoCurricula.class, "t")
                .in("t.codigo", list);
        return all(sql);
    }
}
