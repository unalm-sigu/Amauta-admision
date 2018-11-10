package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;

@Repository
public class AlumnoGrupoRegularDAOH extends AbstractEasyDAO<AlumnoGrupoRegular> implements AlumnoGrupoRegularDAO {

    public AlumnoGrupoRegularDAOH() {
        super();
        setClazz(AlumnoGrupoRegular.class);
    }

    @Override
    public List<AlumnoGrupoRegular> allByLetraGrupoActives(LetraGrupoRegular letraGrupoRegular) {
        return this.allByLetraGrupoAndEstado(letraGrupoRegular, AlumnoRolExamenEstadoEnum.ACT);
    }

    @Override
    public List<AlumnoGrupoRegular> allByLetraGrupoAndEstado(LetraGrupoRegular letraGrupoRegular, AlumnoRolExamenEstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(AlumnoGrupoRegular.class, "agr")
                .join("letraGruposRegulares gs", "userRegistro cur")
                .filter("agr.estado", estadoEnum)
                .filter("gs.id", letraGrupoRegular);
        return all(sql);
    }

}
