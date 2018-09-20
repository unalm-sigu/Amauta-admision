package pe.edu.lamolina.pivot.dao.permisoprogramacion.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;
import pe.edu.lamolina.pivot.dao.permisoprogramacion.PermisoProgramacionHorariosDAO;

@Repository
public class PermisoProgramacionHorarioDAOH extends AbstractEasyDAO<PermisosProgramacionHorarios> implements PermisoProgramacionHorariosDAO {

    public PermisoProgramacionHorarioDAOH() {
        super();
        setClazz(PermisosProgramacionHorarios.class);
    }

    @Override
    public List<PermisosProgramacionHorarios> allPermisos(List<Colaborador> colaboradors) {
        Octavia sql = new Octavia()
                .from(PermisosProgramacionHorarios.class, "pph")
                .join("colaboradorAnexo ca", "permisoProgracion pp","ca.colaborador col","ca.anexoBoletin")
                .filter("pph.estado", ACT)
                .in("col.id", colaboradors);

        return all(sql);
    }

}
