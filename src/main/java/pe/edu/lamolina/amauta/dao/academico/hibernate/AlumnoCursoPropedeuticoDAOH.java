package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoPropedeuticoDAO;
import pe.edu.lamolina.model.academico.AlumnoCursoPropedeutico;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;

@Repository
public class AlumnoCursoPropedeuticoDAOH extends AbstractEasyDAO<AlumnoCursoPropedeutico> implements AlumnoCursoPropedeuticoDAO {

    public AlumnoCursoPropedeuticoDAOH() {
        super();
        setClazz(AlumnoCursoPropedeutico.class);
    }

    @Override
    public List<AlumnoCursoPropedeutico> allBySeccion(Seccion seccion) {
        Octavia sql = new Octavia()
                .from(AlumnoCursoPropedeutico.class, "acp")
                .join("seccion sec", "matriculaResumen mr")
                .join("mr.alumno al", "al.persona")
                .filter("sec.id", seccion);

        return all(sql);
    }

    @Override
    public List<AlumnoCursoPropedeutico> allBySeccionDynatable(DynatableFilter filter, CicloAcademico academico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoCursoPropedeutico.class, "acp")
                .join("seccion sec", "matriculaResumen mr", "sec.grupoSeccion gs")
                .join("gs.cicloAcademico aca", "mr.alumno al", "al.persona per", "per.tipoDocumento")
                .filter("aca.id", academico);

        return all(sql);
    }

    @Override
    public AlumnoCursoPropedeutico findAll(Long id) {
        Octavia sql = new Octavia()
                .from(AlumnoCursoPropedeutico.class, "acp")
                .join("seccion sec", "matriculaResumen mr")
                .join("mr.alumno al", "al.persona")
                .filter("acp.id", id);

        return find(sql);
    }

}
