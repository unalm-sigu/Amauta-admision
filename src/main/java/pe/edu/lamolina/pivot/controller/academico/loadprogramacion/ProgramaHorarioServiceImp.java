package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ProgramaHorarioServiceImp implements ProgramaHorarioService {

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

    @Override
    @Transactional
    public void loadArchivosHorario(MultipartFile[] files, CicloAcademico ciclo, DataSessionPivot ds) {
        String rutaFileGpoSecciones = saveFile(files[0]);
        String rutaFileSecciones = saveFile(files[1]);
        String rutaFilePersonas = saveFile(files[2]);
        String rutaFileProfes = saveFile(files[3]);
        String rutaFileProfeSecciones = saveFile(files[4]);
        String rutaFileAlumnoSecciones = saveFile(files[5]);

        List<GrupoSeccion> gruposSecciones = crearGruposSecciones(rutaFileGpoSecciones);
        List<Seccion> secciones = crearSecciones(rutaFileSecciones);
        List<Persona> personas = crearPersonas(rutaFilePersonas);
        List<Docente> docentes = crearDocentes(rutaFileProfes);
        List<DocenteSeccion> docentesSecciones = crearDocenteSecciones(rutaFileProfeSecciones);
        List<MatriculaSeccion> matriculaSecciones = crearMatriculasSecciones(rutaFileAlumnoSecciones);

        logger.debug("loadDataGpoSecciones");
        Map<String, GrupoSeccion> mapGpoSecciones = loadDataGpoSecciones(gruposSecciones, ciclo);
        logger.debug("loadDataSecciones");
        Map<String, Seccion> mapSecciones = loadDataSecciones(secciones, ciclo, mapGpoSecciones);
        logger.debug("loadDataDocentes");
        Map<String, Docente> mapDocentes = loadDataDocentes(docentes, ciclo);
        logger.debug("loadDataDocentesSecciones");
        Map<String, DocenteSeccion> mapDocenteSecciones = loadDataDocentesSecciones(docentesSecciones, mapSecciones, mapDocentes);

        logger.debug("revisarDocenteSecciones");
        revisarDocenteSecciones(mapDocenteSecciones, ciclo, ds);
        logger.debug("loadDataMatriculados");
        Map<String, MatriculaResumen> mapResumenes = loadDataMatriculados(matriculaSecciones, mapSecciones, ciclo, ds);

        logger.debug("revisarAlumnosMatriculados");
        revisarAlumnosMatriculados(ciclo, mapResumenes);
        logger.debug("revisarSecciones");
        revisarSecciones(secciones);
        logger.debug("revisarGrupoSecciones");
        revisarGrupoSecciones(gruposSecciones);

    }

    private void revisarSecciones(List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            logger.debug("\tprocesando la seccion {}", seccion.getCodigo());
            seccion.setMatriculados(seccion.getMatriculaSeccion().size());
            seccionDAO.update(seccion);
        }
    }

    private void revisarGrupoSecciones(List<GrupoSeccion> gruposSecciones) {
        for (GrupoSeccion gpoSecc : gruposSecciones) {
            logger.debug("\tprocesando el gpo-seccion {}", gpoSecc.getCodigo());
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

    private void revisarAlumnosMatriculados(CicloAcademico ciclo, Map<String, MatriculaResumen> mapResumenes) {
        List<MatriculaResumen> alumnosResumen = matriculaResumenDAO.allByCiclo(ciclo);
        for (MatriculaResumen aluResumen : alumnosResumen) {
            Alumno alumno = aluResumen.getAlumno();
            logger.debug("\tprocesando el alumno {}", alumno.getCodigo());
            boolean gue = alumno.getCodigo().equals("20120023");
            if (!gue) {
                //continue;
            }
            MatriculaResumen resumen = mapResumenes.get(alumno.getCodigo());
            if (gue) {
                System.out.println("**************************");
                List<MatriculaSeccion> mmss = resumen.getMatriculaSeccion();
                for (MatriculaSeccion mms : mmss) {
                    System.out.println("   secc " + mms.getSeccion().getCodigo());
                }
            }
            if (resumen == null) {
                if (gue) {
                    System.out.println(" no existe resumen");
                }
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
                continue;
            }

            if (gue) {
                System.out.println(" existe resumen");
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
                if (gue) {
                    System.out.println(" revisando seccion " + secc.getCodigo());
                }
                if (!existeSeccion(resumen.getMatriculaSeccion(), secc)) {
                    if (gue) {
                        System.out.println(" se retira de la seccion " + secc.getCodigo());
                    }
                    aluSeccion.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                    matriculaSeccionDAO.update(aluSeccion);
                }
            }

            matriculaResumenDAO.update(resumen);
        }
    }

    private Map<String, MatriculaResumen> loadDataMatriculados(List<MatriculaSeccion> matriculasSecciones, Map<String, Seccion> mapSecciones, CicloAcademico ciclo, DataSessionPivot ds) {
        Map<String, MatriculaResumen> mapResumenes = new LinkedHashMap();
        for (MatriculaSeccion matriSecc : matriculasSecciones) {
            logger.debug("\tprocesando el seccion-alumno {}-{}", matriSecc.getCodigoSeccion(), matriSecc.getCodigoAlumno());
            Seccion seccion = mapSecciones.get(matriSecc.getCodigoSeccion());
            if (seccion == null) {
                String msg = String.format("La seccion %s no existe para se incluida en matricula-seccion",
                        matriSecc.getCodigoSeccion());
                throw new PhobosException(msg);
            }

            Alumno alumno = alumnoDAO.findByCodigo(matriSecc.getCodigoAlumno());
            if (alumno == null) {
                String msg = String.format("El alumno %s no existe para se incluida en matricula-seccion",
                        matriSecc.getCodigoAlumno());
                throw new PhobosException(msg);
            }

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

            // if (!existeSeccion(matriculasSecciones, seccion)) {
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
        }

        return mapResumenes;
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
        for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
            Seccion secc = alumnoSeccion.getSeccion();
            if (secc.getId().longValue() == seccion.getId()) {
                return true;
            }
        }
        return false;
    }

    private void revisarDocenteSecciones(Map<String, DocenteSeccion> mapDocenteSecciones, CicloAcademico ciclo, DataSessionPivot ds) {
        List<DocenteSeccion> profeSecciones = docenteSeccionDAO.allByCiclo(ciclo);
        for (DocenteSeccion profeSeccBD : profeSecciones) {
            Seccion secc = profeSeccBD.getSeccion();
            Docente profe = profeSeccBD.getDocente();
            logger.debug("\tprocesando el profe-seccion {}-{}", profe.getCodigo(), secc.getCodigo());

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

    private Map<String, DocenteSeccion> loadDataDocentesSecciones(List<DocenteSeccion> docentesSecciones, Map<String, Seccion> mapSecciones, Map<String, Docente> mapDocentes) {
        int loop = 0;
        Map<String, DocenteSeccion> mapDocenteSecciones = new LinkedHashMap();
        for (DocenteSeccion profeSecc : docentesSecciones) {
            logger.debug("\tprocesando el profe-seccion {}-{}", profeSecc.getCodigoDocente(), profeSecc.getCodigoSeccion());
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

    private Map<String, Docente> loadDataDocentes(List<Docente> docentes, CicloAcademico ciclo) {
        int loop = 0;
        Map<String, Docente> mapDocentes = new LinkedHashMap();
        for (Docente profe : docentes) {
            logger.debug("\tprocesando el profesor {}", profe.getCodigo());
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

    private Map<String, Seccion> loadDataSecciones(List<Seccion> secciones, CicloAcademico ciclo, Map<String, GrupoSeccion> mapGpoSecciones) {
        int loop = 0;
        Map<String, Seccion> mapSecciones = new LinkedHashMap();
        for (Seccion seccion : secciones) {
            logger.debug("\tprocesando la seccion {}", seccion.getCodigo());
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

    private Map<String, GrupoSeccion> loadDataGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo) {
        int loop = 0;
        Map<String, GrupoSeccion> mapGpoSecciones = new LinkedHashMap();
        for (GrupoSeccion gpoSecc : gruposSecciones) {
            logger.debug("\tprocesando el gpoSecc {}", gpoSecc.getCodigo());
            GrupoSeccion gpoSeccBD = grupoSeccionDAO.findByCodeCiclo(gpoSecc.getCodigo(), ciclo);
            Curso curso = cursoDAO.findByCode(gpoSecc.getCodigoCurso());
            if (gpoSeccBD == null) {

                gpoSeccBD = new GrupoSeccion();
                gpoSeccBD.setCicloAcademico(ciclo);
                gpoSeccBD.setCodigo(gpoSecc.getCodigo());
                gpoSeccBD.setCurso(curso);
                gpoSeccBD.setVersion(1);
                gpoSeccBD.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
                gpoSeccBD.setEstadoGrupo(EstadoGrupoSeccionEnum.ABI.name());

                grupoSeccionDAO.save(gpoSeccBD);

            } else {
                gpoSeccBD.setVersion(gpoSeccBD.getVersion() == null ? 1 : gpoSeccBD.getVersion());
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

    private List<MatriculaSeccion> crearMatriculasSecciones(String rutaFile) {
        List<MatriculaSeccion> matriculasSecciones = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 2) {
                    continue;
                }

                String ciclo = getCellValue(1, row);
                String codigoAlumno = getCellValue(2, row);
                String codigoSeccion = getCellValue(3, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                MatriculaSeccion alumnoSecc = new MatriculaSeccion(codigoAlumno, codigoSeccion);
                matriculasSecciones.add(alumnoSecc);
            }
            logger.debug("Se han leido un total de {} alumnos-secciones", loop);

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return matriculasSecciones;
    }

    private List<DocenteSeccion> crearDocenteSecciones(String rutaFile) {
        List<DocenteSeccion> docenteSecciones = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 2) {
                    continue;
                }

                String ciclo = getCellValue(1, row);
                String codigoDocente = getCellValue(2, row);
                String codigoSeccion = getCellValue(3, row);
                Integer principal = Integer.valueOf(getCellValue(4, row));

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                DocenteSeccion profeSecc = new DocenteSeccion(principal, codigoDocente, codigoSeccion);
                docenteSecciones.add(profeSecc);

            }
            logger.debug("Se han leido un total de {} profesores-secciones", loop);

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return docenteSecciones;
    }

    private List<Docente> crearDocentes(String rutaFile) {
        List<Docente> docentes = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 2) {
                    continue;
                }

                String nro = getCellValue(0, row);
                String tipoDocumento = getCellValue(1, row);
                String numeroDoc = getCellValue(2, row);
                String codigo = getCellValue(3, row);
                String dpto = getCellValue(4, row);

                if (StringUtils.isEmpty(nro)) {
                    break;
                }

                Docente docente = new Docente(codigo, tipoDocumento, numeroDoc, dpto);
                docentes.add(docente);
            }
            logger.debug("Se han leido un total de {} personas", loop);

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return docentes;
    }

    private List<Persona> crearPersonas(String rutaFile) {
        List<Persona> personas = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 2) {
                    continue;
                }

                String paterno = getCellValue(1, row);
                String materno = getCellValue(2, row);
                String nombres = getCellValue(3, row);
                String tipoDoc = getCellValue(4, row);
                String numeroDoc = getCellValue(5, row);

                if (StringUtils.isEmpty(paterno)) {
                    break;
                }

                Persona persona = new Persona(paterno, materno, nombres, numeroDoc, tipoDoc);
                personas.add(persona);
            }
            logger.debug("Se han leido un total de {} personas", loop);

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return personas;
    }

    private List<Seccion> crearSecciones(String rutaFile) {
        List<Seccion> secciones = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 2) {
                    continue;
                }

                String ciclo = getCellValue(1, row);
                String clave = getCellValue(2, row);
                String gpo = getCellValue(3, row);
                String aula = getCellValue(4, row);
                String gclave = getCellValue(5, row);
                String tclave = getCellValue(6, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                Seccion seccion = new Seccion(clave, gpo, aula, gclave, tclave);
                secciones.add(seccion);
            }
            logger.debug("Se han leido un total de {} secciones", loop);

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return secciones;
    }

    private List<GrupoSeccion> crearGruposSecciones(String rutaFile) {
        List<GrupoSeccion> gpoSecciones = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 2) {
                    continue;
                }

                String ciclo = getCellValue(1, row);
                String gclave = getCellValue(2, row);
                String curso = getCellValue(3, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                GrupoSeccion gpoSecc = new GrupoSeccion(gclave, curso);
                gpoSecciones.add(gpoSecc);
            }
            logger.debug("Se han leido un total de {} grupos-secciones", loop);

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return gpoSecciones;
    }

    private String saveFile(MultipartFile file) {
        try {
            String fileName = TypesUtil.getUnixTime() + "." + TypesUtil.getClean(file.getOriginalFilename());
            FileHelper.createDirectory(Constantine.TMP_DIR);
            String absoluteName = Constantine.TMP_DIR + fileName;

            FileHelper.saveToDisk(file, absoluteName);
            return absoluteName;
        } catch (IOException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        }
    }

    private String getCellValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String dato = cell.getStringCellValue();
        if (dato != null) {
            dato = StringUtils.replaceChars(dato, '\t', ' ');
            dato = StringUtils.replaceChars(dato, '\r', ' ');
            dato = StringUtils.replaceChars(dato, '\n', ' ');
            dato = StringUtils.replaceChars(dato, ',', ' ');
            dato = StringUtils.replaceChars(dato, '|', ' ');
            dato = StringUtils.trim(dato);
        }
        return dato;
    }

}
