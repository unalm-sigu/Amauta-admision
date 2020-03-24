package pe.edu.lamolina.pivot.dao.academico;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;

public interface RecorridoIngresanteDAO extends EasyDAO<RecorridoIngresante> {

    List<RecorridoIngresante> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo);

    List<RecorridoIngresante> allAtendidosByDynatableCicloFecha(DynatableFilter filter, CicloAcademico ciclo, Date fecha);

    List<RecorridoIngresante> allByCiclo(CicloAcademico ciclo);

    List<RecorridoIngresante> allByDynatableCicloTurno(DynatableFilter filter, CicloAcademico ciclo, TurnoEntrevistaObuae turno);

    List<RecorridoIngresante> allIngresantesDynatableByPersona(DynatableFilter filter, List<Persona> personas);

    List<RecorridoIngresante> allConMuestaByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo);

    List<RecorridoIngresante> allConMuestaByDynatableFechaCiclo(DynatableFilter filter, Date fecha, CicloAcademico ciclo);

    List<RecorridoIngresante> allIngresantesByPersonas(List<Persona> personas);

    List<RecorridoIngresante> allConTurno(CicloAcademico ciclo);

    List<RecorridoIngresante> allConTurno(TurnoEntrevistaObuae turno, CicloAcademico ciclo);

    List<RecorridoIngresante> allAtendidos(Date fecha, CicloAcademico ciclo);

    List<RecorridoIngresante> allConMuestraByCiclo(CicloAcademico ciclo);

    List<RecorridoIngresante> allConMuestraByFechaCiclo(Date fecha, CicloAcademico ciclo);

    RecorridoIngresante findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    public void updateActividadesEjecutadas(CicloAcademico cicloAcademico);

    public void updateTotalActividades(CicloAcademico cicloAcademico);

}
