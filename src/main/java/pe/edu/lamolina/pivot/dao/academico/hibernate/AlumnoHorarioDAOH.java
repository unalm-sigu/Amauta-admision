package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;

@Repository
public class AlumnoHorarioDAOH extends AbstractEasyDAO<AlumnoHorario> implements AlumnoHorarioDAO {

    public AlumnoHorarioDAOH() {
        super();
        setClazz(AlumnoHorario.class);
    }

    @Override
    public List<AlumnoHorario> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo ", "alumno alu")
                .leftJoin("horarioCachimbos hoca")
                .filter("ciclo.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public AlumnoHorario findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo ", "alumno alu")
                .leftJoin("horarioCachimbos hoca")
                .filter("ciclo.id", cicloAcademico)
                .filter("alu.id", alumno);
        return (AlumnoHorario) sql.find(getCurrentSession());
    }

    @Override
    public List<AlumnoHorario> allByAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoHorario.class, "alu")
                .join("alumno alum", "alum.persona per", "cicloAcademico ciclo")
                .leftJoin("horarioCachimbos hora", "alum.orientacionCarrera oca", "alum.carrera ca", "alum.cicloIngreso ci", "alum.situacionAcademica sia", "alum.modalidadEstudio me")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("ca.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("alu.id desc");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico, Carrera carrera) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo ", "alumno alu", "alu.carrera carr", "alu.persona per")
                .leftJoin("per.tipoDocumento td", "horarioCachimbos hoca")
                .filter("estado", EstadoAlumnoHorarioEnum.MATR)
                .filter("ciclo.id", cicloAcademico)
                .filter("carr.id", carrera)
                .isNull("ah.horarioCachimbos")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public AlumnoHorario find(AlumnoHorario alumnoHorario) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo", "alumno alu", "alu.carrera carr", "alu.persona per")
                .leftJoin("per.tipoDocumento td", "horarioCachimbos hoca")
                .filter("ah.id", alumnoHorario.getId());
        return (AlumnoHorario) sql.find(getCurrentSession());
    }

    @Override
    public List<AlumnoHorario> allByHorario(HorarioCachimbos horario) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo ", "alumno alu")
                .leftJoin("horarioCachimbos hoca")
                .filter("hoca.id", horario);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoHorario> allByCicloHorarios(CicloAcademico cicloAcademico, List<HorarioCachimbos> horarios) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo", "alumno alu", "alu.carrera carr", "alu.persona per")
                .leftJoin("per.tipoDocumento td", "horarioCachimbos hoca")
                .in("hoca.id", horarios)
                .filter("ciclo.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoHorario> allByHorarioCachimbos(HorarioCachimbos horarioCachimbos) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo", "alumno alu", "alu.carrera carr", "alu.persona per")
                .leftJoin("per.tipoDocumento td", "horarioCachimbos hoca")
                .filter("hoca.id", horarioCachimbos);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<AlumnoHorario> allByAlumnoHorarioLikeList(AlumnoHorario alumnoHorario) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo ", "alumno alu")
                .leftJoin("horarioCachimbos hoca")
                .filter("ah.id", alumnoHorario);
        return sql.all(getCurrentSession());
    }
}
