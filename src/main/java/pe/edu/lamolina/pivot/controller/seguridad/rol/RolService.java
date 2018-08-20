package pe.edu.lamolina.pivot.controller.seguridad.rol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface RolService {

    void save(Rol rol);

    void update(Rol rol);

    List<Rol> allRol();

    List<Menu> allMenuSystemByRol(Sistema sistema, Long idRol);

    void delete(Rol rol);

    Rol findRol(Rol rol);

    List<Rol> allRolByDynatable(DynatableFilter filter);

    void cambiarEstado(FuncionRol funcionRol, Usuario usuario);

    List<PerfilCompania> allPerfilCompaniaByTipo(PerfilCompania perfilCompania, Compania compania);

    List<FuncionRol> allFuncionRolTipoPerfil(FuncionRol funcionRol);

    List<FuncionRol> allFuncionRol(List<Rol> roles);

    ArrayNode allPerfilCompania(Rol rol, Map<Long, List<FuncionRol>> funcionesRolMap, TipoPerfilCompaniaEnum tipoPerfilCompaniaEnum);

    void saveFuncionRol(FuncionRol funcionRol, Usuario usuario);

    List<Rol> allRolSuperior(String nombre);

}
