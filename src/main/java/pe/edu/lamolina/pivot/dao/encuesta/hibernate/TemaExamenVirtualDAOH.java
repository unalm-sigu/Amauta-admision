package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TemaExamenVirtualDAO;
import pe.edu.lamolina.model.enums.EstadoTemaEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

@Repository
public class TemaExamenVirtualDAOH extends AbstractEasyDAO<TemaExamenVirtual> implements TemaExamenVirtualDAO {

    public TemaExamenVirtualDAOH() {
        super();
        setClazz(TemaExamenVirtual.class);
    }

    @Override
    public List<TemaExamenVirtual> allByEvaluacion(ExamenVirtual evaluacion) {
        Octavia sql = Octavia.query()
                .from(TemaExamenVirtual.class, "tema")
                .join("examenVirtual ev")
                .filter("ev.id", evaluacion)
                .orderBy("tema.orden", "tema.estado");

        return all(sql);
    }

    @Override
    public TemaExamenVirtual findByEvaluacionOrden(TemaExamenVirtual tema, Integer orden) {
        Octavia sql = Octavia.query()
                .from(TemaExamenVirtual.class, "tema")
                .join("examenVirtual ev")
                .filter("ev.id", tema.getExamenVirtual()).
                filter("tema.orden", orden);

        return find(sql);
    }

    @Override
    public List<TemaExamenVirtual> allActivoByEvaluacion(ExamenVirtual evaluacionVirtual) {
        Octavia sql = Octavia.query()
                .from(TemaExamenVirtual.class, "tema")
                .join("examenVirtual ev")
                .filter("ev.id", evaluacionVirtual)
                .filter("tema.estado", EstadoTemaEnum.ACT).
                orderBy("tema.orden");

        return all(sql);
    }

    @Override
    public List<TemaExamenVirtual> allInactivoByEvaluacion(ExamenVirtual evaluacionVirtual) {
        Octavia sql = Octavia.query()
                .from(TemaExamenVirtual.class, "tema")
                .join("examenVirtual ev")
                .filter("ev.id", evaluacionVirtual)
                .filter("estado", EstadoTemaEnum.INA)
                .orderBy("tema.orden");

        return all(sql);
    }

    @Override
    public TemaExamenVirtual findLastInactivo(ExamenVirtual evaluacionVirtual) {
        Octavia sql = Octavia.query()
                .from(TemaExamenVirtual.class, "tema")
                .join("examenVirtual ev")
                .filter("ev.id", evaluacionVirtual)
                .filter("estado", EstadoTemaEnum.INA)
                .orderBy("tema.orden DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public TemaExamenVirtual findLastActivo(ExamenVirtual evaluacionVirtual) {
        Octavia sql = Octavia.query()
                .from(TemaExamenVirtual.class, "tema")
                .join("examenVirtual ev")
                .filter("ev.id", evaluacionVirtual)
                .filter("estado", EstadoTemaEnum.ACT)
                .orderBy("tema.orden DESC")
                .limit(1);

        return find(sql);

    }

}
