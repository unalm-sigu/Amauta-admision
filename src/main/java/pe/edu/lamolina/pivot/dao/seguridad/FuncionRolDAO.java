package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Rol;

public interface FuncionRolDAO extends EasyDAO<FuncionRol> {

    List<FuncionRol> allByPerfilCompania(List<PerfilCompania> perfilCompania);

    public List<FuncionRol> allFuncionRol(Rol rol);

    public FuncionRol findByRolPerfilCompania(FuncionRol funcionRol);

    public FuncionRol find(FuncionRol funcionRol);

    public List<FuncionRol> allFuncionRolTipoPerfil(FuncionRol funcionRol);

    public List<FuncionRol> allFuncionRolByRoles(List<Rol> roles);

    public List<FuncionRol> allFuncionRolActivoByRoles(List<Rol> roles);
}
