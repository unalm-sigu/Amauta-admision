package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
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
    public List<RetiroCiclo> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter) {
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
                .filter("cr.id", ciclo);

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
    public List<RetiroCiclo> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca", "cicloRegistro cr")
                .join("al.situacionAcademica")
                .filter("cr.id", ciclo);

        return all(sql);
    }

    @Override
    public List<RetiroCiclo> allAlumnosByCiclo(List<Long> alumnos, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca", "cicloRegistro cr")
                .in("al.id", alumnos)
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
                .join("resolucion re", "alumno al", "cicloAcademico ca", "cicloRegistro cr")
                .join("al.situacionAcademica", "al.persona per")
                .left("per.tipoDocumento")
                .filter("re.id", resolucion);

        return all(sql);

    }

    @Override
    public List<RetiroCiclo> allByTramites(List<Tramite> tramites) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("tramite tram", "cicloAcademico ")
                .in("tram.id", tramites);

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
}
