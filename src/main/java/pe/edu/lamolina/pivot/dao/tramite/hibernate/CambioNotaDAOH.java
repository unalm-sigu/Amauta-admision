package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.tramite.CambioNotaDAO;

@Repository
public class CambioNotaDAOH extends AbstractEasyDAO<CambioNota> implements CambioNotaDAO {
    
    public CambioNotaDAOH() {
        super();
        setClazz(CambioNota.class);
    }
    
    @Override
    public CambioNota findByTramite(Tramite tramiteForm) {
        
        Octavia sql = new Octavia()
                .from(CambioNota.class, "cn")
                .join("tramite tr", "curso", "alumno", "cicloRegistro", "cicloAcademico")
                .filter("tr.id", tramiteForm);
        
        return find(sql);
    }
    
    @Override
    public List<CambioNota> allByTramites(List<Tramite> tramites) {
        Octavia sql = new Octavia()
                .from(CambioNota.class, "cn")
                .join("tramite tr", "curso", "alumno", "cicloRegistro", "cicloAcademico")
                .in("tr.id", tramites);
        
        return all(sql);
    }
    
    @Override
    public List<CambioNota> allByResolucion(Resolucion resolucionDB) {
        Octavia sql = new Octavia()
                .from(CambioNota.class, "cn")
                .join("resolucion re", "tramite tr", "curso", "alumno al", "cicloRegistro", "cicloAcademico")
                .join("al.persona per ")
                .left("per.tipoDocumento")
                .filter("re.id", resolucionDB);
        
        return all(sql);
    }
    
}
