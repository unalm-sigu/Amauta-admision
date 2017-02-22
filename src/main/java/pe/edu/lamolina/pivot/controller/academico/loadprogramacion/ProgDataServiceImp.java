package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ProgDataServiceImp implements ProgDataService {

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    AulaDAO aulaDAO;
    @Autowired
    GrupoHorasDAO grupoHorasDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static boolean revisar = true;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, GrupoSeccion> loadDataGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo) {
        int loop = 0;
        Map<String, GrupoSeccion> mapGpoSecciones = new LinkedHashMap();
        for (GrupoSeccion gpoSecc : gruposSecciones) {

            //logger.debug("\tprocesando el gpoSecc {}", gpoSecc.getCodigo());
            GrupoSeccion gpoSeccBD = grupoSeccionDAO.findByCodeCiclo(gpoSecc.getCodigo(), ciclo);
            Curso curso = cursoDAO.findByCode(gpoSecc.getCodigoCurso());
            if (gpoSeccBD == null) {

                gpoSeccBD = new GrupoSeccion();
                gpoSeccBD.setCicloAcademico(ciclo);
                gpoSeccBD.setCodigo(gpoSecc.getCodigo());
                gpoSeccBD.setCurso(curso);
                gpoSeccBD.setVersion("1");
                gpoSeccBD.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
                gpoSeccBD.setEstadoGrupo(EstadoGrupoSeccionEnum.ABI.name());

                grupoSeccionDAO.save(gpoSeccBD);

            } else {
                gpoSeccBD.setVersion(gpoSeccBD.getVersion() == null ? "1" : gpoSeccBD.getVersion());
                gpoSeccBD.setEstadoPlanEnum(gpoSeccBD.getEstadoPlan() == null ? EstadoPlanCalificaEnum.PEND : gpoSeccBD.getEstadoPlanEnum());
                gpoSeccBD.setEstadoGrupo(gpoSeccBD.getEstadoGrupo() == null ? EstadoGrupoSeccionEnum.ABI.name() : gpoSeccBD.getEstadoGrupo());
                grupoSeccionDAO.update(gpoSeccBD);

                Curso cursoBD = gpoSeccBD.getCurso();
                if (curso.getId() != cursoBD.getId().longValue()) {
                    String msg = String.format("El curso del grupo-seccion %s está relacionado al curso %s pero en la base de datos es %s",
                            gpoSecc.getCodigo(), cursoBD.getCodigo(), curso.getCodigo());
                    throw new PhobosException(msg);
                }
            }

            gpoSeccBD.setSecciones(new ArrayList());
            gruposSecciones.set(loop, gpoSeccBD);
            mapGpoSecciones.put(gpoSeccBD.getCodigo(), gpoSeccBD);
            loop++;
        }

        return mapGpoSecciones;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Seccion> loadDataSecciones(List<Seccion> secciones, CicloAcademico ciclo, Map<String, GrupoSeccion> mapGpoSecciones) {
        int loop = 0;
        Map<String, Seccion> mapSecciones = new LinkedHashMap();
        for (Seccion seccion : secciones) {

            //logger.debug("\tprocesando la seccion {}", seccion.getCodigo());
            GrupoSeccion gpoSecc = mapGpoSecciones.get(seccion.getCodigoGrupoSeccion());
            if (gpoSecc == null) {
                String msg = String.format("La seccion %s no tiene su padre grupo-seccion %s",
                        seccion.getCodigo(), seccion.getCodigoGrupoSeccion());
                throw new PhobosException(msg);
            }

            Curso curso = gpoSecc.getCurso();
            Seccion seccionBD = seccionDAO.findByCodeCiclo(seccion.getCodigo(), ciclo);
            GrupoHoras gpoHoras = findGrupoHoras(seccion);
            Aula aula = findAula(seccion);

            if (seccionBD == null) {
                seccionBD = new Seccion();
                seccionBD.setCodigo(seccion.getCodigo());
                seccionBD.setGrupoSeccion(gpoSecc);
                seccionBD.setMatriculados(0);
                seccionBD.setRetirados(0);
                seccionBD.setVacantes(0);
                seccionBD.setEsPrincipal(0);
                seccionBD.setTipoSeccionEnum(TipoSeccionEnum.valueOf(seccion.getCodigoTipoSeccion()));
                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);
                seccionBD.setHorasTeoria(curso.getHorasTeoria());
                seccionBD.setHorasPractica(curso.getHorasPractica());
                seccionBD.setHorasSemanales(curso.getHorasTeoria() + curso.getHorasPractica());
                //seccionBD.setSeccionSuperior(seccionBD);

                seccionDAO.save(seccionBD);
            } else {
                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);
                seccionDAO.update(seccionBD);
            }

            gpoSecc.getSecciones().add(seccionBD);
            seccionBD.setDocenteSeccion(new ArrayList());
            seccionBD.setMatriculaSeccion(new ArrayList());
            secciones.set(loop, seccionBD);
            mapSecciones.put(seccionBD.getCodigo(), seccionBD);
            loop++;
        }

        return mapSecciones;
    }

    private GrupoHoras findGrupoHoras(Seccion seccion) {
        String codigo = seccion.getCodigoGrupoHorario();
        if (StringUtils.isEmpty(codigo)) {
            return null;
        }

        GrupoHoras gpoHoras = grupoHorasDAO.findByCode(codigo);
        if (gpoHoras == null) {
            String msg = String.format("El grupo-horas %s de la seccion %s no existe en la base de datos",
                    codigo, seccion.getCodigo());
            throw new PhobosException(msg);
        }
        return gpoHoras;
    }

    private Aula findAula(Seccion seccion) {
        String codigo = seccion.getCodigoAula();
        if (StringUtils.isEmpty(codigo)) {
            return null;
        }

        Aula aula = aulaDAO.findByCode(codigo);
        if (aula == null) {
            String msg = String.format("El aula %s de la seccion %s no existe en la base de datos",
                    codigo, seccion.getCodigo());
            throw new PhobosException(msg);
        }
        return aula;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Docente> loadDataDocentes(List<Docente> docentes, CicloAcademico ciclo) {
        int loop = 0;
        Map<String, Docente> mapDocentes = new LinkedHashMap();
        for (Docente profe : docentes) {
            //logger.debug("\tprocesando el profesor {}", profe.getCodigo());
            Docente profeBD = docenteDAO.findByCode(profe.getCodigo());
            if (profeBD == null) {
                String msg = String.format("No existe en base de datos el docente de codigo %s", profe.getCodigo());
                throw new PhobosException(msg);
            }

            docentes.set(loop, profeBD);
            mapDocentes.put(profeBD.getCodigo(), profeBD);
            loop++;
        }

        return mapDocentes;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, DocenteSeccion> loadDataDocentesSecciones(
            List<DocenteSeccion> docentesSecciones,
            Map<String, Seccion> mapSecciones,
            Map<String, Docente> mapDocentes) {

        int loop = 0;
        Map<String, DocenteSeccion> mapDocenteSecciones = new LinkedHashMap();
        for (DocenteSeccion profeSecc : docentesSecciones) {
            //logger.debug("\tprocesando el profe-seccion {}-{}", profeSecc.getCodigoDocente(), profeSecc.getCodigoSeccion());
            Seccion seccion = mapSecciones.get(profeSecc.getCodigoSeccion());
            Docente profe = mapDocentes.get(profeSecc.getCodigoDocente());
            if (seccion == null) {
                String msg = String.format("La seccion %s no existe para se incluida en docente-seccion",
                        profeSecc.getCodigoSeccion());
                throw new PhobosException(msg);
            }
            if (profe == null) {
                String msg = String.format("El docente %s no existe para se incluida en docente-seccion",
                        profeSecc.getCodigoDocente());
                throw new PhobosException(msg);
            }

            DocenteSeccion profeSeccBD = docenteSeccionDAO.findByDocenteSeccion(profe, seccion);

            if (profeSeccBD == null) {
                profeSeccBD = new DocenteSeccion();
                profeSeccBD.setDocente(profe);
                profeSeccBD.setSeccion(seccion);
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                docenteSeccionDAO.save(profeSeccBD);

            } else {
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                profeSeccBD.setUserAnulacion(null);
                profeSeccBD.setFechaAnulacion(null);
                docenteSeccionDAO.update(profeSeccBD);
            }

            seccion.getDocenteSeccion().add(profeSeccBD);
            docentesSecciones.set(loop, profeSeccBD);
            mapDocenteSecciones.put(profe.getCodigo() + "-" + seccion.getCodigo(), profeSeccBD);
            loop++;
        }

        return mapDocenteSecciones;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarDocenteSecciones(Map<String, DocenteSeccion> mapDocenteSecciones, CicloAcademico ciclo, DataSessionPivot ds) {
        List<DocenteSeccion> profeSecciones = docenteSeccionDAO.allByCiclo(ciclo);
        for (DocenteSeccion profeSeccBD : profeSecciones) {
            Seccion secc = profeSeccBD.getSeccion();
            Docente profe = profeSeccBD.getDocente();
            //logger.debug("\tprocesando el profe-seccion {}-{}", profe.getCodigo(), secc.getCodigo());

            DocenteSeccion profeSecc = mapDocenteSecciones.get(profe.getCodigo() + "-" + secc.getCodigo());
            if (profeSecc != null) {
                continue;
            }

            profeSeccBD.setEstado(EstadoEnum.INA.name());
            profeSeccBD.setUserAnulacion(ds.getUsuario());
            profeSeccBD.setFechaAnulacion(new Date());
            docenteSeccionDAO.update(profeSeccBD);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loadDataMatriculados(
            MatriculaSeccion matriSecc,
            Map<String, MatriculaResumen> mapResumenes,
            Map<String, Seccion> mapSecciones,
            CicloAcademico ciclo, DataSessionPivot ds) {

        Seccion seccion = mapSecciones.get(matriSecc.getCodigoSeccion());
        if (seccion == null) {
            String msg = String.format("La seccion %s no existe para se incluida en matricula-seccion",
                    matriSecc.getCodigoSeccion());
            throw new PhobosException(msg);
        }

        //ObjectUtil.printAttr(seccion);
        Alumno alumno = alumnoDAO.findByCodigo(matriSecc.getCodigoAlumno());
        if (alumno == null) {
            String msg = String.format("El alumno %s no existe para se incluida en matricula-seccion",
                    matriSecc.getCodigoAlumno());
            throw new PhobosException(msg);
        }
        System.out.println("bloquearemos alumno " + alumno.getCodigo() + " para loadDataMatriculados");
        alumnoDAO.findLock(alumno.getId());
        System.out.println("\talumno " + alumno.getCodigo() + " desbloqueado en loadDataMatriculados");

        MatriculaResumen resumen = mapResumenes.get(alumno.getCodigo());
        if (resumen == null) {
            resumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
            if (resumen != null) {
                resumen.setMatriculaSeccion(new ArrayList());
                resumen.setMatriculaCurso(new ArrayList());
                mapResumenes.put(alumno.getCodigo(), resumen);
            }
        }

        if (resumen == null) {
            resumen = new MatriculaResumen();
            resumen.setAlumno(alumno);
            resumen.setCicloAcademico(ciclo);
            resumen.setCreditosMatriculados(0);
            resumen.setCreditosRetirados(0);
            resumen.setCursosMatriculados(0);
            resumen.setCursosRetirados(0);
            resumen.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            resumen.setNotaAcumulada("0");
            resumen.setNotaAvance("0");
            resumen.setNotaFinal("0");
            resumen.setPorcentajeAvance(0);
            matriculaResumenDAO.save(resumen);

            resumen.setMatriculaSeccion(new ArrayList());
            resumen.setMatriculaCurso(new ArrayList());
            mapResumenes.put(alumno.getCodigo(), resumen);
        }

        resumen.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
        matriculaResumenDAO.update(resumen);

        MatriculaSeccion matriSeccBD = matriculaSeccionDAO.findByAlumnoSeccion(alumno, seccion);
        if (matriSeccBD == null) {
            matriSeccBD = new MatriculaSeccion();
            matriSeccBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            matriSeccBD.setFechaRegistro(new Date());
            matriSeccBD.setUserRegistro(ds.getUsuario());
            matriSeccBD.setSeccion(seccion);
            matriSeccBD.setMatriculaResumen(resumen);
            matriculaSeccionDAO.save(matriSeccBD);

            resumen.getMatriculaSeccion().add(matriSeccBD);
        }

        seccion.getMatriculaSeccion().add(matriSeccBD);
        if (!existeSeccion(resumen.getMatriculaSeccion(), seccion)) {
            resumen.getMatriculaSeccion().add(matriSeccBD);
        }

        matriSeccBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
        matriculaSeccionDAO.update(matriSeccBD);

        Curso curso = seccion.getGrupoSeccion().getCurso();
        MatriculaCurso matriCursoBD = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, ciclo);
        if (matriCursoBD == null) {
            matriCursoBD = new MatriculaCurso();
            matriCursoBD.setCreditos(curso.getCreditos());
            matriCursoBD.setCurso(curso);
            matriCursoBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            matriCursoBD.setMatriculaResumen(resumen);
            matriCursoBD.setNotaAcumulada("0");
            matriCursoBD.setNotaAvance("0");
            matriCursoBD.setNotaFinal("0");
            matriCursoBD.setPorcentajeAvanceNota(0);
            matriculaCursoDAO.save(matriCursoBD);

            resumen.getMatriculaCurso().add(matriCursoBD);
        }

        if (!existeCurso(resumen.getMatriculaCurso(), curso)) {
            resumen.getMatriculaCurso().add(matriCursoBD);
            resumen.setCursosMatriculados(resumen.getCursosMatriculados() + 1);
            resumen.setCreditosMatriculados(resumen.getCreditosMatriculados() + curso.getCreditos());
            matriculaResumenDAO.update(resumen);
        }

        matriCursoBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
        matriculaCursoDAO.update(matriCursoBD);
        matriSecc.setProcesado(1);
    }

    private boolean existeCurso(List<MatriculaCurso> alumnoCursos, Curso curso) {
        for (MatriculaCurso alumnoCurso : alumnoCursos) {
            Curso cur = alumnoCurso.getCurso();
            if (cur.getId().longValue() == curso.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean existeSeccion(List<MatriculaSeccion> alumnoSecciones, Seccion seccion) {
        //System.out.print(seccion.getId() + "-->");
        for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
            Seccion secc = alumnoSeccion.getSeccion();
            //System.out.print(secc.getId() + "::::");
            if (secc.getId().longValue() == seccion.getId()) {
                //System.out.println("");
                return true;
            }
        }
        //System.out.println("");
        return false;
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarAlumnoMatriculado(MatriculaResumen aluResumen, Map<String, MatriculaResumen> mapResumenes, Map<String, String> mapBloqueados) {
        Alumno alumno = aluResumen.getAlumno();

        System.out.println("bloquearemos alumno " + alumno.getCodigo() + " para revisarAlumnoMatriculado");
        alumnoDAO.findLock(alumno.getId());
        mapBloqueados.put(alumno.getCodigo(), System.currentTimeMillis() + "::::revisarAlumnoMatriculado");
        //System.out.println("\talumno " + alumno.getCodigo() + " desbloqueado en revisarAlumnoMatriculado");

        MatriculaResumen resumen = mapResumenes.get(alumno.getCodigo());

        if (resumen == null) {
            aluResumen.setEstadoEnum(EstadoMatriculaCursoEnum.RCI);
            aluResumen.setCreditosRetirados(aluResumen.getCreditosRetirados() + aluResumen.getCreditosMatriculados());
            aluResumen.setCreditosMatriculados(0);
            aluResumen.setCursosRetirados(aluResumen.getCursosRetirados() + aluResumen.getCursosMatriculados());
            aluResumen.setCursosMatriculados(0);
            matriculaResumenDAO.update(aluResumen);

            List<MatriculaCurso> alumnoCursos = matriculaCursoDAO.allByMatriculaResumen(aluResumen);
            for (MatriculaCurso alumnoCurso : alumnoCursos) {
                alumnoCurso.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                matriculaCursoDAO.update(alumnoCurso);
            }

            List<MatriculaSeccion> alumnoSecciones = matriculaSeccionDAO.allByMatriculaSeccion(aluResumen);
            for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
                alumnoSeccion.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                matriculaSeccionDAO.update(alumnoSeccion);
            }
            mapBloqueados.remove(alumno.getCodigo());
            return;
        }

        List<MatriculaCurso> alumnoCursos = matriculaCursoDAO.allByMatriculaResumen(resumen);
        for (MatriculaCurso aluCurso : alumnoCursos) {
            Curso curso = aluCurso.getCurso();

            if (!existeCurso(resumen.getMatriculaCurso(), curso)) {
                resumen.setCursosRetirados(resumen.getCursosRetirados() + 1);
                resumen.setCursosMatriculados(resumen.getCursosMatriculados() - 1);
                resumen.setCreditosRetirados(resumen.getCreditosRetirados() + curso.getCreditos());
                resumen.setCreditosMatriculados(resumen.getCreditosMatriculados() - curso.getCreditos());

                aluCurso.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                matriculaCursoDAO.update(aluCurso);
            }
        }

        List<MatriculaSeccion> alumnoSecciones = matriculaSeccionDAO.allByMatriculaSeccion(resumen);
        for (MatriculaSeccion aluSeccion : alumnoSecciones) {
            Seccion secc = aluSeccion.getSeccion();
            if (!existeSeccion(resumen.getMatriculaSeccion(), secc)) {
                aluSeccion.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                matriculaSeccionDAO.update(aluSeccion);
            }
        }

        matriculaResumenDAO.update(resumen);
        mapBloqueados.remove(alumno.getCodigo());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarSecciones(List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            //logger.debug("\tprocesando la seccion {}", seccion.getCodigo());
            seccion.setMatriculados(seccion.getMatriculaSeccion().size());
            seccionDAO.update(seccion);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarGrupoSecciones(List<GrupoSeccion> gruposSecciones) {
        for (GrupoSeccion gpoSecc : gruposSecciones) {
            //logger.debug("\tprocesando el gpo-seccion {}", gpoSecc.getCodigo());
            Seccion seccSuperior = null;
            List<Seccion> secciones = gpoSecc.getSecciones();
            for (Seccion secc : secciones) {
                if (secc.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                    continue;
                }
                seccSuperior = secc;
                break;
            }
            for (Seccion secc : secciones) {
                if (secc == seccSuperior) {
                    continue;
                }
                secc.setSeccionSuperior(seccSuperior);
                seccionDAO.update(secc);
            }
        }
    }

    @Async
    @Override
    public void revisarBloqueados(Map<String, String> mapBloqueados) {
        for (;;) {
            for (Map.Entry<String, String> bloqueado : mapBloqueados.entrySet()) {
                long ahora = System.currentTimeMillis();
                String alumno = bloqueado.getKey();
                long hora = Long.valueOf(bloqueado.getValue().split("::::")[0]);
                String zona = bloqueado.getValue().split("::::")[1];

                System.out.println("alumno " + alumno + " bloqueado por " + (ahora - hora) + " mseg en " + zona);
            }
            if (!revisar) {
                break;
            }

            long t1 = System.currentTimeMillis();
            for (;;) {
                long t2 = System.currentTimeMillis();
                if ((t2 - t1) > 5000) {
                    break;
                }
            }
        }

    }

    @Override
    public void detenerRevisionBloqueado() {
        revisar = false;
    }

}
