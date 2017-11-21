package pe.edu.lamolina.pivot.model.horario;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.Seccion;

@Entity
@Table(name = "hor_grupo_horas")
public class GrupoHoras implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "letra")
    private String letra;

    @Column(name = "tipo_ciclo")
    private String tipoCiclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_grupo_horas")
    private TipoGrupoHoras tipoGrupoHoras;

    @Column(name = "tipo_seccion")
    private String tipoSeccion;

    @Column(name = "color")
    private String color;

    @Column(name = "con_horario")
    private String conHorario;

    @OneToMany(mappedBy = "grupoHoras", fetch = FetchType.LAZY)
    private List<Seccion> seccion;

    @OneToMany(mappedBy = "grupoHorario", fetch = FetchType.LAZY)
    private List<DiaHoraGrupo> diaHoraGrupo;

    public GrupoHoras() {
    }

    public GrupoHoras(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getTipoCiclo() {
        return tipoCiclo;
    }

    public void setTipoCiclo(String tipoCiclo) {
        this.tipoCiclo = tipoCiclo;
    }

    public TipoGrupoHoras getTipoGrupoHoras() {
        return tipoGrupoHoras;
    }

    public void setTipoGrupoHoras(TipoGrupoHoras tipoGrupoHoras) {
        this.tipoGrupoHoras = tipoGrupoHoras;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTipoSeccion() {
        return tipoSeccion;
    }

    public void setTipoSeccion(String tipoSeccion) {
        this.tipoSeccion = tipoSeccion;
    }

    public List<Seccion> getSeccion() {
        return seccion;
    }

    public void setSeccion(List<Seccion> seccion) {
        this.seccion = seccion;
    }

    public List<DiaHoraGrupo> getDiaHoraGrupo() {
        return diaHoraGrupo;
    }

    public void setDiaHoraGrupo(List<DiaHoraGrupo> diaHoraGrupo) {
        this.diaHoraGrupo = diaHoraGrupo;
    }

    public String getConHorario() {
        return conHorario;
    }

    public void setConHorario(String conHorario) {
        this.conHorario = conHorario;
    }

}
