package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

@Repository
public class AlumnoNivelacionDAOH extends AbstractEasyDAO<AlumnoNivelacion> implements AlumnoNivelacionDAO {

    public AlumnoNivelacionDAOH() {
        super();
        setClazz(AlumnoNivelacion.class);
    }

    @Override
    public AlumnoNivelacion find(long id) {
        Octavia sql = Octavia.query()
                .from(AlumnoNivelacion.class, "aln")
                .join("alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("cicloAcademico ci")
                .leftJoin("prelamolina", "evaluado", "per.tipoDocumento")
                .filter("aln.id", id);

        return find(sql);
    }

    @Override
    public List<AlumnoNivelacion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoNivelacion.class, "aln")
                .join("alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("cicloAcademico ci")
                .leftJoin("prelamolina", "evaluado", "per.tipoDocumento")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<AlumnoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoNivelacion.class, "aln")
                .join("alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("cicloAcademico ci")
                .leftJoin("prelamolina", "evaluado", "per.tipoDocumento")
                .filter("ci.id", ciclo)
                .searchFields("car.nombre", "fac.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("aln.id DESC");

        return all(sql);
    }

    @Override
    public AlumnoNivelacion findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoNivelacion.class, "aln")
                .join("alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("cicloAcademico ci")
                .leftJoin("prelamolina", "evaluado", "per.tipoDocumento")
                .filter("alu.id", alumno)
                .filter("ci.id", ciclo);

        return find(sql);
    }

}
