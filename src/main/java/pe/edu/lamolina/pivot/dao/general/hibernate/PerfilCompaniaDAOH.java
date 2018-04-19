package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.general.Oficina;
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

    @Override
    public List<PerfilCompania> allTipoCargoByOfi(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .join("oficinaContiene ofi")
                .filter("ofi.id", oficina)
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO.name());

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allTipoCargo() {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .isNull("oficinaContiene")
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO.name());

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allTipoFuncion() {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("tipo", TipoPerfilCompaniaEnum.PERFIL.name());

        return all(sql);
    }

    @Override
    public PerfilCompania findUltimoCodigo() {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO.name())
                .filter("esAutomatico", 1)
                .orderBy("id desc")
                .limit(1);

        return find(sql);
    }
}
