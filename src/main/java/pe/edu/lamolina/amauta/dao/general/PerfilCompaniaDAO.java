package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.PerfilColaboradorEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;

public interface PerfilCompaniaDAO extends EasyDAO<PerfilCompania> {

    List<PerfilCompania> allByNombre(String nombre);

    List<PerfilCompania> allTipoCargoByOfi(Oficina oficina);

    List<PerfilCompania> allTipoCargo();

    List<PerfilCompania> allTipoFuncion();

    PerfilCompania findUltimoCodigoCargo();

    List<PerfilCompania> allFuncion(String nombre, Compania compania);

    List<PerfilCompania> allCargo(String nombre, Compania compania);

    List<PerfilCompania> allPerfilCompaniaByTipo(PerfilCompania perfilCompania, Compania compania);

    PerfilCompania findFuncionByNombre(String nombre);

    PerfilCompania findCargoByNombre(String nombre);

    PerfilCompania findUltimoCodigoFuncion();

    List<PerfilCompania> allCargoByOficinaAltoPerfil(Oficina oficina);

    List<PerfilCompania> allCargoByOficina(Oficina oficina);

    List<PerfilCompania> allFuncionesByOficinaAltoPerfil(Oficina oficina);

    List<PerfilCompania> allFuncionesByOficina(Oficina oficina);

    PerfilCompania findByCodigo(PerfilColaboradorEnum perfilEnum);

    List<PerfilCompania> allCargosByContexto(String contexto);

    List<PerfilCompania> allFuncionesByContexto(String contexto);

}
