package pe.edu.lamolina.amauta.dao.finanza.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.amauta.dao.finanza.ConceptoPagoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.TipoGestionEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.DescuentoExamen;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Repository
public class ConceptoPagoDAOH extends AbstractEasyDAO<ConceptoPago> implements ConceptoPagoDAO {

    public ConceptoPagoDAOH() {
        super();
        setClazz(ConceptoPago.class);
    }

    @Override
    public ConceptoPago find(long id) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md")
                .filter("co.id", id);

        return find(sql);
    }

    @Override
    public ConceptoPago findByModalidad(ModalidadIngreso modalidadIngreso) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md")
                .filter("md.id", modalidadIngreso);

        return find(sql);
    }

    @Override
    public List<ConceptoPago> allSinOrigenByModalidad(ModalidadIngreso modalidad) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md")
                .leftJoin("conceptoOrigen ori")
                .isNull("ori.id")
                .filter("md.id", modalidad);

        return all(sql);
    }

    @Override
    public List<ConceptoPago> all() {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("cuentaBancaria")
                .leftJoin("modalidadIngreso", "conceptoOrigen");

        return all(sql);
    }

    @Override
    public List<ConceptoPago> allSuperiores() {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("cuentaBancaria")
                .leftJoin("modalidadIngreso", "conceptoOrigen ori")
                .isNull("ori.id");

        return all(sql);
    }

    @Override
    public List<ConceptoPago> allSinOrigen() {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .leftJoin("conceptoOrigen ori")
                .isNull("ori.id");

        return all(sql);
    }

    @Override
    public List<ConceptoPago> allByDynatable(DynatableFilter filter, CicloPostula ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ConceptoPago.class, "cpa")
                .join("cuentaBancaria cta")
                .leftJoin("conceptoOrigen ori", "modalidadIngreso mi")
                .searchFields("cpa.codigo", "cpa.codigoBanco", "cpa.estado", "cpa.descripcion", "cpa.tipo", "cpa.descuento")
                .searchFields("cta.numero", "cta.nombre", "cta.empresa", "cta.banco", "mi.nombre")
                .orderBy("mi.orden", "cpa.id DESC");

        return sql.all(getCurrentSession());
    }

    @Override
    public ConceptoPago findByModalidadTipoGestion(ModalidadIngreso modalidadIngreso, DescuentoExamen descuentoExamen, TipoGestionEnum tipoGestionEnum) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso mi")
                .leftJoin("conceptoOrigen ori")
                .filter("mi.id", modalidadIngreso)
                .filter("co.tipo", tipoGestionEnum)
                .filter("co.descuento", descuentoExamen.getPorcentaje())
                .in("co.codigoBanco", AdmisionConstantine.CONCEPTOS_DSCTO_GENERICOS);

        return find(sql);
    }

    @Override
    public List<ConceptoPago> allByModalidad(ModalidadIngreso modalidad) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md")
                .leftJoin("conceptoOrigen ori")
                .filter("md.id", modalidad);

        return all(sql);
    }

    @Override
    public ConceptoPago findConceptoPago(Long idConceptoPago) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .filter("co.id", idConceptoPago);

        return find(sql);
    }

    @Override
    public List<ConceptoPago> allByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .select("cpag")
                .from(ConceptoPrecio.class, "copre")
                .join("conceptoPago cpag", "cicloPostula cp")
                .filter("cp.id", ciclo);

        return all(sql);
    }

    @Override
    public List<ConceptoPago> allHijos(ConceptoPago concepto) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("conceptoOrigen ori", "cuentaBancaria")
                .leftJoin("modalidadIngreso")
                .filter("ori.id", concepto);

        return all(sql);
    }

    @Override
    public ConceptoPago findDsctoByModalidadGestionAmbito(
            DescuentoExamen descuentoExamen, ModalidadIngreso modalidad,
            TipoGestionEnum tipoGestionEnum, String ambito) {
        Octavia sql = Octavia.query()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso mi")
                .leftJoin("conceptoOrigen ori")
                .filter("mi.id", modalidad)
                .in("co.tipo", Arrays.asList(tipoGestionEnum, TipoGestionEnum.AMB))
                .filter("co.descuento", descuentoExamen.getPorcentaje())
                .filter("co.ambitoDescuento", ambito);

        return find(sql);
    }

    @Override
    public ConceptoPago findByCodigo(String codigo) {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .left("conceptoOrigen ori", "modalidadIngreso md", "cuentaBancaria")
                .filter("co.codigo", codigo);
        return find(sqlUtil);
    }

}

