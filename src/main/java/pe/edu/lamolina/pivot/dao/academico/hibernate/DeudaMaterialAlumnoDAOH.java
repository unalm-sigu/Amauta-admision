package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaMaterial;
import pe.edu.lamolina.model.enums.DeudaAlumnoEstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.DeudaMaterialAlumnoDAO;

@Repository
public class DeudaMaterialAlumnoDAOH extends AbstractEasyDAO<DeudaMaterialAlumno> implements DeudaMaterialAlumnoDAO {

    public DeudaMaterialAlumnoDAOH() {
        super();
        setClazz(DeudaMaterialAlumno.class);
    }

    @Override
    public List<DeudaMaterialAlumno> allByDynatableTipoDeuda(DynatableFilter filter, TipoDeudaMaterial tipo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DeudaMaterialAlumno.class, "da")
                .join("tipoDeudaMaterial tdm")
                .leftJoin("alumno alu", "alu.persona per", "tdm.responsable resp", "resp.persona resper")
                .filter("tdm.id", tipo)
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchComplexField("concat(coalesce(resper.paterno,''),' ',coalesce(resper.materno,''),' ',coalesce(resper.nombres,''))")
                .searchComplexField("concat(coalesce(resper.nombres,''),' ',coalesce(resper.paterno,''),' ',coalesce(resper.materno,''))")
                .searchFields("da.estado", "da.descripcion", "tdm.nombre", "tdm.codigo")
                .orderBy("da.id desc");

        return sql.all(getCurrentSession());
    }

    @Override
    public DeudaMaterialAlumno findByTipoAlumno(TipoDeudaMaterial tipo, Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(DeudaMaterialAlumno.class, "da")
                .filter("estado", DeudaAlumnoEstadoEnum.REST)
                .filter("alumno", alumno)
                .filter("tipoDeuda", tipo);

        return (DeudaMaterialAlumno) sql.find(getCurrentSession());
    }

}
