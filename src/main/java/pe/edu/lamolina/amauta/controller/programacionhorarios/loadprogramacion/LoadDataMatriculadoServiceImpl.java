package pe.edu.lamolina.amauta.controller.programacionhorarios.loadprogramacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.amauta.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.dao.general.PersonaCargoDAO;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;

@Service
@Transactional(readOnly = true)
public class LoadDataMatriculadoServiceImpl implements LoadDataMatriculadoService {

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
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    PersonaCargoDAO personaPerfilDAO;
    @Autowired
    UsuarioDAO usuarioDAO;
    @Autowired
    UsuarioRolDAO usuarioRolDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    VisorLoadProgramacion visor;
    @Autowired
    VerificadorProgramacioService verificadorProgramacioService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer random;

    private synchronized Integer getRandom() {
        if (random == null) {
            random = 0;
        }
        random++;
        return random;
    }

    private MatriculaSeccion findMatriculaSeccion(List<MatriculaSeccion> alumnoSecciones, Seccion seccion) {
        for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
            Seccion secc = alumnoSeccion.getSeccion();
            if (secc.getId().longValue() == seccion.getId() && alumnoSeccion.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                return alumnoSeccion;
            }
        }
        for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
            Seccion secc = alumnoSeccion.getSeccion();
            if (secc.getId().longValue() == seccion.getId()) {
                return alumnoSeccion;
            }
        }
        return null;
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

    private MatriculaCurso findMatriculaCurso(List<MatriculaCurso> alumnoCursos, Curso curso, int rr) {
        for (MatriculaCurso alumnoCurso : alumnoCursos) {
            Curso cur = alumnoCurso.getCurso();
            if (cur.getId().longValue() == curso.getId()) {
                //System.out.println("\t" + rr + " entregando " + alumnoCurso.getId() + " mat-curso");
                return alumnoCurso;
            }
        }
        return null;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void load(
            Acumulador acumulador,
            ControlMatriCurso control,
            MatriculaSeccion matriSecc,
            Map<String, MatriculaResumen> mapResumenes,
            Map<String, Seccion> mapSecciones,
            Map<Long, CicloAcademico> mapCiclo,
            //CicloAcademico ciclo, 
            DataSessionPivot ds) {

        if (visor.isStop()) {
            throw new PhobosException("Carga detenida intempestivamente");
        }

        int rr = acumulador.getValor();
        matriSecc.setFechaInicioProceso(new Date());

        Seccion seccion = mapSecciones.get(matriSecc.getCodigoSeccion());
        if (seccion == null) {
            String msg = String.format("La seccion %s no existe para se incluida en matricula-seccion", matriSecc.getCodigoSeccion());
            matriSecc.setLiberarPermiso();
            throw new PhobosException(msg);
        }

        Alumno alumno = alumnoDAO.findFlatByCodigo(matriSecc.getCodigoAlumno());
        if (alumno == null) {
            String msg = String.format("El alumno %s no existe para se incluida en matricula-seccion", matriSecc.getCodigoAlumno());
            matriSecc.setLiberarPermiso();
            throw new PhobosException(msg);
        }

        long t1 = System.currentTimeMillis();
        long t3 = t1;
        long veces = 0;
        System.out.println(rr + " vamos a bloquear alumno " + alumno.getCodigo() + "(" + alumno.getId() + ") para loadDataMatriculados");
        for (;;) {
            boolean ok = control.bloquearAlumno(alumno);
            if (ok) {
                long t2 = System.currentTimeMillis();
                System.out.println(rr + " se demoró " + (t2 - t1) + " mseg en obtener alumno " + alumno.getCodigo());
                break;
            } else {
                TypesUtil.delay(100);
                long t2 = System.currentTimeMillis();

                if ((t2 - t3) > 500) {
                    veces++;
                    if (veces % 5 == 0) {
                        System.out.println(rr + " ya esta demorando " + (t2 - t1) + " mseg en obtener al alumno " + alumno.getCodigo());
                        veces = 0;
                        t3 = System.currentTimeMillis();
                    }
                }
            }
        }

        visor.addAlumno(alumno, matriSecc.getCodigoSeccion(), rr);
        alumnoDAO.findLock(alumno.getId());
        visor.bloqueadoAlumno(alumno, matriSecc.getCodigoSeccion());
        System.out.println("\t" + rr + " alumno " + alumno.getCodigo() + "(" + alumno.getId() + ") bloqueado para loadDataMatriculados");

        MatriculaResumen resumen = mapResumenes.get(alumno.getCodigo());

        if (resumen == null) {
            CicloAcademico ciclo = mapCiclo.get(alumno.getModalidadEstudio().getId());
            //System.out.println("\t" + rr + " creando mat-resumen del alumno " + alumno.getCodigo() + " :::: ");
            resumen = new MatriculaResumen();
            resumen.setAlumno(alumno);
            resumen.setCicloAcademico(ciclo);
            resumen.setCreditosMatriculados(0);
            resumen.setCreditosRetirados(0);
            resumen.setCursosMatriculados(0);
            resumen.setCursosRetirados(0);
            resumen.setEstadoEnum(EstadoMatriculaEnum.MAT);
            resumen.setNotaAcumulada("0");
            resumen.setNotaAvance("0");
            resumen.setNotaFinal("0");
            resumen.setPorcentajeAvance(0);
            resumen.setCreditosTrikaPagados(0);
            resumen.setCreditosTrikaSeparados(0);
            matriculaResumenDAO.save(resumen);
            System.out.println("\t" + rr + " mat-resumen es " + resumen.getId());

            resumen.setMatriculaSeccion(new ArrayList());
            resumen.setMatriculaCurso(new ArrayList());
            mapResumenes.put(alumno.getCodigo(), resumen);
        }

        if (resumen.getEstadoEnum() != EstadoMatriculaEnum.MAT) {
            System.out.println("\t" + rr + " guardando mat-resumen " + resumen.getId() + " del alumno " + alumno.getCodigo());
            resumen.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaResumenDAO.update(resumen);
        }

        MatriculaSeccion matriSeccBD = findMatriculaSeccion(resumen.getMatriculaSeccion(), seccion);
        Curso curso = seccion.getGrupoSeccion().getCurso();
        boolean esNuevaSeccion = false;

        if (matriSeccBD == null) {
            System.out.println("\t" + rr + " creando mat-seccion del alumno " + alumno.getCodigo() + " en la seccion " + seccion.getCodigo3() + " del curso " + curso.getId());
            matriSeccBD = new MatriculaSeccion();
            matriSeccBD.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriSeccBD.setFechaRegistro(new Date());
            matriSeccBD.setUserRegistro(ds.getUsuario());
            matriSeccBD.setSeccion(seccion);
            matriSeccBD.setMatriculaResumen(resumen);
            matriculaSeccionDAO.save(matriSeccBD);
            esNuevaSeccion = true;

            //System.out.println("\t" + rr + " mat-seccion es " + matriSeccBD.getId());
        }
        matriSeccBD.setCargado(1);

        //MatriculaCurso matriCursoBD = findMatriculaCurso(resumen.getMatriculaCurso(), curso, rr);
        MatriculaCurso matriCursoBD = resumen.getMatriculaCursoByCurso(curso);
        boolean noEsNuevo = true;

        if (matriCursoBD.getId() == null) {
            //System.out.print("\t" + rr + " creando mat-curso del alumno " + alumno.getCodigo() + " :::: ");
            //matriCursoBD = new MatriculaCurso();
            //matriCursoBD.setCurso(curso);

            matriCursoBD.setEstadoEnum(EstadoMatriculaEnum.MAT);
            //matriCursoBD.setMatriculaResumen(resumen);
            matriCursoBD.setNotaAcumulada("0");
            matriCursoBD.setNotaAvance("0");
            matriCursoBD.setNotaFinal("0");
            matriCursoBD.setPorcentajeAvanceNota(0);
            matriCursoBD.setCreditos(matriSecc.getCreditos());
            matriCursoBD.setFechaMatricula(new Date());
            matriculaCursoDAO.save(matriCursoBD);
            noEsNuevo = false;
            //resumen.getMatriculaCurso().add(matriCursoBD);

            System.out.println("\t" + rr + " mat-curso es " + matriCursoBD.getId() + " en " + (System.currentTimeMillis()));
            visor.agregarLog("aluSecc", "saveAluSecc", "Alumno-Seccion " + alumno.getCodigo() + "-" + seccion.getCodigo() + " creado", true, "info");

        } else {
            matriCursoBD.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriCursoBD.setCreditos(matriSecc.getCreditos());
            //matriCursoBD.setMatriculaResumen(resumen);
            //matriculaCursoDAO.update(matriCursoBD);
            System.out.println("\t" + rr + " actualizando mat-curso " + matriCursoBD.getId() + " del alumno " + alumno.getCodigo() + " :::: en " + System.currentTimeMillis());
            visor.agregarLog("aluSecc", "saveAluSecc", "Alumno-Seccion " + alumno.getCodigo() + "-" + seccion.getCodigo() + " ya existe", true, "info");
        }

        matriCursoBD.setCargado(1);
        //matriCursoBD.setCreditos(matriSecc.getCreditos());
        matriculaCursoDAO.update(matriCursoBD);

        if (esNuevaSeccion) {
            resumen.getMatriculaSeccion().add(matriSeccBD);
        }

        resumen.setCargado(1);

        matriSecc.setProcesado(1);
        matriSecc.setFechaFinProceso(new Date());
        matriSecc.setLiberarPermiso();

        //System.out.println("\t" + rr + " alumno " + alumno.getCodigo() + " desbloqueado en loadDataMatriculados");
        visor.agregarLog("aluSecc", "saveAluSecc", "Registro de alumno-Seccion " + alumno.getCodigo() + "-" + seccion.getCodigo() + " finalizado", false, "info");
        //control.marcarMatriSeccion(matriSecc);
        //control.desbloquearAlumno(alumno);

    }

}
