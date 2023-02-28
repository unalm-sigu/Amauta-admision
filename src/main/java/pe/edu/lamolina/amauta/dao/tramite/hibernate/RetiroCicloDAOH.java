package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import pe.edu.lamolina.amauta.dao.tramite.RetiroCicloDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import static pe.edu.lamolina.model.enums.TipoRetiroCicloEnum.EXCEP;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.PEND;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.Tramite;

@Repository
public class RetiroCicloDAOH extends AbstractEasyDAO<RetiroCiclo> implements RetiroCicloDAO {

    public RetiroCicloDAOH() {
        super();
        setClazz(RetiroCiclo.class);
    }

    @Override
    public RetiroCiclo find(long id) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca")
                .left("cicloRegistro cr")
                .filter("rc.id", id);

        return find(sql);
    }

    @Override
    public List<RetiroCiclo> allByCicloDynatable(CicloAcademico cicloAcademico, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RetiroCiclo.class, "rc")
                .join("cicloRegistro cr", "alumno al", "cicloAcademico ca")
                .left("al.persona per", "al.carrera car", "car.facultad")
                .searchFields("ca.nombre", "al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("cr.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public RetiroCiclo findByAlumnoCicloRegistro(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .left("alumno al", "cicloRegistro cr", "cicloAcademico ca")
                .filter("al.id", alumno)
                .filter("ca.codigo", ciclo.getCodigo());

        return find(sql);
    }

    @Override
    public RetiroCiclo findByAlumnoCicloRetiro(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloRegistro cr", "cicloAcademico ca")
                .filter("al.id", alumno)
                .filter("ca.id", ciclo);

        return find(sql);
    }

    @Override
    public List<RetiroCiclo> allByCicloCondicional(CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca", "cicloRegistro cr")
                .join("al.situacionAcademica", "al.modalidadEstudio")
                .filter("esCondicional", 1)
                .in("rc.estado", Arrays.asList(PEND.name()))
                .filter("cr.id", ciclo);

        return all(sql);
    }

    @Override
    public List<RetiroCiclo> allAlumnosByCicloCondicional(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca", "cicloRegistro cr")
                .in("al.id", alumnos)
                .filter("esCondicional", 1)
                .filter("cr.id", ciclo);

        return all(sql);
    }

    @Override
    public List<RetiroCiclo> allAlumnosByCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca", "cicloRegistro cr", "tramite")
                .in("al.id", alumnos)
                .filter("esCondicional", 0)
                .filter("cr.id", ciclo);

        return all(sql);
    }

    @Override
    public List<RetiroCiclo> allByRetiroCiclo(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca", "cicloRegistro cr")
                .filter("estado", TramiteEstadoEnum.ACEP)
                .filter("al.id", alumno);

        return all(sql);
    }

    @Override
    public List<RetiroCiclo> allByResolucion(Resolucion resolucion) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("resolucion re", "alumno al", "cicloAcademico ca")
                .join("al.situacionAcademica", "al.persona per")
                .left("per.tipoDocumento", "cicloRegistro cr")
                .filter("re.id", resolucion);

        return all(sql);

    }

    @Override
    public List<RetiroCiclo> allByTramitesCondicional(CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("tramite tram", "cicloAcademico ", "cicloRegistro cr")
                .filter("cr.id", cicloAcademico)
                .filter("esCondicional", 1);

        return all(sql);
    }

    @Override
    public RetiroCiclo findByTramite(Tramite tramite) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("tramite tram", "cicloAcademico ")
                .filter("tram.id", tramite);

        return find(sql);
    }

    @Override
    public List<RetiroCiclo> allInfo() {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .left("alumno al", "cicloRegistro cr", "cicloAcademico ca")
                .filter("rc.estado", ACEP.name());

        return all(sql);
    }

    @Override
    public List<RetiroCiclo> allRetiroCicloByAlumno(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca")
                .left("cicloRegistro cr", "tramite tram")
                .filter("al.id", alumno);
        return all(sql);
    }

    @Override
    public void updateColumns(RetiroCiclo retiro, String... columns) {
        Octavia sql = Octavia.update(RetiroCiclo.class, "ret");
        for (String column : columns) {
            sql.set(retiro, column);
        }
        this.update(sql);
    }

    @Override
    public List<RetiroCiclo> allByDynatableExcepcional(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RetiroCiclo.class, "rc")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("al.carrera car", "car.facultad", "cicloAcademico", "cicloRegistro cr")
                .left("resolucion")
                .searchFields("per.numeroDocIdentidad", "al.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("rc.tipo", EXCEP)
                .filter("cr.id", cicloAcademico)
                .orderBy("rc.id desc");

        return all(sql);
    }

    @Override
    public List<RetiroCiclo> allByCiclo(CicloAcademico cicloAplica) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("tramite tram", "cicloAcademico ca", "cicloRegistro cr")
                .join("tram.alumno")
                .filter("ca.id", cicloAplica);

        return all(sql);
    }

    @Override
    public RetiroCiclo findByExcepcional(Alumno alumnoDB) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("tramite tram", "cicloAcademico ", "tram.alumno al")
                .filter("tipo", TipoRetiroCicloEnum.EXCEP)
                .beginBlock()
                .in("rc.estado", Arrays.asList(PEND, SOL))
                .endBlock()
                .filter("al.id", alumnoDB);

        return find(sql);
    }

    @Override
    public List<RetiroCiclo> allExepcionalByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("tramite tram", "cicloAcademico ", "tram.alumno al")
                .join("al.persona per", "per.tipoDocumento")
                .filter("tipo", TipoRetiroCicloEnum.EXCEP)
                .beginBlock()
                .in("rc.estado", Arrays.asList(PEND, SOL))
                .endBlock();

        return all(sql);
    }

    @Override
    public RetiroCiclo findByAlumnoCicloRegistroUnique(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .left("alumno al", "cicloRegistro cr", "cicloAcademico ca")
                .filter("al.id", alumno)
                .filter("rc.estado", ACEP)
                .filter("ca.codigo", ciclo.getCodigo())
                .limit(1);

        return find(sql);
    }

    @Override
    public List<RetiroCiclo> allByRetiroCicloAceptadoContable(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca")
                .left("cicloRegistro cr")
                .filter("estado", TramiteEstadoEnum.ACEP)
                .filter("esContable", 1)
                .filter("al.id", alumno);

        return all(sql);
    }

    @Override
    public RetiroCiclo allByAlumnoCicloRegistroUniqueNoAnulado(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .left("alumno al", "cicloRegistro cr", "cicloAcademico ca")
                .filter("al.id", alumno)
                .filter("rc.estado", "<>", TramiteEstadoEnum.ANU)
                .filter("ca.codigo", ciclo.getCodigo())
                .limit(1);

        return find(sql);
    }

    @Override
    public RetiroCiclo allByAlumnoCicloRegistroNoAnuladoNiPendiente(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .left("alumno al", "cicloRegistro cr", "cicloAcademico ca")
                .filter("al.id", alumno)
                .notIn("rc.estado", Arrays.asList(TramiteEstadoEnum.ANU.name(), TramiteEstadoEnum.PEND.name()))
                .filter("ca.codigo", ciclo.getCodigo())
                .limit(1);

        return find(sql);
    }

}
