package pe.edu.lamolina.pivot.model.horario;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.Dia;

@Entity
@Table(name = "hor_dia_hora_grupo")
public class DiaHoraGrupo implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo_horario")
    private GrupoHoras grupoHorario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia")
    private Dia dia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hora")
    private Hora hora;

    public DiaHoraGrupo() {
    }

    public DiaHoraGrupo(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoHoras getGrupoHorario() {
        return grupoHorario;
    }

    public void setGrupoHorario(GrupoHoras grupoHorario) {
        this.grupoHorario = grupoHorario;
    }

    public Dia getDia() {
        return dia;
    }

    public void setDia(Dia dia) {
        this.dia = dia;
    }

    public Hora getHora() {
        return hora;
    }

    public void setHora(Hora hora) {
        this.hora = hora;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DiaHoraGrupo)) {
            return false;
        }
        DiaHoraGrupo other = (DiaHoraGrupo) obj;
        if (id.compareTo(other.id) != 0) {
            return false;
        }
        return true;
    }
}
