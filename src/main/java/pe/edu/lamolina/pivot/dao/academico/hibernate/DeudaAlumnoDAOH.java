package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DeudaAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaAlumno;
import pe.edu.lamolina.model.enums.DeudaAlumnoEstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.DeudaAlumnoDAO;

@Repository
public class DeudaAlumnoDAOH extends AbstractEasyDAO<DeudaAlumno> implements DeudaAlumnoDAO {

    public DeudaAlumnoDAOH() {
        super();
        setClazz(DeudaAlumno.class);
    }

    @Override
    public List<DeudaAlumno> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DeudaAlumno.class, "da")
                .leftJoin("alumno alu", "alu.persona per", "tipoDeuda tipo", "tipo.responsable resp", "resp.persona resper")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchComplexField("concat(coalesce(resper.paterno,''),' ',coalesce(resper.materno,''),' ',coalesce(resper.nombres,''))")
                .searchComplexField("concat(coalesce(resper.nombres,''),' ',coalesce(resper.paterno,''),' ',coalesce(resper.materno,''))")
                .searchFields("da.estado", "da.descripcion", "tipo.nombre", "tipo.codigo")
                .orderBy("da.id desc");

        return sql.all(getCurrentSession());
    }

    @Override
    public DeudaAlumno findByTipoAlumno(TipoDeudaAlumno tipo, Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(DeudaAlumno.class, "da")
                .filter("estado", DeudaAlumnoEstadoEnum.REST)
                .filter("alumno", alumno)
                .filter("tipoDeuda", tipo);

        return (DeudaAlumno) sql.find(getCurrentSession());
    }

}
