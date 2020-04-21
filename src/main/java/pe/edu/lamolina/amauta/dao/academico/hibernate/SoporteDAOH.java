package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Soporte;
import pe.edu.lamolina.amauta.dao.academico.SoporteDAO;

@Repository
public class SoporteDAOH extends AbstractEasyDAO<Soporte> implements SoporteDAO {

    public SoporteDAOH() {
        super();
        setClazz(Soporte.class);
    }

    @Override
    public void updateColumns(Soporte matriculaResumen, String... params) {
        Octavia sql = Octavia.update(Soporte.class);
        for (String column : params) {
            sql.set(matriculaResumen, column);
        }
        this.update(sql);
    }

    @Override
    public List<Soporte> allDyanatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Soporte.class, "so")
                .join("alumno al", "al.persona per", "al.carrera ca", "ca.modalidadEstudio moe", "ca.facultad fac")
                .leftJoin("al.situacionAcademica sita", "per.tipoDocumento tdoc", "al.cicloIngreso ci", "al.cicloActivo cia")
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("so.id desc");

        return all(sql);
    }

}
