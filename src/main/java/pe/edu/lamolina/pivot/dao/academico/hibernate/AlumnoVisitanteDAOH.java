package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.seguridad.Usuario;
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
                .join("persona per", "per.tipoDocumento tdoc")
                .join("paisUniversidad pu", "cicloEstudia ci")
                .leftJoin("paisUniversidad pa")
                .leftJoin("universidad uni")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .orderBy("av.id desc");

        return all(sql);
    }

}
