package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.FuncionRolEstadoEnum;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.pivot.dao.seguridad.FuncionRolDAO;

@Repository
public class FuncionRolDAOH extends AbstractEasyDAO<FuncionRol> implements FuncionRolDAO {

    public FuncionRolDAOH() {
        super();
        setClazz(FuncionRol.class);
    }

    @Override
    public List<FuncionRol> allByPerfilCompania(List<PerfilCompania> perfilCompania) {

        Octavia sql = Octavia.query()
                .from(FuncionRol.class, "fr")
                .join("rol r", "perfilCompania p")
                .filter("fr.estado", FuncionRolEstadoEnum.ACT)
                .in("p.id", perfilCompania);

        return all(sql);

    }

    @Override
    public List<FuncionRol> allCargoRol(Rol rol) {

        Octavia sql = Octavia.query()
                .from(FuncionRol.class, "fr")
                .join("rol r", "perfilCompania p")
                .filter("p.tipo", TipoPerfilCompaniaEnum.CARGO.name())
                .filter("r.id", rol);
        return all(sql);
    }

    @Override
    public List<FuncionRol> allFuncionRol(Rol rol) {

        Octavia sql = Octavia.query()
                .from(FuncionRol.class, "fr")
                .join("rol r", "perfilCompania p")
                .filter("p.tipo", TipoPerfilCompaniaEnum.PERFIL.name())
                .filter("r.id", rol);
        return all(sql);
    }

    @Override
    public FuncionRol findByRolPerfilCompania(FuncionRol funcionRol) {
        Octavia sql = Octavia.query()
                .from(FuncionRol.class, "fr")
                .join("rol r", "perfilCompania p")
                .filter("p.id", funcionRol.getPerfilCompania())
                .filter("r.id", funcionRol.getRol());
        return find(sql);
    }

    @Override
    public FuncionRol find(FuncionRol funcionRol) {
        Octavia sql = Octavia.query()
                .from(FuncionRol.class, "fr")
                .join("rol r", "perfilCompania p")
                .filter("fr.id", funcionRol.getId());
        return find(sql);
    }

    @Override
    public List<FuncionRol> allFuncionRolTipoPerfil(FuncionRol funcionRol) {
        Octavia sql = Octavia.query()
                .from(FuncionRol.class, "fr")
                .join("rol r", "perfilCompania p")
                .filter("p.tipo", funcionRol.getPerfilCompania().getTipo())
                .filter("r.id", funcionRol.getRol());
        return all(sql);
    }

    @Override
    public List<FuncionRol> allFuncionRolByRoles(List<Rol> roles) {
        Octavia sql = Octavia.query()
                .from(FuncionRol.class, "fr")
                .join("rol r", "perfilCompania p")
                .in("r.id", roles);
        return all(sql);
    }

}
