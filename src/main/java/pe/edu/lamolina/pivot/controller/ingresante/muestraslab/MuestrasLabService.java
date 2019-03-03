package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MuestrasLabService {

//    List<HistoriaLaboratorio> laboratorioDynatableFecha(DynatableFilter filter, Date fecha);
    List<RecorridoIngresante> ingresantesDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo);

    List<RecorridoIngresante> ingresantesDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<RecorridoIngresante> allIngresantesDynatableByPersona(DynatableFilter filter, List<Persona> personas);

    List<RecorridoIngresante> allIngresantesCiclo(CicloAcademico ciclo);

    TurnoEntrevistaObuae findTurno(long idTurno);

    List<TurnoEntrevistaObuae> allTurnos(CicloAcademico ciclo);

    HistoriaLaboratorio findLaboratorioByRecorridoIngresante(RecorridoIngresante recorrido);

    HistoriaClinica findHistoriaClinica(RecorridoIngresante recorrido);

    void inicializarVisor();

    void saveLaboratorio(HistoriaLaboratorio laboratorio);

    List<HistoriaLaboratorio> allLabByPersonas(List<Persona> personas);

    List<HistoriaLaboratorio> allLabByPersonasFilterFecha(List<Persona> personas, Date fecha);

    List<HistoriaClinica> allHistoriaByPersonas(List<Persona> personas);

    void deleteLaboratorio(HistoriaLaboratorio laboratorio);

    Boolean findRiesgoAlumno(HistoriaClinica historia);

    List<RecorridoIngresante> allIngresantesConTurno(CicloAcademico ciclo);
    
    HistoriaClinica crearHistoriaClinica(RecorridoIngresante recorrido, DataSessionPivot ds);
}
