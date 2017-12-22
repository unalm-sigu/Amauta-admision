package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.enums.TipoOficinaEnum;

public interface AulaDAO extends Crud<Aula> {

    Aula findByCode(String codigo);

    List<Aula> allByDynatable(DynatableFilter filter);

    Integer findAforoByEdificio(Aula aula);

    List<Aula> allAulasSuperioresByName(String forLike);

    List<Aula> allByAulaSuperior(Aula aula);

    Aula find(Long id);

    List<Aula> allAulasSuperiorByOficina(Oficina oficina);

    List<Aula> allBySuperior(Aula aula);

    List<Aula> allAulasSuperiorByTipoOficina(TipoOficinaEnum tipoOficinaEnum);

    List<Aula> allSuperiorByOficinaWithAulas(List<Oficina> oficinas);

    List<Aula> searchByNombreFilter(String nombre, Integer limit);

}
