package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularConnector;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GrupoEspecialServiceImp implements GrupoEspecialService {

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Autowired
    SeccionExcluidoDAO seccionExcluidoDAO;

    @Autowired
    GrupoHorasExamenDAO grupoHorasExamenDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    GrupoRegularConnector grupoRegularConnector;

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    public List<SeccionGrupoEspecial> allSeccionesGrupoEspecialByRolExamenes(DynatableFilter filter, RolExamenes rolExamenes) {
        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByDynatableAndRolExamenes(filter, rolExamenes);
        Map<Long, Integer> mapAlumnosBySeccion = alumnoGrupoEspecialDAO.countBySeccionesGrupoEspecial(seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum.ACT);
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecial) {
            seccionGrupoEspecial.setAlumnosEspecialesActivosCount(mapAlumnosBySeccion.get(seccionGrupoEspecial.getId()) == null ? 0 : mapAlumnosBySeccion.get(seccionGrupoEspecial.getId()));
        }
        return seccionesGrupoEspecial;
    }

    @Override
    public void deleteGrupoEspecial(RolExamenes rolExamenes) {
        alumnoGrupoEspecialDAO.deleteByRolExamenes(rolExamenes);
        seccionGrupoEspecialDAO.deleteByRolExamenes(rolExamenes);
    }

    @Override
    @Transactional(readOnly = false)
    public void calcularExamenesGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds) {
        List<SeccionExcluido> seccionesExcluidasByRolExamen = seccionExcluidoDAO.allByRolExamenes(rolExamenes);

        List<SemanaExamen> semanasByRolExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        List<SeccionGrupoEspecial> seccionesGrupoEspeciales = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);

        List<AlumnoGrupoEspecial> alumnosGruposEspeciales = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionesGrupoEspeciales, AlumnoRolExamenEstadoEnum.ACT);
        Map<Long, List<AlumnoGrupoEspecial>> mapAlumnosGruposEspecialesBySecGpoEspecial = TypesUtil.convertListToMapList("seccionGrupoEspecial.id", alumnosGruposEspeciales);

        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesGrupoEspeciales.stream().map(x -> x.getSeccion()).collect(Collectors.toList()));
        Map horariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);

        for (SeccionExcluido seccionExcluido : seccionesExcluidasByRolExamen) {
            seccionesGrupoEspeciales.removeIf(x -> x.getSeccion().equals(seccionExcluido.getSeccion()));
        }
        List<GrupoHorasExamen> gruposHorasExamen = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<LetraGrupoRegular> letrasGrupoRegular = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        List<FechaHoraGrupoExamen> fechasHorasExamens = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(gruposHorasExamen);
        for (GrupoHorasExamen grupoHorasExamen : gruposHorasExamen) {
            List<FechaHoraGrupoExamen> fechasHoraGrupoExamen = fechasHorasExamens.stream()
                    .filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen)).collect(Collectors.toList());
            grupoHorasExamen.setFechasHorasGruposExamen(fechasHoraGrupoExamen);
            grupoHorasExamen.setSemanaExamen(fechasHoraGrupoExamen.get(0).getSemanaExamen());
        }

        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspeciales) {
            List<AlumnoGrupoEspecial> alumnosGrupoEspecial = mapAlumnosGruposEspecialesBySecGpoEspecial.get(seccionGrupoEspecial.getId());
            seccionGrupoEspecial.setAlumnosGrupoEspecial(alumnosGrupoEspecial);

            Seccion seccion = seccionGrupoEspecial.getSeccion();
            List<HorarioSeccion> horariosSeccion = this.allHorarioSeccionWithHours(seccion, rolExamenes, horariosBySeccion);
            seccion.setHorarioSeccion(horariosSeccion);

            SemanaExamen semanaExamenByHorSec = this.findSemanaExamenByHorarioSeccion(seccion.getHorarioSeccion(), semanasByRolExamen);

            int originalDay = seccion.getHorarioSeccion().get(0).getDia().getNumeroDia();
            int currentDay = -1;
            int initialNumeroHora = -1;

            while (currentDay != originalDay) {
                currentDay = originalDay;
                if (initialNumeroHora == -1) {
                    initialNumeroHora = semanaExamenByHorSec.getHoraInicio().getNumero();
                }
                this.reasignarHorarioSeccion(seccion.getHorarioSeccion(), currentDay, initialNumeroHora);
                for (GrupoHorasExamen grupoHorasExamen : gruposHorasExamen) {
                    LetraGrupoRegular letraGrupoRegular = letrasGrupoRegular.stream().filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen)).findFirst().orElse(null);
                    if (seccion.getDiaHoraList().containsAll(grupoHorasExamen.getDiaHoraList())
                            || grupoHorasExamen.getDiaHoraList().containsAll(seccion.getDiaHoraList())) {

                        List<Alumno> alumnos = seccionGrupoEspecial.getAlumnosGrupoEspecial().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
                        List<Aula> aulas = Arrays.asList(seccionGrupoEspecial.getAula());
                        List<Docente> docentes = Arrays.asList(seccionGrupoEspecial.getDocente());

                        boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(rolExamenes, docentes, aulas, alumnos, grupoHorasExamen);
                        boolean validacionGrupoRegular = grupoRegularConnector.validarGrupoRegular(letraGrupoRegular, alumnos, docentes, aulas);

                        if (true) {

                        } else {
                            initialNumeroHora += rolExamenes.getHorasExamen();
                            if (initialNumeroHora > semanaExamenByHorSec.getHoraFin().getNumero()) {
                                initialNumeroHora = -1;
                                currentDay++;
                                if (currentDay > 7) {
                                    currentDay = 1;
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    private List<HorarioSeccion> allHorarioSeccionWithHours(Seccion seccion, RolExamenes rolExamenes, Map horariosBySeccion) {
        List<HorarioSeccion> horariosSeccion = (List<HorarioSeccion>) horariosBySeccion.get(seccion.getId());
        Map<Long, List<HorarioSeccion>> mapGroupByDia = TypesUtil.convertListToMapList("dia.id", horariosSeccion);
        for (Map.Entry<Long, List<HorarioSeccion>> entry : mapGroupByDia.entrySet()) {
            Long key = entry.getKey();
            List<HorarioSeccion> value = entry.getValue();
            if (value.size() < rolExamenes.getHorasExamen()) {
                horariosSeccion.removeIf(x -> x.getDia().getId().compareTo(key) == 0);
            }
        }
        if (horariosSeccion.size() > rolExamenes.getHorasExamen()) {
            List<Dia> removeDays = new ArrayList<>();
            for (int i = rolExamenes.getHorasExamen(); i < horariosSeccion.size(); i++) {
                HorarioSeccion horarioSeccion = horariosSeccion.get(i);
                removeDays.add(horarioSeccion.getDia());
            }
            for (Dia removeDay : removeDays) {
                horariosSeccion.removeIf(x -> x.getDia().equals(removeDay));
            }
        }
        return horariosSeccion;
    }

    public SemanaExamen findSemanaExamenByHorarioSeccion(List<HorarioSeccion> horariosSeccion, List<SemanaExamen> semanasExamen) {

        SEMANA_EXAMEN:
        for (SemanaExamen semanaExamen : semanasExamen) {
            for (HorarioSeccion horarioSeccion : horariosSeccion) {
                if (horarioSeccion.getHora().getNumero() < semanaExamen.getHoraInicio().getNumero()
                        || horarioSeccion.getHora().getNumero() > semanaExamen.getHoraFin().getNumero()) {
                    continue SEMANA_EXAMEN;
                }
            }
            return semanaExamen;
        }
        return null;
    }

    public void reasignarHorarioSeccion(List<HorarioSeccion> horariosSeccion, Integer numeroDia, Integer numeroHoraInicio) {
        HorarioSeccion previousHorarioSeccion = null;
        for (HorarioSeccion horarioSeccion : horariosSeccion) {
            horarioSeccion.getDia().setNumeroDia(numeroDia);
            if (previousHorarioSeccion == null) {
                horarioSeccion.getHora().setNumero(numeroHoraInicio);
            } else {
                horarioSeccion.getHora().setNumero(previousHorarioSeccion.getHora().getNumero() + 1);
            }
            previousHorarioSeccion = horarioSeccion;
        }
    }

}
