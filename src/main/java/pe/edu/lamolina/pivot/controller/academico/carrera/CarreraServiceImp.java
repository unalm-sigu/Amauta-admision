package pe.edu.lamolina.pivot.controller.academico.carrera;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Service
@Transactional(readOnly = true)
public class CarreraServiceImp implements CarreraService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    OrientacionCarreraDAO orientacionCarreraDAO;

    @Override
    public List<Carrera> allByDynatable(DynatableFilter filter) {
        return carreraDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void cambiarEstadoCarrera(Carrera carrera) {
        Carrera carrreraBD = carreraDAO.find(carrera.getId());
        if (carrreraBD.getEstado().equals(EstadoEnum.ACT.name())) {
            carrreraBD.setEstado(EstadoEnum.INA);
            carrreraBD.setMotivoAnulacion(carrera.getMotivoAnulacion());
            carrreraBD.setFechaAnulacion(new Date());
        } else {
            carrreraBD.setEstado(EstadoEnum.ACT);
        }
        carreraDAO.update(carrreraBD);
    }

    @Override
    public List<ModalidadEstudio> allModalidades() {
        return modalidadEstudioDAO.allActivos();
    }

    @Override
    @Transactional
    public void save(Carrera carrera, Usuario usuario) {
        if (carrera.getId() == null) {
            carrera.setEstado(EstadoEnum.ACT);
            carrera.setIdUserRegistro(usuario.getId());
            carrera.setFechaRegistro(new Date());
            carreraDAO.save(carrera);

        } else {
            Carrera carreraBD = carreraDAO.find(carrera.getId());
            carreraBD.setNombre(carrera.getNombre());
            carreraBD.setFacultad(carrera.getFacultad());
            carreraBD.setModalidadEstudio(carrera.getModalidadEstudio());
            carreraBD.setTipo(carrera.getTipoEnum());
            carreraDAO.update(carreraBD);
            if (carrera.getOrientacionCarrera() == null) {
                return;
            }

            for (OrientacionCarrera orientacion : carrera.getOrientacionCarrera()) {
                OrientacionCarrera orientacionBD = orientacionCarreraDAO.find(orientacion.getId());
                orientacionBD.setNombre(orientacion.getNombre());
                orientacionCarreraDAO.update(orientacionBD);
            }

        }
    }

    private Integer findLastCodigo(Carrera carrera) {
        OrientacionCarrera orientacion = orientacionCarreraDAO.findLastByCarrera(carrera);

        String correlativoTmp = "";
        Integer correlativo = null;
        if (orientacion != null) {
            String codigo = orientacion.getCodigo();
            Integer tamañoCodigo = orientacion.getCodigo().length();
            correlativoTmp = codigo.substring(tamañoCodigo - 1, tamañoCodigo);

            correlativo = Integer.valueOf(correlativoTmp) + 1;
        } else {
            correlativo = 1;
        }

        return correlativo;
    }

    @Override
    public List<Facultad> allFacultades() {
        return facultadDAO.allActivos();
    }

    @Override
    public Carrera find(Long id) {
        Carrera carrera = carreraDAO.find(id);
        List<OrientacionCarrera> orientaciones = orientacionCarreraDAO.allByCarrera(carrera);
        carrera.setOrientacionCarrera(orientaciones);

        return carrera;
    }

    @Override
    @Transactional
    public void saveOrientacion(Long idCarrera, Long idOrientacion, String nombreOrientacion, Usuario usuario) {
        try {
            if (idOrientacion == null) {
                Carrera carrera = carreraDAO.find(idCarrera);
                Integer correlativo = this.findLastCodigo(carrera);

                OrientacionCarrera oriCarrera = new OrientacionCarrera();
                oriCarrera.setCarrera(carrera);
                oriCarrera.setCodigo(carrera.getCodigo() + correlativo);
                oriCarrera.setNombre(nombreOrientacion);
                oriCarrera.setEstado(EstadoEnum.ACT.name());
                oriCarrera.setIdUserRegistro(usuario.getId());
                oriCarrera.setFechaRegistro(new Date());
                orientacionCarreraDAO.save(oriCarrera);

            } else {
                OrientacionCarrera orientacion = orientacionCarreraDAO.find(idOrientacion);
                orientacion.setNombre(nombreOrientacion);
                orientacionCarreraDAO.update(orientacion);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    @Transactional
    public void deleteOrientacion(Long idOrientacion) {
        orientacionCarreraDAO.delete(idOrientacion);
    }

    @Override
    @Transactional
    public void cambioEstado(OrientacionCarrera orientacion) {
        OrientacionCarrera orientacionBD = orientacionCarreraDAO.find(orientacion.getId());
        if (orientacionBD.getEstado().equals(EstadoEnum.ACT.name())) {
            orientacionBD.setEstado(EstadoEnum.INA.name());
            orientacionBD.setMotivoAnulacion(orientacion.getMotivoAnulacion());
            orientacionBD.setFechaRegistro(new Date());
        } else {
            orientacionBD.setEstado(EstadoEnum.ACT.name());
        }
        orientacionCarreraDAO.update(orientacionBD);

    }

    @Override
    public List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera) {
        return orientacionCarreraDAO.allByIdCarreraDynatable(filter, idCarrera);
    }

    @Override
    public OrientacionCarrera editarOrientacion(Long id) {
        return orientacionCarreraDAO.find(id);
    }

}
