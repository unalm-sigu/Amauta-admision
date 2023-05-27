package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.CitaConsejeroAlumnoDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;

@Repository
public class CitaConsejeroAlumnoDAOH extends AbstractEasyDAO<CitaConsejeroAlumno> implements CitaConsejeroAlumnoDAO {

    public CitaConsejeroAlumnoDAOH() {
        super();
        setClazz(CitaConsejeroAlumno.class);
    }

    @Override
    public CitaConsejeroAlumno find(long id) {
        Octavia sql = new Octavia()
                .from(CitaConsejeroAlumno.class, "cca")
                .join("alumno alu", "consejero con", "con.colaborador col", "col.persona perc")
                .join("cicloAcademico ci")
                .leftJoin("perc.tipoDocumento")
                .filter("cca.id", id);

        return find(sql);
    }

    @Override
    public List<CitaConsejeroAlumno> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CitaConsejeroAlumno.class, "cca")
                .join("alumno alu", "consejero con", "con.colaborador col", "col.persona perc")
                .join("cicloAcademico ci")
                .leftJoin("perc.tipoDocumento")
                .searchFields("cca.fecha", "cca.asunto", "perc.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(perc.paterno,''),' ',coalesce(perc.materno,''),' ',coalesce(perc.nombres,''))")
                .searchComplexField("concat(coalesce(perc.nombres,''),' ',coalesce(perc.paterno,''),' ',coalesce(perc.materno,''))")
                .filter("alu.id", alumno)
                .filter("ci.id", ciclo)
                .orderBy("cca.id desc");

        return all(sql);
    }

    @Override
    public List<CitaConsejeroAlumno> allByAlumnoFecha(Alumno alumno, Date fecha) {
        Octavia sql = new Octavia()
                .from(CitaConsejeroAlumno.class, "cca")
                .join("alumno alu", "consejero con", "cicloAcademico ci")
                .filter("alu.id", alumno)
                .filter("cca.estado", EstadoCitaTutorEnum.PENDIENTE)
                .filter("cca.fecha", fecha);

        return all(sql);
    }

}
