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

@Entity
@Table(name = "gen_perfil_compania")
public class PerfilCompania implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "cargo", fetch = FetchType.LAZY)
    private List<Colaborador> colaborador;

    @OneToMany(mappedBy = "cargoJefe", fetch = FetchType.LAZY)
    private List<Oficina> oficina;

    @OneToMany(mappedBy = "perfilCompania", fetch = FetchType.LAZY)
    private List<PersonaPerfil> personaPerfil;

    public PerfilCompania() {
    }

    public PerfilCompania(Object id) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Colaborador> getColaborador() {
        return colaborador;
    }

    public void setColaborador(List<Colaborador> colaborador) {
        this.colaborador = colaborador;
    }

    public List<Oficina> getOficina() {
        return oficina;
    }

    public void setOficina(List<Oficina> oficina) {
        this.oficina = oficina;
    }

    public List<PersonaPerfil> getPersonaPerfil() {
        return personaPerfil;
    }

    public void setPersonaPerfil(List<PersonaPerfil> personaPerfil) {
        this.personaPerfil = personaPerfil;
    }

}

