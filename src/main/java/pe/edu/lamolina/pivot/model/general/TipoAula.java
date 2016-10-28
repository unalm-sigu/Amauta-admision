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
import pe.edu.lamolina.pivot.model.academico.FormatoCurso;

@Entity
@Table(name = "gen_tipo_aula")
public class TipoAula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @OneToMany(mappedBy = "tipoAula", fetch = FetchType.LAZY)
    private List<FormatoCurso> formatoCurso;

    @OneToMany(mappedBy = "tipoAula", fetch = FetchType.LAZY)
    private List<Aula> aula;

    public TipoAula() {
    }

    public TipoAula(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<FormatoCurso> getFormatoCurso() {
        return formatoCurso;
    }

    public void setFormatoCurso(List<FormatoCurso> formatoCurso) {
        this.formatoCurso = formatoCurso;
    }

    public List<Aula> getAula() {
        return aula;
    }

    public void setAula(List<Aula> aula) {
        this.aula = aula;
    }

}

