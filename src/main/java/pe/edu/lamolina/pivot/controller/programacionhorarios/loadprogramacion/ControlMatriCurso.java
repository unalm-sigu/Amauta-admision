package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;

public class ControlMatriCurso {

    private Map<Long, Alumno> mapAlumno;
    private Map<Long, Alumno> mapAlumnoError;
    private Map<Long, Alumno> mapAlumnoMarcado;
    private Map<Long, Alumno> mapAlumnoBloqueado;
    private Map<Long, List<MatriculaCurso>> mapMatriculaCurso;
    private Map<Long, List<MatriculaSeccion>> mapMatriculaSeccionByAlumno;
    private Map<String, MatriculaSeccion> mapMatriculaSeccion;
    private Map<Long, MatriculaResumen> mapMatriculaResumen;

    public ControlMatriCurso() {
        mapAlumnoMarcado = new LinkedHashMap();
        mapAlumnoError = new LinkedHashMap();
        mapAlumno = new LinkedHashMap();
        mapAlumnoBloqueado = new LinkedHashMap();
        mapMatriculaCurso = new LinkedHashMap();
        mapMatriculaSeccion = new LinkedHashMap();
        //mapMatriculaSeccionByAlumno = new LinkedHashMap();
    }

    public synchronized boolean bloquearAlumno(Alumno alumno) {
        Alumno alumnoMap = mapAlumno.get(alumno.getId());
        if (alumnoMap == null) {
            mapAlumno.put(alumno.getId(), alumno);
            mapAlumnoBloqueado.put(alumno.getId(), alumno);
            return true;
        }
        alumnoMap = mapAlumnoBloqueado.get(alumno.getId());
        if (alumnoMap == null) {
            mapAlumnoBloqueado.put(alumno.getId(), alumno);
            return true;
        }
        return false;
    }

    public synchronized void desbloquearAlumno(Alumno alumno) {
        mapAlumnoBloqueado.remove(alumno.getId());
    }

    public synchronized void marcarMatriSeccion(MatriculaSeccion matriSecc) {
        System.out.println("CONTROL: marcando matri-secc=" + matriSecc.getKeyLoad());
        MatriculaSeccion ms = mapMatriculaSeccion.get(matriSecc.getKeyLoad());
        if (ms == null) {
            mapMatriculaSeccion.put(matriSecc.getKeyLoad(), matriSecc);
        } else {
            System.out.println("CONTROL-WARNING: marcando por segunda vez a matri-secc=" + matriSecc.getKeyLoad());
        }
    }

    public synchronized void marcarAlumno(Alumno alumno) {
        System.out.println("CONTROL: marcando alumno=" + alumno.getCodigo());
        mapAlumnoMarcado.put(alumno.getId(), alumno);
    }

    public synchronized void marcarAlumnoError(Alumno alumno) {
        System.out.println("CONTROL: marcando alumno-error=" + alumno.getCodigo());
        mapAlumnoError.put(alumno.getId(), alumno);
    }

    public synchronized boolean verificarAlumnoError(Alumno alumno) {
        Alumno alu = mapAlumnoError.get(alumno.getId());
        return alu != null;
    }

    public int getMatriSeccMarcadas() {
        return mapMatriculaSeccion.size();
    }

    public int getAlumnosMarcadas() {
        return mapAlumnoMarcado.size();
    }

    public int getAlumnosMarcadosError() {
        return mapAlumnoError.size();
    }

}
