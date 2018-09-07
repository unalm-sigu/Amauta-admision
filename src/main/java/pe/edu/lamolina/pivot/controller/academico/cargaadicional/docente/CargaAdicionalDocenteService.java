package pe.edu.lamolina.pivot.controller.academico.cargaadicional.docente;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.DocenteCiclo;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CargaAdicionalDocenteService {

    List<DocenteCiclo> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    public void eliminarCarga(CicloAcademico cicloAcademico, DataSessionPivot ds);

    public void eliminarMontos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    ConfiguraCargaAdicional findConfiguracionByCicloAcademico(CicloAcademico cicloAcademico);

    public void generarCarga(CicloAcademico cicloAcademico, DataSessionPivot ds);

    public void generarMontos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void saveConfiguracion(ConfiguraCargaAdicional configuraCargaAdicional, CicloAcademico cicloAcademico, DataSessionPivot ds);

}
