package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.amauta.dao.academico.RecorridoIngresanteDAO;

@Repository
public class RecorridoIngresanteDAOH extends AbstractEasyDAO<RecorridoIngresante> implements RecorridoIngresanteDAO {

    public RecorridoIngresanteDAOH() {
        super();
        setClazz(RecorridoIngresante.class);
    }

    @Override
    public List<RecorridoIngresante> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {

        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .filter("ci.id", ciclo)
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ri.numeroAtencion asc");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allIngresantesDynatableByPersona(DynatableFilter filter, List<Persona> personas) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .in("per.id", personas)
                .orderBy("ri.numeroAtencion asc");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allConMuestaByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .isNotNull("ri.numeroMuestraSangre")
                .filter("ci.id", ciclo)
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ri.numeroMuestraSangre");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allConMuestraByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .isNotNull("ri.numeroMuestraSangre")
                .filter("ci.id", ciclo)
                .orderBy("ri.numeroMuestraSangre");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allConMuestraByFechaCiclo(Date today, CicloAcademico ciclo) {
        Date tomorrow = new DateTime(today).plusDays(1).toDate();

        Octavia subQuery = Octavia.query()
                .from(HistoriaLaboratorio.class, "lab")
                .join("historiaClinica hc", "hc.paciente pac", "pac.persona pp")
                .filter("fechaMuestra", ">=", today)
                .filter("fechaMuestra", "<", tomorrow);

        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .isNotNull("ri.numeroMuestraSangre")
                .filter("ci.id", ciclo)
                .exists(subQuery)
                .linkedBy("per.id", "pp.id")
                .orderBy("ri.numeroMuestraSangre");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allConMuestaByDynatableFechaCiclo(DynatableFilter filter, Date today, CicloAcademico ciclo) {
        Date tomorrow = new DateTime(today).plusDays(1).toDate();

        Octavia subQuery = Octavia.query()
                .from(HistoriaLaboratorio.class, "lab")
                .join("historiaClinica hc", "hc.paciente pac", "pac.persona pp")
                .filter("fechaMuestra", ">=", today)
                .filter("fechaMuestra", "<", tomorrow);

        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .isNotNull("ri.numeroMuestraSangre")
                .filter("ci.id", ciclo)
                .exists(subQuery)
                .linkedBy("per.id", "pp.id")
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ri.numeroMuestraSangre");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allIngresantesByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .in("per.id", personas)
                .orderBy("per.paterno asc", "per.materno asc", "per.nombres asc");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno a", "a.persona per")
                .leftJoin("turnoEntrevistaObuae tu")
                .filter("ci.id", ciclo)
                .orderBy("ri.numeroAtencion asc");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allByDynatableCicloTurno(DynatableFilter filter, CicloAcademico ciclo, TurnoEntrevistaObuae turno) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .filter("ci.id", ciclo)
                .filter("tu.id", turno)
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ri.numeroAtencion");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allAtendidosByDynatableCicloFecha(DynatableFilter filter, CicloAcademico ciclo, Date today) {
        Date tomorrow = new DateTime(today).plusDays(1).toDate();

        Octavia subQuery = Octavia.query()
                .from(HistoriaLaboratorio.class, "lab")
                .join("historiaClinica hc", "hc.paciente pac", "pac.persona pp")
                .filter("fechaMuestra", ">=", today)
                .filter("fechaMuestra", "<", tomorrow);

        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .filter("ci.id", ciclo)
                .exists(subQuery)
                .linkedBy("per.id", "pp.id")
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ri.numeroAtencion");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allConTurno(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno a", "a.persona per")
                .leftJoin("turnoEntrevistaObuae tu")
                .filter("ci.id", ciclo)
                .isNotNull("turnoEntrevistaObuae")
                .orderBy("per.paterno asc", "per.materno asc", "per.nombres asc");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allConTurno(TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno a", "a.persona per")
                .leftJoin("turnoEntrevistaObuae tu")
                .filter("ci.id", ciclo)
                .filter("tu.id", turno)
                .isNotNull("turnoEntrevistaObuae")
                .orderBy("per.paterno asc", "per.materno asc", "per.nombres asc");

        return all(sql);
    }

    @Override
    public List<RecorridoIngresante> allAtendidos(Date today, CicloAcademico ciclo) {
        Date tomorrow = new DateTime(today).plusDays(1).toDate();

        Octavia subQuery = Octavia.query()
                .from(HistoriaLaboratorio.class, "lab")
                .join("historiaClinica hc", "hc.paciente pac", "pac.persona pp")
                .filter("fechaMuestra", ">=", today)
                .filter("fechaMuestra", "<", tomorrow);

        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno a", "a.persona per")
                .leftJoin("turnoEntrevistaObuae tu")
                .filter("ci.id", ciclo)
                .exists(subQuery)
                .linkedBy("per.id", "pp.id");

        return all(sql);
    }

    @Override
    public RecorridoIngresante findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno a", "a.persona per")
                .filter("ci.id", cicloAcademico)
                .filter("a.id", alumno);

        return find(sql);
    }

    @Override
    public void updateActividadesEjecutadas(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE aca_recorrido_ingresante as table1 ");
        sql.append(" inner join ");
        sql.append(" (SELECT ari.id_alumno,ari.id ariId,  COUNT(ai.id) cant");
        sql.append(" from aca_recorrido_ingresante ari ");
        sql.append(" inner join aca_actividad_ingresante ai ");
        sql.append(" on ai.id_recorrido_ingresante = ari.id ");
        sql.append(" where  ari.id_ciclo_academico = :CICLO ");
        sql.append(" and ai.estado = 'ACT' ");
        sql.append(" GROUP by ari.id_alumno, ari.actividades_ejecutadas) as table2 ");
        sql.append(" on table1.id = table2.ariId ");
        sql.append(" set table1.actividades_ejecutadas = table2.cant ");

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();

    }

    @Override
    public void updateTotalActividades(CicloAcademico cicloAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE aca_recorrido_ingresante as table1  ");
        sql.append(" inner join (SELECT COUNT(id) cant, cri.id_ciclo_academico ciclo ");
        sql.append(" from aca_config_recorrido_ingresante  cri ");
        sql.append(" where cri.id_ciclo_academico = :CICLO ");
        sql.append(" GROUP by cri.id_ciclo_academico) as table2 ");
        sql.append(" on table1.id_ciclo_academico = table2.ciclo ");
        sql.append(" set table1.total_actividades = table2.cant ");

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

}
