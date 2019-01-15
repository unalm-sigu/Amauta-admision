package pe.edu.lamolina.pivot.dao.bienestar.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.pivot.dao.bienestar.ReservaAulaDAO;

@Repository
public class ReservaAulaDAOH extends AbstractEasyDAO<ReservaAula> implements ReservaAulaDAO {

    public ReservaAulaDAOH() {
        super();
        setClazz(ReservaAula.class);
    }

    @Override
    public List<ReservaAula> allDynatableFilter(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(ReservaAula.class, "ra")
                .join("tramite tra", "tra.tipoTramite", "tra.cicloAcademico ca")
                .leftJoin("tra.compania cia", "tra.empresa em", "tra.docente doc", "tra.alumno al")
                .leftJoin("al.persona per", "doc.persona perr")
                .orderBy("ra.id desc");

        return all(sql);
    }

}
