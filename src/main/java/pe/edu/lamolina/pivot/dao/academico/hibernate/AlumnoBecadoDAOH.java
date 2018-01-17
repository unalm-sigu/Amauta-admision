package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.AlumnoBecado;
import pe.edu.lamolina.pivot.dao.academico.AlumnoBecadoDAO;

@Repository
public class AlumnoBecadoDAOH extends AbstractDAO<AlumnoBecado> implements AlumnoBecadoDAO {

    public AlumnoBecadoDAOH() {
        super();
        setClazz(AlumnoBecado.class);
    }

    @Override
    public AlumnoBecado findAlumnoBecado(AlumnoBecado alumnoBecado) {
        Octavia sql = Octavia.query()
                .from(AlumnoBecado.class, "ab")
                .join("universidad uni", "cicloBeca ciclo", "alumno alu", "paisDestino pd", "alu.persona per", "alu.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td")
                .filter("ab.id", alumnoBecado);
        return (AlumnoBecado) sql.find(getCurrentSession());
    }

    @Override
    public List<AlumnoBecado> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoBecado.class, "ab")
                .join("universidad uni", "cicloBeca ciclo", "alumno alu", "paisDestino pd", "alu.persona per", "alu.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td")
                .searchFields("ciclo.descripcion", "ab.monto", "ab.universidadDestino", "ab.facultadDestino")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ab.id desc");
        return sql.all(getCurrentSession());
    }

}
