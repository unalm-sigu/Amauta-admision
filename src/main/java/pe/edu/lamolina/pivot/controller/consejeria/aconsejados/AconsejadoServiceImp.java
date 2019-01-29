package pe.edu.lamolina.pivot.controller.consejeria.aconsejados;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class AconsejadoServiceImp implements AconsejadoService {

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;

    @Override
    public List<AlumnoConsejero> allAconsejadoByDynatableCarrera(DynatableFilter filter) {

        return alumnoConsejeroDAO.allByCarrera(filter);
    }

    @Override
    @Transactional
    public void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.find(alumnoConsejeroForm.getId());
        alumnoConsejero.setConsejero(alumnoConsejeroForm.getConsejero());
        alumnoConsejero.setFechaAsigna(new Date());
        alumnoConsejeroDAO.update(alumnoConsejero);
    }

}
