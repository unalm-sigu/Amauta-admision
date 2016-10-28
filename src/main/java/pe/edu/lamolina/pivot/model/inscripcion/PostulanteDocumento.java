package pe.edu.lamolina.pivot.model.inscripcion;

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

@Entity
@Table(name = "sip_postulante_documento")
public class PostulanteDocumento implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "entregado")
    private Integer entregado;

    @Column(name = "fecha_recepcion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRecepcion;

    @Column(name = "id_user_recepcion")
    private Long idUserRecepcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulante")
    private Postulante postulante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento_modalidad")
    private DocumentoModalidad documentoModalidad;

    public PostulanteDocumento() {
    }

    public PostulanteDocumento(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Postulante getPostulante() {
        return postulante;
    }

    public void setPostulante(Postulante postulante) {
        this.postulante = postulante;
    }

    public DocumentoModalidad getDocumentoModalidad() {
        return documentoModalidad;
    }

    public void setDocumentoModalidad(DocumentoModalidad documentoModalidad) {
        this.documentoModalidad = documentoModalidad;
    }

    public Integer getEntregado() {
        return entregado;
    }

    public void setEntregado(Integer entregado) {
        this.entregado = entregado;
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public Long getIdUserRecepcion() {
        return idUserRecepcion;
    }

    public void setIdUserRecepcion(Long idUserRecepcion) {
        this.idUserRecepcion = idUserRecepcion;
    }

}

