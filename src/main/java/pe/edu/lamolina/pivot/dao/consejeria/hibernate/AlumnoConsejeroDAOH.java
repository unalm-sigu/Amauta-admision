package pe.edu.lamolina.pivot.dao.consejeria.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.Aconsejado;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.ID_CONSEJERO_NN;

@Service
public class AlumnoConsejeroDAOH extends AbstractEasyDAO<AlumnoConsejero> implements AlumnoConsejeroDAO {

    public AlumnoConsejeroDAOH() {
        super();
        setClazz(AlumnoConsejero.class);
    }

    @Override
    public void insertAlumnoConsejero(Consejero consejero, CicloAcademico cicloAcademico, Usuario usuario, Carrera carrera, List<Alumno> alumnos) {
        List<Long> idsAlumnos = alumnos.stream().map(Alumno::getId).collect(Collectors.toList());

        StringBuilder strb = new StringBuilder("");
        strb.append("insert into AlumnoConsejero ");
        strb.append("   ( estado, fechaAsigna, alumno, consejero, cicloAcademico, userAsigna ) ");
        strb.append(" select 'ACT', :HOY, alum, :CONSEJERO, cic, :USER ");
        strb.append("   from MatriculaResumen mat ");
        strb.append("  inner join mat.alumno alum ");
        strb.append("  inner join alum.carrera car ");
        strb.append("  inner join mat.cicloAcademico cic ");
        strb.append("  where car.id = :CARRERA ");
        strb.append("    and cic.id = :CICLO ");
        strb.append("    and mat.estado in (:ESTADOS)");
        strb.append("    and alum.id in (:ALUMNOS)");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("CARRERA", carrera.getId());
        query.setParameter("CICLO", cicloAcademico.getId());
        query.setParameter("CONSEJERO", consejero);
        query.setParameter("USER", usuario);
        query.setParameter("HOY", new Date());
        query.setParameterList("ESTADOS", Arrays.asList(MAT.name(), NMAT.name()));
        query.setParameterList("ALUMNOS", idsAlumnos);

        query.executeUpdate();
    }

    @Override
    public List<AlumnoConsejero> allByDynatableCarrera(Carrera carrera, DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con", "cicloAcademico ca", "al.situacionAcademica")
                .join("al.persona per", "al.carrera car", "car.facultad")
                .leftJoin("per.tipoDocumento", "al.cicloIngreso", "con.colaborador col", "col.persona perc", "perc.tipoDocumento")
                .filter("estado", EstadoEnum.ACT)
                .filter("car.id", carrera)
                .filter("ca.id", cicloAcademico)
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchComplexField("concat(coalesce(perc.paterno,''),' ',coalesce(perc.materno,''),' ',coalesce(perc.nombres,''))")
                .searchComplexField("concat(coalesce(perc.nombres,''),' ',coalesce(perc.paterno,''),' ',coalesce(perc.materno,''))")
                .searchFields("al.codigo")
                .orderBy("al.id desc");
        sql.beginRelativeFilters();
        setCondicion(filter, sql, cicloAcademico);
        return all(sql);
    }

