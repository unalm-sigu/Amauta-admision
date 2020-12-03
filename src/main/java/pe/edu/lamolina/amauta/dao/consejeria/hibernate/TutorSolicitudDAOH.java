package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TutorSolicitudDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.TutorSolicitud;

@Service
public class TutorSolicitudDAOH extends AbstractEasyDAO<TutorSolicitud> implements TutorSolicitudDAO {

    public TutorSolicitudDAOH() {
        super();
        setClazz(TutorSolicitud.class);
    }

    @Override
    public List<TutorSolicitud> allTutorSolicitudByFilter(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TutorSolicitud.class, "ts")
                .join("alumnoConsejero ac")
                .join("ac.alumno alu", "ac.cicloAcademico ca", "ac.consejero co")
                .join("alu.persona per", "per.tipoDocumento","alu.carrera car","car.facultad","co.colaborador col", "col.persona")
                .leftJoin("usuarioRegistra", "usuarioVerifica")
                .searchFields("alu.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", ciclo)
                .orderBy("ts.id");
        return all(sql);
    }

}
