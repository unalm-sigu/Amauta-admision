package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.generar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoVacanteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionCursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.misc.Acumulador;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CrearHorarioServiceImp implements CrearHorarioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    CursoCachimbosDAO cursoCachimbosDAO;
    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;
    @Autowired
    CarreraDAO carreraDAO;

//    @Autowired
//    CursoDAO cursoDAO;
//    @Autowired
//    DiaDAO diaDAO;
//    @Autowired
//    HoraDAO horaDAO;
//    @Autowired
//    HorarioSeccionDAO horarioSeccionDAO;
    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;

//    @Autowired
//    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    SeccionDAO seccionDAO;

//    @Autowired
//    SeccionCursoCachimbosDAO seccionCursoCachimbosDAO;
    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;

//    @Autowired
//    GrupoSeccionDAO grupoSeccionDAO;
//    @Autowired
//    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHorario(
            AlumnoHorario alumno,
            List<Curso> cursos,
            List<Seccion> horarioTempo,
            Carrera carrera,
            CicloAcademico ciclo,
            Map<String, HorarioCachimbos> mapHorario,
            Map<Long, CarreraCachimbos> mapCarreraCachimbos,
            Map<Long, List<VacanteAlumno>> vacanteAlumnosMap,
            Acumulador code, DataSessionPivot ds) {
        
        HorarioCachimbos horario = createHorario(horarioTempo, carrera, ciclo, cursos.size(), mapHorario, code, ds);
        //HorarioCachimbos horario = createHorario(horarioTempo, carrera, ciclo, cursos.size(), mapHorario, code, ds);
        horario.setSuscritos(horario.getSuscritos() + 1);
        alumno.setHorarioCachimbos(horario);
        alumno.setEstado(EstadoAlumnoHorarioEnum.CHOR);
        CarreraCachimbos cc = mapCarreraCachimbos.get(carrera.getId());
        cc.setConHorario(cc.getConHorario() + 1);
        cc.setSinHorario(cc.getSinHorario() - 1);
        
        alumnoHorarioDAO.update(alumno);
        carreraCachimbosDAO.update(cc);

        List<SeccionHorarioCachimbos> seccHorCachimbos = horario.getSeccionHorarioCachimbos();
        for (SeccionHorarioCachimbos seccHorCachimbo : seccHorCachimbos) {
            Seccion secc = seccHorCachimbo.getSeccion();
//                        secc.setReservados(secc.getReservados() + 1);
            this.updateSeccionReserva(secc, alumno, vacanteAlumnosMap, ds);
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
            Integer matSecc = 0; //seccion.getMatriculados();
            if (vacSecc == null) {
                vacSecc = 0;
            }
            if (matSecc == null) {
                matSecc = 0;
            }
            vac = ((vacSecc - matSecc) < vac) ? (vacSecc - matSecc) : vac;
        }
        return vac;
    }
    
    private void updateSeccionReserva(Seccion seccion, AlumnoHorario alumnoHorario, Map<Long, List<VacanteAlumno>> vacanteAlumnosMap, DataSessionPivot ds) {

        Alumno alumno = alumnoHorario.getAlumno();

        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnosMap.get(seccion.getId());

        if (vacanteAlumnos == null) {
            vacanteAlumnos = new ArrayList();
        }

        if (vacanteAlumnos.isEmpty()) {
            for (int i = 0; i < seccion.getVacantes(); i++) {
                int conteo = (i + 1);
                VacanteAlumno vacanteAlumno = new VacanteAlumno();
                vacanteAlumno.setEstado(AlumnoVacanteEstadoEnum.LIBE.name());
                vacanteAlumno.setNumero(conteo);
                vacanteAlumno.setSeccion(seccion);
                vacanteAlumno.setActivo(1);
                vacanteAlumno.setUserRegistro(ds.getUsuario());
                vacanteAlumno.setFechaRegistro(new Date());
                if (conteo == 1) {
                    vacanteAlumno.setAlumno(alumno);
                    vacanteAlumno.setEstado(AlumnoVacanteEstadoEnum.RESV.name());
                }
                vacanteAlumnoDAO.save(vacanteAlumno);
                vacanteAlumnos.add(vacanteAlumno);
            }
        } else {
            Collections.sort(vacanteAlumnos, new VacanteAlumno.CompareOrden());
            Iterator<VacanteAlumno> vacanteIterator = vacanteAlumnos.iterator();
            while (vacanteIterator.hasNext()) {
                VacanteAlumno vacanteAlumno = vacanteIterator.next();
                if (AlumnoVacanteEstadoEnum.LIBE.name().equals(vacanteAlumno.getEstado())) {
                    vacanteAlumno.setAlumno(alumno);
                    vacanteAlumno.setEstado(AlumnoVacanteEstadoEnum.RESV.name());
                    vacanteAlumnoDAO.update(vacanteAlumno);
                    break;
                }
            }
        }

        vacanteAlumnosMap.put(seccion.getId(), vacanteAlumnos);
        seccion.setReservados(seccion.getReservados() + 1);
        logger.debug("Aumentando la cantidad de reservados {} de la seccion {}", seccion.getReservados(), seccion.getId());
        seccionDAO.update(seccion);
    }


}
