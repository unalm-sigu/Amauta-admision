package pe.edu.lamolina.amauta.dao.academico.hibernate;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.RegistroBorradoAlumnoDAO;
import pe.edu.lamolina.model.academico.RegistroBorradoAlumno;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RegistroBorradoAlumnoDAOH extends AbstractEasyDAO<RegistroBorradoAlumno> implements RegistroBorradoAlumnoDAO {

    @Override
    public List<RegistroBorradoAlumno> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RegistroBorradoAlumno.class, "br")
                .join("alumno al")
                .join("userRegistra user", "user.persona p")
                .join("al.persona per", "per.tipoDocumento")
                .join("al.carrera car", "car.facultad", "al.modalidadEstudio")
                .searchFields("al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("al.id");

        return all(sql);
    }
}
