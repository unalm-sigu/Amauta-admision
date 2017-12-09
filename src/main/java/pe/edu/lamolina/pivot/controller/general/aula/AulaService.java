package pe.edu.lamolina.pivot.controller.general.aula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.Sede;
import pe.edu.lamolina.pivot.model.general.TipoAula;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AulaService {

    List<Aula> allByDynatable(DynatableFilter filter);

    List<TipoAula> allTiposAula();

    List<Aula> allAulasSuperioresByName(String nombre);

    List<Oficina> allOficinasByName(String nombre);

    List<Sede> allSedes();

    void save(Aula aula, Usuario usuario);

    void update(Aula aula, Usuario usuario);

    Aula findAulaById(Long id);

    void cambioEstado(Aula aula, DataSessionPivot ds);

}
