package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionGeneradas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ResolucionReincorporacionServiceImp implements ResolucionReincorporacionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TipoResolucionDAO tipoResolucionDAO;

    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    MatriculableService matriculableService;

    @Override
    public List<Alumno> allAlumnoDesertorByNombre(String nombre, Long instanciaOficina) {
        return alumnoDAO.allDesertorByName(nombre, instanciaOficina);
    }

    @Override
    @Transactional
    public List<Alumno> save(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {

        List<Alumno> alumnos = new ArrayList<>();

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.REIC);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(usuario);
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        Assert.isFalse(resolucionForm.getReincorporaciones().isEmpty(), "Debe Agregar alumnos.");

        Map<Long, Long> couterMap = resolucionForm.getReincorporaciones().stream().collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));
        for (Long count : couterMap.values()) {
            Assert.isFalse(count > 1, "Está repitiendo alumno");
        }
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByCicloReincorporacion(ds.getCicloAcademico());
        Map<Long, Alumno> map = TypesUtil.convertListToMap("alumno", reincorporacions);

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACEP);
        System.out.println("Estado" + estadoTramite.getId());
        for (Reincorporacion reincorporacione : resolucionForm.getReincorporaciones()) {
            Alumno alumno = map.get(reincorporacione.getAlumno().getId());
            if (alumno != null) {
                throw new PhobosException("El alumno" + alumno.getCodigo() + " ya cuenta con una resolución para el ciclo activo");
            }
            Facultad facultad = reincorporacione.getAlumno().getCarrera().getFacultad();
            reincorporacione.setAceptado(1);
            reincorporacione.setFechaRegistro(new Date());
            reincorporacione.setResolucion(resolucion);
            reincorporacione.setEstadoTramite(estadoTramite);
            reincorporacione.setUserRegistro(usuario);
            reincorporacione.setFacultad(facultad);
            reincorporacionDAO.save(reincorporacione);
            alumnos.add(reincorporacione.getAlumno());
        }

        return alumnos;
    }

    @Override
    public List<Reincorporacion> findByResolucion(Long resolucion, DataSessionPivot ds) {

        return reincorporacionDAO.allByResolucion(new Resolucion(resolucion));
    }

}
