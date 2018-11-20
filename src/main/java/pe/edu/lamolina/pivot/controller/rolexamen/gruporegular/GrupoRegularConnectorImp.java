package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;

@Service
public class GrupoRegularConnectorImp implements GrupoRegularConnector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savedLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        letraGrupoRegularDAO.save(letraGrupoRegular);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void crearLetraGrupoRegularByLetra(
            LetraGrupoRegular letraGrupoRegular,
            Map<String, List<Seccion>> grupoHorasLetraMap,
            List<Seccion> seccionesEspeciales,
            DateTime today,
            Usuario usuario) {

        long ini = System.currentTimeMillis();
        List<Seccion> seccionesByLetra = grupoHorasLetraMap.get(letraGrupoRegular.getLetra());
        if (seccionesByLetra == null) {
            return;
        }
        int i = 1;
        letraGrupoRegular.setContadorSecciones(BigDecimal.ZERO.intValue());
        List<AlumnoGrupoRegular> alumnosGrupoRegularesByLetra = letraGrupoRegular.getAlumnosGruposRegulares();

        for (Seccion seccion : seccionesByLetra) {
            boolean result = this.procesarSeccionesByLetra(letraGrupoRegular, seccion, seccionesByLetra, usuario, today);
            if (!result) {
                seccionesEspeciales.add(seccion);
            }
            /*
            List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
            logger.debug("Letra {}, seccion {}, cant. alumnos {}, numero {}",
                    letraGrupoRegular.getLetra(),
                    seccion.getId(),
                    matriculadosPorSeccion.size(),
                    i++ + " de " + seccionesByLetra.size());
            
            boolean conConflictos = false;
            for (MatriculaSeccion matriculaSeccion : matriculadosPorSeccion) {
                AlumnoGrupoRegular alumnoGrupoRegularFound = alumnosGrupoRegularesByLetra
                        .stream().filter(x -> x.getAlumno().equals(matriculaSeccion.getMatriculaResumen().getAlumno())).findFirst().orElse(null);
                if (alumnoGrupoRegularFound != null) {
                    conConflictos = true;
                    break;
                }
            }
            if (conConflictos) {
                seccionesEspeciales.add(seccion);
            } else {
                SeccionGrupoRegular seccionGrupoRegular = new SeccionGrupoRegular();
                seccionGrupoRegular.setSeccion(seccion);
                seccionGrupoRegular.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
                seccionGrupoRegular.setFechaRegistro(today.toDate());
                seccionGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
                seccionGrupoRegular.setUserRegistro(usuario);
                letraGrupoRegular.getSeccionesGruposRegulares().add(seccionGrupoRegular);
                
                GrupoRegularExamen grupoRegularExamen = letraGrupoRegular.getGruposRegularesExamenes()
                        .stream().filter(x -> x.getGrupoHoras().equals(seccion.getGrupoHoras()))
                        .findFirst().orElse(null);
                
                if (grupoRegularExamen == null) {
                    grupoRegularExamen = new GrupoRegularExamen();
                    grupoRegularExamen.setEstadoEnum(GrupoHorasRolExamenEstadoEnum.ACT);
                    grupoRegularExamen.setFechaRegistro(today.toDate());
                    grupoRegularExamen.setGrupoHoras(seccion.getGrupoHoras());
                    grupoRegularExamen.setLetraGrupoRegular(letraGrupoRegular);
                    grupoRegularExamen.setUserRegistro(usuario);
                    letraGrupoRegular.getGruposRegularesExamenes().add(grupoRegularExamen);
                }
                
                matriculadosPorSeccion.forEach(x -> {
                    AlumnoGrupoRegular alumnoGrupoRegular = new AlumnoGrupoRegular();
                    alumnoGrupoRegular.setAlumno(x.getMatriculaResumen().getAlumno());
                    alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                    alumnoGrupoRegular.setFechaRegistro(today.toDate());
                    alumnoGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
                    alumnoGrupoRegular.setUserRegistro(usuario);
                    letraGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
                });
            }*/
        }
        long end = System.currentTimeMillis();

        long milis = end - ini;
        logger.debug("Termino en Segundos {}, MiliSeconds {}", TimeUnit.MILLISECONDS.toSeconds(milis), milis);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean procesarSeccionesByLetra(
            LetraGrupoRegular letraGrupoRegular, Seccion seccion,
            List<Seccion> seccionesByLetra,
            Usuario usuario, DateTime today) {
        letraGrupoRegular.setContadorSecciones(letraGrupoRegular.getContadorSecciones() + 1);
        List<AlumnoGrupoRegular> alumnosGrupoRegularesByLetra = letraGrupoRegular.getAlumnosGruposRegulares();
        List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        logger.debug("Letra {}, seccion {}, cant. alumnos {}, numero {}",
                letraGrupoRegular.getLetra(),
                seccion.getId(),
                matriculadosPorSeccion.size(),
                letraGrupoRegular.getContadorSecciones() + " de " + seccionesByLetra.size());

        boolean conConflictos = false;
        for (MatriculaSeccion matriculaSeccion : matriculadosPorSeccion) {
            AlumnoGrupoRegular alumnoGrupoRegularFound = alumnosGrupoRegularesByLetra
                    .stream().filter(x -> x.getAlumno().equals(matriculaSeccion.getMatriculaResumen().getAlumno())).findFirst().orElse(null);
            if (alumnoGrupoRegularFound != null) {
                conConflictos = true;
                break;
            }
        }
        if (!conConflictos) {
            SeccionGrupoRegular seccionGrupoRegular = new SeccionGrupoRegular();
            seccionGrupoRegular.setSeccion(seccion);
            seccionGrupoRegular.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionGrupoRegular.setFechaRegistro(today.toDate());
            seccionGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
            seccionGrupoRegular.setUserRegistro(usuario);
            letraGrupoRegular.getSeccionesGruposRegulares().add(seccionGrupoRegular);

            GrupoRegularExamen grupoRegularExamen = letraGrupoRegular.getGruposRegularesExamenes()
                    .stream().filter(x -> x.getGrupoHoras().equals(seccion.getGrupoHoras()))
                    .findFirst().orElse(null);

            if (grupoRegularExamen == null) {
                grupoRegularExamen = new GrupoRegularExamen();
                grupoRegularExamen.setEstadoEnum(GrupoHorasRolExamenEstadoEnum.ACT);
                grupoRegularExamen.setFechaRegistro(today.toDate());
                grupoRegularExamen.setGrupoHoras(seccion.getGrupoHoras());
                grupoRegularExamen.setLetraGrupoRegular(letraGrupoRegular);
                grupoRegularExamen.setUserRegistro(usuario);
                letraGrupoRegular.getGruposRegularesExamenes().add(grupoRegularExamen);
            }

            matriculadosPorSeccion.forEach(x -> {
                AlumnoGrupoRegular alumnoGrupoRegular = new AlumnoGrupoRegular();
                alumnoGrupoRegular.setAlumno(x.getMatriculaResumen().getAlumno());
                alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                alumnoGrupoRegular.setFechaRegistro(today.toDate());
                alumnoGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
                alumnoGrupoRegular.setUserRegistro(usuario);
                letraGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
            });
        }
        if (conConflictos) {
            return false;
        }
        return true;
    }

}
