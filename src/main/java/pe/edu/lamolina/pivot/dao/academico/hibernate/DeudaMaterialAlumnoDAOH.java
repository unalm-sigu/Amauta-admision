package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;
import pe.edu.lamolina.model.enums.DeudaAlumnoEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.dao.academico.DeudaMaterialAlumnoDAO;

@Repository
public class DeudaMaterialAlumnoDAOH extends AbstractEasyDAO<DeudaMaterialAlumno> implements DeudaMaterialAlumnoDAO {

    public DeudaMaterialAlumnoDAOH() {
        super();
        setClazz(DeudaMaterialAlumno.class);
    }

    @Override
    public List<DeudaMaterialAlumno> allByDynatableTipoDeuda(DynatableFilter filter, List<Oficina> oficina) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DeudaMaterialAlumno.class, "da")
                .join("oficina ofi")
                .leftJoin("alumno alu", "alu.persona per")
                .in("ofi.id", oficina)
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("da.estado", "da.descripcion", "ofi.nombre", "ofi.codigo")
                .orderBy("da.id desc");

        return sql.all(getCurrentSession());
    }

    @Override
    public DeudaMaterialAlumno findByTipoAlumno(Oficina oficina, Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(DeudaMaterialAlumno.class, "da")
                .join("oficina ofi", "alumno al")
                .filter("estado", DeudaAlumnoEstadoEnum.REST)
                .filter("al.id", alumno)
                .filter("ofi.id", oficina);

        return (DeudaMaterialAlumno) sql.find(getCurrentSession());
    }

}
