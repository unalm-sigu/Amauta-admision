package pe.edu.lamolina.pivot.controller.general.oficina;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;

public interface OficinaService {

    List<Oficina> allByDynatable(DynatableFilter filter, Compania compania);

    Oficina find(Oficina oficina);

    void update(Oficina oficina);

    void save(Oficina oficina);

    void delete(Oficina oficina);

    List<Colaborador> allColaborador(List<Oficina> oficinas);

}
