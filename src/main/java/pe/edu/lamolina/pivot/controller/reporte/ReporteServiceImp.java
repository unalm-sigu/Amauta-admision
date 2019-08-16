package pe.edu.lamolina.pivot.controller.reporte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImp implements ReporteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;
    
    
    @Autowired
    OficinaDAO oficinaDAO;

    @Override
    public List<Hora> allHorario(AlumnoHorario alumno) {

        List<HorarioSeccion> seccionesHorarios = this.allSeccionHorarioAlumnoByAlumnoCicloACademico(alumno);

        Map<Long, List<HorarioSeccion>> mapHorariosSeccionHora = TypesUtil.convertListToMapList("hora.id", seccionesHorarios);
        Map<Long, Hora> seccionHorarioHorasMap = TypesUtil.convertListToMap("hora.id", "hora", seccionesHorarios);

        List<Seccion> secciones = seccionesHorarios.stream().map(HorarioSeccion::getSeccion).collect(Collectors.toList());

        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allDocenteSeccionPrincipalBySeccion(secciones);
        Map<Long, DocenteSeccion> docenteSeccionesMap = TypesUtil.convertListToMap("seccion.id", docenteSecciones);

        List<Dia> dias = diaDAO.allOrderDias();

        List<Hora> horas = new ArrayList(seccionHorarioHorasMap.values());
    
        horas = horas.isEmpty() ? horaDAO.allHoras() : horas;
        Collections.sort(horas, new Hora.CompareCodigo());

        for (Hora hora : horas) {
            List<HorarioSeccion> horariosSeccionesHora = mapHorariosSeccionHora.get(hora.getId());
            horariosSeccionesHora = (horariosSeccionesHora == null) ? new ArrayList() : horariosSeccionesHora;
            Map<Long, List<HorarioSeccion>> mapHorarioSeccionDia = TypesUtil.convertListToMapList("dia.id", horariosSeccionesHora);
            List<Dia> diass = new ArrayList();

            for (Dia dia : dias) {
                Dia diaClone = dia.clone();

                List<HorarioSeccion> horariosSeccionesDia = mapHorarioSeccionDia.get(dia.getId());
                horariosSeccionesDia = (horariosSeccionesDia == null) ? new ArrayList() : horariosSeccionesDia;

                diaClone.setHorarioSeccion(horariosSeccionesDia);
                List<DocenteSeccion> listDs = new ArrayList<>();

                if (!diaClone.getHorarioSeccion().isEmpty()) {
                    for (HorarioSeccion horarioSeccion : diaClone.getHorarioSeccion()) {
                        DocenteSeccion dss = docenteSeccionesMap.get(horarioSeccion.getSeccion().getId());
                        if (dss != null) {
                            listDs.add(dss);
                        }
                    }
                    diaClone.getHorarioSeccion().get(0).getSeccion().setDocenteSeccion(listDs);
                }
                diass.add(diaClone);
            }
            hora.setDias(diass);
        }
        return horas;

    }

    @Override
    public List<Hora> allHorasEscuela() {

        return horaDAO.allHorasByRango(8, 18);
    }

    @Override
    public List<Dia> allDiaForPrinter() {
        return diaDAO.allDiaForPrinter();
    }

    private List<HorarioSeccion> allSeccionHorarioAlumnoByAlumnoCicloACademico(AlumnoHorario alumnoHorario ) {
        
        List<SeccionHorarioCachimbos> seccionHorarioCachimbos = seccionHorarioCachimbosDAO.allByHorarioCachimbos(alumnoHorario.getHorarioCachimbos());
       
        if (seccionHorarioCachimbos.isEmpty()) {
            return new ArrayList();
        }

        List<Seccion> secciones = new ArrayList();
        for (SeccionHorarioCachimbos seccionHorarioCachimbo : seccionHorarioCachimbos) {
            secciones.add(seccionHorarioCachimbo.getSeccion());
        }
        
        return horarioSeccionDAO.allBySecciones(secciones);
    }

    @Override
    public List<AlumnoHorario> allAlumnoHorario(CicloAcademico ciclo) {
        return alumnoHorarioDAO.allByCicloAcademicoOrder(ciclo);
    }

    @Override
    public List<Oficina> allOficinaConsejero() {
        return oficinaDAO.allOficinaConsejero();
    }

}
