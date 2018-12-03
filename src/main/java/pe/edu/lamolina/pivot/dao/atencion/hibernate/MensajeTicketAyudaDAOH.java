package pe.edu.lamolina.pivot.dao.atencion.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.atencion.MensajeTicketAyudaDAO;
import pe.edu.lamolina.model.atencion.MensajeTicketAyuda;
import pe.edu.lamolina.model.atencion.TicketAtencionResumen;
import pe.edu.lamolina.model.atencion.TicketAyuda;
import pe.edu.lamolina.model.enums.EstadoTicketAyudaEnum;
import pe.edu.lamolina.model.enums.TipoMensajeTicketAyudaEnum;
import pe.edu.lamolina.model.general.Oficina;

@Repository
public class MensajeTicketAyudaDAOH extends AbstractEasyDAO<MensajeTicketAyuda> implements MensajeTicketAyudaDAO {

    public MensajeTicketAyudaDAOH() {
        super();
        setClazz(MensajeTicketAyuda.class);
    }

    @Override
    public List<MensajeTicketAyuda> allByDynatable(DynatableFilter filter,  Oficina oficina) {

        DynatableSql sql = new DynatableSql(filter);
        sql.from(MensajeTicketAyuda.class, "me")
                .join("ticketAyuda tic", "tic.persona per", "tic.oficina ofi")
                .leftJoin("tic.colaborador co","co.persona peco")
                .filter("me.tipo", TipoMensajeTicketAyudaEnum.TICKET.name())
                .filter("ofi.id", oficina)
                .orderBy("me.fechaRegistro desc");
        sql.beginRelativeFilters();
        this.setCondicionEstado(filter, sql);
        return all(sql);

    }

    private void setCondicionEstado(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("tic.estado")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("ACTIVO")) {
                sql.filter("me.estado", EstadoTicketAyudaEnum.ACTIVO);
            } else if (values.equals("RESPONDIDO")) {
                sql.filter("me.estado", EstadoTicketAyudaEnum.RESPONDIDO);
            } else if (values.equals("RESUELTO")) {
                sql.filter("me.estado", EstadoTicketAyudaEnum.RESUELTO);
            }
        }

    }

    @Override
    public MensajeTicketAyuda findByTicket(TicketAyuda ticket) {
        Octavia sql = Octavia.query()
                .from(MensajeTicketAyuda.class, "msj")
                .join("ticketAyuda tic", "tic.persona per", "tic.oficina ofi")
                .leftJoin("tic.colaborador co")
                .filter("msj.tipo", TipoMensajeTicketAyudaEnum.TICKET.name())
                .filter("tic.id", ticket);
        return find(sql);
    }

    @Override
    public List<MensajeTicketAyuda> allByTicketAyuda(TicketAyuda ticket) {

        Octavia sql = Octavia.query()
                .from(MensajeTicketAyuda.class, "msj")
                .join("ticketAyuda tic", "tic.persona per", "tic.oficina ofi")
                .leftJoin("tic.colaborador co")
                .filter("tic.id", ticket);
        return all(sql);
    }

    @Override
    public List<MensajeTicketAyuda> allByTicketExcept(TicketAyuda ticket, MensajeTicketAyuda mensaje) {
        Octavia sql = Octavia.query()
                .from(MensajeTicketAyuda.class, "msj")
                .join("ticketAyuda tic", "tic.persona per", "tic.oficina ofi")
                .leftJoin("tic.colaborador co")
                .filter("tic.id", ticket)
                .notIn("msj.id", Arrays.asList(mensaje.getId()))
                .orderBy("msj.fechaRegistro");
        return all(sql);
    }

    @Override
    public TicketAtencionResumen findResumen(Oficina oficina) {

        StringBuilder sql = new StringBuilder();

        sql.append("select new ").append(TicketAtencionResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case msj.estado when :ACTIVO then 1 else 0 end),   ");
        sql.append("   sum(case msj.estado when :RESPONDIDO then 1 else 0 end),   ");
        sql.append("   sum(case msj.estado when :RESUELTO  then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(MensajeTicketAyuda.class.getName()).append(" as msj ");
        sql.append("  inner join msj.ticketAyuda tic ");
        sql.append("  inner join tic.oficina ofi ");
        sql.append("  where msj.tipo = :TIPOTICKET ");
        sql.append("      and ofi.id = :IDOFICINA ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setString("ACTIVO", EstadoTicketAyudaEnum.ACTIVO.name());
        query.setString("RESPONDIDO", EstadoTicketAyudaEnum.RESPONDIDO.name());
        query.setString("RESUELTO", EstadoTicketAyudaEnum.RESUELTO.name());

        query.setString("TIPOTICKET", TipoMensajeTicketAyudaEnum.TICKET.name());
        query.setLong("IDOFICINA",oficina.getId());

        return (TicketAtencionResumen) query.uniqueResult();
    }

}
