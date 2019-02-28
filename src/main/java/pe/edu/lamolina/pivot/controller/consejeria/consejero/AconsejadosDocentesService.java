package pe.edu.lamolina.pivot.controller.consejeria.consejero;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AconsejadosDocentesService {

    public List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, Persona persona);

    public AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico cicloAcademico);

    void matriculaAutorizacion(MatriculaResumen matriculaResumen, DataSessionPivot ds);
}
