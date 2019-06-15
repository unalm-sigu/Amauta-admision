package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoDAO;

@Repository
public class CursoDirigidoDAOH extends AbstractEasyDAO<CursoDirigido> implements CursoDirigidoDAO {

    public CursoDirigidoDAOH() {
        super();
        setClazz(CursoDirigido.class);
    }

    @Override
    public CursoDirigido findByTramite(Tramite tramite) {
        Octavia sql = Octavia.query(CursoDirigido.class, "cd")
                .join("tramite tra")
                .filter("tra.id", tramite);

        return find(sql);
    }

    @Override
    public List<CursoDirigido> allByfacultades(DynatableFilter filters, Docente docente) {
        DynatableSql sql = new DynatableSql(filters)
                .from(CursoDirigido.class, "cd")
                .join("tramite tra", "facultad fac", "curso ", "docenteAsignado da", "estado")
                .join("tra.tipoTramite")
                .left("tra.alumno al", "al.persona per")
                .join("al.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "al.cicloActivo cia", "al.cicloIngreso ci", "al.modalidadEstudio me", "al.situacionAcademica situ")
                .leftJoin("per.paisNacer", "al.orientacionCarrera")
                .filter("da.id", docente);

        return all(sql);
    }

    @Override
    public void updateEstado(CursoDirigido cursoDirigido) {
        Octavia octavia = Octavia.update(CursoDirigido.class);
        octavia.set(cursoDirigido, "estado");
        this.update(octavia);
    }

}
