package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Aula;

@Service
@Transactional(readOnly = true)
public class SeccionServiceImp implements SeccionService {

    @Override
    public List<Aula> allAulasSinHorarioDyna(DynatableFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
