package pe.edu.lamolina.pivot.controller.ingresante.resultadoslab;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.DiarioLaboratorio;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;

public interface ResultadosLabService {

    CicloAcademico findCicloActivoAdmision();

    List<TurnoEntrevistaObuae> allTurnos(CicloAcademico ciclo);

    List<RecorridoIngresante> ingresantesCiclo(CicloAcademico ciclo);

    HistoriaClinica findHistoriaClinica(RecorridoIngresante recorrido);

    void saveLaboratorio(HistoriaLaboratorio laboratorio);

    List<HistoriaLaboratorio> allLabByPersonas(List<Persona> personas);

    List<HistoriaClinica> allHistoriaByPersonas(List<Persona> personas);

    DiarioLaboratorio getDiarioLabActual();

    List<RecorridoIngresante> allIngresantesDynatableByPersona(DynatableFilter filter, List<Persona> personas);

    List<RecorridoIngresante> allIngresantesByPersona(List<Persona> personas);

    List<RecorridoIngresante> allConMuestraByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo);

    List<RecorridoIngresante> allConMuestraByDynatableTurnoCiclo(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo);

    TurnoEntrevistaObuae findTurno(Long idTurno);

    List<RecorridoIngresante> allRecorridosConMuestra(TurnoEntrevistaObuae turno, CicloAcademico ciclo);

}
