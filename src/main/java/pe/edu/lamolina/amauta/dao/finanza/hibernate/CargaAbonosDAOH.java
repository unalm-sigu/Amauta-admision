package pe.edu.lamolina.amauta.dao.finanza.hibernate;

import java.util.Date;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TipoArchivoEnum;
import pe.edu.lamolina.model.finanzas.CargaAbonos;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.amauta.dao.finanza.CargaAbonosDAO;

@Repository
public class CargaAbonosDAOH extends AbstractEasyDAO<CargaAbonos> implements CargaAbonosDAO {

    public CargaAbonosDAOH() {
        super();
        setClazz(CargaAbonos.class);
    }

    @Override
    public CargaAbonos findHistoricoByCtaBancoFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha) {
        Octavia sql = Octavia.query()
                .from(CargaAbonos.class, "ca")
                .join("cuentaBancaria cta", "cicloPostula ci")
                .filter("cta.id", ctaBanco)
                .filter("ci.id", ciclo)
                .filter("ca.fechaCarga", fecha)
                .filter("ca.tipoArchivo", TipoArchivoEnum.HI);

        return find(sql);
    }

    @Override
    public Long countByFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(CargaAbonos.class, "ca")
                .join("cuentaBancaria cta", "cicloPostula ci")
                .filter("cta.id", ctaBanco)
                .filter("ci.id", ciclo)
                .filter("ca.fechaCarga", fecha);

        return (Long) sql.find(getCurrentSession());
    }

}
