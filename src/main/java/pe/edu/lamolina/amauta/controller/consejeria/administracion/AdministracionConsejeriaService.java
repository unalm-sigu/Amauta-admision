package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;

public interface AdministracionConsejeriaService {

    public List<ConsejeriaHistorial> allConsejeriaHistorialByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<CicloAcademico> allCiclo();

    public void clonar(ClonarConsejerosDTO clonarDTO);

}
