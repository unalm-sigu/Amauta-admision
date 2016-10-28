package pe.edu.lamolina.pivot.model.inscripcion;

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
@Table(name = "sip_documento_modalidad")
public class DocumentoModalidad implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "opcional")
    private Integer opcional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad")
    private ModalidadIngreso modalidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_postula")
    private CicloPostula cicloPostula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento_requisito")
    private DocumentoRequisito documentoRequisito;

    @OneToMany(mappedBy = "documentoModalidad", fetch = FetchType.LAZY)
    private List<PostulanteDocumento> postulanteDocumento;

    public DocumentoModalidad() {
    }

    public DocumentoModalidad(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModalidadIngreso getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadIngreso modalidad) {
        this.modalidad = modalidad;
    }

    public CicloPostula getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(CicloPostula cicloPostula) {
        this.cicloPostula = cicloPostula;
    }

    public DocumentoRequisito getDocumentoRequisito() {
        return documentoRequisito;
    }

    public void setDocumentoRequisito(DocumentoRequisito documentoRequisito) {
        this.documentoRequisito = documentoRequisito;
    }

    public Integer getOpcional() {
        return opcional;
    }

    public void setOpcional(Integer opcional) {
        this.opcional = opcional;
    }

    public List<PostulanteDocumento> getPostulanteDocumento() {
        return postulanteDocumento;
    }

    public void setPostulanteDocumento(List<PostulanteDocumento> postulanteDocumento) {
        this.postulanteDocumento = postulanteDocumento;
    }

}

