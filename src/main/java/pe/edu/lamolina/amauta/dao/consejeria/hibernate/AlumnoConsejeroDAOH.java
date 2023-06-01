package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
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
import static pe.edu.lamolina.model.constantines.GlobalConstantine.ID_CONSEJERO_NN;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.controller.consejeria.consejeros.Aconsejado;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;

@Repository
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
    public List<AlumnoConsejero> allByPersonaTutor(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor) {
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
                .filter("perc.id", tutor)
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
                .left("alu.persona per", "alu.situacionAcademica sa")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .filter("car.id", carrera)
                .filter("co.id", consejero)
                //.in("sa.codigo", Arrays.asList(S_N.getValue(),S_1.getValue(),S_2.getValue(),S_3.getValue(), S_5.getValue()))  // SE DESACTIVO SOLO POR EL CICLO 2023-I
                .orderBy("per.paterno", "alu.codigo");
        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allActivosByCarreraCiclo(Carrera carrera, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero co", "cicloAcademico ca", "alumno alu", "alu.carrera car")
                .join("alu.persona", "car.facultad")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .filter("car.id", carrera);
        return all(sql);
    }

    @Override
    public Aconsejado countAconsejadosMatriculables(Carrera carrera, CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select ca.id idCarrera, ");
        sql.append("        sum(case when ac.id_consejero is null then 1 else 0 end) sinRegistro, ");
        sql.append("        sum(case when ac.id_consejero = 1 then 1 else 0 end) sinConsejeros, ");
        sql.append("        sum(case when ac.id_consejero <> 1 then 1 else 0 end) conConsejeros, ");
        sql.append("        sum(case when mr.estado = 'MAT' then 1 else 0 end) matriculados, ");
        sql.append("        sum(case when mr.estado = 'MAT' then (case when ac.id_consejero <> 1 then 1 else 0 end) else 0 end) matriculadosConConsejeros, ");
        sql.append("        sum(case when mr.estado = 'MAT' then (case when ac.id_consejero = 1 then 1 else 0 end) else 0 end) matriculadosSinConsejeros, ");
        sql.append("        sum(case when mr.estado <> 'MAT' then 1 else 0 end) noMatriculados, ");
        sql.append("        sum(case when mr.estado <> 'MAT' then (case when ac.id_consejero <> 1 then 1 else 0 end) else 0 end) noMatriculadosConConsejeros, ");
        sql.append("        sum(case when mr.estado <> 'MAT' then (case when ac.id_consejero = 1 then 1 else 0 end) else 0 end) noMatriculadosSinConsejeros ");
        sql.append("   from aca_matricula_resumen mr ");
        sql.append("   join aca_alumno a on a.id = mr.id_alumno ");
        sql.append("   join aca_carrera ca on ca.id = a.id_carrera ");
        sql.append("   left join aca_alumno_consejero ac on ac.id_alumno = mr.id_alumno and ac.id_ciclo_academico = mr.id_ciclo_academico ");
        sql.append("  where mr.id_ciclo_academico = :CICLO ");
        sql.append("    and a.id_carrera = :CARRERA ");
        sql.append("    and mr.estado in ('PMAT','MAT','NMAT') ");
        sql.append("  group by ca.id ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
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
        StringBuilder sql = new StringBuilder("");
        sql.append(" select ca.id idCarrera, ");
        sql.append("        sum(case when mr.id is null then 1 else 0 end) inhabilitados ");
        sql.append("   from aca_alumno_consejero ac ");
        sql.append("   join aca_alumno a on a.id = ac.id_alumno ");
        sql.append("   join aca_carrera ca on ca.id = a.id_carrera ");
        sql.append("   left join aca_matricula_resumen mr on ac.id_alumno = mr.id_alumno ");
        sql.append("                   and ac.id_ciclo_academico = mr.id_ciclo_academico  ");
        sql.append("                   and mr.estado in ('MAT','NMAT','PMAT')  ");
        sql.append("  where ac.id_ciclo_academico = :CICLO ");
        sql.append("    and a.id_carrera = :CARRERA ");
        sql.append("  group by ca.id ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("idCarrera", LongType.INSTANCE)
                .addScalar("inhabilitados", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Aconsejado.class));

        query.setParameter("CARRERA", carrera.getId());
        query.setParameter("CICLO", ciclo.getId());

        return (Aconsejado) query.uniqueResult();
    }

    @Override
    public List<AlumnoConsejero> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("cicloAcademico ca", "alumno alu", "alu.carrera car")
                .join("consejero co", "co.colaborador cola", "cola.persona")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .in("alu.id", alumnos);

        return all(sql);
    }

    public List<AlumnoConsejero> allForReport(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("cicloAcademico ca", "alumno alu", "alu.carrera car")
                .join("consejero co", "co.colaborador cola", "cola.persona")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .in("alu.id", alumnos);

        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allByConsejerosAndCiclo(List<Consejero> consejeros, CicloAcademico ciclo, EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("cicloAcademico ca", "alumno alu", "alu.carrera car")
                .join("consejero co", "co.colaborador cola", "cola.persona", "alu.persona per")
                .in("estado", Arrays.asList(estados))
                .filter("ca.id", ciclo)
                .in("co.id", consejeros);

        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allAlumnosOtraEspecialidad(Carrera carreraConsejero, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero co", "co.carrera carco", "cicloAcademico ca", "alumno alu", "alu.carrera caralu")
                .join("co.colaborador col", "col.persona")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .filter("carco.id", carreraConsejero)
                .filterSpecial("carco.id", "!=", "caralu.id");
        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allByDynatablePersonaTutor(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor) {

        Octavia subquery = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno almr", "cicloAcademico ciac")
                .filter("ciac.id", cicloAcademico)
                .in("mr.estado", this.setCondicionDynatablePersonaTutorCarreraEstado(filter));

        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con", "con.colaborador col", "col.persona perc")
                .join("al.persona per", "al.carrera car", "car.facultad")
                .join("al.situacionAcademica sa", "cicloAcademico ca")
                .leftJoin("per.tipoDocumento", "al.cicloIngreso", "perc.tipoDocumento")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("al.codigo")
                .filter("estado", EstadoEnum.ACT)
                .filter("perc.id", tutor)
                .filter("ca.id", cicloAcademico)
                .exists(subquery)
                .linkedBy("al.id", "almr.id")
                .linkedBy("ca.id", "ciac.id")
                .orderBy("ac.id desc");
        sql.beginRelativeFilters();
        return all(sql);

    }

    private void setCondicionDynatablePersonaTutorOERA(DynatableFilter filter, DynatableSql sql, CicloAcademico ciclo) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        Octavia subqueryMat = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno almr", "cicloAcademico ciac")
                .filter("ciac.id", ciclo)
                .filter("mr.estado", MAT);
        Octavia subqueryNmat = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno almr", "cicloAcademico ciac")
                .filter("ciac.id", ciclo)
                .filter("mr.estado", NMAT);
        Octavia subqueryRCI = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno almr", "cicloAcademico ciac")
                .filter("ciac.id", ciclo)
                .filter("mr.estado", RCI);

        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            if (key.equals("estado")) {
                String value = (String) queries.get(key);
                switch (value) {
                    case "matriculado":
                        sql.exists(subqueryMat);
                        sql.linkedBy("al.id", "almr.id");
                        sql.linkedBy("ca.id", "ciac.id");
                        break;

                    case "noMatriculado":
                        sql.exists(subqueryNmat);
                        sql.linkedBy("al.id", "almr.id");
                        sql.linkedBy("ca.id", "ciac.id");
                        break;

                    case "retirado":
                        sql.exists(subqueryRCI);
                        sql.linkedBy("al.id", "almr.id");
                        sql.linkedBy("ca.id", "ciac.id");
                        break;
                }
            }
            if (key.equals("situacion")) {
                String value = (String) queries.get(key);
                sql.filter("sa.codigo", value);
            }

        }
    }

    private void setCondicionDynatablePersonaTutorCarrera(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            if (key.equals("estado")) {
                String value = (String) queries.get(key);
                switch (value) {
                    case "matriculado":
                        break;

                    case "noMatriculado":
                        break;

                    case "retirado":
                        break;
                }
            }
            if (key.equals("situacion")) {
                String value = (String) queries.get(key);
                sql.filter("sa.codigo", value);
            }

        }
    }

    private List setCondicionDynatablePersonaTutorCarreraEstado(DynatableFilter filter) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return asList(MAT, NMAT, RCI);
        }
        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            if (key.equals("estado")) {
                String value = (String) queries.get(key);
                switch (value) {
                    case "matriculado":
                        return asList(MAT);
                    case "noMatriculado":
                        return asList(NMAT);
                    case "retirado":
                        return asList(RCI);
                }
            }
        }
        return asList(MAT, NMAT, RCI);
    }

    @Override
    public AlumnoConsejero findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero con", "cicloAcademico ca", "alumno alu", "alu.carrera car")
                .join("con.colaborador col", "col.persona perc", "con.carrera")
                .leftJoin("perc.tipoDocumento")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", ciclo)
                .filter("alu.id", alumno);
        return find(sql);
    }

    @Override
    public List<AlumnoConsejero> allByDynatablePersonaTutorCarrera(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor, Carrera carrera) {

        Octavia subquery = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno almr", "cicloAcademico ciac")
                .filter("ciac.id", cicloAcademico)
                .in("mr.estado", this.setCondicionDynatablePersonaTutorCarreraEstado(filter));

        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con", "con.colaborador col", "col.persona perc")
                .join("con.carrera carcon", "al.persona per", "al.carrera car", "car.facultad")
                .join("al.situacionAcademica sa", "cicloAcademico ca")
                .leftJoin("per.tipoDocumento", "al.cicloIngreso", "perc.tipoDocumento")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("al.codigo")
                .filter("estado", EstadoEnum.ACT)
                .filter("perc.id", tutor)
                .filter("ca.id", cicloAcademico)
                .filter("carcon.id", carrera)
                .exists(subquery)
                .linkedBy("al.id", "almr.id")
                .linkedBy("ca.id", "ciac.id")
                .orderBy("ac.id desc");

        sql.beginRelativeFilters();
        setCondicionDynatablePersonaTutorCarrera(filter, sql);
        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allByDynatablePersonaTutorCarreraOERA(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor, Carrera carrera) {

        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con", "con.colaborador col", "col.persona perc")
                .join("con.carrera carcon", "al.persona per", "al.carrera car", "car.facultad")
                .join("al.situacionAcademica sa", "cicloAcademico ca")
                .leftJoin("per.tipoDocumento", "al.cicloIngreso", "perc.tipoDocumento")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("al.codigo")
                .filter("estado", EstadoEnum.ACT)
                .filter("perc.id", tutor)
                .filter("ca.id", cicloAcademico)
                .filter("carcon.id", carrera)
                .orderBy("ac.id desc");

        sql.beginRelativeFilters();
        setCondicionDynatablePersonaTutorOERA(filter, sql, cicloAcademico);
        return all(sql);
    }

    @Override
    public List<AlumnoConsejero> allByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero co", "cicloAcademico ca", "alumno alu", "alu.carrera car")
                .join("co.colaborador col", "col.persona")
                .filter("estado", EstadoEnum.ACT)
                .filter("ca.id", cicloAcademico)
                .filter("car.id", carrera);
        return all(sql);
    }

    @Override
    public void deleteByCiclo(CicloAcademico cicloAcademico) {

        String strQuery = "delete from AlumnoConsejero ac where ac.cicloAcademico.id=:CICLO_ACADEMICO";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("CICLO_ACADEMICO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public AlumnoConsejero findAll(Long idAlumnoConsejero) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .left("consejero co", "cicloAcademico ca", "alumno alu", "alu.carrera car")
                .left("co.colaborador col", "col.persona")
                .filter("ac.id", idAlumnoConsejero);
        return find(sql);
    }

    @Override
    public List<AlumnoConsejero> allSimpleByCicloConsejeros(List<Consejero> consejeros, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoConsejero.class, "ac")
                .join("consejero co", "cicloAcademico ca", "alumno alu")
                .in("co.id", consejeros)
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public Long countConsejeria(CicloAcademico cicloAcademico, Carrera carrera, String estado) {

        Octavia subquery = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("cicloAcademico ciac", "alumno almr")
                .in("mr.estado", Arrays.asList(MAT, NMAT, PMAT, RCI));

        Octavia sql = Octavia.query()
                .selectCount()
                .from(AlumnoConsejero.class, "ac")
                .join("alumno al", "consejero con", "cicloAcademico ca", "al.situacionAcademica")
                .join("al.persona per", "al.carrera car", "car.facultad")
                .leftJoin("per.tipoDocumento", "al.cicloIngreso", "con.colaborador col", "col.persona perc", "perc.tipoDocumento")
                .filter("estado", EstadoEnum.ACT)
                .filter("car.id", carrera)
                .filter("ca.id", cicloAcademico)
                .orderBy("al.id desc");

        switch (estado) {
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

        return (Long) sql.find(getCurrentSession());

    }

}
