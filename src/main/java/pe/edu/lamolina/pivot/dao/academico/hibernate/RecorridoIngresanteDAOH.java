package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;

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

}
