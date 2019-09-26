package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Rol;

public interface FuncionRolDAO extends EasyDAO<FuncionRol> {

    List<FuncionRol> allByPerfil(PerfilCompania cargoJefe);

    List<FuncionRol> allByPerfiles(List<PerfilCompania> perfilCompania);

    List<FuncionRol> allFuncionRol(Rol rol);

    FuncionRol findByRolPerfilCompania(FuncionRol funcionRol);

    FuncionRol find(FuncionRol funcionRol);

    List<FuncionRol> allFuncionRolTipoPerfil(FuncionRol funcionRol);

    List<FuncionRol> allFuncionRolByRoles(List<Rol> roles);

    List<FuncionRol> allFuncionRolActivoByRoles(List<Rol> roles);

}
