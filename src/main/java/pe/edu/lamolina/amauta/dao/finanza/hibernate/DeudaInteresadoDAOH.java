package pe.edu.lamolina.amauta.dao.finanza.hibernate;

import java.util.LinkedHashMap;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.amauta.dao.finanza.DeudaInteresadoDAO;

@Repository
public class DeudaInteresadoDAOH extends AbstractEasyDAO<DeudaInteresado> implements DeudaInteresadoDAO {

    public DeudaInteresadoDAOH() {
        super();
        setClazz(DeudaInteresado.class);
    }

    @Override
    public DeudaInteresado findActivaByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("postulante po", "interesado inte", "conceptoPrecio")
                .filter("po.id", postulante)
                .filter("di.estado", EstadoEnum.ACT);

        return find(sql);
    }

    @Override
    public DeudaInteresado findLastInactiveByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("postulante po", "interesado inte", "conceptoPrecio")
                .filter("po.id", postulante)
                .filter("di.estado", EstadoEnum.INA)
                .orderBy("di.id DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public DeudaInteresado findByProspectoCiclo(Interesado interesado, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte", "conceptoPrecio cpre", "cpre.conceptoPago cpag", "cpre.cicloPostula cp")
                .filter("cpag.codigo", AdmisionConstantine.CODE_PROSPECTO)
                .filter("cp.id", ciclo)
                .filter("inte.id", interesado);

        return find(sql);
    }

    @Override
    public List<DeudaInteresado> allActivosByCicloPostula(CicloPostula cicloActivo) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte", "conceptoPrecio cpre", "cpre.conceptoPago cpa", "cpre.cicloPostula cp")
                .leftJoin("postulante po")
                .filter("di.estado", EstadoEnum.ACT)
                .filter("cp.id", cicloActivo);

        return all(sql);
    }

    @Override
    public List<DeudaInteresado> allByInteresados(List<Interesado> interesados) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte", "conceptoPrecio cpre", "cpre.conceptoPago cpa", "cpre.cicloPostula cp")
                .filter("cpa.codigo", AdmisionConstantine.CODE_PROSPECTO)
                .in("inte.id", interesados);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<DeudaInteresado> allActivasByInteresado(Interesado interesado, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte", "conceptoPrecio cpre", "cpre.conceptoPago cpa", "cpre.cicloPostula cp")
                .leftJoin("postulante po")
                .filter("cp.id", ciclo)
                .filter("inte.id", interesado)
                .filter("di.estado", EstadoEnum.ACT)
                .orderBy("di.id DESC");

        return all(sql);
    }

    @Override
    public List<DeudaInteresado> allByInteresado(Interesado interesado, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte", "conceptoPrecio cpre", "cpre.conceptoPago cpa", "cpre.cicloPostula cp")
                .leftJoin("postulante po")
                .filter("cp.id", ciclo)
                .filter("inte.id", interesado)
                .orderBy("di.id DESC");

        return all(sql);
    }

    @Override
    public List<DeudaInteresado> allByPostulanteModCiclo(Postulante postulante, ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("postulante po", "interesado inte", "conceptoPrecio cpre")
                .join("cpre.conceptoPago cpag", "cpre.cicloPostula cp")
                .join("cpag.modalidadIngreso mi")
                .filter("po.id", postulante)
                .filter("mi.id", modalidadIngreso)
                .filter("cp.id", cicloPostula)
                .in("cpag.codigoBanco", AdmisionConstantine.CONCEPTOS_DSCTO_BASES);

        return all(sql);
    }

    @Override
    public List<LinkedHashMap> deudaInteresadoPagadosByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();

        sql.append("select ii.id_interesado interesado ,count(*) cant,count(case when monto=abono then 1 else null end ) pagos,max(fecha_abono) fecha ");
        sql.append("from fin_deuda_interesado ii ");
        sql.append("join sip_interesado tt on tt.id = ii.id_interesado ");
        sql.append("where ii.estado = 'ACT' and tt.id_ciclo_postula = :CICLO ");
        sql.append("group by ii.id_interesado ");
        sql.append("having count(*) = count(case when monto=abono then 1 else null end ) ");

        SQLQuery query = getCurrentSession().createSQLQuery(sql.toString());
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        query.setParameter("CICLO", ciclo.getId());

        return query.list();
    }

    @Override
    public List<DeudaInteresado> allActivosByInteresado(Interesado interesado) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte", "conceptoPrecio cpre", "cpre.conceptoPago cpa", "cpre.cicloPostula", "cpa.cuentaBancaria")
                .leftJoin("postulante po")
                .filter("inte.id", interesado)
                .filter("di.estado", EstadoEnum.ACT)
                .orderBy("cpa.orden");

        return all(sql);
    }

}
