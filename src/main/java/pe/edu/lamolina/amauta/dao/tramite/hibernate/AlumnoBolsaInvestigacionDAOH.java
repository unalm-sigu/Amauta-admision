package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.tramite.AlumnoBolsaInvestigacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;

@Repository
public class AlumnoBolsaInvestigacionDAOH extends AbstractEasyDAO<AlumnoBolsaInvestigacion> implements AlumnoBolsaInvestigacionDAO {

    public AlumnoBolsaInvestigacionDAOH() {
        super();
        setClazz(AlumnoBolsaInvestigacion.class);
    }

    @Override
    public List<AlumnoBolsaInvestigacion> allByBolsaInvestigacion(BolsaInvestigacion bi) {
         Octavia sql = Octavia.query(AlumnoBolsaInvestigacion.class, "abi")
                .join("bolsaInvestigacion bi", "alumno a")
                .filter("bi.id", bi);
        
        return all(sql);
    }

    @Override
    public List<AlumnoBolsaInvestigacion> allByDynatableBolsaInvestigacion(DynatableFilter filter, BolsaInvestigacion bi) {
        
        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoBolsaInvestigacion.class, "abi")
                .join("abi.supervisor s", "abi.alumno a", "abi.bolsaInvestigacion bi")
                .filter("bi.id", bi)
                .orderBy("abi.id desc");

        return all(sql);
    }

    @Override
    public AlumnoBolsaInvestigacion findByBolsaInvestigacionAlumno(BolsaInvestigacion bi, Alumno alumno) {
        Octavia sql = Octavia.query(AlumnoBolsaInvestigacion.class, "abi")
                .join("bolsaInvestigacion bi", "alumno a")
                .filter("bi.id", bi)
                .filter("a.id", alumno);
        
        return find(sql);
    }

}
