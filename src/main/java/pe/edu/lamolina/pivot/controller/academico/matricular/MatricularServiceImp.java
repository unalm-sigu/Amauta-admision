package pe.edu.lamolina.pivot.controller.academico.matricular;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
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
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
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
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public TurnoAtencion findTurnoAtencion(Long turnoAtencion) {
        return turnoAtencionDAO.findById(turnoAtencion);
    }

    @Override
    @Transactional
    public void matricular(TurnoAtencion turnoAtencionForm, DataSessionPivot ds) {

        logger.debug("**init matricula**");
        Date date = new Date();
        TurnoAtencion turnoAtencion = turnoAtencionDAO.findById(turnoAtencionForm.getId());
        boolean beetwen = date.after(turnoAtencion.getFechaHoraEspera()) && date.before(turnoAtencion.getFechaHoraFin());

        logger.debug("date {} inicio {} fin {} ",
                new DateTime(date).toString("dd/MM/yyyy HH:mm"),
                new DateTime(turnoAtencion.getFechaHoraEspera()).toString("dd/MM/yyyy HH:mm"),
                new DateTime(turnoAtencion.getFechaHoraFin()).toString("dd/MM/yyyy HH:mm")
        );

        if (!beetwen) {
            //throw new PhobosException("No está dentro del horario de matrícula");
        }
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        Usuario usuario = ds.getUsuario();

        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allNoMatriculadoByCiclo(cicloAcademico);

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaCurso>> matriculaCursosMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaCursos);

        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaSeccion>> matriculaSeccionsMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaSeccions);
        List<Seccion> secciones = matriculaSeccions.stream().map(MatriculaSeccion::getSeccion).collect(Collectors.toList());

        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allActivoBySeccion(secciones);
        Map<Long, List<VacanteAlumno>> vacanteAlumnosMap = TypesUtil.convertListToMapList("seccion.id", vacanteAlumnos);

        logger.debug("cantidad matricula resumen NMAT {} curso PMAT {} seccion PMAT {} vacantes alumnos  {} ",
                matriculaResumens.size(),
                matriculaCursosMap.size(),
                matriculaSeccionsMap.size(),
                vacanteAlumnosMap.size()
        );

        List<MatriculaSimultaneo> matriculaSimultaneos = matriculaSimultaneoDAO.allByMatriculaCurso(matriculaCursos);
        Map<Long, List<MatriculaSimultaneo>> matriculaSimultaneosMap = TypesUtil.convertListToMapList("matriculaCurso.id", matriculaSimultaneos);

        Notificacion notify = new Notificacion();
        notify.setTotalCurso(matriculaCursos.size());
        notify.setTotalSeccion(matriculaSeccions.size());
        this.notify(notify, usuario);

        for (MatriculaResumen mr : matriculaResumens) {

            Alumno alumno = mr.getAlumno();

            List<MatriculaCurso> misMatriculaCurso = matriculaCursosMap.get(mr.getId());
            if (misMatriculaCurso == null) {
                StringBuilder sd = new StringBuilder();
                sd.append("alumno ");
                sd.append(alumno.getCodigo());
                sd.append(" no tiene cursos prematriculados ");
                notify.setMessage(sd.toString());
                notify.setState(false);
                this.notify(notify, usuario);
                continue;
            }
            Map<Long, MatriculaCurso> misMatriculaCursoMap = TypesUtil.convertListToMap("curso.id", misMatriculaCurso);

            List<MatriculaSeccion> misMatriculaSeccions = matriculaSeccionsMap.get(mr.getId());
            Map<Long, MatriculaSeccion> misMatriculaSeccionMap = TypesUtil.convertListToMap("seccion.id", misMatriculaSeccions);

            logger.debug("== alumno  {} {} cantidad de cursos a matricular  {} cantidad de secciones a matricular  {} ==",
                    alumno.getId(), alumno.getCodigo(),
                    misMatriculaCursoMap.size(),
                    misMatriculaCurso.size()
            );

            List<Long> pendientesMatriculaSeccion = new ArrayList();

            for (MatriculaSeccion ms : misMatriculaSeccionMap.values()) {

                Seccion seccion = ms.getSeccion();
                int disponibles = seccion.getVacantes() - (seccion.getReservados() + seccion.getMatriculados());
                logger.debug("matriculando a seccion   {} {} vacantes {} reservados {} matriculados {} disponibles {} ",
                        seccion.getId(),
                        seccion.getCodigo(),
                        seccion.getVacantes(),
                        seccion.getReservados(),
                        seccion.getMatriculados(),
                        disponibles
                );

                grupoSeccionDAO.findLock(seccion.getGrupoSeccion().getId());

                if (seccion.getVacantes() < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);
                    StringBuilder sd = new StringBuilder();
                    sd.append("alumno ");
                    sd.append(alumno.getCodigo());
                    sd.append(" no hay vacantes en la clave ");
                    sd.append(seccion.getCodigo());
                    notify.setMessage(sd.toString());
                    notify.setState(false);
                    this.notify(notify, usuario);
                    continue;
                }

                if (disponibles < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);
                    StringBuilder sd = new StringBuilder();
                    sd.append("alumno ");
                    sd.append(alumno.getCodigo());
                    sd.append(" no hay vacantes disponibles en la clave ");
                    sd.append(seccion.getCodigo());
                    notify.setMessage(sd.toString());
                    notify.setState(false);
                    this.notify(notify, usuario);
                    continue;
                }

                VacanteAlumno vacante = this.getVacanteAlumno(vacanteAlumnosMap, seccion, usuario);
                if (vacante == null) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);
                    StringBuilder sd = new StringBuilder();
                    sd.append("alumno ");
                    sd.append(alumno.getCodigo());
                    sd.append(" no hay vacante alumno en la clave ");
                    sd.append(seccion.getCodigo());
                    notify.setMessage(sd.toString());
                    notify.setState(false);
                    this.notify(notify, usuario);
                    continue;
                }

                GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
                Curso curso = grupoSeccion.getCurso();

                logger.debug("matriculando a grupo seccion   {} {} matriculando a curso  {} {} ",
                        grupoSeccion.getId(), grupoSeccion.getCodigo(),
                        curso.getId(), curso.getNombre()
                );

                MatriculaCurso mc = misMatriculaCursoMap.get(curso.getId());

                List<MatriculaSimultaneo> misMatriculaSimultaneo = matriculaSimultaneosMap.get(mc.getId());
                if (misMatriculaSimultaneo == null || misMatriculaSimultaneo.isEmpty()) {

                    if (mc.getEstadoEnum() != EstadoMatriculaEnum.MAT) {
                        mc.setEstadoEnum(EstadoMatriculaEnum.MAT);
                        matriculaCursoDAO.update(mc);
                        notify.setCurrentCurso(notify.getCurrentCurso() + 1);
                    }

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

                notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);

            }

            logger.debug("requieren simultaneo {}", pendientesMatriculaSeccion.size());
            for (Long idSeccionSimultaneo : pendientesMatriculaSeccion) {

                MatriculaSeccion ms = misMatriculaSeccionMap.get(idSeccionSimultaneo);

                Seccion seccion = ms.getSeccion();

                int disponibles = seccion.getVacantes() - (seccion.getReservados() + seccion.getMatriculados());
                logger.debug("matriculando a seccion simultaneo   {} {} vacantes {} reservados {} matriculados {} disponibles {} ",
                        seccion.getId(),
                        seccion.getCodigo(),
                        seccion.getVacantes(),
                        seccion.getReservados(),
                        seccion.getMatriculados(),
                        disponibles
                );

                if (disponibles < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);
                    StringBuilder sd = new StringBuilder();
                    sd.append("alumno ");
                    sd.append(alumno.getCodigo());
                    sd.append(" no hay vacante disponible en la clave ");
                    sd.append(seccion.getCodigo());
                    notify.setMessage(sd.toString());
                    notify.setState(false);
                    this.notify(notify, usuario);
                    continue;
                }

                VacanteAlumno vacante = this.getVacanteAlumno(vacanteAlumnosMap, seccion, usuario);
                if (vacante == null) {
                    logger.debug("no hay vacante alumno para ms   {} ", ms.getId());
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);
                    StringBuilder sd = new StringBuilder();
                    sd.append("alumno ");
                    sd.append(alumno.getCodigo());
                    sd.append(" no hay vacante alumno en la clave ");
                    sd.append(seccion.getCodigo());
                    notify.setMessage(sd.toString());
                    notify.setState(false);
                    this.notify(notify, usuario);
                    continue;
                }

                GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
                Curso curso = grupoSeccion.getCurso();

                logger.debug("matriculando a grupo seccion   {} {} matriculando a curso  {} {} ",
                        grupoSeccion.getId(), grupoSeccion.getCodigo(),
                        curso.getId(), curso.getNombre()
                );

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

                    if (matriculaCurso.getEstadoEnum() != EstadoMatriculaEnum.MAT) {
                        matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
                        matriculaCursoDAO.update(matriculaCurso);
                        notify.setCurrentCurso(notify.getCurrentCurso() + 1);
                    }

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
                notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);
            }
            notify.setState(true);
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
//        logger.debug("iniciando buequeda de vacante alumno");
        int vacantes = seccion.getVacantes();
