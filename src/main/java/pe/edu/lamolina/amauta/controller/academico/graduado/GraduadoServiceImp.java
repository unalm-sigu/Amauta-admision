package pe.edu.lamolina.amauta.controller.academico.graduado;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.model.tramite.ObtencionGrado;

@Service
@Transactional(readOnly = true)
public class GraduadoServiceImp implements GraduadoService {

    @Autowired
    EgresadoDAO egresadoDAO;
    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<ObtencionGrado> allEgresadoByDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        return obtencionGradoDAO.allByCarrerasDynatable(filter, carreras, todo);
    }

    @Override
    public GraduadoResumen findResumenEgresado(List<Carrera> carreras, String todo) {
        return obtencionGradoDAO.findResumenGraduados(carreras, todo);
    }

}
