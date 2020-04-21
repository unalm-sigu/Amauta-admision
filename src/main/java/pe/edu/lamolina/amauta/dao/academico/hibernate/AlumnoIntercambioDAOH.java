package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.AlumnoIntercambio;
import pe.edu.lamolina.amauta.dao.academico.AlumnoIntercambioDAO;

@Repository
public class AlumnoIntercambioDAOH extends AbstractDAO<AlumnoIntercambio> implements AlumnoIntercambioDAO {

    public AlumnoIntercambioDAOH() {
        super();
        setClazz(AlumnoIntercambio.class);
    }

    @Override
    public AlumnoIntercambio find(AlumnoIntercambio alumnoIntercambio) {
        Octavia sql = Octavia.query()
                .from(AlumnoIntercambio.class, "ab")
                .join("universidadDestino uni", "cicloIntercambio ciclo", "alumno alu", "paisDestino pd", "alu.persona per", "alu.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td")
                .filter("ab.id", alumnoIntercambio);
        return (AlumnoIntercambio) sql.find(getCurrentSession());
    }

    @Override
    public List<AlumnoIntercambio> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoIntercambio.class, "ab")
                .join("universidadDestino uni", "cicloIntercambio ciclo", "alumno alu", "paisDestino pd", "alu.persona per", "alu.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td")
                .searchFields("ciclo.descripcion", "ab.monto", "uni.nombre", "ab.facultadDestino")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ab.id desc");
        return sql.all(getCurrentSession());
    }

}
