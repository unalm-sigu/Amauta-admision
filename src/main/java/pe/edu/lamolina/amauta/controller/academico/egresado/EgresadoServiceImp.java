package pe.edu.lamolina.amauta.controller.academico.egresado;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;

@Service
@Transactional(readOnly = true)
public class EgresadoServiceImp implements EgresadoService {

    @Autowired
    EgresadoDAO egresadoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Egresado> allEgresadoByDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        return egresadoDAO.allByCarrerasDynatable(filter, carreras, todo);
    }

    @Override
    public EgresadoResumen findResumenEgresado(List<Carrera> carreras, String todo) {
        return egresadoDAO.findResumenEgresado(carreras, todo);
    }

}
