package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AsistenciaNivelacionDAO;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Repository
public class AsistenciaNivelacionDAOH extends AbstractEasyDAO<AsistenciaNivelacion> implements AsistenciaNivelacionDAO {

    public AsistenciaNivelacionDAOH() {
        super();
        setClazz(AsistenciaNivelacion.class);
    }

    @Override
    public AsistenciaNivelacion find(long id) {
        Octavia sql = Octavia.query()
                .from(AsistenciaNivelacion.class, "asn")
                .join("alumnoNivelacion an", "temaAsistencia tas")
                .filter("asn.id", id);

        return find(sql);
    }

    @Override
    public List<AsistenciaNivelacion> allLeccionByDynatable(DynatableFilter filter, TemaAsistencia leccion) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AsistenciaNivelacion.class, "asn")
                .join("alumnoNivelacion an", "temaAsistencia tas")
                .join("an.alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("an.cicloAcademico ci")
                .leftJoin("per.tipoDocumento")
                .filter("tas.id", leccion)
                .searchFields("car.nombre", "fac.nombre", "per.numeroDocIdentidad", "alu.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

}
