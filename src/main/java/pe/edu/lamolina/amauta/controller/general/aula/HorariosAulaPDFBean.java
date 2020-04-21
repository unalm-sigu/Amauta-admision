package pe.edu.lamolina.amauta.controller.general.aula;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;

public class HorariosAulaPDFBean implements Serializable  {

    private String strAulaSuperior;
    private String strAula;
    private Aula aula;
    private Aula aulaSuperior;
    private Date fechaFin;
    private Date fechaInicio;
    private String strListDias;
    private String strListHoras;
    private List<Dia> dias;
    private List<Hora> horas;

    public HorariosAulaPDFBean() {
    }

    public String getStrAulaSuperior() {
        return strAulaSuperior;
    }

    public void setStrAulaSuperior(String strAulaSuperior) {
        this.strAulaSuperior = strAulaSuperior;
    }

    public String getStrAula() {
        return strAula;
    }

    public void setStrAula(String strAula) {
        this.strAula = strAula;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public Aula getAulaSuperior() {
        return aulaSuperior;
    }

    public void setAulaSuperior(Aula aulaSuperior) {
        this.aulaSuperior = aulaSuperior;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getStrListDias() {
        return strListDias;
    }

    public void setStrListDias(String strListDias) {
        this.strListDias = strListDias;
    }

    public String getStrListHoras() {
        return strListHoras;
    }

    public void setStrListHoras(String strListHoras) {
        this.strListHoras = strListHoras;
    }

    public List<Dia> getDias() {
        return dias;
    }

    public void setDias(List<Dia> dias) {
        this.dias = dias;
    }

    public List<Hora> getHoras() {
        return horas;
    }

    public void setHoras(List<Hora> horas) {
        this.horas = horas;
    }

}
