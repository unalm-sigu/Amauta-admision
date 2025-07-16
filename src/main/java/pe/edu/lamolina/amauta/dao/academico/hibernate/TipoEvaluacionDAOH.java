package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.amauta.dao.academico.TipoEvaluacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoEvaluacion;

@Repository
public class TipoEvaluacionDAOH extends AbstractEasyDAO<TipoEvaluacion> implements TipoEvaluacionDAO {
    
    public TipoEvaluacionDAOH() {
        super();
        setClazz(TipoEvaluacion.class);
    }
    
    public List<TipoEvaluacion> all() {
        Octavia sql = Octavia.query()
                .from(TipoEvaluacion.class, "te")
                .orderBy("te.orden");
        
        return all(sql);
    }

    @Override
    public List<TipoEvaluacion> allByDynaTable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TipoEvaluacion.class, "te")
                .searchFields("te.nombre", "te.codigo")
                .orderBy("te.id desc");
        return all(sql);
    }

    @Override
    public List<TipoEvaluacion> findByOrdenGreater(int orden) {
        Octavia sql = Octavia.query()
                .from(TipoEvaluacion.class, "te")
                .filter("te.orden",">=" ,orden);

        return all(sql);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(TipoEvaluacion.class, "te")
                .filter("te.codigo",codigo);

        return !all(sql).isEmpty();
    }

    @Override
    public boolean existsByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(TipoEvaluacion.class, "te")
                .filter("te.nombre" ,nombre);

        return !all(sql).isEmpty();
    }

    @Override
    public boolean existsByNombreAndIdNot(String nombre, Long idExcluir) {
        Octavia sql = Octavia.query()
                .from(TipoEvaluacion.class, "te")
                .filter("te.nombre", nombre)
                .filter("te.id", "!=", idExcluir);

        return !all(sql).isEmpty();
    }

    @Override
    public boolean existsByCodigoAndIdNot(String codigo, Long idExcluir) {
        Octavia sql = Octavia.query()
                .from(TipoEvaluacion.class, "te")
                .filter("te.codigo", codigo)
                .filter("te.id", "!=", idExcluir);

        return !all(sql).isEmpty();
    }
}
