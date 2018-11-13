package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
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

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    GrupoRegularExamenDAO grupoRegularExamenDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

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

        List<Seccion> seccionesEspecialesRecolected = new ArrayList<>();
        List<Seccion> secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.REGULAR); //grupo horas regulares
        Map<String, List<Seccion>> grupoHorasLetraMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        List<String> letras = new ArrayList<>(grupoHorasLetraMap.keySet());
        Collections.sort(letras, (p1, p2) -> p1.compareTo(p2));

        List<LetraGrupoRegular> letrasGruposRegulares = new ArrayList<>();
        letras.forEach(x -> {
            LetraGrupoRegular letraGrupoRegular = new LetraGrupoRegular();
            letraGrupoRegular.setLetra(x);
            letraGrupoRegular.setRolExamenes(rolExamenes);
            letraGrupoRegular.setFechaRegistro(today.toDate());
            letraGrupoRegular.setUserRegistro(ds.getUsuario());

            letraGrupoRegular.setGruposRegularesExamenes(new ArrayList<>());
            letraGrupoRegular.setAlumnosGruposRegulares(new ArrayList<>());
            letraGrupoRegular.setSeccionesGruposRegulares(new ArrayList<>());
            letrasGruposRegulares.add(letraGrupoRegular);
        });

        this.crearLetraGrupoRegular(seccionesEspecialesRecolected, letrasGruposRegulares, grupoHorasLetraMap, today, ds.getUsuario());

        secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ESPECIAL);
        //consultar si se puede encontrar mas legras
        grupoHorasLetraMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        this.crearLetraGrupoRegular(seccionesEspecialesRecolected, letrasGruposRegulares, grupoHorasLetraMap, today, ds.getUsuario());

        for (LetraGrupoRegular letrasGruposRegulare : letrasGruposRegulares) {
            letraGrupoRegularDAO.save(letrasGruposRegulare);
        }
    }

    @Override
    public List<LetraGrupoRegular> listGruposRegulares(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<LetraGrupoRegular> letrasGruposRegulares = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);

        Map<Long, Integer> countGruposRegulares = grupoRegularExamenDAO.countByLetrasGruposRegulares(letrasGruposRegulares, GrupoHorasRolExamenEstadoEnum.ACT);
        Map<Long, Integer> countSeccionesGruposRegulares = seccionGrupoRegularDAO.countByLetrasGruposRegulares(letrasGruposRegulares, SeccionRolExamenEstadoEnum.ACT);
        Map<Long, Integer> countAlumnosGruposRegulares = alumnoGrupoRegularDAO.countByLetrasGruposRegulares(letrasGruposRegulares, AlumnoRolExamenEstadoEnum.ACT);

        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            letraGruposRegular.setGruposRegularesActivosCount(countGruposRegulares.get(letraGruposRegular.getId()));
            letraGruposRegular.setSeccionesRegularesActivosCount(countSeccionesGruposRegulares.get(letraGruposRegular.getId()));
            letraGruposRegular.setAlumnosRegularesActivosCount(countAlumnosGruposRegulares.get(letraGruposRegular.getId()));
        }

        return letrasGruposRegulares;
    }

    @Override
    public List<GrupoRegularExamen> allGruposRegularExamenByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        List<GrupoRegularExamen> grupos = grupoRegularExamenDAO.
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, Arrays.asList(GrupoHorasRolExamenEstadoEnum.ACT, GrupoHorasRolExamenEstadoEnum.EXC));
        return grupos;
    }

    @Override
    public List<SeccionGrupoRegular> allSeccionesGrupoRegularExamenByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        List<SeccionGrupoRegular> secciones = seccionGrupoRegularDAO.
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, Arrays.asList(SeccionRolExamenEstadoEnum.ACT, SeccionRolExamenEstadoEnum.EXC));
        return secciones;
    }

    @Override
    public List<AlumnoGrupoRegular> allAlumnosGrupoRegularByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        List<AlumnoGrupoRegular> alumnos = alumnoGrupoRegularDAO.
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, Arrays.asList(AlumnoRolExamenEstadoEnum.ACT, AlumnoRolExamenEstadoEnum.EXC));
        return alumnos;
    }

    public void crearLetraGrupoRegular(
            List<Seccion> seccionesEspeciales,
            List<LetraGrupoRegular> letrasGruposRegulares,
            Map<String, List<Seccion>> grupoHorasLetraMap,
            DateTime today,
            Usuario usuario) {

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            List<Seccion> seccionesByLetra = grupoHorasLetraMap.get(letraGrupoRegular.getLetra());
            if (seccionesByLetra == null) {
                continue;
            }
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
            }
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void excluirGrupoRegular(GrupoRegularExamen grupoRegularExamen, Usuario usuario, CicloAcademico cicloAcademico) {
        grupoRegularExamen = grupoRegularExamenDAO.find(grupoRegularExamen.getId());
        grupoRegularExamen.setUsuarioExclusion(usuario);
        grupoRegularExamen.setFechaExclusion(new Date());
        grupoRegularExamen.setEstadoEnum(GrupoHorasRolExamenEstadoEnum.EXC);
        grupoRegularExamenDAO.updateEstado(grupoRegularExamen);

        List<Seccion> secciones = seccionDAO.allByCicloAndGrupoHoras(cicloAcademico, grupoRegularExamen.getGrupoHoras());
        List<SeccionGrupoRegular> seccionesGruposRegulares
                = seccionGrupoRegularDAO.allByLetraGrupoRegularAndSecciones(grupoRegularExamen.getLetraGrupoRegular(), secciones);
        for (SeccionGrupoRegular seccionGrupoRegular : seccionesGruposRegulares) {
            this.excluirGrupoRegular(seccionGrupoRegular, usuario);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirGrupoRegular(SeccionGrupoRegular seccionGrupoRegular, Usuario usuario) {
        seccionGrupoRegular = seccionGrupoRegularDAO.find(seccionGrupoRegular.getId());

        DateTime today = new DateTime();
        SeccionGrupoRegular seccionGrupoRegularUpd = new SeccionGrupoRegular();
        seccionGrupoRegularUpd.setId(seccionGrupoRegular.getId());
        seccionGrupoRegularUpd.setUsuarioExclusion(usuario);
        seccionGrupoRegularUpd.setFechaExclusion(today.toDate());
        seccionGrupoRegularUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.EXC);
        seccionGrupoRegularDAO.updateEstado(seccionGrupoRegularUpd);

        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccionGrupoRegular.getSeccion());
        List<Alumno> alumnos = matriculasSeccion.stream().map(x -> x.getMatriculaResumen().getAlumno()).collect(Collectors.toList());
        alumnoGrupoRegularDAO.updateEstado(alumnos, AlumnoRolExamenEstadoEnum.EXC, usuario, today.toDate());
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirGrupoRegular(AlumnoGrupoRegular alumnoGrupoRegular, Usuario usuario) {
        alumnoGrupoRegular.setUsuarioExclusion(usuario);
        alumnoGrupoRegular.setFechaExclusion(new Date());
        alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.EXC);
        alumnoGrupoRegularDAO.updateEstado(alumnoGrupoRegular);
    }

}
