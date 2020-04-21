package pe.edu.lamolina.amauta.dao.finanza;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface ConceptoPrecioDAO extends EasyDAO<ConceptoPrecio> {

    List<ConceptoPrecio> allByCicloImporte(CicloPostula ciclo, BigDecimal importe);

    ConceptoPrecio findByConceptoCiclo(ConceptoPago concepto, CicloPostula ciclo);

    ConceptoPrecio findActivoByConceptoCiclo(ConceptoPago concepto, CicloPostula ciclo);

    List<ConceptoPrecio> allByConceptosCiclo(List<ConceptoPago> conceptos, CicloPostula ciclo);

    List<ConceptoPrecio> allByCuentaCiclo(CuentaBancaria cuenta, CicloPostula ciclo, EstadoEnum estadoEnum);

    List<ConceptoPrecio> allByCiclo(CicloPostula ciclo);

    List<ConceptoPrecio> allByConceptoOtroCiclo(ConceptoPago concepto, CicloPostula ciclo);

    List<ConceptoPrecio> allByCicloPostulaEsAntesExamen(CicloPostula ciclo, int con);

    public ConceptoPrecio findProspectoByCiclo(CicloPostula ciclo);

    ConceptoPrecio find(Long id);

}
