package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TramiteRetiroCicloService {

    public List<CicloAcademico> allCiclos(CicloAcademico academico);

    public List<RetiroCiclo> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

    public void save(RetiroCiclo retiroCiclo, DataSessionPivot ds);

    public MatriculaResumen update(RetiroCiclo retiroCiclo, DataSessionPivot ds);

    public Parametro findParametro();

}
