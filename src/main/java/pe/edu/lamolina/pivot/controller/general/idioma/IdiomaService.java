package pe.edu.lamolina.pivot.controller.general.idioma;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface IdiomaService {

    List<Idioma> allByDynatable(DynatableFilter filter);

    void save(Idioma idioma, DataSessionPivot ds);

    void update(Idioma idioma, DataSessionPivot ds);

}
