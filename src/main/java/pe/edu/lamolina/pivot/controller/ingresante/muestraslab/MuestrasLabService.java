package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;

public interface MuestrasLabService {

//    List<HistoriaLaboratorio> laboratorioDynatableFecha(DynatableFilter filter, Date fecha);

    List<RecorridoIngresante> laboratorioDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno,CicloAcademico ciclo);

    List<TurnoEntrevistaObuae> allTurnos(CicloAcademico ciclo);
    
    HistoriaLaboratorio findLaboratorioByRecorridoIngresante (RecorridoIngresante recorrido);
    
    HistoriaClinica findHistoriaClinica(RecorridoIngresante recorrido);
    
    long findNumLab(CicloAcademico ciclo);
    
    void saveLaboratorio(HistoriaLaboratorio laboratorio);
}
