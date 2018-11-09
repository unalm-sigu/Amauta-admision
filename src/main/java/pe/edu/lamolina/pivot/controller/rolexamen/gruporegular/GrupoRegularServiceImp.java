package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GrupoRegularServiceImp implements GrupoRegularService {

    @Autowired
    RolExamenesDAO rolExamenesDAO;
    /*
    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;
     */
    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    @Transactional(readOnly = false)
    public void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        //    List<CursoMasivoExamen> allCursoMasivoExamenByGrupoRegular = cursoMasivoExamenDAO.allActiveByRolExamen(grupoRegularExamen.getRolExamen());
        //  List<SeccionCursoMasivo> allSeccionesCursoMasivosActives = seccionCursoMasivoDAO.allActiveByCursosMasivos(allCursoMasivoExamenByGrupoRegular);
        DateTime today = new DateTime();

        List<Seccion> seccionesEspeciales = new ArrayList<>();
        List<Seccion> secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.REGULAR); //grupo horas regulares
        Map<String, List<Seccion>> grupoHorasLetraMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        List<String> letras = new ArrayList<>(grupoHorasLetraMap.keySet());
        Collections.sort(letras, (p1, p2) -> p1.compareTo(p2));

        List<LetraGrupoRegular> letrasGruposRegulares = new ArrayList<>();
        letras.forEach(x -> {
            LetraGrupoRegular letraGrupoRegular = new LetraGrupoRegular();
            letraGrupoRegular.setLetra(x);
            letraGrupoRegular.setRolExamenes(rolExamenes);

            letraGrupoRegular.setAlumnosGruposRegulares(new ArrayList<>());
            letraGrupoRegular.setSeccionesGruposRegulares(new ArrayList<>());
            letrasGruposRegulares.add(letraGrupoRegular);
        });

        this.crearLetraGrupoRegular(seccionesEspeciales, letrasGruposRegulares, grupoHorasLetraMap, today, ds.getUsuario());

        secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ESPECIAL);
        //consultar si se puede encontrar mas legras
        grupoHorasLetraMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        this.crearLetraGrupoRegular(seccionesEspeciales, letrasGruposRegulares, grupoHorasLetraMap, today, ds.getUsuario());

        throw new PhobosException("no pasaras papu");
    }

    public void crearLetraGrupoRegular(
            List<Seccion> seccionesEspeciales,
            List<LetraGrupoRegular> letrasGruposRegulares,
            Map<String, List<Seccion>> grupoHorasLetraMap,
            DateTime today,
            Usuario usuario) {

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            List<Seccion> seccionesByLetra = grupoHorasLetraMap.get(letraGrupoRegular.getLetra());
            for (Seccion seccion : seccionesByLetra) {
                //   List<AlumnoGrupoRegular> alumnosGrupoRegulares = alumnoGrupoRegularDAO.allByLetraGrupoActives(letraGrupoRegular);
                List<AlumnoGrupoRegular> alumnosGrupoRegulares = letraGrupoRegular.getAlumnosGruposRegulares();

                List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
                boolean conConflictos = false;
                for (MatriculaSeccion matriculaSeccion : matriculadosPorSeccion) {
                    AlumnoGrupoRegular alumnoGrupoRegularFound = alumnosGrupoRegulares
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
                    seccionGrupoRegular.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
                    seccionGrupoRegular.setFechaRegistro(today.toDate());
                    seccionGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
                    seccionGrupoRegular.setUserRegistro(usuario);
                    letraGrupoRegular.getSeccionesGruposRegulares().add(seccionGrupoRegular);

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
            }
        }

    }

}
