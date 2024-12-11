package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.TemaAsistenciaDAO;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Repository
public class TemaAsistenciaDAOH extends AbstractEasyDAO<TemaAsistencia> implements TemaAsistenciaDAO {

    public TemaAsistenciaDAOH() {
        super();
        setClazz(TemaAsistencia.class);
    }

    @Override
    public TemaAsistencia find(long id) {
        Octavia sql = Octavia.query()
                .from(TemaAsistencia.class, "ta")
                .join("cursoNivelacion cn")
                .join("cn.docente doc", "cn.cursoCiclo cuci")
                .join("cuci.curso cu", "cuci.cicloAcademico ci")
                .leftJoin("cn.aula", "doc.persona per")
                .filter("ta.id", id);

        return find(sql);
    }

    @Override
    public List<TemaAsistencia> allSeccionByDynatable(DynatableFilter filter, CursoNivelacion seccion) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TemaAsistencia.class, "ta")
                .join("cursoNivelacion cn")
                .filter("cn.id", seccion)
                .searchFields("ta.fecha", "ta.temaClase")
                .orderBy("ta.fecha DESC");

        return all(sql);
    }

    @Override
    public List<TemaAsistencia> allByCursoNivelacion(CursoNivelacion seccion) {
        Octavia sql = Octavia.query()
                .from(TemaAsistencia.class, "ta")
                .join("cursoNivelacion cn")
                .filter("cn.id", seccion)
                .orderBy("ta.fecha DESC");

        return all(sql);
    }

    @Override
    public List<TemaAsistencia> allByCursosNivelaciones(List<CursoNivelacion> secciones) {
        Octavia sql = Octavia.query()
                .from(TemaAsistencia.class, "ta")
                .join("cursoNivelacion cn")
                .in("cn.id", secciones)
                .orderBy("ta.fecha");

        return all(sql);
    }

    @Override
    public TemaAsistencia findByCursoNivelacionFecha(CursoNivelacion seccion, Date fecha) {
        Octavia sql = Octavia.query()
                .from(TemaAsistencia.class, "ta")
                .join("cursoNivelacion cn")
                .filter("cn.id", seccion)
                .filter("ta.fecha", fecha);

        return find(sql);
    }

}
