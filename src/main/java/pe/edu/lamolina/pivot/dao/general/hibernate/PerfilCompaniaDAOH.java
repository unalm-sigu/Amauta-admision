package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.general.PerfilCompania;

@Repository
public class PerfilCompaniaDAOH extends AbstractEasyDAO<PerfilCompania> implements PerfilCompaniaDAO {

    public PerfilCompaniaDAOH() {
        super();
        setClazz(PerfilCompania.class);
    }

    @Override
    public List<PerfilCompania> allByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO.name())
                .like("pc.nombreDocumento", nombre)
                .orderBy("pc.nombreDocumento")
                .limit(15);

        return all(sql);
    }
}
