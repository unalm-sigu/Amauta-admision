package pe.edu.lamolina.amauta.dao.mensajeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.MensajeSistemaDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.mensajeria.EstadoMensajeEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.social.MensajeSistema;

@Repository
public class MensajeSistemaDAOH extends AbstractEasyDAO<MensajeSistema> implements MensajeSistemaDAO {

    public MensajeSistemaDAOH() {
        super();
        setClazz(MensajeSistema.class);
    }

    @Override
    public MensajeSistema findByTablaInstancia(NombreTablasEnum tabla, Long instancia) {
        Octavia sql = Octavia.query()
                .from(MensajeSistema.class, "ms")
                .join("remitente rem", "destinatario dest", "asuntoMensaje am")
                .join("rem.persona", "dest.persona")
                .filter("am.nombreTabla", tabla)
                .filter("am.instanciaTabla", instancia);

        return find(sql);
    }

    @Override
    public List<MensajeSistema> allPendientesByDocente(Docente docente) {
        Octavia sql = Octavia.query()
                .from(MensajeSistema.class, "ms")
                .join("remitente rem", "destinatario dest", "rem.persona rper", "dest.persona dper")
                .join("asuntoMensaje")
                .leftJoin("rper.tipoDocumento", "dper.tipoDocumento")
                .join("dest.docente doc")
                .filter("doc.id", docente)
                .filter("estado", EstadoMensajeEnum.ENVIADO)
                .orderBy("ms.fechaRegistro DESC");

        return all(sql);
    }

    @Override
    public List<MensajeSistema> allPendientesByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(MensajeSistema.class, "ms")
                .join("remitente rem", "destinatario dest", "rem.persona rper", "dest.persona dper")
                .join("asuntoMensaje")
                .leftJoin("rper.tipoDocumento", "dper.tipoDocumento")
                .leftJoin("dest.alumno", "dest.docente")
                .isNull("dest.alumno")
                .isNull("dest.docente")
                .filter("dper.id", persona)
                .filter("estado", EstadoMensajeEnum.ENVIADO)
                .orderBy("ms.fechaRegistro DESC");

        return all(sql);
    }
}
