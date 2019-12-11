package pe.edu.lamolina.pivot.controller.academico.preciocursociclo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PrecioCursoCicloService {

    List<CursoCicloAcademico> allCursoCiclo(DynatableFilter filter, CicloAcademico cicloAcademico);

    void save(List<CursoCicloAcademico> precioCursoCiclos, CicloAcademico cicloAcademico, DataSessionPivot ds);

    void configurarcantidad(CantidadAlumno cantidadAlumno, CicloAcademico cicloAcademico);

    List<TipoCarpeta> allTipoCarpeta();

    void update(CursoCicloAcademico cursoCicloAcademico, DataSessionPivot ds);

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);
}
