package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import java.util.Arrays;
import java.util.List;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.InteresadoDAO;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PRE;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

@Repository
public class InteresadoDAOH extends AbstractEasyDAO<Interesado> implements InteresadoDAO {

    public InteresadoDAOH() {
        super();
        setClazz(Interesado.class);

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Interesado findLock(Long id) {
        return (Interesado) getCurrentSession().load(Interesado.class, id, LockOptions.UPGRADE);
    }


    @Override
    public List<Interesado> allByNombre(String nombre, CicloPostula ciclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Interesado.class, "inte")
                .join("cicloPostula cp")
                .beginBlock()
                .__().complexFilter("concat(coalesce(inte.paterno,''),' ',coalesce(inte.materno,''),' ',coalesce(inte.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(inte.nombres,''),' ',coalesce(inte.paterno,''),' ',coalesce(inte.materno,''))", "like", nombre)
                .endBlock()
                .in("inte.estado", Arrays.asList(InteresadoEstadoEnum.CRE.name(), InteresadoEstadoEnum.REG.name()))
                .filter("cp.id", ciclo)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public Interesado findUltimoByDocumentoIdentidad(String documento) {
        Octavia sql = Octavia.query()
                .from(Interesado.class, "int")
                .join("cicloPostula ciclo")
                .join("ciclo.cicloAcademico aca")
                .filter("int.numeroDocIdentidad", documento)
                .orderBy("aca.codigo desc")
                .limit(1);

        return (Interesado) sql.find(getCurrentSession());
    }

    @Override
    public Interesado findByDocumentoIdentidad(String documento, CicloPostula ciclo) {

        Octavia sql = Octavia.query()
                .from(Interesado.class, "int")
                .join("cicloPostula ciclo")
                .filter("int.numeroDocIdentidad", documento)
                .filter("ciclo.id", ciclo.getId());

        return (Interesado) sql.find(getCurrentSession());
    }

    @Override
    public Interesado findByCelular(String celular, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Interesado.class, "int")
                .join("cicloPostula ciclo")
                .filter("int.celular", celular)
                .filter("ciclo.id", ciclo);

        return (Interesado) sql.find(getCurrentSession());
    }

    @Override
    public Interesado findUltimoByCelular(String celular) {
        Octavia sql = Octavia.query()
                .from(Interesado.class, "int")
                .join("cicloPostula ciclo")
                .join("ciclo.cicloAcademico aca")
                .filter("int.celular", celular)
                .orderBy("aca.codigo desc")
                .limit(1);

        return (Interesado) sql.find(getCurrentSession());
    }

    @Override
    public Interesado findByFacebook(String faceIdentifier, CicloPostula ciclo) {

        Octavia sql = Octavia.query()
                .from(Interesado.class, "int")
                .join("cicloPostula ciclo")
                .filter("int.facebook", faceIdentifier)
                .filter("ciclo.id", ciclo);

        return (Interesado) sql.find(getCurrentSession());
    }

}

