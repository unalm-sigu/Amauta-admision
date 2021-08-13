package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.tramite.AlumnoBolsaInvestigacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.AlumnoBolsaInvestigacionEstadoEnum;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;

@Repository
public class AlumnoBolsaInvestigacionDAOH extends AbstractEasyDAO<AlumnoBolsaInvestigacion> implements AlumnoBolsaInvestigacionDAO {

    public AlumnoBolsaInvestigacionDAOH() {
        super();
        setClazz(AlumnoBolsaInvestigacion.class);
    }

    @Override
    public AlumnoBolsaInvestigacion find(long id) {
        Octavia sql = Octavia.query(AlumnoBolsaInvestigacion.class, "abi")
                .join("supervisor s", "alumno a", "bolsaInvestigacion bi")
                .join("tramiteSubvencion tsub", "tsub.tramite tra", "tra.tipoTramite", "tsub.tipoSubvencion")
                .filter("abi.estado", "<>", AlumnoBolsaInvestigacionEstadoEnum.ANU)
                .filter("abi.id", id);

        return find(sql);
    }

    @Override
    public List<AlumnoBolsaInvestigacion> allByBolsaInvestigacion(BolsaInvestigacion bolsa) {
        Octavia sql = Octavia.query(AlumnoBolsaInvestigacion.class, "abi")
                .join("supervisor s", "alumno a", "bolsaInvestigacion bi")
                .join("tramiteSubvencion tsub", "tsub.tramite tra", "tra.tipoTramite", "tsub.tipoSubvencion")
                .filter("abi.estado", "<>", AlumnoBolsaInvestigacionEstadoEnum.ANU)
                .filter("bi.id", bolsa);

        return all(sql);
    }

    @Override
    public List<AlumnoBolsaInvestigacion> allByDynatableBolsaInvestigacion(DynatableFilter filter, BolsaInvestigacion bolsa) {

        DynatableSql sql = new DynatableSql(filter)
                .from(AlumnoBolsaInvestigacion.class, "abi")
                .join("supervisor s", "alumno a", "bolsaInvestigacion bi")
                .join("tramiteSubvencion tsub", "tsub.tramite tra", "tra.tipoTramite", "tsub.tipoSubvencion")
                .filter("bi.id", bolsa)
                //.filter("abi.estado", "<>", AlumnoBolsaInvestigacionEstadoEnum.ANU)
                .orderBy("abi.id desc");

        return all(sql);
    }

    @Override
    public AlumnoBolsaInvestigacion findByBolsaInvestigacionAlumno(BolsaInvestigacion bolsa, Alumno alumno) {
        Octavia sql = Octavia.query(AlumnoBolsaInvestigacion.class, "abi")
                .join("supervisor s", "alumno a", "bolsaInvestigacion bi")
                .join("tramiteSubvencion tsub", "tsub.tramite tra", "tra.tipoTramite", "tsub.tipoSubvencion")
                .filter("abi.estado", "<>", AlumnoBolsaInvestigacionEstadoEnum.ANU)
                .filter("bi.id", bolsa)
                .filter("a.id", alumno);

        return find(sql);
    }

}