//        logger.debug("cantidad de vacantes en la seccion {} {}", seccion.getId(), vacantes);
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
//        logger.debug("vacantes para la  seccion   {}  ", vacanteAlumnos.size());
        Map<Integer, VacanteAlumno> vacantesMap = TypesUtil.convertListToMap("numero", vacanteAlumnos);
//        logger.debug("vacantes para la  seccion after map  {} vacantes {} ", vacantesMap.size(), vacantes);
//        logger.debug("*******estado del map  key ");
        for (Integer integer : vacantesMap.keySet()) {
//            logger.debug("*******estado del map key {}", integer);
        }
        VacanteAlumno vacanteAlumno = null;
//        logger.debug("vacantes on  seccion   {} ", vacantes);
        for (int i = 1; i <= vacantes; i++) {
//            logger.debug("buscando la vacante     {} en un total de {}  ", i, vacantesMap.size());
            vacanteAlumno = vacantesMap.get(i);
//            logger.debug("vacanteAlumno   {} ", (vacanteAlumno != null));
            if (vacanteAlumno.getEstadoEnum() != EstadoVacanteAlumnoEnum.DISP) {
                vacanteAlumno = null;
            } else {
                break;
            }
        }
        return vacanteAlumno;
    }

    private void notify(Notificacion notify, Usuario usuario) {
        messagingTemplate.convertAndSendToUser(usuario.getUsuario(), "/monitoreo/notify", notify);
    }

}
