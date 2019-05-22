package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoAulaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.TipoAula;

public interface AulaDAO extends EasyDAO<Aula> {

    Aula findByCode(String codigo);

    Aula findActiveByCode(String code);

    List<Aula> allByDynatable(DynatableFilter filter);

    Integer findAforoByEdificio(Aula aula);

    List<Aula> allAulasSuperioresByName(String forLike);

    List<Aula> allByAulaSuperior(Aula aula);

    List<Aula> allByAulasSuperiores(List<Aula> aulas);

    Aula find(Long id);

    List<Aula> allPabellonesByOficina(Oficina oficina);

    List<Aula> allPabellonesByOficina(OficinaEnum oficinaEnum);

    List<Aula> allByPabellon(Aula aula);

    List<Aula> allAulasSuperiorByTipoOficina(TipoOficinaEnum tipoOficinaEnum);

    List<Aula> allPabellonesByOficinasNoOera(List<Oficina> oficinas);

    List<Aula> searchByNombreFilter(String nombre, Integer limit);

    List<Aula> allByOficinaModulo(Oficina oficina, Aula modulo);

    List<Aula> allByDynatableFilterTramite(DynatableFilter filter, List<Aula> aulasNoIncluidas, Oficina oficina);

    List<Aula> allAulaModuloByName(String nombre, Integer limit, Oficina oficina);

    List<Aula> allByOficinaSupervisora(Oficina oficinaEstudios);

    List<Aula> allByEstado(EstadoEnum... estadoEnum);

    List<Aula> allByTipoAula(TipoAulaEnum tipoAulaEnum);

    List<Aula> allByOficinaSupervisora(OficinaEnum oficinaEnum);

    List<Aula> allByOficinaSupervisora(OficinaEnum oficinaEnum, EstadoEnum estadoEnum);

    Aula findAulaMaxAforo(OficinaEnum oficinaEnum, EstadoEnum estadoEnum);

}
