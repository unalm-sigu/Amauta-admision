package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;

@Service
@Transactional(readOnly = true)
public class ProgramacionAulaServiceImp implements ProgramacionAulaService {

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Override
    public List<Aula> allAulasSinHorarioDyna(DynatableFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    public List<Aula> allAulasDisponibles(DynatableFilter filter, List<Dia> dias) {
//        horarioAulaDAO.allByAulasAndNotInSecciones(aulas, secciones, fechaInicio, fechaFin)
//    }

}
