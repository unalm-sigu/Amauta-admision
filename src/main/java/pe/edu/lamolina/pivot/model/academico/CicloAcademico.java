package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.TipoCicloEnum;

@Entity
@Table(name = "aca_ciclo_academico")
public class CicloAcademico implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "numero_ciclo")
    private String numeroCiclo;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "descripcion2")
    private String descripcion2;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "ini_matricula")
    private Integer iniMatricula;

    @Column(name = "sgte_matricula")
    private Integer sgteMatricula;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_estudio")
    private ModalidadEstudio modalidadEstudio;

    public CicloAcademico() {
    }

    public CicloAcademico(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModalidadEstudio getModalidadEstudio() {
        return modalidadEstudio;
    }

    public void setModalidadEstudio(ModalidadEstudio modalidadEstudio) {
        this.modalidadEstudio = modalidadEstudio;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getNumeroCiclo() {
        return numeroCiclo;
    }

    public void setNumeroCiclo(String numeroCiclo) {
        this.numeroCiclo = numeroCiclo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdUserRegistro() {
        return idUserRegistro;
    }

    public void setIdUserRegistro(Long idUserRegistro) {
        this.idUserRegistro = idUserRegistro;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isTipoRegular() {
        if (this.getTipo() != null) {
            if (TipoCicloEnum.REG.name().equals(this.getTipo())) {
                return true;
            }
        }
        return false;
    }

    public boolean isTipoNivelacion() {
        if (this.getTipo() != null) {
            if (TipoCicloEnum.NIV.name().equals(this.getTipo())) {
                return true;
            }
        }
        return false;
    }

    public TipoCicloEnum getTipoCicloEnum() {
        return TipoCicloEnum.valueOf(this.tipo);
    }

    public String getDescripcion2() {
        return descripcion2;
    }

    public void setDescripcion2(String descripcion2) {
        this.descripcion2 = descripcion2;
    }

    public Integer getIniMatricula() {
        return iniMatricula;
    }

    public void setIniMatricula(Integer iniMatricula) {
        this.iniMatricula = iniMatricula;
    }

    public Integer getSgteMatricula() {
        return sgteMatricula;
    }

    public void setSgteMatricula(Integer sgteMatricula) {
        this.sgteMatricula = sgteMatricula;
    }

}
