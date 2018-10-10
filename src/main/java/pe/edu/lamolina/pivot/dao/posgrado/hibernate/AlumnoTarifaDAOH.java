package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoTarifaDAO;

@Repository
public class AlumnoTarifaDAOH extends AbstractEasyDAO<AlumnoTarifa> implements AlumnoTarifaDAO {

    public AlumnoTarifaDAOH() {
        super();
        setClazz(AlumnoTarifa.class);
    }

    @Override
    public List<AlumnoTarifa> allDynaTable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoTarifa.class, "at")
                .join("alumno al", "tarifaCarrera tc", "tc.carrera ctc")
                .join("al.carrera cal", "tc.cicloinicio ci", "al.persona per")
                .searchFields("al.codigo", "cal.nombre", "ctc.nombre", "ci.descripcion", "ci.descripcion2")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("at.id desc");
        return all(sql);
    }

}
