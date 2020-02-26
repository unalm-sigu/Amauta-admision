package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.Reincorporacion;

public class ListBeanPromedios {

    private List<CicloAcademico> ciclos;
    private List<CicloAcademico> ciclosActivos;
    private CicloAcademico cicloPregrado;
    private CicloAcademico cicloPosgrado;
    private List<Egresado> egresados;
    private List<AlumnoCiclo> alumnosCiclosAll;
    private List<AlumnoCicloCurso> alumnosCiclosCursosActivos;
    private List<AlumnoCicloCurso> alumnosCiclosCursosAll;
    private List<Reincorporacion> reincorporaciones;

    public List<CicloAcademico> getCiclos() {
        return ciclos;
    }

    public void setCiclos(List<CicloAcademico> ciclos) {
        this.ciclos = ciclos;
    }

    public List<CicloAcademico> getCiclosActivos() {
        return ciclosActivos;
    }

    public void setCiclosActivos(List<CicloAcademico> ciclosActivos) {
        this.ciclosActivos = ciclosActivos;
    }

    public CicloAcademico getCicloPregrado() {
        return cicloPregrado;
    }

    public void setCicloPregrado(CicloAcademico cicloPregrado) {
        this.cicloPregrado = cicloPregrado;
    }

    public CicloAcademico getCicloPosgrado() {
        return cicloPosgrado;
    }

    public void setCicloPosgrado(CicloAcademico cicloPosgrado) {
        this.cicloPosgrado = cicloPosgrado;
    }

    public List<Egresado> getEgresados() {
        return egresados;
    }

    public void setEgresados(List<Egresado> egresados) {
        this.egresados = egresados;
    }

    public List<AlumnoCiclo> getAlumnosCiclosAll() {
        return alumnosCiclosAll;
    }

    public void setAlumnosCiclosAll(List<AlumnoCiclo> alumnosCiclosAll) {
        this.alumnosCiclosAll = alumnosCiclosAll;
    }

    public List<AlumnoCicloCurso> getAlumnosCiclosCursosActivos() {
        return alumnosCiclosCursosActivos;
    }

    public void setAlumnosCiclosCursosActivos(List<AlumnoCicloCurso> alumnosCiclosCursosActivos) {
        this.alumnosCiclosCursosActivos = alumnosCiclosCursosActivos;
    }

    public List<AlumnoCicloCurso> getAlumnosCiclosCursosAll() {
        return alumnosCiclosCursosAll;
    }

    public void setAlumnosCiclosCursosAll(List<AlumnoCicloCurso> alumnosCiclosCursosAll) {
        this.alumnosCiclosCursosAll = alumnosCiclosCursosAll;
    }

    public List<Reincorporacion> getReincorporaciones() {
        return reincorporaciones;
    }

    public void setReincorporaciones(List<Reincorporacion> reincorporaciones) {
        this.reincorporaciones = reincorporaciones;
    }

}
