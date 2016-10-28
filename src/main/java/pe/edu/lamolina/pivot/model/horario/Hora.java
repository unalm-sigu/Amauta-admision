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

@Entity
@Table(name = "hor_hora")
public class Hora implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "hora")
    private Integer hora;

    @Column(name = "minutos")
    private Integer minutos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_hora")
    private TipoHora tipoHora;

    @OneToMany(mappedBy = "hora", fetch = FetchType.LAZY)
    private List<DiaHoraGrupo> diaHoraGrupo;

    @OneToMany(mappedBy = "hora", fetch = FetchType.LAZY)
    private List<HorarioSeccion> horarioSeccion;

    public Hora() {
    }

    public Hora(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoHora getTipoHora() {
        return tipoHora;
    }

    public void setTipoHora(TipoHora tipoHora) {
        this.tipoHora = tipoHora;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getHora() {
        return hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public Integer getMinutos() {
        return minutos;
    }

    public void setMinutos(Integer minutos) {
        this.minutos = minutos;
    }

    public List<DiaHoraGrupo> getDiaHoraGrupo() {
        return diaHoraGrupo;
    }

    public void setDiaHoraGrupo(List<DiaHoraGrupo> diaHoraGrupo) {
        this.diaHoraGrupo = diaHoraGrupo;
    }

    public List<HorarioSeccion> getHorarioSeccion() {
        return horarioSeccion;
    }

    public void setHorarioSeccion(List<HorarioSeccion> horarioSeccion) {
        this.horarioSeccion = horarioSeccion;
    }

}

