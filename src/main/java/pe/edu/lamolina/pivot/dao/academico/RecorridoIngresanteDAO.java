package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;

public interface RecorridoIngresanteDAO extends EasyDAO<RecorridoIngresante> {

    List<RecorridoIngresante> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo);

    List<RecorridoIngresante> allByCiclo( CicloAcademico ciclo);

    List<RecorridoIngresante> allByDynatableCicloTurno(DynatableFilter filter, CicloAcademico ciclo, TurnoEntrevistaObuae turno);
    
    List<RecorridoIngresante> allIngresantesDynatableByPersona(DynatableFilter filter, List<Persona> personas);
    

}
