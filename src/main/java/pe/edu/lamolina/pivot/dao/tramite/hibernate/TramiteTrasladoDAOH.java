package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.pivot.dao.tramite.TramiteTrasladoDAO;

@Repository
public class TramiteTrasladoDAOH extends AbstractEasyDAO<TramiteTraslado> implements TramiteTrasladoDAO {

    public TramiteTrasladoDAOH() {
        super();
        setClazz(TramiteTraslado.class);
    }

    @Override
    public TramiteTraslado findByResolucion(Resolucion resolucion) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "tra.tipoTramite")
                .left("tra.cicloAcademico cic", "tra.alumno al", "al.persona per", "userRegistro ur", "ur.persona per")
                .join("al.carrera car", "car.facultad fa", "resolucion res")
                .leftJoin("per.tipoDocumento td", "al.cicloActivo cia", "al.cicloIngreso ci", "al.modalidadEstudio me", "al.situacionAcademica situ")
                .leftJoin("per.paisNacer", "al.orientacionCarrera")
                .filter("res.id", resolucion);

        return find(sql);
    }

    @Override
    public List<TramiteTraslado> allByAlumno(Alumno alumno) {
        Octavia sql = new Octavia()
                .from(TramiteTraslado.class, "tras")
                .join("tramite tra", "resolucion res", "cicloAcademico cic")
                .leftJoin("tra.alumno al", "res.tipoResolucion", "res.oficina", "userRegistro ur", "ur.persona per")
                .filter("al.id", alumno);

        return all(sql);
    }
}
