package pe.edu.lamolina.amauta.controller.deuda;

import java.math.BigDecimal;
import pe.edu.lamolina.model.enums.NombreTablasEnum;

public class DeudaDTO {

    private Long personaId;
    private String personaDni;
    private String tabla;
    private String estado;
    private BigDecimal monto;
    private Long cuentaBancaria;
    private String descripcion;
    private Long cantidad;

    public DeudaDTO() {
    }

    public DeudaDTO(Long personaId, String personaDni, String tabla, String estado,
            BigDecimal monto, Long cuentaBancaria, String descripcion, Long cantidad) {
        this.personaId = personaId;
        this.personaDni = personaDni;
        this.tabla = tabla;
        this.estado = estado;
        this.monto = monto;
        this.cuentaBancaria = cuentaBancaria;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getPersonaDni() {
        return personaDni;
    }

    public void setPersonaDni(String personaDni) {
        this.personaDni = personaDni;
    }

    public String getTabla() {
        return tabla;
    }

    public NombreTablasEnum getTablaEnum() {
        return NombreTablasEnum.valueOf(this.tabla);
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Long getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(Long cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public boolean isDeudaAlumno() {
        return NombreTablasEnum.FIN_DEUDA_ALUMNO == this.getTablaEnum();
    }

    @Override
    public String toString() {
        return "DeudaDTO{" + "personaId=" + personaId + ", personaDni=" + personaDni + ", tabla=" + tabla + ", estado=" + estado + ", monto=" + monto + ", cuentaBancaria=" + cuentaBancaria + ", descripcion=" + descripcion + ", cantidad=" + cantidad + '}';
    }

}
