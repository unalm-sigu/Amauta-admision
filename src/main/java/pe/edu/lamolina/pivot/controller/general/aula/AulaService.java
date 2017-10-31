package pe.edu.lamolina.pivot.controller.general.aula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.Sede;
import pe.edu.lamolina.pivot.model.general.TipoAula;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface AulaService {

    List<Aula> allByDynatable(DynatableFilter filter);

    List<TipoAula> allTiposAula();

    List<Aula> allAulasSuperioresByName(String nombre);

    List<Sede> allSedesByName(String nombre);

    List<Oficina> allOficinasByName(String nombre);

    void save(Aula aula, Usuario usuario);

    Aula find(Long id);

    void cambioEstado(Aula aula);

}
