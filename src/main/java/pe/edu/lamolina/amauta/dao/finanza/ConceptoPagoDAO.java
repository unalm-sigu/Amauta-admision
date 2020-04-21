package pe.edu.lamolina.amauta.dao.finanza;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoGestionEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.DescuentoExamen;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

public interface ConceptoPagoDAO extends EasyDAO<ConceptoPago> {

    ConceptoPago findByModalidad(ModalidadIngreso modalidad);

    List<ConceptoPago> allSinOrigenByModalidad(ModalidadIngreso modalidad);

    List<ConceptoPago> allSinOrigen();

    List<ConceptoPago> allByDynatable(DynatableFilter filter, CicloPostula ciclo);

    ConceptoPago findByModalidadTipoGestion(ModalidadIngreso modalidadIngreso, DescuentoExamen descuentoExamen, TipoGestionEnum tipoGestionEnum);

    List<ConceptoPago> allByModalidad(ModalidadIngreso modalidad);

    List<ConceptoPago> allSuperiores();

    ConceptoPago findConceptoPago(Long idConceptoPago);

    List<ConceptoPago> allByCiclo(CicloPostula ciclo);

    List<ConceptoPago> allHijos(ConceptoPago concepto);

    ConceptoPago findDsctoByModalidadGestionAmbito(DescuentoExamen descuentoExamen, ModalidadIngreso modalidad, TipoGestionEnum tipoGestionEnum, String ambito);

    ConceptoPago findByCodigo(String code);

}
