package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTrasladoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.TRAS_INT;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;
import pe.edu.lamolina.model.tramite.Tramite;

@Repository
public class TramiteTrasladoDAOH extends AbstractEasyDAO<TramiteTraslado> implements TramiteTrasladoDAO {

    public TramiteTrasladoDAOH() {
        super();
        setClazz(TramiteTraslado.class);
    }

    @Override
    public List<TramiteTraslado> allByResolucion(Resolucion resolucion) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "tra.tipoTramite", "resolucion res")
                .left("tra.cicloAcademico cic", "tra.alumno al", "al.persona per", "userRegistro ur", "ur.persona per1")
                .left("carrera ", "carreraOrigen")
                .join("al.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "per1.tipoDocumento ", "al.cicloActivo cia", "al.cicloIngreso ci", "al.modalidadEstudio me", "al.situacionAcademica situ")
                .leftJoin("per.paisNacer", "al.orientacionCarrera")
                .filter("res.id", resolucion);

        return all(sql);
    }

    @Override
    public List<TramiteTraslado> allByAlumno(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "resolucion res", "cicloAcademico cic")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .left("carrera ", "carreraOrigen")
                .filter("al.id", alumno)
                .filter("estado", ACEP)
                .orderBy("tras.id desc");

        return all(sql);
    }

    @Override
    public List<TramiteTraslado> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "cicloAcademico cic")
                .left("carrera ", "carreraOrigen", "resolucion res")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .searchFields("al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("cic.id", cicloAcademico)
                .orderBy("tras.id desc");

        return all(sql);
    }

    @Override
    public TramiteTraslado findByAlumnoCiclo(Alumno alumnoDB, CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "cicloAcademico cic")
                .left("carrera ", "carreraOrigen", "resolucion res")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .filter("al.id", alumnoDB)
                .filter("cic.id", cicloAcademico)
                .orderBy("tras.id desc");

        return find(sql);
    }

    @Override
    public TramiteTraslado findByTramite(Tramite tramite) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "cicloAcademico cic")
                .left("carrera car", "car.facultad", "carreraOrigen car2", "car2.facultad", "resolucion res")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .filter("tra.id", tramite)
                .orderBy("tras.id desc");

        return find(sql);
    }

    @Override
    public List<TramiteTraslado> findByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "cicloAcademico cic")
                .left("carrera car", "car.facultad", "carreraOrigen car2", "car2.facultad", "resolucion res")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .filter("cic.id", cicloAcademico)
                .filter("tras.estado", ACEP)
                .filter("tras.tipoTraslado", TRAS_INT)
                .orderBy("tras.id desc");

        return all(sql);
    }

    @Override
    public TramiteTraslado findAll(Long idTramiteTraslado) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "cicloAcademico cic")
                .left("carrera car", "car.facultad", "carreraOrigen car2", "car2.facultad", "resolucion res")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .filter("tras.id", idTramiteTraslado);

        return find(sql);
    }

    @Override
    public TramiteTraslado findTramiteExistenteByAlumnoCiclo(Alumno alumnoDB, CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "cicloAcademico cic")
                .left("carrera ", "carreraOrigen", "resolucion res")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .filter("al.id", alumnoDB)
                .in("tras.estado",Arrays.asList(SOL,ACEP))
                .filter("cic.id", cicloAcademico)
                .orderBy("tras.id desc")
                .limit(1);

        return find(sql);
    }

}
