package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TemaLeccion;
import pe.edu.lamolina.pivot.dao.academico.TemaLeccionDAO;

@Repository
public class TemaLeccionDAOH extends AbstractEasyDAO<TemaLeccion> implements TemaLeccionDAO {

    public TemaLeccionDAOH() {
        super();
        setClazz(TemaLeccion.class);
    }

    @Override
    public List<TemaLeccion> allBySeccionOrder(Seccion seccion, String orderBy) {
        Octavia sql = Octavia.query()
                .from(TemaLeccion.class, "t")
                .join("seccion sec", "docente doc")
                .filter("sec.id", seccion)
                .orderBy(orderBy);
        return this.all(sql);
    }

    @Override
    public List<TemaLeccion> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(TemaLeccion.class, "t")
                .join("seccion sec", "docente doc")
                .filter("sec.id", seccion);
        return this.all(sql);
    }

    @Override
    public List<TemaLeccion> allBySeccionDocenteFecha(Seccion seccion, Docente docente, Date fecha) {
        Octavia sql = Octavia.query()
                .from(TemaLeccion.class, "t")
                .join("seccion sec", "docente doc")
                .filter("t.fecha", fecha)
                .filter("sec.id", seccion)
                .filter("doc.id", docente);
        return this.all(sql);
    }

    @Override
    public List<TemaLeccion> allBySeccionDocenteDyna(Seccion seccion, Docente docente, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TemaLeccion.class, "t")
                .join("seccion sec", "docente doc")
                .filter("sec.id", seccion)
                .filter("doc.id", docente);
        return this.all(sql);
    }

    @Override
    public TemaLeccion findBySeccionDocenteFecha(Seccion seccion, Docente docente, Date fecha) {
        Octavia sql = Octavia.query()
                .from(TemaLeccion.class, "t")
                .join("seccion sec", "docente doc")
                .filter("t.fecha", fecha)
                .filter("sec.id", seccion)
                .filter("doc.id", docente);

        return find(sql);
    }

    @Override
    public TemaLeccion find(Long idTemaLeccion) {
        Octavia sql = Octavia.query()
                .from(TemaLeccion.class, "t")
                .join("seccion sec", "docente doc")
                .filter("t.id", idTemaLeccion);
        return find(sql);
    }

    @Override
    public void updateTema(TemaLeccion temaLeccion) {
        Octavia octavia = Octavia.update(TemaLeccion.class);
        octavia.set(temaLeccion, "id");
        octavia.set(temaLeccion, "tema");
        this.update(octavia);
    }

}
