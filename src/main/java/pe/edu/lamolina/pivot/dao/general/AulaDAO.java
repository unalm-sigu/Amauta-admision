package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;

public interface AulaDAO extends EasyDAO<Aula> {

    Aula findByCode(String codigo);

    List<Aula> allByDynatable(DynatableFilter filter);

    Integer findAforoByEdificio(Aula aula);

    List<Aula> allAulasSuperioresByName(String forLike);

    List<Aula> allByAulaSuperior(Aula aula);

    List<Aula> allByAulasSuperiores(List<Aula> aulas);

    Aula find(Long id);

    List<Aula> allAulasSuperiorByOficina(Oficina oficina);

    List<Aula> allBySuperior(Aula aula);

    List<Aula> allAulasSuperiorByTipoOficina(TipoOficinaEnum tipoOficinaEnum);

    List<Aula> allSuperiorByOficinaWithAulas(List<Oficina> oficinas);

    List<Aula> searchByNombreFilter(String nombre, Integer limit);

}
