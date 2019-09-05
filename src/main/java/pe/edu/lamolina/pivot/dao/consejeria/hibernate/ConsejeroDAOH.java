package pe.edu.lamolina.pivot.dao.consejeria.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
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
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoEnum.INA;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.AConsejeroEstado;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.ConsejeroEstado;
import pe.edu.lamolina.pivot.dao.consejeria.ConsejeroDAO;

@Service
public class ConsejeroDAOH extends AbstractEasyDAO<Consejero> implements ConsejeroDAO {

    public ConsejeroDAOH() {
        super();
        setClazz(Consejero.class);
    }

    @Override
    public List<Consejero> allByCarreraDynatable(Carrera carrera, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Consejero.class, "con")
                .join("carrera car", "colaborador col", "col.persona per", "per.docente doc")
                .leftJoin("per.tipoDocumento")
                .searchFields("per.numeroDocIdentidad", "doc.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("car.id", carrera)
                .orderBy("con.id desc");
        if (filter.getQueries() != null && filter.getQueries().get("consjeroPrm") != null) {
            sql.filter("con.id", filter.getQueries().get("consjeroPrm"));
        }
        sql.beginRelativeFilters();
        setCondicion(filter, sql);

        return all(sql);
    }

    private void setCondicion(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }

            if (key.equals("status")) {
                String values = (String) queries.get(key);
                if (values.equals("Habilitado")) {
                    sql.filter("estado", ACT);
                } else if (values.equals("Inhabilitado")) {
                    sql.filter("estado", INA);
                }
            }
        }
    }

    @Override
    public Consejero finByIdPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .selectDistinct("con")
                .from(Colaborador.class, "col")
                .join("persona per", "consejero con")
                .filter("per.id", persona);
        return find(sql);
    }

    @Override
    public ConsejeroEstado countConsejerosByCarrera(Carrera carrera) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(ConsejeroEstado.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case conse.estado when :ACT then 1 else 0 end),   ");
        sql.append("   sum(case conse.estado when :INA then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(Consejero.class.getName()).append(" as conse ");
        sql.append(" inner join conse.carrera ca ");
        sql.append(" where ca.id = :CARRERA ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setString("ACT", ACT.name());
        query.setString("INA", INA.name());
        query.setLong("CARRERA", carrera.getId());

        return (ConsejeroEstado) query.uniqueResult();
    }

    @Override
    public List<Consejero> allActivosByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(Consejero.class, "conse")
                .join("carrera car", "colaborador cola")
                .filter("car.id", carrera)
                .filter("estado", ACT);

        return all(sql);
    }

    @Override
    public List<Consejero> allByNombreAndCarrera(String nombre, Carrera carrera) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Consejero.class, "con")
                .join("colaborador col", "col.persona per", "carrera carr")
                .filter("carr.id", carrera)
                .filter("con.estado", EstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public List<Alumno> allAlumnosByConsejero(Consejero consejero) {
        return allAlumnosByConsejero(Arrays.asList(consejero));
    }

    @Override
    public List<Alumno> allAlumnosByConsejero(List<Consejero> consejeros) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "al")
                .join("consejero conse")
                .in("conse.id", consejeros);
        return sql.all(getCurrentSession());
    }

    @Override
    public Long findByMatriculaActivo(List<Alumno> alumnos, Long carrera, CicloAcademico cicloacademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno al")
                .filter("estado", MAT)
                .join(" mr.cicloAcademico ci ")
                .filter(" mr.cicloAcademico ", cicloacademico)
                .filter(" al.carrera ", carrera)
                .in("al.id", alumnos);

        return Long.parseLong(sql.all(getCurrentSession()).size() + "");
    }

    @Override
    public Long findByMatriculaInactivo(List<Alumno> alumnos, Long carrera, CicloAcademico cicloacademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaResumen.class, "mr")
                .join("alumno al")
                .join(" mr.cicloAcademico ci ")
                .filter(" mr.cicloAcademico ", cicloacademico)
                .filter(" al.carrera ", carrera)
                .in("mr.estado", Arrays.asList(NMAT, RCI))
                .in("al.id", alumnos);

        return Long.parseLong(sql.all(getCurrentSession()).size() + "");
    }

    @Override
    public AConsejeroEstado findAconsejadosByMatricula(Long carrera, CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(AConsejeroEstado.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case when conse.id = :CONSEJEROCOMODIN then 0 when conse.id is not null then 1 else 0 end),   ");
        sql.append("   sum(case when conse.id = :CONSEJEROCOMODIN then 1 else 0 end),   ");
        sql.append("   sum(case when conse.id is null then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(MatriculaResumen.class.getName()).append(" as mr ");
        sql.append(" inner join mr.cicloAcademico ci ");
        sql.append(" inner join mr.alumno alm");
        sql.append(" inner join alm.carrera carr");
        sql.append(" left join alm.consejero conse on conse.id = alm.consejero ");
        sql.append(" where carr.id = :CARRERA ");
        sql.append(" and ci.id = :CICLO ");
        sql.append(" and mr.estado in ( :ESTADOS )");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setInteger("CONSEJEROCOMODIN", 1);
        query.setLong("CARRERA", carrera);
        query.setLong("CICLO", cicloAcademico.getId());
        query.setParameterList("ESTADOS", Arrays.asList(NMAT.name(), MAT.name(), RCI.name()));

        return (AConsejeroEstado) query.uniqueResult();
    }

    @Override
    public Consejero findByColaboradorCarrera(Colaborador colaborador, Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(Consejero.class, "conse")
                .join("carrera ca", "colaborador cola", "cola.persona per")
                .filter("cola.id", colaborador)
                .filter("ca.id", carrera);
        return find(sql);
    }

    @Override
    public List<Consejero> allCountAconsejadosMatriculadosByCiclo(List<Consejero> consejeros, CicloAcademico cicloAcademico, EstadoEnum... estadoEnums) {
        StringBuilder strb = new StringBuilder();
        strb.append("   Select   ");
        strb.append("   ac.id_consejero id,   ");
        strb.append("   sum(if(mres.id is null,1,0)) aconsejadosNmat,   ");
        strb.append("   sum(if(mres.id is not null,1,0)) aconsejadosMat   ");
        strb.append("   from aca_alumno_consejero ac   ");
        strb.append("  left join aca_matricula_resumen mres on mres.id_alumno=ac.id_alumno and mres.id_ciclo_academico=ac.id_ciclo_academico and mres.estado='MAT'    ");
        strb.append("  inner join aca_consejero con on con.id=ac.id_consejero    ");
        strb.append("  inner join  aca_carrera carcon on carcon.id=con.id_carrera    ");
        strb.append("  where ac.id_ciclo_academico=:CICLO and ac.estado IN (:ESTADOS) and con.id IN (:CONSEJEROS)   ");
        strb.append("  group by ac.id_consejero,carcon.id;    ");

        Query query = getCurrentSession().createSQLQuery(strb.toString())
                .addScalar("id", LongType.INSTANCE)
                .addScalar("aconsejadosNmat", IntegerType.INSTANCE)
                .addScalar("aconsejadosMat", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Consejero.class));

        List<EstadoEnum> lEstados = Arrays.asList(estadoEnums);
        query.setParameter("CICLO", cicloAcademico.getId());
        query.setParameterList("ESTADOS", lEstados.stream().map(x -> x.name()).collect(Collectors.toList()));
        query.setParameterList("CONSEJEROS", consejeros.stream().map(x -> x.getId()).collect(Collectors.toList()));

        return query.list();
    }

}
