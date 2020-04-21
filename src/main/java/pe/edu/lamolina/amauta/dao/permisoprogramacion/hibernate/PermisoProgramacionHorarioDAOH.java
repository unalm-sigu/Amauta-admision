package pe.edu.lamolina.amauta.dao.permisoprogramacion.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;
import pe.edu.lamolina.amauta.dao.permisoprogramacion.PermisoProgramacionHorariosDAO;

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
                .join("colaboradorAnexo ca", "permisoProgramacion pp", "ca.colaborador col", "ca.anexoBoletin")
                .filter("pph.estado", ACT)
                .in("col.id", colaboradors);

        return all(sql);
    }

    @Override
    public List<PermisosProgramacionHorarios> allByNivelPermiso(PermisoProgramacionNivelEnum nivelEnum, Long idColaboradorAnexo) {
        Octavia sql = new Octavia()
                .from(PermisosProgramacionHorarios.class, "pph")
                .join("colaboradorAnexo ca", "permisoProgramacion pp", "ca.colaborador col", "ca.anexoBoletin")
                .filter("pph.estado", ACT)
                .filter("pp.nivel", nivelEnum)
                .filter("ca.id", idColaboradorAnexo);

        return all(sql);
    }

    @Override
    public PermisosProgramacionHorarios findByColaborador(Long id, PermisoProgramacion permisoProgramacion) {
        Octavia sql = new Octavia()
                .from(PermisosProgramacionHorarios.class, "pph")
                .join("colaboradorAnexo ca", "permisoProgramacion pp", "ca.colaborador col", "ca.anexoBoletin")
                .filter("ca.id", id)
                .filter("pp.id", permisoProgramacion);

        return find(sql);
    }

}
