package pe.edu.lamolina.pivot.dao.finanza;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;

public interface CuentaBancariaDAO extends EasyDAO<CuentaBancaria> {

    CuentaBancaria findByNumero(String numero);

}
