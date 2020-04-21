package pe.edu.lamolina.amauta.dao.finanza;

import java.util.Date;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.CargaAbonos;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface CargaAbonosDAO extends EasyDAO<CargaAbonos> {

    CargaAbonos findHistoricoByCtaBancoFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha);

    Long countByFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha);

}
