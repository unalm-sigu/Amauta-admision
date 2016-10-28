package pe.edu.lamolina.pivot.model.general;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;

@Entity
@Table(name = "gen_dia")
public class Dia implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "numero_dia")
    private Integer numeroDia;

    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "dia", fetch = FetchType.LAZY)
    private List<DiaHoraGrupo> diaHoraGrupo;

    @OneToMany(mappedBy = "dia", fetch = FetchType.LAZY)
    private List<HorarioSeccion> horarioSeccion;

    public Dia() {
    }

    public Dia(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroDia() {
        return numeroDia;
    }

    public void setNumeroDia(Integer numeroDia) {
        this.numeroDia = numeroDia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

