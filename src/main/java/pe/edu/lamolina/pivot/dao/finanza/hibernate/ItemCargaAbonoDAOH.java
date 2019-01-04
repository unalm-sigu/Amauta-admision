package pe.edu.lamolina.pivot.dao.finanza.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import pe.edu.lamolina.pivot.dao.finanza.ItemCargaAbonoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class ItemCargaAbonoDAOH extends AbstractEasyDAO<ItemCargaAbono> implements ItemCargaAbonoDAO {

    public ItemCargaAbonoDAOH() {
        super();
        setClazz(ItemCargaAbono.class);
    }

    @Override
    public List<ItemCargaAbono> allInscripcionByCicloPostula(CicloPostula cicloPostula) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "cuentaBancaria cta", "conceptoPago cp", "po.cicloPostula cip")
                .leftJoin("cargaAbonos ca")
                .notIn("cp.codigo", Arrays.asList("000", "001", "002", "003"))
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0)
                .filter("ica.esExtorno", 0)
                .filter("ica.descripcion", "like", "EFEC%")
                .filter("cip.id", cicloPostula);

        return all(sql);
    }

    @Override
    public List<ItemCargaAbono> allActivosByCicloPostula(CicloPostula cicloPostula) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("cargaAbonos ca", "ca.cuentaBancaria cta")
                .join("postulante po", "po.cicloPostula cip")
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0)
                .filter("ica.esExtorno", 0)
                .filter("ica.descripcion", "like", "EFEC%")
                .filter("cip.id", cicloPostula);

        return all(sql);
    }

    @Override
    public ItemCargaAbono findByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po")
                .filter("po.id", postulante)
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0)
                .filter("ica.esExtorno", 0)
                .filter("ica.descripcion", "like", "EFEC%");

        return find(sql);
    }

    @Override
    public ItemCargaAbono findRedundante(ItemCargaAbono item) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .filter("ica.descripcion", item.getDescripcion())
                .filter("ica.fecha", item.getFechaAbono());

        return find(sql);
    }

    @Override
    public List<ItemCargaAbono> allActivosByFecha(CuentaBancaria cta, String tipoArchivo, Date fecha) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("cargaAbonos ca", "ca.cuentaBancaria cta")
                .leftJoin("postulante")
                .filter("ca.tipoArchivo", tipoArchivo)
                .filter("ca.fechaCarga", fecha)
                .filter("cta.id", cta)
                .filter("ica.redundante", 0);

        return all(sql);
    }

    @Override
    public List<ItemCargaAbono> allActivosSinExtornosByFecha(CuentaBancaria cta, String tipoArchivo, Date fecha) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("cargaAbonos ca", "ca.cuentaBancaria cta")
                .leftJoin("postulante")
                .filter("ca.tipoArchivo", tipoArchivo)
                .filter("ca.fechaCarga", fecha)
                .filter("cta.id", cta)
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0)
                .filter("ica.esExtorno", 0)
                .filter("ica.descripcion", "like", "EFEC%");

        return all(sql);
    }

    @Override
    public List<ItemCargaAbono> allByDynaTable(DynatableFilter filter, CicloPostula ciclo, String tipoCarga) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ItemCargaAbono.class, "ica")
                .join("cuentaBancaria cta")
                .leftJoin("cargaAbonos ca", "ca.cicloPostula")
                .leftJoin("postulante po", "po.persona per", "po.modalidadIngreso mod")
                .leftJoin("po.cicloPostula cip", "cip.cicloAcademico")
                //                .filter("cip.id", ciclo)
                .in("cta.id", Arrays.asList(1L, 3L))
                .filter("ica.redundante", 0)
                .searchFields("mod.nombre", "po.estado", "po.codigo", "per.numeroDocIdentidad")
                .searchFields("ica.descripcion", "ica.numeroOperacion", "ica.importe", "ica.sucursal", "ica.usuarioBanco", "ica.fechaAbono")
                .searchFields("cta.numero", "cta.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ica.id desc");

        return all(sql);

    }

    @Override
    public List<ItemCargaAbono> allByFechaAbono(Date fecha) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "po.persona")
                .filter("ica.fechaAbono", fecha)
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0)
                .filter("ica.esExtorno", 0)
                .filter("ica.descripcion", "like", "EFEC%");

        return all(sql);

    }

    @Override
    public List<ItemCargaAbono> allSinFacturarByRangoFechaAbono(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fechaInicio, Date fechaFin) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "po.persona", "po.modalidadIngreso")
                .join("cargaAbonos ca", "ca.cuentaBancaria cb", "ca.cicloPostula cp")
                .leftJoin("conceptoPago con", "con.conceptoOrigen")
                .filter("ica.fechaAbono", ">=", fechaInicio)
                .filter("ica.fechaAbono", "<=", fechaFin)
                .filter("cp.id", ciclo)
                .filter("cb.id", ctaBanco)
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0)
                .filter("ica.esExtorno", 0)
                .filter("ica.descripcion", "like", "EFEC%")
                .isNull("ica.fechaImpresion");

        return all(sql);
    }

    @Override
    public List<ItemCargaAbono> allActivosByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "cuentaBancaria cb")
                .leftJoin("conceptoPago con", "con.conceptoOrigen")
                .leftJoin("cargaAbonos ca", "ca.cicloPostula cp")
                .leftJoin("po.persona", "po.modalidadIngreso")
                .filter("po.id", postulante)
                .filter("ica.redundante", 0)
                .filter("ica.esExtorno", 0)
                .filter("ica.descripcion", "like", "EFEC%")
                .filter("ica.extornado", 0);

        return all(sql);
    }

    @Override
    public List<ItemCargaAbono> allPosiblesExtornados(ItemCargaAbono itemCargaForm) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "po.persona")
                .leftJoin("extornador")
                .filter("ica.fechaAbono", itemCargaForm.getFechaAbono())
                .filter("ica.importe", itemCargaForm.getImporte())
                .filter("ica.usuarioBanco", itemCargaForm.getUsuarioBanco())
                .filter("ica.sucursal", itemCargaForm.getSucursal())
                .filter("ica.redundante", 0);

        return all(sql);
    }

    @Override
    public List<ItemCargaAbono> allPosiblesExtornados2(ItemCargaAbono itemCargaForm) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "po.persona")
                .leftJoin("extornador")
                .filter("ica.fechaAbono", itemCargaForm.getFechaAbono())
                .filter("ica.importe", itemCargaForm.getImporte())
                .filter("ica.usuarioBanco", "<>", itemCargaForm.getUsuarioBanco())
                .filter("ica.sucursal", itemCargaForm.getSucursal())
                .filter("ica.redundante", 0);

        return all(sql);
    }

    @Override
    public ItemCargaAbono findByExtonador(ItemCargaAbono extornador) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "po.persona", "extornador e")
                .filter("e.id", extornador)
                .filter("ica.redundante", 0);

        return find(sql);
    }


}

