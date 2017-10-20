package pe.edu.lamolina.pivot.controller.academico.carrera;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Service
@Transactional(readOnly = true)
public class CarreraServiceImp implements CarreraService {

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Override
    public List<Carrera> allByDynatable(DynatableFilter filter) {
        return carreraDAO.allByModalidadEstudio(filter);
    }

    @Override
    @Transactional
    public void desactivar(Carrera carrera) {
        Carrera carrreraBD = carreraDAO.find(carrera.getId());
        carrreraBD.setMotivo(carrera.getMotivo());
        carrreraBD.setEstado(EstadoEnum.INA);
        carreraDAO.update(carrreraBD);
    }

    @Override
    public List<ModalidadEstudio> allModalidades() {
        return modalidadEstudioDAO.all();
    }

}
