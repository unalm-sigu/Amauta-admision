package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.ResponsableAulaAsignacion;
import pe.edu.lamolina.pivot.dao.general.ResponsableAulaAsignacionDAO;

@Repository
public class ResponsableAulaAsignacionDAOH extends AbstractEasyDAO<ResponsableAulaAsignacion> implements ResponsableAulaAsignacionDAO {

    public ResponsableAulaAsignacionDAOH() {
        this.setClazz(ResponsableAulaAsignacion.class);
    }

    @Override
    public List<ResponsableAulaAsignacion> allByResponsable(List<ResponsableAula> responsables, EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(ResponsableAulaAsignacion.class, "raa")
                .join("responsableAula ra", "aula au", "turnoAtencionAula ta")
                .join("au.tipoAula tpa")
                .in("ra.id", responsables)
                .in("raa.estado", Arrays.asList(estados));
        return all(sql);
    }

    @Override
    public List<ResponsableAulaAsignacion> allByAulas(List<Aula> aulas, EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(ResponsableAulaAsignacion.class, "raa")
                .join("responsableAula ra", "ra.persona per", "aula au", "turnoAtencionAula ta")
                .in("au.id", aulas)
                .in("raa.estado", Arrays.asList(estados));
        return all(sql);
    }

    @Override
    public List<ResponsableAulaAsignacion> allByEstado(EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(ResponsableAulaAsignacion.class, "ras")
                .join("turnoAtencionAula ta", "aula au", "responsableAula ra", "ra.persona per")
                .in("ra.estado", Arrays.asList(estados));
        return all(sql);
    }

}
