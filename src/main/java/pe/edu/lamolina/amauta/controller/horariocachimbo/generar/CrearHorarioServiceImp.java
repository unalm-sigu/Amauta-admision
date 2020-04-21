package pe.edu.lamolina.amauta.controller.horariocachimbo.generar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.HorarioFallido;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.amauta.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioFallidoDAO;
import pe.edu.lamolina.amauta.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.amauta.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CrearHorarioServiceImp implements CrearHorarioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;

    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;

    @Autowired
    HorarioFallidoDAO horarioFallidoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHorario(
            AlumnoHorario aluHorario,
            List<Curso> cursos,
            List<Seccion> horarioTempo,
            Carrera carrera,
            CicloAcademico ciclo,
            Map<String, HorarioCachimbos> mapHorario,
            Map<Long, CarreraCachimbos> mapCarreraCachimbos,
            Map<Long, List<VacanteAlumno>> vacanteAlumnosMap,
            Acumulador code, DataSessionPivot ds) {

        HorarioCachimbos horario = createHorario(horarioTempo, carrera, ciclo, cursos.size(), mapHorario, code, ds);
        horario.setSuscritos(horario.getSuscritos() + 1);
        aluHorario.setHorarioCachimbos(horario);
        aluHorario.setEstado(EstadoAlumnoHorarioEnum.CHOR);
        CarreraCachimbos cc = mapCarreraCachimbos.get(carrera.getId());
        cc.setConHorario(cc.getConHorario() + 1);
        cc.setSinHorario(cc.getSinHorario() - 1);

        alumnoHorarioDAO.update(aluHorario);
        carreraCachimbosDAO.update(cc);
        horarioCachimbosDAO.updateColumns(horario, "suscritos");

        List<SeccionHorarioCachimbos> seccHorCachimbos = horario.getSeccionHorarioCachimbos();
        for (SeccionHorarioCachimbos seccHorCachimbo : seccHorCachimbos) {
            Seccion secc = seccHorCachimbo.getSeccion();
            secc.setReservados(secc.getReservados() + 1);
            seccionDAO.updateColumns(secc, "reservados");
        }
    }

    private HorarioCachimbos createHorario(
            List<Seccion> horarioTempo,
            Carrera carrera,
            CicloAcademico ciclo,
            int cursos,
            Map<String, HorarioCachimbos> mapHorario,
            Acumulador code, DataSessionPivot ds) {

        Collections.sort(horarioTempo, new Seccion.CompareCodigo());
        String huella = getHorarioString(horarioTempo);
        HorarioCachimbos horarioAnt = mapHorario.get(huella);
        if (horarioAnt != null) {
            return horarioAnt;
        }

        Map<Long, Curso> mapCursos = new LinkedHashMap();
        for (Seccion seccion : horarioTempo) {
            Curso curso = seccion.getGrupoSeccion().getCurso();
            mapCursos.put(curso.getId(), curso);
        }

        Integer vac = getVacanteMinima(horarioTempo);
        HorarioCachimbos horario = new HorarioCachimbos();
        horario.setCapacidad(vac);
        horario.setCarrera(carrera);
        horario.setCicloAcademico(ciclo);
        horario.setCursos(mapCursos.size());
        horario.setMatriculados(0);
        horario.setSuscritos(0);
        horario.setSeccionHorarioCachimbos(new ArrayList());
        horario.setCodigo("H-" + NumberFormat.codigo(code.getValor(), 3));
        horarioCachimbosDAO.save(horario);
        code.incrementar();

        for (Seccion seccion : horarioTempo) {
            SeccionHorarioCachimbos sh = new SeccionHorarioCachimbos();
            sh.setHorarioCachimbos(horario);
            sh.setSeccion(seccion);
            sh.setUserCreacion(ds.getUsuario());
            sh.setFechaCreacion(new Date());
            seccionHorarioCachimbosDAO.save(sh);
            horario.getSeccionHorarioCachimbos().add(sh);
        }
        mapHorario.put(huella, horario);
        return horario;
    }

    private String getHorarioString(List<Seccion> horarioTempo) {
        String horario = "[";
        for (Seccion seccion : horarioTempo) {
            horario += horario.equals("[") ? "" : ",";
            horario += seccion.getCodigo();
        }
        horario += "]";
        return horario;
    }

    private Integer getVacanteMinima(List<Seccion> horarioTempo) {
        Integer vac = 1000;
        for (Seccion seccion : horarioTempo) {
            Integer vacSecc = seccion.getVacantes();
            Integer matSecc = 0;
            if (vacSecc == null) {
                vacSecc = 0;
            }
            vac = ((vacSecc - matSecc) < vac) ? (vacSecc - matSecc) : vac;
        }
        return vac;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHorarioFallido(
            Map<Long, Map<String, String>> mapFallidosCarrera,
            List<Carrera> carreras,
            CicloAcademico ciclo,
            DataSessionPivot ds) {

        for (Carrera carrera : carreras) {
            Map<String, String> mapFallidos = mapFallidosCarrera.get(carrera.getId());
            if (mapFallidos == null) {
                continue;
            }

            Iterator<Map.Entry<String, String>> iterator = mapFallidos.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();

                HorarioFallido horarioFalla = new HorarioFallido();
                horarioFalla.setCarrera(carrera);
                horarioFalla.setCicloAcademico(ciclo);
                horarioFalla.setUserRegistro(ds.getUsuario());
                horarioFalla.setFechaRegistro(new Date());
                horarioFalla.setSecciones(entry.getKey());
                horarioFalla.setFalla(entry.getValue());
                horarioFallidoDAO.save(horarioFalla);

            }
        }
    }

}
