package pe.edu.lamolina.amauta.controller.academico.promedio;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.Reincorporacion;

public class BeanPromedios {

    private Alumno alumno;
    private CicloAcademico cicloActivo;
    private Egresado egresado;
    private List<CicloAcademico> ciclos;
    private List<AlumnoCiclo> alumnoCiclos;
    private List<AlumnoCicloCurso> alumnoCicloCursosOperativos;
    private List<AlumnoCicloCurso> alumnoCicloCursosAll;
    private List<Reincorporacion> reincorporaciones;

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public CicloAcademico getCicloActivo() {
        return cicloActivo;
    }

    public void setCicloActivo(CicloAcademico cicloActivo) {
        this.cicloActivo = cicloActivo;
    }

    public Egresado getEgresado() {
        return egresado;
    }

    public void setEgresado(Egresado egresado) {
        this.egresado = egresado;
    }

    public List<CicloAcademico> getCiclos() {
        return ciclos;
    }

    public void setCiclos(List<CicloAcademico> ciclos) {
        this.ciclos = ciclos;
    }

    public List<AlumnoCiclo> getAlumnoCiclos() {
        return alumnoCiclos;
    }

    public void setAlumnoCiclos(List<AlumnoCiclo> alumnoCiclos) {
        this.alumnoCiclos = alumnoCiclos;
    }

    public List<AlumnoCicloCurso> getAlumnoCicloCursosOperativos() {
        return alumnoCicloCursosOperativos;
    }

    public void setAlumnoCicloCursosOperativos(List<AlumnoCicloCurso> alumnoCicloCursosOperativos) {
        this.alumnoCicloCursosOperativos = alumnoCicloCursosOperativos;
    }

    public List<AlumnoCicloCurso> getAlumnoCicloCursosAll() {
        return alumnoCicloCursosAll;
    }

    public void setAlumnoCicloCursosAll(List<AlumnoCicloCurso> alumnoCicloCursosAll) {
        this.alumnoCicloCursosAll = alumnoCicloCursosAll;
    }

    public List<Reincorporacion> getReincorporaciones() {
        return reincorporaciones;
    }

    public void setReincorporaciones(List<Reincorporacion> reincorporaciones) {
        this.reincorporaciones = reincorporaciones;
    }

}
