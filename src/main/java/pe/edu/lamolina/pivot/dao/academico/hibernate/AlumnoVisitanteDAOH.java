package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.dao.academico.AlumnoVisitanteDAO;

@Repository
public class AlumnoVisitanteDAOH extends AbstractEasyDAO<AlumnoVisitante> implements AlumnoVisitanteDAO {

    public AlumnoVisitanteDAOH() {
        super();
        setClazz(AlumnoVisitante.class);
    }

    @Override
    public AlumnoVisitante findByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(AlumnoVisitante.class, "av")
                .join("persona per", "userRegistro us", "cicloEstudia ci")
                .leftJoin("paisUniversidad pa")
                .filter("per.id", persona);

        return find(sql);
    }

    @Override
    public AlumnoVisitante findByPersona(Persona persona, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoVisitante.class, "av")
                .join("persona per", "userRegistro us", "cicloEstudia ci")
                .leftJoin("paisUniversidad pa")
                .filter("per.id", persona)
                .filter("ci.id", cicloAcademico);

        return find(sql);
    }

    @Override
    public List<AlumnoVisitante> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoVisitante.class, "av")
                .join("persona per", "paisUniversidad pu", "cicloEstudia ci")
                .leftJoin("per.tipoDocumento td", "av.universidad uni")
                .searchFields("td.simbolo", "per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("per.id desc");
        return all(sql);
    }

    @Override
    public AlumnoVisitante findAlumnoVisitante(AlumnoVisitante alumnoVisitante) {
        Octavia sql = Octavia.query()
                .from(AlumnoVisitante.class, "av")
                .join("persona per", "cicloEstudia ci")
                .leftJoin("paisUniversidad pa", "userRegistro us")
                .filter("av.id", alumnoVisitante);
        return find(sql);
    }
}
