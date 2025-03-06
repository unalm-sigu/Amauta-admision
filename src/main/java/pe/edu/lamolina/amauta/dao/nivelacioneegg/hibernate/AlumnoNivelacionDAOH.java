package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.AlumnosNivelacionResumen;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
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
    public List<AlumnoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, List<Carrera> carreras, String todo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoNivelacion.class, "aln")
                .join("alumno alu", "alu.carrera car", "car.facultad fac")
                .join("alu.situacionAcademica", "alu.modalidadEstudio", "alu.persona per")
                .join("alu.postulantePregrado pp", "pp.modalidadIngreso mi", "pp.cicloPostula cp", "cp.cicloAcademico cai")
                .join("cicloAcademico ci")
                .leftJoin("prelamolina", "evaluado", "per.tipoDocumento")
                .filter("ci.id", ciclo)
                .searchFields("car.nombre", "fac.nombre", "per.numeroDocIdentidad", "alu.codigo", "cai.codigoAnterior", "mi.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("aln.id DESC");

        if (!todo.equalsIgnoreCase("TODOS")) {
            sql.in("car.id", carreras);
        }

        sql.beginRelativeFilters();
        this.setCondicionEstado(filter, sql);

        return all(sql);
    }

    private void setCondicionEstado(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("situacion")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("matriculados")) {
                sql.filter("aln.estado", MAT);
            } else if (values.equals("noMatriculados")) {
                sql.filter("aln.estado", NMAT);
            } else if (values.equals("inhabilitados")) {
                sql.filter("aln.estado", INH);
            }
        }

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

    @Override
    public AlumnosNivelacionResumen findResumen(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(AlumnosNivelacionResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case an.estado when 'NMAT' then 1 else 0 end),   ");
        sql.append("   sum(case an.estado when 'MAT' then 1 else 0 end),   ");
        sql.append("   sum(case an.estado when 'INH' then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(AlumnoNivelacion.class.getName()).append(" as an ");
        sql.append(" inner join an.cicloAcademico ci ");
        sql.append(" where ci.id = :CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());

        return (AlumnosNivelacionResumen) query.uniqueResult();
    }

}
