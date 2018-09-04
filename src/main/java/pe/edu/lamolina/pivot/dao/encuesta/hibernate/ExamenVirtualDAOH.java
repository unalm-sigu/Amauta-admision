package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.ExamenVirtualEstadoEnum;
import static pe.edu.lamolina.model.enums.TipoExamenVirtualEnum.ENC;
import static pe.edu.lamolina.model.enums.TipoExamenVirtualEnum.ENC_CUR;
import static pe.edu.lamolina.model.enums.TipoExamenVirtualEnum.ENC_DOC;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;

@Repository
public class ExamenVirtualDAOH extends AbstractEasyDAO<ExamenVirtual> implements ExamenVirtualDAO {

    public ExamenVirtualDAOH() {
        super();
        setClazz(ExamenVirtual.class);
    }

    @Override
    public List<ExamenVirtual> allEncuestasByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ExamenVirtual.class, "ev")
                .join("tipoExamen tipo")
                .in("tipo.codigo", Arrays.asList(ENC_CUR, ENC_DOC))
                .searchFields("ev.nombre", "ev.estado", "ev.codigo")
                .orderBy("ev.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public ExamenVirtual findEncuestaUltimoCodigo() {
        Octavia sql = Octavia.query()
                .from(ExamenVirtual.class, "ev")
                .join("tipoExamen tipo")
                .in("tipo.codigo", Arrays.asList(ENC_CUR, ENC_DOC, ENC))
                .orderBy("SUBSTRING(ev.codigo,3) desc")
                .limit(1);
        return find(sql);
    }

    @Override
    public Long countRespuestas(ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(EncuestaPostulante.class, "ep")
                .join("ep.pregunta pr", "pr.examenVirtual ev")
                .filter("ev.id", encuesta);

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public Long countRespuestasByCiclo(ExamenVirtual encuesta, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(EncuestaPostulante.class, "ep")
                .join("ep.pregunta pr", "pr.examenVirtual ev", "postulante po", "po.cicloPostula cp")
                .filter("ev.id", encuesta)
                .filter("cp.id", ciclo);

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public ExamenVirtual findEncuestaActivaByTipo(TipoExamenVirtual tipoExamen) {
        Octavia sql = Octavia.query()
                .from(ExamenVirtual.class, "exv")
                .join("tipoExamen tipo")
                .filter("tipo.id", tipoExamen)
                .filter("exv.estado", ExamenVirtualEstadoEnum.ACT);
        return find(sql);
    }

    @Override
    public ExamenVirtual findExamenVirtual(ExamenVirtual examenVirtual) {
        Octavia sql = Octavia.query()
                .from(ExamenVirtual.class, "ev")
                .join("tipoExamen tipo")
                .in("tipo.codigo", Arrays.asList(ENC_CUR, ENC_DOC))
                .filter("ev.id", examenVirtual);
        return find(sql);
    }

}
