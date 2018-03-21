package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaPerfilDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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
    PersonaPerfilDAO personaPerfilDAO;
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
                System.out.println("\t" + rr + " entregando " + alumnoCurso.getId() + " mat-curso");
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
            MatriculaSeccion matriSecc,
            Map<String, MatriculaResumen> mapResumenes,
            Map<String, Seccion> mapSecciones,
            CicloAcademico ciclo, DataSessionPivot ds) {

        int rr = getRandom();
        matriSecc.setFechaInicioProceso(new Date());

        Seccion seccion = mapSecciones.get(matriSecc.getCodigoSeccion());
        if (seccion == null) {
            String msg = String.format("La seccion %s no existe para se incluida en matricula-seccion", matriSecc.getCodigoSeccion());
            throw new PhobosException(msg);
        }

        Alumno alumno = alumnoDAO.findFlatByCodigo(matriSecc.getCodigoAlumno());
        if (alumno == null) {
            String msg = String.format("El alumno %s no existe para se incluida en matricula-seccion", matriSecc.getCodigoAlumno());
            throw new PhobosException(msg);
        }

        System.out.println(rr + " vamos a bloquear alumno " + alumno.getCodigo() + "(" + alumno.getId() + ") para loadDataMatriculados");
        alumnoDAO.findLock(alumno.getId());
        System.out.println("\t" + rr + " alumno " + alumno.getCodigo() + "(" + alumno.getId() + ") bloqueado para loadDataMatriculados");

        MatriculaResumen resumen = mapResumenes.get(alumno.getCodigo());
        if (resumen == null) {
            resumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
            if (resumen != null) {
                resumen.setMatriculaSeccion(new ArrayList());
                resumen.setMatriculaCurso(new ArrayList());
                resumen.setCreditosMatriculados(0);
                resumen.setCreditosRetirados(0);
                resumen.setCursosMatriculados(0);
                resumen.setCursosRetirados(0);
                mapResumenes.put(alumno.getCodigo(), resumen);
            }
        }

        if (resumen == null) {
            System.out.println("\t" + rr + " creando mat-resumen del alumno " + alumno.getCodigo() + " :::: ");
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
        if (matriSeccBD == null) {
            matriSeccBD = matriculaSeccionDAO.findByAlumnoSeccion(alumno, seccion);
        }
        if (matriSeccBD == null) {
            System.out.println("\t" + rr + " creando mat-seccion del alumno " + alumno.getCodigo());
            matriSeccBD = new MatriculaSeccion();
            matriSeccBD.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriSeccBD.setFechaRegistro(new Date());
            matriSeccBD.setUserRegistro(ds.getUsuario());
            matriSeccBD.setSeccion(seccion);
            matriSeccBD.setMatriculaResumen(resumen);
            matriculaSeccionDAO.save(matriSeccBD);

            System.out.println("\t" + rr + " mat-seccion es " + matriSeccBD.getId());
        }

        if (!existeSeccion(resumen.getMatriculaSeccion(), seccion)) {
            System.out.println("\t" + rr + " mat-seccion " + matriSeccBD.getId() + " se agrega al alumno " + alumno.getCodigo());
            resumen.getMatriculaSeccion().add(matriSeccBD);
        }

        if (matriSeccBD.getEstadoEnum() != EstadoMatriculaEnum.MAT) {
            System.out.println("\t" + rr + " guardando mat-seccion " + matriSeccBD.getId() + " del alumno " + alumno.getCodigo());
            matriSeccBD.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaSeccionDAO.update(matriSeccBD);
        }

        Curso curso = seccion.getGrupoSeccion().getCurso();
        MatriculaCurso matriCursoBD = findMatriculaCurso(resumen.getMatriculaCurso(), curso, rr);
        if (matriCursoBD == null) {
            matriCursoBD = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, ciclo);
        }
        if (matriCursoBD == null) {
            System.out.print("\t" + rr + " creando mat-curso del alumno " + alumno.getCodigo() + " :::: ");
            matriCursoBD = new MatriculaCurso();

            matriCursoBD.setCurso(curso);
            matriCursoBD.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriCursoBD.setMatriculaResumen(resumen);
            matriCursoBD.setNotaAcumulada("0");
            matriCursoBD.setNotaAvance("0");
            matriCursoBD.setNotaFinal("0");
            matriCursoBD.setPorcentajeAvanceNota(0);
            matriculaCursoDAO.save(matriCursoBD);

            System.out.println("\t" + rr + " mat-curso es " + matriCursoBD.getId());

        } else {
            System.out.print("\t" + rr + " actualizando mat-curso " + matriCursoBD.getId() + " del alumno " + alumno.getCodigo() + " :::: ");
        }

        matriCursoBD.setCreditos(matriSecc.getCreditos());
        matriculaCursoDAO.update(matriCursoBD);

        if (!existeCurso(resumen.getMatriculaCurso(), curso)) {
            System.out.println("\t" + rr + " mat-curso " + matriCursoBD.getId() + " agregado al mat-resumen " + resumen.getId() + " del alumno " + alumno.getCodigo());
            resumen.getMatriculaCurso().add(matriCursoBD);
            resumen.setCursosMatriculados(resumen.getCursosMatriculados() + 1);
            resumen.setCreditosMatriculados(resumen.getCreditosMatriculados() + matriSecc.getCreditos());
            matriculaResumenDAO.update(resumen);

            System.out.println("\t" + rr + " finalizo actualizacion mat-resumen " + resumen.getId() + " para el mat-curso " + matriCursoBD.getId() + " del alumno " + alumno.getCodigo());
        }

        if (matriCursoBD.getEstadoEnum() != EstadoMatriculaEnum.MAT) {
            matriCursoBD.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaCursoDAO.update(matriCursoBD);
        }
        matriSecc.setProcesado(1);
        matriSecc.setFechaFinProceso(new Date());

        System.out.println("\t" + rr + " alumno " + alumno.getCodigo() + " desbloqueado en loadDataMatriculados");
    }

}
