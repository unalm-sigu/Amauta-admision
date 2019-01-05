package pe.edu.lamolina.pivot.dao.finanza;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface ItemCargaAbonoDAO extends EasyDAO<ItemCargaAbono> {

    ItemCargaAbono findByPostulante(Postulante postulante);

    ItemCargaAbono findRedundante(ItemCargaAbono item);

    List<ItemCargaAbono> allActivosByFecha(CuentaBancaria cta, String tipo, Date hoy);

    List<ItemCargaAbono> allActivosSinExtornosByFecha(CuentaBancaria cta, String tipo, Date fecha);

    List<ItemCargaAbono> allByDynaTable(DynatableFilter filter, CicloPostula ciclo, String tipoCarga);

    List<ItemCargaAbono> allByFechaAbono(Date fecha);

    List<ItemCargaAbono> allSinFacturarByRangoFechaAbono(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fechaInicio, Date fechaFin);

    List<ItemCargaAbono> allActivosByPostulante(Postulante postulante);

//    List<ItemCargaAbono> allActivosByPostulante(Postulante postulante, CicloPostula ciclo);
    List<ItemCargaAbono> allPosiblesExtornados(ItemCargaAbono itemCarga);

    List<ItemCargaAbono> allPosiblesExtornados2(ItemCargaAbono itemCarga);

    ItemCargaAbono findByExtonador(ItemCargaAbono extornador);

    List<ItemCargaAbono> allInscripcionByCicloPostula(CicloPostula cicloPostula);

    List<ItemCargaAbono> allActivosByCicloPostula(CicloPostula cicloPostula);
}
