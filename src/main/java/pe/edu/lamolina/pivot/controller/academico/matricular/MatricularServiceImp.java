package pe.edu.lamolina.pivot.controller.academico.matricular;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoVacanteAlumnoEnum;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.model.Notificacion;

@Service
@Transactional(readOnly = true)
public class MatricularServiceImp implements MatricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSimultaneoDAO matriculaSimultaneoDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public TurnoAtencion findTurnoAtencion(Long turnoAtencion) {
        return turnoAtencionDAO.findById(turnoAtencion);
    }

    @Override
    @Transactional
    public void matricular(TurnoAtencion turnoAtencion, DataSessionPivot ds) {

        //logger.debug("init matricula");
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        Usuario usuario = ds.getUsuario();

        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allNoMatriculadoByCiclo(cicloAcademico);
        //logger.debug("cantidad matricula resumen NMAT {}", matriculaResumens.size());

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaCurso>> matriculaCursosMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaCursos);
        //logger.debug("cantidad matricula curso NMAT {}", matriculaCursosMap.size());

        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaSeccion>> matriculaSeccionsMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaSeccions);
        //logger.debug("cantidad matricula seccion NMAT {}", matriculaSeccionsMap.size());
        List<Seccion> secciones = matriculaSeccions.stream().map(MatriculaSeccion::getSeccion).collect(Collectors.toList());

        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allActivoBySeccion(secciones);
        Map<Long, List<VacanteAlumno>> vacanteAlumnosMap = TypesUtil.convertListToMapList("seccion.id", vacanteAlumnos);
        //logger.debug("cantidad vacantes alumnos  {}", vacanteAlumnosMap.size());

        List<MatriculaSimultaneo> matriculaSimultaneos = matriculaSimultaneoDAO.allByMatriculaCurso(matriculaCursos);
        Map<Long, List<MatriculaSimultaneo>> matriculaSimultaneosMap = TypesUtil.convertListToMapList("matriculaCurso.id", matriculaSimultaneos);
        Notificacion notify = new Notificacion();
        notify.setTotal(matriculaResumens.size());
        this.notify(notify, usuario);
        for (MatriculaResumen mr : matriculaResumens) {

            Alumno alumno = mr.getAlumno();
            //logger.debug("alumno  {}", alumno.getId(), alumno.getCodigo());

            List<MatriculaCurso> misMatriculaCurso = matriculaCursosMap.get(mr.getId());
            Map<Long, MatriculaCurso> misMatriculaCursoMap = TypesUtil.convertListToMap("curso.id", misMatriculaCurso);
            //logger.debug("cantidad de cursos a matricular  {}", misMatriculaCursoMap.size());

            List<MatriculaSeccion> misMatriculaSeccions = matriculaSeccionsMap.get(mr.getId());
            Map<Long, MatriculaSeccion> misMatriculaSeccionMap = TypesUtil.convertListToMap("seccion.id", misMatriculaSeccions);
            //logger.debug("cantidad de secciones a matricular  {}", misMatriculaCurso.size());

            List<Long> pendientesMatriculaSeccion = new ArrayList();

            for (MatriculaSeccion ms : misMatriculaSeccionMap.values()) {

                Seccion seccion = ms.getSeccion();
                //logger.debug("matriculando a seccion   {} {} ", seccion.getId(), seccion.getCodigo());
                //logger.debug("vacantes en  seccion   {}  ", seccion.getVacantes());
                //logger.debug("reservados en  seccion   {}  ", seccion.getReservados());
                //logger.debug("matriculados en  seccion   {} ", seccion.getMatriculados());
                int disponibles = seccion.getVacantes() - (seccion.getReservados() + seccion.getMatriculados());
                //logger.debug("disponibles en  seccion   {}  ", disponibles);

                if (seccion.getVacantes() < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    continue;
                }

                if (disponibles < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    continue;
                }

                VacanteAlumno vacante = this.getVacanteAlumno(vacanteAlumnosMap, seccion, usuario);
                if (vacante == null) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    continue;
                }

                GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
                //logger.debug("matriculando a grupo seccion   {} {} ", grupoSeccion.getId(), grupoSeccion.getCodigo());
                Curso curso = grupoSeccion.getCurso();
                //logger.debug("matriculando a curso  {} {} ", curso.getId(), curso.getNombre());

                MatriculaCurso mc = misMatriculaCursoMap.get(curso.getId());

                List<MatriculaSimultaneo> misMatriculaSimultaneo = matriculaSimultaneosMap.get(mc.getId());
                if (misMatriculaSimultaneo == null || misMatriculaSimultaneo.isEmpty()) {

                    mc.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaCursoDAO.update(mc);

                    mr.setCreditosMatriculados(curso.getCreditos() + mr.getCreditosMatriculados());
                    matriculaResumenDAO.update(mr);

                    ms.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaSeccionDAO.update(ms);

                    vacante.setAlumno(alumno);
                    vacante.setEstadoEnum(EstadoVacanteAlumnoEnum.OCUP);
                    vacanteAlumnoDAO.update(vacante);

                } else {

                    pendientesMatriculaSeccion.add(ms.getId());

                }

            }

            //logger.debug("requieren simultaneo {}", pendientesMatriculaSeccion.size());
            for (Long idSeccionSimultaneo : pendientesMatriculaSeccion) {

                MatriculaSeccion ms = misMatriculaSeccionMap.get(idSeccionSimultaneo);

                Seccion seccion = ms.getSeccion();
                //logger.debug("matriculando a seccion   {} {} ", seccion.getId(), seccion.getCodigo());
                //logger.debug("vacantes en  seccion   {}  ", seccion.getVacantes());
                //logger.debug("reservados en  seccion   {} ", seccion.getReservados());
                //logger.debug("matriculados en  seccion   {}  ", seccion.getMatriculados());
                int disponibles = seccion.getVacantes() - (seccion.getReservados() + seccion.getMatriculados());
                //logger.debug("disponibles en  seccion   {} ", disponibles);

                if (disponibles < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    continue;
                }

                VacanteAlumno vacante = this.getVacanteAlumno(vacanteAlumnosMap, seccion, usuario);
                if (vacante == null) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    continue;
                }

                GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
                //logger.debug("matriculando a grupo seccion   {} {} ", grupoSeccion.getId(), grupoSeccion.getCodigo());
                Curso curso = grupoSeccion.getCurso();
                //logger.debug("matriculando a curso  {} {} ", curso.getId(), curso.getNombre());
                MatriculaCurso matriculaCurso = misMatriculaCursoMap.get(curso.getId());

                List<MatriculaSimultaneo> misMatriculaSimultaneo = matriculaSimultaneosMap.get(curso.getId());
                boolean cumpleSimultaneo = true;

                for (MatriculaSimultaneo matriculaSimultaneo : misMatriculaSimultaneo) {
                    MatriculaCurso matriculaCursoSimultaneo = misMatriculaCursoMap.get(matriculaSimultaneo.getMatriculaCursoSimultaneo().getId());
                    if (matriculaCursoSimultaneo.getEstadoEnum() == EstadoMatriculaEnum.NMAT) {
                        cumpleSimultaneo = false;
                    }
                }

                if (cumpleSimultaneo) {

                    matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaCursoDAO.update(matriculaCurso);

                    mr.setCreditosMatriculados(curso.getCreditos() + mr.getCreditosMatriculados());
                    matriculaResumenDAO.update(mr);

                    ms.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaSeccionDAO.update(ms);

                    vacante.setAlumno(alumno);
                    vacante.setEstadoEnum(EstadoVacanteAlumnoEnum.OCUP);
                    vacanteAlumnoDAO.update(vacante);

                } else {

                    ms.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                    matriculaSeccionDAO.update(ms);

                }
            }

            notify.setProcesados(notify.getProcesados() + 1);
            this.notify(notify, usuario);
        }
    }

    @Override
    public Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaCursoDAO.countAllAlumnoPrematriculado(cicloAcademico);
    }

    @Override
    public Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaSeccionDAO.countAllSeccionPrematriculado(cicloAcademico);
    }

    private VacanteAlumno getVacanteAlumno(Map<Long, List<VacanteAlumno>> vacanteAlumnosMap, Seccion seccion, Usuario usuario) {
        logger.debug("iniciando buequeda de vacante alumno");
        int vacantes = seccion.getVacantes();
        logger.debug("cantidad de vacantes en la seccion {} {}", seccion.getId(), vacantes);
        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnosMap.get(seccion.getId());
        if (vacanteAlumnos == null || vacanteAlumnos.isEmpty()) {
            vacanteAlumnos = new ArrayList();
            int cantidadVacantes = seccion.getVacantes();
            for (int i = 1; i <= cantidadVacantes; i++) {
                VacanteAlumno va = new VacanteAlumno();
                va.setNumero(i);
                va.setSeccion(seccion);
                va.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                va.setFechaRegistro(new Date());
                va.setUserRegistro(usuario);
                va.setActivo(1);
                vacanteAlumnoDAO.save(va);
                vacanteAlumnos.add(va);
            }
            vacanteAlumnosMap.put(seccion.getId(), vacanteAlumnos);
            return vacanteAlumnos.get(0);
        }
        int vacantesActuales = vacanteAlumnos.size();
        if (vacantesActuales < vacantes) {
            int delta = vacantes - vacanteAlumnos.size();
            for (int i = 1; i <= delta; i++) {
                VacanteAlumno va = new VacanteAlumno();
                va.setNumero(i + vacantesActuales);
                va.setSeccion(seccion);
                va.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                va.setFechaRegistro(new Date());
                va.setUserRegistro(usuario);
                va.setActivo(1);
                vacanteAlumnoDAO.save(va);
                vacanteAlumnos.add(va);
            }
            vacanteAlumnosMap.put(seccion.getId(), vacanteAlumnos);
        }
        logger.debug("vacantes para la  seccion   {}  ", vacanteAlumnos.size());
        Map<Integer, VacanteAlumno> vacantesMap = TypesUtil.convertListToMap("numero", vacanteAlumnos);
        logger.debug("vacantes para la  seccion after map  {} vacantes {} ", vacantesMap.size(), vacantes);
        logger.debug("*******estado del map  key ");
        for (Integer integer : vacantesMap.keySet()) {
            logger.debug("*******estado del map key {}", integer);
        }
        VacanteAlumno vacanteAlumno = null;
        logger.debug("vacantes on  seccion   {} ", vacantes);
        for (int i = 1; i <= vacantes; i++) {
            logger.debug("buscando la vacante     {} en un total de {}  ", i, vacantesMap.size());
            vacanteAlumno = vacantesMap.get(i);
            logger.debug("vacanteAlumno   {} ", (vacanteAlumno != null));
            if (vacanteAlumno.getEstadoEnum() != EstadoVacanteAlumnoEnum.DISP) {
                vacanteAlumno = null;
            } else {
                break;
            }
        }
        return vacanteAlumno;
    }

    private void notify(Notificacion notify, Usuario usuario) {
        messagingTemplate.convertAndSendToUser(usuario.getUsuario(), "/progreso/notify", notify);
    }

}
