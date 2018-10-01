package pe.edu.lamolina.pivot.controller.posgrado.tarifa;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TarifaService {

    void save(TarifaCarrera tarifaCarrera, DataSessionPivot ds);

    void clonar(TarifaCarrera tarifaCarrera, DataSessionPivot ds);

    TarifaCarrera find(Long id);

    void update(TarifaCarrera tarifaCarrera, DataSessionPivot ds);

    void eliminar(TarifaCarrera tarifaCarrera, DataSessionPivot ds);

    void activar(TarifaCarrera tarifaCarrera, DataSessionPivot ds);

    List<TarifaCarrera> allByDynatable(DynatableFilter filter);

    public List<Carrera> allCarreraMaestria();

    public List<CicloAcademico> allCicloAcademico();
    
    public List<ConceptoPosgrado> allConceptoPosgrado();

}
