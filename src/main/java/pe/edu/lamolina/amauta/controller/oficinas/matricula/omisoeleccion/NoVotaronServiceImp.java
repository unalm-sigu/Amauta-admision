package pe.edu.lamolina.amauta.controller.oficinas.matricula.omisoeleccion;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoOmisoEleccionDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;

@Service
@Transactional(readOnly = true)
public class NoVotaronServiceImp implements NoVotaronService {

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoOmisoEleccionDAO alumnoOmisoEleccionDAO;
    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void anularOmisosSeleccionados(List<AlumnoOmisoEleccion> omisosElecciones, DataSessionPivot ds) {
        if (omisosElecciones.isEmpty()) {
            logger.info("No se enviaron registros para anular alumnos omisos votantes");
            return;
        }

        List<AlumnoOmisoEleccion> omisionesBD = alumnoOmisoEleccionDAO.allByAlumnosOmisos(omisosElecciones);
        Map<Long, AlumnoOmisoEleccion> mapOmisiones = omisionesBD.stream()
                .collect(Collectors.toMap(AlumnoOmisoEleccion::getId, Function.identity()));

        Alumno alumno = omisosElecciones.get(0).getAlumno();
        Alumno alumnoBD = alumnoDAO.find(alumno);
        String motivoAnula = omisosElecciones.get(0).getMotivoAnulacion();
        int loop = 0;

        for (AlumnoOmisoEleccion omiso : omisosElecciones) {
            if (omiso.getSeleccionado()) {
                AlumnoOmisoEleccion omisoBD = mapOmisiones.get(omiso.getId());
                omisoBD.setEstadoEnum(DeudaEstadoEnum.ANU);
                omisoBD.setMotivoAnulacion(motivoAnula);
                omisoBD.setFechaAnulacion(new Date());
                omisoBD.setUserAnulacion(ds.getUsuario());

                alumnoOmisoEleccionDAO.updateAnulacion(omisoBD);

                AporteAlumnoCiclo aporteAlumno = omisoBD.getAporteAlumnoCiclo();
                if (aporteAlumno != null) {
                    Assert.isFalse(registroPagado(aporteAlumno), "Este registro está relacionado a un aporte pagado");
                    aporteAlumno.setEstadoEnum(EstadoAporteEnum.ANU);
                    aporteAlumnoCicloDAO.update(aporteAlumno);
                }

                loop++;
            }
        }
        logger.info("Se anularon {} omisiones votantes del alumno {}", loop, alumnoBD.getCodigo());
    }

    private boolean registroPagado(AporteAlumnoCiclo aporteAlumno) {
        return (aporteAlumno.getEstadoRegistroEnum() == EstadoEnum.ACT && aporteAlumno.getEstadoEnum() == EstadoAporteEnum.PAGO);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deshacerAnuladosOmisosSeleccionados(List<AlumnoOmisoEleccion> omisosElecciones, DataSessionPivot ds) {
        if (omisosElecciones.isEmpty()) {
            logger.info("No se enviaron registros para deshacer la anulación de alumnos omisos votantes");
            return;
        }

        Alumno alumno = omisosElecciones.get(0).getAlumno();
        Alumno alumnoBD = alumnoDAO.find(alumno);
        int loop = 0;

        for (AlumnoOmisoEleccion omiso : omisosElecciones) {
            if (omiso.getSeleccionado()) {
                omiso.setEstadoEnum(DeudaEstadoEnum.DEU);
                omiso.setMotivoAnulacion(null);
                omiso.setFechaAnulacion(null);
                omiso.setUserAnulacion(null);
                alumnoOmisoEleccionDAO.updateAnulacion(omiso);
                loop++;
            }
        }

        logger.info("Se deshicieron {} omisiones votantes del alumno {}", loop, alumnoBD.getCodigo());
    }

}