    private void setCondicion(DynatableFilter filter, DynatableSql sql, CicloAcademico ciclo) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        Octavia subquery = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("cicloAcademico ciac", "alumno almr")
                .in("mr.estado", Arrays.asList(MAT, NMAT, PMAT, RCI));

        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            if (key.equals("estado")) {
                String value = (String) queries.get(key);
                switch (value) {
                    case "conConsejero":
                        sql.filter("con.id", "<>", ID_CONSEJERO_NN);
                        sql.exists(subquery);
                        sql.linkedBy("al.id", "almr.id");
                        sql.linkedBy("ca.id", "ciac.id");
                        break;

                    case "sinConsejero":
                        sql.filter("con.id", ID_CONSEJERO_NN);
                        sql.exists(subquery);
                        sql.linkedBy("al.id", "almr.id");
                        sql.linkedBy("ca.id", "ciac.id");
                        break;

                    case "inhabilitado":
                        sql.notExists(subquery);
                        sql.linkedBy("al.id", "almr.id");
                        sql.linkedBy("ca.id", "ciac.id");
                        break;
                }
            }

        }
    }

    @Override
    public List<AlumnoConsejero> allByPersona(DynatableFilter filter, CicloAcademico cicloAcademico, Persona persona) {
        Octavia subquery = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno alum", "cicloAcademico ciac")
                .filter("ciac.id", cicloAcademico);
        setCondicionEstadoMatricula(filter, subquery);

        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con", "con.colaborador col", "col.persona perc")
                .join("al.persona per", "al.carrera car", "car.facultad")
                .join("al.situacionAcademica ", "cicloAcademico ca")
                .leftJoin("per.tipoDocumento", "al.cicloIngreso", "perc.tipoDocumento")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("al.codigo")
                .exists(subquery)
                .linkedBy("al.id", "alum.id")
                .filter("estado", EstadoEnum.ACT)
                .filter("perc.id", persona)
                .filter("ca.id", cicloAcademico)
                .orderBy("ac.id desc");
        return all(sql);
    }

    private void setCondicionEstadoMatricula(DynatableFilter filter, Octavia sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            if (key.equals("estado")) {
                String value = (String) queries.get(key);
                switch (value) {
                    case "matriculado":
                        sql.filter("mr.estado", MAT);
                        break;
                    case "noMatriculado":
                        sql.filter("mr.estado", NMAT);
                        break;
                    case "retirado":
                        sql.filter("mr.estado", RCI);
                        break;
                }
            }

        }
    }

    @Override
    public List<AlumnoConsejero> allByConsejeroCiclo(Consejero consejero, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero co", "cicloAcademico ca", "alumno alu", "alu.carrera")
                .filter("ca.id", ciclo)
                .filter("co.id", consejero);
        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allActivosByConsejeroCarreraCiclo(Consejero consejero, Carrera carrera, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero co", "cicloAcademico ca", "alumno alu", "alu.carrera car")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .filter("car.id", carrera)
                .filter("co.id", consejero);
        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allActivosByCarreraCiclo(Carrera carrera, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero co", "cicloAcademico ca", "alumno alu", "alu.carrera car")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .filter("car.id", carrera);
        return all(sql);
    }

    @Override
    public Aconsejado countAconsejadosMatriculables(Carrera carrera, CicloAcademico ciclo) {
        StringBuilder strb = new StringBuilder("");
        strb.append(" select ca.id idCarrera, ");
        strb.append("        sum(case when ac.id_consejero is null then 1 else 0 end) sinRegistro, ");
        strb.append("        sum(case when ac.id_consejero = 1 then 1 else 0 end) sinConsejeros, ");
        strb.append("        sum(case when ac.id_consejero <> 1 then 1 else 0 end) conConsejeros, ");
        strb.append("        sum(case when mr.estado = 'MAT' then 1 else 0 end) matriculados, ");
        strb.append("        sum(case when mr.estado = 'MAT' then (case when ac.id_consejero <> 1 then 1 else 0 end) else 0 end) matriculadosConConsejeros, ");
        strb.append("        sum(case when mr.estado = 'MAT' then (case when ac.id_consejero = 1 then 1 else 0 end) else 0 end) matriculadosSinConsejeros, ");
        strb.append("        sum(case when mr.estado <> 'MAT' then 1 else 0 end) noMatriculados, ");
        strb.append("        sum(case when mr.estado <> 'MAT' then (case when ac.id_consejero <> 1 then 1 else 0 end) else 0 end) noMatriculadosConConsejeros, ");
        strb.append("        sum(case when mr.estado <> 'MAT' then (case when ac.id_consejero = 1 then 1 else 0 end) else 0 end) noMatriculadosSinConsejeros ");
        strb.append("   from aca_matricula_resumen mr ");
        strb.append("   join aca_alumno a on a.id = mr.id_alumno ");
        strb.append("   join aca_carrera ca on ca.id = a.id_carrera ");
        strb.append("   left join aca_alumno_consejero ac on ac.id_alumno = mr.id_alumno and ac.id_ciclo_academico = mr.id_ciclo_academico ");
        strb.append("  where mr.id_ciclo_academico = :CICLO ");
        strb.append("    and a.id_carrera = :CARRERA ");
        strb.append("    and mr.estado in ('PMAT','MAT','NMAT') ");
        strb.append("  group by ca.id ");

        Query query = getCurrentSession().createSQLQuery(strb.toString())
                .addScalar("idCarrera", LongType.INSTANCE)
                .addScalar("sinRegistro", LongType.INSTANCE)
                .addScalar("sinConsejeros", LongType.INSTANCE)
                .addScalar("conConsejeros", LongType.INSTANCE)
                .addScalar("matriculados", LongType.INSTANCE)
                .addScalar("matriculadosConConsejeros", LongType.INSTANCE)
                .addScalar("matriculadosSinConsejeros", LongType.INSTANCE)
                .addScalar("noMatriculados", LongType.INSTANCE)
                .addScalar("noMatriculadosConConsejeros", LongType.INSTANCE)
                .addScalar("noMatriculadosSinConsejeros", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Aconsejado.class));

        query.setParameter("CARRERA", carrera.getId());
        query.setParameter("CICLO", ciclo.getId());

        return (Aconsejado) query.uniqueResult();

    }

    @Override
    public Aconsejado countAconsejadosNoMatriculables(Carrera carrera, CicloAcademico ciclo) {
        StringBuilder strb = new StringBuilder("");
        strb.append(" select ca.id idCarrera, ");
        strb.append("        sum(case when mr.id is null then 1 else 0 end) inhabilitados ");
        strb.append("   from aca_alumno_consejero ac ");
        strb.append("   join aca_alumno a on a.id = ac.id_alumno ");
        strb.append("   join aca_carrera ca on ca.id = a.id_carrera ");
        strb.append("   left join aca_matricula_resumen mr on ac.id_alumno = mr.id_alumno ");
        strb.append("                   and ac.id_ciclo_academico = mr.id_ciclo_academico  ");
        strb.append("                   and mr.estado in ('MAT','NMAT','PMAT')  ");
        strb.append("  where ac.id_ciclo_academico = :CICLO ");
        strb.append("    and a.id_carrera = :CARRERA ");
        strb.append("  group by ca.id ");

        Query query = getCurrentSession().createSQLQuery(strb.toString())
                .addScalar("idCarrera", LongType.INSTANCE)
                .addScalar("inhabilitados", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Aconsejado.class));

        query.setParameter("CARRERA", carrera.getId());
        query.setParameter("CICLO", ciclo.getId());

        return (Aconsejado) query.uniqueResult();
    }

}
