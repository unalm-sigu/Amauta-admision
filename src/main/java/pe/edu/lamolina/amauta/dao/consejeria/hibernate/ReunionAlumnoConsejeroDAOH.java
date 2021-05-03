package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ReunionAlumnoConsejeroDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import static pe.edu.lamolina.model.enums.ReunionAlumnoConsejeroEstadoEnum.AGEN;

@Service
public class ReunionAlumnoConsejeroDAOH extends AbstractEasyDAO<ReunionAlumnoConsejero> implements ReunionAlumnoConsejeroDAO {
    
    public ReunionAlumnoConsejeroDAOH() {
        super();
        setClazz(ReunionAlumnoConsejero.class);
    }
    
    @Override
    public int saveList(List<ReunionAlumnoConsejero> reunionAlumnoConsejeros) {
        if (reunionAlumnoConsejeros.isEmpty()) {
            return 0;
        }
        
        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(ReunionAlumnoConsejero.class)
                .columns("estado", "alumnoConsejero", "agendaConsejero", "fechaRegistro", "userRegistro")
                .values(reunionAlumnoConsejeros);
        
        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();
        
        long t2 = System.currentTimeMillis();
        return rows;
    }
    
    @Override
    public List<ReunionAlumnoConsejero> allByAgendaConsejero(AgendaConsejero agendaConsejeroForm) {
        Octavia sql = new Octavia()
                .from(ReunionAlumnoConsejero.class, "rac")
                .join("agendaConsejero ac")
                .filter("rac.estado", AGEN)
                .filter("ac.id", agendaConsejeroForm);
        
        return all(sql);
    }
    
    @Override
    public List<ReunionAlumnoConsejero> allDynatableByConsejero(DynatableFilter filter, Consejero consejero, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ReunionAlumnoConsejero.class, "rac")
                .join("alumnoConsejero ac", "agendaConsejero acon", "ac.cicloAcademico ca")
                .join("ac.alumno al", "al.persona per", "per.tipoDocumento")
                .join("ac.consejero con", "acon.hora")
                .join("al.carrera car", "car.facultad")
                .searchFields("acon.titulo", "al.codigo", "per.numeroDocIdentidad", "acon.fecha")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", cicloAcademico)
                .filter("con.id", consejero)
                .orderBy("acon.fecha", "acon.hora");
        
        return all(sql);
    }
    
}
