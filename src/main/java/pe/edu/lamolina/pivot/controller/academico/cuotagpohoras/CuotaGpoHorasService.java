package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotaGpoHoras;

public interface CuotaGpoHorasService {

    public List<CuotaGpoHoras> allCuotasGpoHoras(DynatableFilter filter, CicloAcademico cicloAcademico);

    
}
