package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.encuesta.SubTituloExamenDAO;
import pe.edu.lamolina.model.enums.EstadoSubTituloEnum;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

@Repository
public class SubTituloExamenDAOH extends AbstractEasyDAO<SubTituloExamen> implements SubTituloExamenDAO {

    public SubTituloExamenDAOH() {
        super();
        setClazz(SubTituloExamen.class);
    }

    @Override
    public List<SubTituloExamen> allByTemas(List<TemaExamenVirtual> temas) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "subti")
                .join("temaExamen tema")
                .in("tema.id", temas)
                .orderBy("subti.orden");

        return all(sql);
    }

    @Override
    public SubTituloExamen findByTemaOrden(SubTituloExamen subtitulo, Integer orden) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "subti")
                .join("temaExamen tema")
                .filter("tema.id", subtitulo.getTemaExamen()).
                filter("subti.orden", orden);

        return find(sql);
    }

    @Override
    public List<SubTituloExamen> allByTema(TemaExamenVirtual tema) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "subti")
                .join("temaExamen tema")
                .filter("tema.id", tema).
                orderBy("subti.orden");

        return all(sql);
    }

    @Override
    public SubTituloExamen findSubTituloEvaluacionVirtual(Long instancia) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "subti")
                .join("temaExamen tema")
                .filter("subti.id", instancia);

        return find(sql);
    }

    @Override
    public SubTituloExamen findLastActivo(TemaExamenVirtual temaEvaluacionVirtual) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "subti")
                .join("temaExamen tema")
                .filter("estado", EstadoSubTituloEnum.ACT)
                .orderBy("orden DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public SubTituloExamen findLastInactivo(TemaExamenVirtual temaEvaluacionVirtual) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "subti")
                .join("temaExamen tema")
                .filter("tema.id", temaEvaluacionVirtual)
                .filter("estado", EstadoSubTituloEnum.INA)
                .orderBy("orden DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<SubTituloExamen> allActivoByTema(TemaExamenVirtual temaEvaluacionVirtual) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "subti")
                .join("temaExamen tema")
                .filter("tema.id", temaEvaluacionVirtual)
                .filter("estado", EstadoSubTituloEnum.ACT)
                .orderBy("tema.orden");

        return all(sql);
    }

}
