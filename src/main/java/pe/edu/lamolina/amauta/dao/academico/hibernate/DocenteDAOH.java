package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.academico.profesor.DocenteCicloBean;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.AptoPreBean;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import static pe.edu.lamolina.model.enums.DocenteEstadoEnum.ACT;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PerfilColaboradorEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;

@Repository
public class DocenteDAOH extends AbstractEasyDAO<Docente> implements DocenteDAO {

    public DocenteDAOH() {
        super();
        setClazz(Docente.class);
    }

    @Override
    public List<Docente> allByFacultadesDyantable(DynatableFilter filter, List<DepartamentoAcademico> departamento) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .in("da.id", departamento)
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania", "tdoc.simbolo", "doc.codigo")
                .searchFields("da.nombre", "fa.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("doc.id desc");

        return all(sql);
    }

    @Override
    public Docente find(long id) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .filter("doc.id", id);

        return find(sql);
    }

    @Override
    public Docente findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .leftJoin("persona per", "modalidadEstudio", "departamentoAcademico")
                .filter("doc.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<Docente> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio me", "departamentoAcademico da", "da.facultad")
                .filter("per.id", persona)
                .in("me.codigo", Arrays.asList(ModalidadEstudioEnum.PRE, ModalidadEstudioEnum.EPG));

        return all(sql);
    }

    @Override
    public List<Docente> allByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio", "departamentoAcademico")
                .in("per.id", personas);

        return all(sql);
    }

    @Override
    public List<Docente> allActivos(ModalidadEstudio modalidad) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "modalidadEstudio me")
                .leftJoin("departamentoAcademico")
                .filter("doc.estado", EstadoEnum.ACT)
                .filter("me.id", modalidad);

        return all(sql);
    }

    @Override
    public List<Docente> allByFilter(DynatableFilter filter, List<DepartamentoAcademico> dptos) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .in("da.id", dptos)
                .searchFields("per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania", "tdoc.simbolo")
                .searchFields("da.nombre", "fa.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("doc.id desc");

        return all(sql);
    }

    @Override
    public Docente findByDocente(Docente docente) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per")
                .leftJoin("modalidadEstudio me", "departamentoAcademico", "per.paisDomicilio")
                .filter("doc.id", docente);

        return find(sql);
    }

    @Override
    public List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "do")
                .join("persona per", "departamentoAcademico dpto")
                .filter("dpto.id", idDpto)
                .__().complexFilter("concat(per.paterno,' ',per.materno,' ',per.nombres)", "like", nombre)
                .__().complexFilter("concat(per.nombres,' ',per.paterno,' ',per.materno)", "like", nombre)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByNombreFilter(String nombre, Integer cantidad, String codigoDep) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        System.out.println("nombre = <<" + nombre + ">>");
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .__().filter("da.nombre", "like", nombre)
                .endBlock()
                .limit(cantidad);

        if (codigoDep != null) {
            sql.filter("da.codigo", codigoDep);
        }

        return all(sql);
    }

    @Override
    public List<Docente> allByDptoEstado(Long idDpto, String estado) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico dpto")
                .filter("dpto.id", idDpto)
                .filter("doc.estado", estado);
        return all(sql);
    }

    @Override
    public List<Docente> allByDepartamentosAcademicoEstado(List<DepartamentoAcademico> departamentos, EnteAcademicoEstadoEnum estado) {
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico dpto")
                .leftJoin("dpto.facultad fa")
                .in("dpto.id", departamentos)
                .filter("doc.estado", estado)
                .orderBy("fa.id", "dpto.id", "per.paterno");
        return all(sql);
    }

    @Override
    public List<Docente> allByNombreDepartamento(String nombre, DepartamentoAcademico departamento, int limit) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .leftJoin("per.tipoDocumento")
                .filter("da.id", departamento)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .endBlock()
                .limit(limit);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("doc.estado", EstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Docente> allByNombreDepartamentos(String nombre, List<DepartamentoAcademico> departamentos) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia subQuery = new Octavia()
                .from(Colaborador.class, "col")
                .join("persona perc", "oficina ofi", "ofi.tipoOficina tip", "cargo carg")
                .in("estado", Arrays.asList(ColaboradorEstadoEnum.ACT, ColaboradorEstadoEnum.DSC, ColaboradorEstadoEnum.PER, ColaboradorEstadoEnum.VAC))
                .filter("carg.codigo", PerfilColaboradorEnum.DOC)
                .filter("tip.codigo", TipoOficinaEnum.DPTO)
                .in("ofi.instanciaOficina", departamentos);

        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento")
                .filter("per.estado", PersonaEstadoEnum.ACT)
                .filter("doc.estado", EstadoEnum.ACT)
                .in("da.id", departamentos)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("per.numeroDocIdentidad", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .endBlock()
                .exists(subQuery)
                .linkedBy("per.id", "perc.id")
                .linkedBy("da.id", "ofi.instanciaOficina")
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Docente> allByNombreActivoFilter(String nombre, Integer cantidad, String codigoDep) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        System.out.println("nombre = <<" + nombre + ">>");
        Octavia sql = Octavia.query()
                .from(Docente.class, "doc")
                .join("persona per", "departamentoAcademico da")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("doc.codigo", "like", nombre)
                .__().filter("da.nombre", "like", nombre)
                .endBlock()
                .filter("doc.estado", ACT)
                .limit(cantidad);

        if (codigoDep != null) {
            sql.filter("da.codigo", codigoDep);
        }

        return all(sql);
    }

    @Override
    public List<DocenteCicloBean> AllDocentecicloAcademico(List<CicloAcademico> cicloAcademicos) {
        StringBuilder sql = new StringBuilder();
//        sql.append(" select caa.descripcion ,caa.codigo_anterior , dc.codigo cod_docente2,cad2.codigo pro_cad2,  ");
//        sql.append(" CONCAT(IFNULL(gp.paterno, ''), ' ', IFNULL(gp.materno, ''), ', ', IFNULL(gp.nombres, '')) AS nombreDocente,  ");        
//        sql.append(" cad2.nombre categ_nombre2,sid2.codigo pro_situac2,sid2.nombre situac_nombre2,  ");
//        sql.append(" ded2.codigo cod_dedica2,ded2.nombre,ada.nombre_largo   ");
//        sql.append("             from rrhh_contrato_docente a  ");
//        sql.append("             join aca_ciclo_academico caa on a.id_ciclo_inicio_contrato = caa.id and a.id_ciclo_inicio_contrato  = caa.id  ");
//        sql.append("             join aca_docente dc on a.id_docente = dc.id  ");
//        sql.append("             join rrhh_categoria_docente cad2 on a.id_categoria = cad2.id  ");
//        sql.append("             join gen_persona gp on gp.id=dc.id_persona  ");
//        sql.append("             join rrhh_situacion_docente sid2 on a.id_situacion = sid2.id  ");
//        sql.append("             join rrhh_dedicacion_docente ded2 on a.id_dedicacion = ded2.id  ");
//        sql.append("             join aca_docente ad on ad.id =a.id_docente   ");
//        sql.append("             join aca_departamento_academico ada on ada.id =ad.id_departamento_academico   ");
//        sql.append(" where caa.id = :CICLO order by  nombreDocente" );

        sql.append("  select distinct d.codigo as cod_docente2,ca.descripcion,ca.codigo_anterior ,CONCAT(IFNULL(p.paterno,''),' ',IFNULL(p.materno,''),' ',IFNULL(p.nombres,'')) as nombreDocente, ");
        sql.append("  cad.codigo pro_cad2, ");
        sql.append("  cad.nombre categ_nombre2,sid.codigo pro_situac2,sid.nombre situac_nombre2, ");
        sql.append("  ded.codigo cod_dedica2,ded.nombre nombre,cad2.descripcion2,ada.nombre_largo  ");
        sql.append("  from aca_grupo_seccion gs  ");
        sql.append("  join aca_seccion s on s.id_grupo_seccion = gs.id ");
        sql.append("  join aca_ciclo_academico ca on gs.id_ciclo = ca.id ");
        sql.append("  join aca_docente_seccion ds on ds.id_seccion = s.id ");
        sql.append("  join aca_docente d on ds.id_docente = d.id ");
        sql.append("  join gen_persona p on d.id_persona = p.id ");
        sql.append("  join rrhh_contrato_docente cd on cd.id_docente = d.id ");
        sql.append("  join rrhh_categoria_docente cad on cd.id_categoria = cad.id ");
        sql.append("  join rrhh_situacion_docente sid on cd.id_situacion = sid.id ");
        sql.append("  join rrhh_dedicacion_docente ded on cd.id_dedicacion = ded.id ");
        sql.append("  join aca_ciclo_academico cad2 on cd.id_ciclo_inicio_contrato = cad2.id and ca.id = cad2.id ");
        sql.append("  join aca_departamento_academico ada on ada.id =d.id_departamento_academico  ");
        sql.append("  where ca.id  = :CICLO ");
        sql.append("  and gs.estado = 'ACT' ");
        sql.append("  and s.estado = 'ACT' ");
        sql.append("  and s.matriculados > 0 ");
        sql.append("  order by 3 ");

        Query query = getCurrentSession().createSQLQuery(sql.toString())
                .addScalar("descripcion", StringType.INSTANCE)
                .addScalar("codigo_anterior", StringType.INSTANCE)
                .addScalar("cod_docente2", StringType.INSTANCE)
                .addScalar("nombreDocente", StringType.INSTANCE)
                .addScalar("pro_cad2", StringType.INSTANCE)
                .addScalar("categ_nombre2", StringType.INSTANCE)
                .addScalar("pro_situac2", StringType.INSTANCE)
                .addScalar("situac_nombre2", StringType.INSTANCE)
                .addScalar("cod_dedica2", StringType.INSTANCE)
                .addScalar("nombre", StringType.INSTANCE)
                .addScalar("nombre_largo", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DocenteCicloBean.class));
        List<Long> cicloIds = new ArrayList<>();
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            cicloIds.add(cicloAcademico.getId());
        }
        query.setParameterList("CICLO", cicloIds);
        return (List<DocenteCicloBean>) query.list();

    }

}
