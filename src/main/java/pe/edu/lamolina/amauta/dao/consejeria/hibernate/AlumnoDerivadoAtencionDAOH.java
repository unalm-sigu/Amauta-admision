package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import static java.util.Locale.filter;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoDerivadoAtencionDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;

@Repository
public class AlumnoDerivadoAtencionDAOH extends AbstractEasyDAO<AlumnoDerivadoAtencion> implements AlumnoDerivadoAtencionDAO {

    public AlumnoDerivadoAtencionDAOH() {
        super();
        setClazz(AlumnoDerivadoAtencion.class);
    }

    @Override
    public List<AlumnoDerivadoAtencion> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoDerivadoAtencion.class, "ada")
                .join("alumno alu", "cicloAcademico ci", "tipoAtencionTutorado", "personaRemitente perr", "tipoRemitenteDerivacion")
                .leftJoin("especialidadMedica esp", "curso cur")
                .leftJoin("consejero con", "con.colaborador colc", "colc.persona percn", "percn.tipoDocumento")
                .leftJoin("medico med", "med.colaborador colm", "colm.persona")
                .leftJoin("colaborador col", "col.persona perco", "perco.tipoDocumento")
                .leftJoin("col.oficina ofi", "ofi.oficinaPrincipal", "col.cargo")
                .searchFields("ada.motivoDerivacion", "perr.numeroDocIdentidad", "esp.nombre", "cur.codigo", "cur.nombre")
                .filter("alu.id", alumno)
                .filter("ci.id", ciclo)
                .orderBy("ada.id desc");

        return all(sql);
    }

    @Override
    public List<AlumnoDerivadoAtencion> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoDerivadoAtencion.class, "ada")
                .join("alumno alu", "cicloAcademico ci", "tipoAtencionTutorado", "personaRemitente perr", "tipoRemitenteDerivacion")
                .leftJoin("especialidadMedica esp", "curso cur")
                .leftJoin("consejero con", "con.colaborador colc", "colc.persona percn", "percn.tipoDocumento")
                .leftJoin("medico med", "med.colaborador colm", "colm.persona")
                .leftJoin("colaborador col", "col.persona perco", "perco.tipoDocumento")
                .leftJoin("col.oficina ofi", "ofi.oficinaPrincipal", "col.cargo")
                .in("alu.id", alumnos)
                .filter("ci.id", ciclo);

        return all(sql);
    }

}
