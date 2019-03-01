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
import pe.edu.lamolina.model.tramite.RetiroCiclo;

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
    public RetiroCiclo findByAlumno(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloRegistro cr", "cicloAcademico")
                .filter("al.id", alumno)
                .filter("cr.id", ciclo);
        
        return find(sql);
    }
    
    @Override
    public List<RetiroCiclo> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = new Octavia()
                .from(RetiroCiclo.class, "rc")
                .join("alumno al", "cicloAcademico ca", "cicloRegistro cr")
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
}
