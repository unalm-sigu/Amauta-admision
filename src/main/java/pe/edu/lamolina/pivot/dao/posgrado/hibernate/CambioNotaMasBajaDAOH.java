package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import static pe.edu.lamolina.model.enums.EstadoTramiteEnum.EPG_TRAM_GRADO_AGEN;
import static pe.edu.lamolina.model.enums.EstadoTramiteEnum.EPG_TRAM_GRADO_PEND;
import static pe.edu.lamolina.model.enums.EstadoTramiteEnum.EPG_TRAM_GRADO_SOL;
import pe.edu.lamolina.model.tramite.CambioNotaMasBaja;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.posgrado.CambioNotaMasBajaDAO;

@Repository
public class CambioNotaMasBajaDAOH extends AbstractEasyDAO<CambioNotaMasBaja> implements CambioNotaMasBajaDAO {

    public CambioNotaMasBajaDAOH() {
        super();
        setClazz(CambioNotaMasBaja.class);
    }

    @Override
    public List<CambioNotaMasBaja> allByTramite(List<Tramite> tramites) {

        Octavia sql = Octavia.query()
                .from(CambioNotaMasBaja.class, "cnmb")
                .join("alumno al", "tramite tra", "curso cu", "estadoTramite et")
                .left("resolucion re", "al.carrera car", "al.persona per", "per.tipoDocumento td")
                .in("tra.id", tramites);
        return all(sql);
    }

    @Override
    public List<CambioNotaMasBaja> allByEstadoTramite(EstadoTramite estadoSolicitado) {

        Octavia sql = Octavia.query()
                .from(CambioNotaMasBaja.class, "cnmb")
                .join("alumno al", "tramite tra", "curso cu", "estadoTramite et")
                .left("resolucion re", "al.carrera car", "al.persona per", "per.tipoDocumento td")
                .filter("et.id", estadoSolicitado);
        return all(sql);
    }

    @Override
    public List<CambioNotaMasBaja> allByAlumnosEstadoTramite(List<Alumno> alumnos, EstadoTramite estadoAgendado) {

        Octavia sql = Octavia.query()
                .from(CambioNotaMasBaja.class, "cnmb")
                .join("alumno al", "tramite tra", "curso cu", "estadoTramite et")
                .left("resolucion re", "al.carrera car", "al.persona per", "per.tipoDocumento td")
                .filter("et.id", estadoAgendado)
                .in("al.id", alumnos);
        return all(sql);
    }

    @Override
    public void updateColumns(CambioNotaMasBaja cambioNotaMasBaja, String... columns) {
        Octavia sql = Octavia.update(CambioNotaMasBaja.class);
        for (String column : columns) {
            sql.set(cambioNotaMasBaja, column);
        }
        this.update(sql);
    }

    @Override
    public List<CambioNotaMasBaja> allByResolucion(Resolucion resolucion) {
        Octavia sql = Octavia.query()
                .from(CambioNotaMasBaja.class, "cnmb")
                .join("alumno al", "tramite tra", "curso cu", "estadoTramite et")
                .left("resolucion re", "al.carrera car", "al.persona per", "per.tipoDocumento td")
                .filter("re.id", resolucion);
        return all(sql);
    }

    @Override
    public List<CambioNotaMasBaja> all(List<Long> cambioNotaMasBajas) {
        Octavia sql = Octavia.query()
                .from(CambioNotaMasBaja.class, "cnmb")
                .join("alumno al", "tramite tra", "curso cu", "estadoTramite et")
                .left("resolucion re", "al.carrera car", "al.persona per", "per.tipoDocumento td")
                .in("cnmb.id", cambioNotaMasBajas);
        return all(sql);
    }

    @Override
    public List<CambioNotaMasBaja> allPendienteByAlumnoCurso(Alumno alumno, Curso curso) {
        Octavia sql = Octavia.query()
                .from(CambioNotaMasBaja.class, "cnmb")
                .join("alumno al", "tramite tra", "curso cu", "estadoTramite et")
                .left("resolucion re", "al.carrera car", "al.persona per", "per.tipoDocumento td")
                .filter("al.id", alumno)
                .filter("cu.id", curso)
                .in("et.codigo", Arrays.asList(EPG_TRAM_GRADO_PEND, EPG_TRAM_GRADO_SOL, EPG_TRAM_GRADO_AGEN));
        return all(sql);
    }

    @Override
    public CambioNotaMasBaja findByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(CambioNotaMasBaja.class, "cnmb")
                .join("alumno al", "tramite tra", "curso cu", "estadoTramite et")
                .left("resolucion re", "al.carrera car", "al.persona per", "per.tipoDocumento td")
                .filter("tra.id", tramite);

        return find(sql);
    }

}
