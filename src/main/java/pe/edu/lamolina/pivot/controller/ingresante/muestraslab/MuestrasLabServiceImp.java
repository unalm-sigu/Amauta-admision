package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.laboratorio.HistoriaLaboratorioDAO;

@Service
@Transactional(readOnly = true)
public class MuestrasLabServiceImp implements MuestrasLabService {

    @Autowired
    HistoriaLaboratorioDAO historiaLaboratorioDAO;

    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<RecorridoIngresante> laboratorioDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo) {

        return recorridoIngresanteDAO.allByDynatableCicloTurno(filter, ciclo, turno);
    }

    @Override
    public HistoriaLaboratorio findLaboratorioByRecorridoIngresante(RecorridoIngresante recorrido) {
        return historiaLaboratorioDAO.findByRecorridoIngresante(recorrido);
    }

    @Override
    public List<TurnoEntrevistaObuae> allTurnos(CicloAcademico ciclo) {
        List<RecorridoIngresante> listaRecorridos = recorridoIngresanteDAO.allByCiclo(ciclo);
        List<TurnoEntrevistaObuae> turnos = new ArrayList();
        for (RecorridoIngresante recorrido : listaRecorridos) {
            if (!turnos.contains(recorrido.getTurnoEntrevistaObuae()) && recorrido.getTurnoEntrevistaObuae()!=null) {
                turnos.add(recorrido.getTurnoEntrevistaObuae());
            }
        }
        return turnos;
    }

}
