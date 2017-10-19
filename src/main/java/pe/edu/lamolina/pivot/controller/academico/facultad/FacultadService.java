package pe.edu.lamolina.pivot.controller.academico.facultad;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Facultad;

public interface FacultadService {

    List<Facultad> allFacultad(DynatableFilter filter);

    Facultad findFacultad(Long idFacultad);

    void save(Facultad facultad);

    void update(Facultad facultad);

    void delete(Facultad facultad);

    void estado(Facultad facultad);
}
