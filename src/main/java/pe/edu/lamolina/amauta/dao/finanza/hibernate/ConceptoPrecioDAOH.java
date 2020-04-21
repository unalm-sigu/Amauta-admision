package pe.edu.lamolina.amauta.dao.finanza.hibernate;

import java.math.BigDecimal;
import java.util.List;
import pe.edu.lamolina.amauta.dao.finanza.ConceptoPrecioDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class ConceptoPrecioDAOH extends AbstractEasyDAO<ConceptoPrecio> implements ConceptoPrecioDAO {

    public ConceptoPrecioDAOH() {
        super();
        setClazz(ConceptoPrecio.class);
    }

    @Override
    public ConceptoPrecio find(Long id) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp", "cp.cuentaBancaria cb")
                .leftJoin("cp.modalidadIngreso")
                .filter("pre.id", id);
        return find(sql);
    }

    @Override
    public List<ConceptoPrecio> allByCicloImporte(CicloPostula ciclo, BigDecimal importe) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", ciclo)
                .filter("pre.monto", importe);

        return all(sql);
    }

    @Override
    public ConceptoPrecio findByConceptoCiclo(ConceptoPago concepto, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", ciclo)
                .filter("cp.id", concepto);

        return find(sql);
    }

    @Override
    public ConceptoPrecio findActivoByConceptoCiclo(ConceptoPago concepto, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", ciclo)
                .filter("cp.id", concepto)
                .filter("pre.estado", EstadoEnum.ACT);

        return find(sql);
    }

    @Override
    public List<ConceptoPrecio> allByConceptosCiclo(List<ConceptoPago> conceptos, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp")
                .leftJoin("cp.modalidadIngreso", "tipoCambioInfo")
                .filter("ci.id", ciclo)
                .in("cp.id", conceptos);

        return all(sql);
    }

    @Override
    public List<ConceptoPrecio> allByCuentaCiclo(CuentaBancaria cuenta, CicloPostula ciclo, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp", "cp.cuentaBancaria cta")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", ciclo)
                .filter("cta.id", cuenta)
                .filter("pre.estado", estadoEnum.name());

        return all(sql);
    }

    @Override
    public List<ConceptoPrecio> allByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp", "cp.cuentaBancaria cta")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<ConceptoPrecio> allByConceptoOtroCiclo(ConceptoPago concepto, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp", "cp.cuentaBancaria cta")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", "<>", ciclo)
                .filter("cp.id", concepto);

        return all(sql);
    }

    @Override
    public List<ConceptoPrecio> allByCicloPostulaEsAntesExamen(CicloPostula ciclo, int con) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp", "cp.cuentaBancaria cta")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", ciclo)
                .filter("pre.esAntesExamen", con);

        return all(sql);
    }

    @Override
    public ConceptoPrecio findProspectoByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp")
                .leftJoin("cp.modalidadIngreso")
                .filter("ci.id", ciclo)
                .filter("cp.codigo", "000");

        return find(sql);
    }

}
