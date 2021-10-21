package pe.edu.lamolina.amauta.dao.contabilidad.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.contabilidad.JustificacionGastoAlumnoDAO;
import pe.edu.lamolina.model.contabilidad.ItemJustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGastoAlumno;

@Repository
public class JustificacionGastoAlumnoDAOH extends AbstractEasyDAO<JustificacionGastoAlumno> implements JustificacionGastoAlumnoDAO {

    public JustificacionGastoAlumnoDAOH() {
        super();
        setClazz(JustificacionGastoAlumno.class);
    }

    @Override
    public JustificacionGastoAlumno find(long id) {
        Octavia sql = Octavia.query()
                .from(JustificacionGastoAlumno.class, "jga")
                .join("itemJustificacionGasto ijg", "ijg.justificacionGasto jg")
                .join("alumno alu", "alu.persona per")
                .leftJoin("jg.viajeCurso vc", "per.tipoDocumento", "vc.alumnoDelegado")
                .filter("jga.id", id);

        return find(sql);
    }

    @Override
    public List<JustificacionGastoAlumno> allByItemJustificacion(ItemJustificacionGasto itemJustificacion) {
        Octavia sql = Octavia.query()
                .from(JustificacionGastoAlumno.class, "jga")
                .join("itemJustificacionGasto ijg", "ijg.justificacionGasto jg")
                .join("alumno alu", "alu.persona per")
                .leftJoin("jg.viajeCurso vc", "per.tipoDocumento", "vc.alumnoDelegado")
                .filter("ijg.id", itemJustificacion);

        return all(sql);
    }

    @Override
    public List<JustificacionGastoAlumno> allActivosByItemJustificacion(ItemJustificacionGasto itemJustificacion) {
        Octavia sql = Octavia.query()
                .from(JustificacionGastoAlumno.class, "jga")
                .join("itemJustificacionGasto ijg", "ijg.justificacionGasto jg")
                .join("alumno alu", "alu.persona per")
                .leftJoin("jg.viajeCurso vc", "per.tipoDocumento", "vc.alumnoDelegado")
                .filter("ijg.id", itemJustificacion)
                .isNull("jga.fechaAnulacion");

        return all(sql);
    }

    @Override
    public List<JustificacionGastoAlumno> allByJustificacion(JustificacionGasto justificacion) {
        Octavia sql = Octavia.query()
                .from(JustificacionGastoAlumno.class, "jga")
                .join("itemJustificacionGasto ijg", "ijg.justificacionGasto jg")
                .join("alumno alu", "alu.persona per")
                .leftJoin("jg.viajeCurso vc", "per.tipoDocumento", "vc.alumnoDelegado")
                .filter("jg.id", justificacion);

        return all(sql);
    }

}
