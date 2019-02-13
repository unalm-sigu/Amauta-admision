package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionGeneradas;

import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.record.chart.DatRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;

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

    @Override
    public List<Alumno> allAlumnoDesertorByNombre(String nombre, Long instanciaOficina) {
        return alumnoDAO.allDesertorByName(nombre, instanciaOficina);
    }

    @Override
    @Transactional
    public void save(Resolucion resolucionForm, Usuario usuario) {
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

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ACP);
        logger.debug("estadp {}" , estadoTramite.getId());
        System.out.println("Estado" + estadoTramite.getId());
        for (Reincorporacion reincorporacione : resolucionForm.getReincorporaciones()) {
            Facultad facultad = reincorporacione.getAlumno().getCarrera().getFacultad();
            reincorporacione.setAceptado(1);
            reincorporacione.setFechaRegistro(new Date());
            reincorporacione.setResolucion(resolucion);
            reincorporacione.setEstadoTramite(estadoTramite);
            reincorporacione.setUserRegistro(usuario);
            reincorporacione.setFacultad(facultad);
            reincorporacionDAO.save(reincorporacione);
        }
    }

}
