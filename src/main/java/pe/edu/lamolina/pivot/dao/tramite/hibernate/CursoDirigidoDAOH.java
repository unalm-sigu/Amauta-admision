package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import static pe.edu.lamolina.model.enums.EstadoTramiteEnum.SOL_ANU;
import static pe.edu.lamolina.model.enums.EstadoTramiteEnum.SOL_CUR_DIR;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Resolucion;
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
                .join("tramite tra", "tra.tipoTramite", "curso cur")
                .left("docenteAsignado da", "cur.departamentoAcademico", "da.persona")
                .filter("tra.id", tramite);
        
        return find(sql);
    }
    
    @Override
    public List<CursoDirigido> allByfacultades(DynatableFilter filters, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filters)
                .from(CursoDirigido.class, "cd")
                .join("tramite tra", "curso ", "estado")
                .join("tra.tipoTramite")
                .left("tra.alumno al", "tra.cicloAcademico ca", "al.persona per")
                .join("al.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "al.cicloActivo cia", "al.cicloIngreso ci", "al.modalidadEstudio me", "al.situacionAcademica situ")
                .leftJoin("per.paisNacer", "al.orientacionCarrera")
                .filter("ca.id", ciclo);
        sql.beginRelativeFilters();
        this.setFacultad(filters, sql);
        return all(sql);
    }
    
    private void setFacultad(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        
        for (String key : queries.keySet()) {
            if (!key.equals("facultad-dirigido")) {
                continue;
            }
            
            String values = (String) queries.get(key);
            if (values != null) {
                sql.filter("fa.id", values);
            }
        }
    }
    
    @Override
    public void updateEstado(CursoDirigido cursoDirigido) {
        Octavia octavia = Octavia.update(CursoDirigido.class);
        octavia.set(cursoDirigido, "estado");
        this.update(octavia);
    }
    
    @Override
    public List<CursoDirigido> allByTramites(List<Tramite> tramites) {
        Octavia sql = Octavia.query(CursoDirigido.class, "cd")
                .left("tramite tra", "docenteAsignado doc", "estado", "facultad")
                .left("doc.persona", "doc.departamentoAcademico")
                .in("tra.id", tramites);
        
        return all(sql);
    }
    
    @Override
    public List<CursoDirigido> allByResolucion(DynatableFilter filter, Resolucion resolucion) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoDirigido.class, "cd")
                .join("tramite tra", "facultad fac", "curso ", "docenteAsignado da", "estado")
                .join("tra.tipoTramite")
                .left("tra.alumno al", "al.persona per", "userRegistro ur", "ur.persona")
                .join("al.carrera car", "car.facultad fa", "resolucion res")
                .leftJoin("per.tipoDocumento td", "al.cicloActivo cia", "al.cicloIngreso ci", "al.modalidadEstudio me", "al.situacionAcademica situ")
                .leftJoin("per.paisNacer", "al.orientacionCarrera")
                .filter("res.id", resolucion);
        
        return all(sql);
    }
    
    @Override
    public List<CursoDirigido> allByResolucion(Resolucion resolucion) {
        Octavia sql = new Octavia()
                .from(CursoDirigido.class, "cd")
                .join("tramite tra", "facultad fac", "curso ", "docenteAsignado da", "estado")
                .join("tra.tipoTramite")
                .left("tra.alumno al", "al.persona per", "userRegistro ur", "ur.persona")
                .join("al.carrera car", "car.facultad fa", "resolucion res")
                .leftJoin("per.tipoDocumento td", "al.cicloActivo cia", "al.cicloIngreso ci", "al.modalidadEstudio me", "al.situacionAcademica situ")
                .leftJoin("per.paisNacer", "al.orientacionCarrera")
                .filter("res.id", resolucion);
        
        return all(sql);
    }
    
    @Override
    public List<CursoDirigido> allByCicloAcademicoSol(CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(CursoDirigido.class, "cd")
                .join("tramite tra", "facultad fac", "curso ", "docenteAsignado da", "estado es")
                .join("tra.tipoTramite")
                .left("tra.alumno al", "tra.cicloAcademico ca", "userRegistro ur")
                .filter("es.codigo", SOL_CUR_DIR.name())
                .filter("ca.id", cicloAcademico);
        
        return all(sql);
    }
    
    @Override
    public List<CursoDirigido> allByfacultades(Facultad facultad) {
        Octavia sql = new Octavia()
                .from(CursoDirigido.class, "cd")
                .join("tramite tra", "curso ", "estado es")
                .join("tra.tipoTramite")
                .left("tra.alumno al", "al.persona per")
                .join("al.carrera car", "car.facultad fa")
                .leftJoin("per.tipoDocumento td", "al.cicloActivo cia", "al.cicloIngreso ci", "al.modalidadEstudio me", "al.situacionAcademica situ")
                .leftJoin("per.paisNacer", "al.orientacionCarrera")
                .filter("es.codigo", "!=", SOL_ANU.name()
                )
                .filter("fa.id", facultad);
        
        return all(sql);
    }
    
}
