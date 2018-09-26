package pe.edu.lamolina.pivot.dao.mensajeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.MensajeIntranet;
import pe.edu.lamolina.pivot.dao.mensajeria.MensajeIntranetDAO;

@Repository
public class MensajeIntranetDAOH extends AbstractEasyDAO<MensajeIntranet> implements MensajeIntranetDAO {

    public MensajeIntranetDAOH() {
        super();
        setClazz(MensajeIntranet.class);
    }

    @Override
    public List<MensajeIntranet> allByDynatble(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(MensajeIntranet.class, "mi")
                .join("grupoAlumno ga", "tipoMensajeIntranet ti", "cicloAcademico ca")
                .searchFields("mi.fechaInicio", "mi.fechaFin", "ga.nombre", "mi.contenido")
                .orderBy("mi.id desc");

        return all(sql);
    }

    @Override
    public MensajeIntranet find(MensajeIntranet mensajeriaForm) {
        Octavia sql = Octavia.query()
                .from(MensajeIntranet.class, "mi")
                .join("cicloAcademico", "grupoAlumno", "tipoMensajeIntranet")
                .filter("mi.id", mensajeriaForm);
        return (MensajeIntranet) sql.find(getCurrentSession());
    }
}
