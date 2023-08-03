package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ObjetivoCitaConsejeroDAO;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.ObjetivoCitaConsejero;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

@Repository
public class ObjetivoCitaConsejeroDAOH extends AbstractEasyDAO<ObjetivoCitaConsejero> implements ObjetivoCitaConsejeroDAO {

    public ObjetivoCitaConsejeroDAOH() {
        super();
        setClazz(ObjetivoCitaConsejero.class);
    }

    @Override
    public List<ObjetivoCitaConsejero> allByPlanTutorial(PlanTutorial plan) {
        Octavia sql = new Octavia()
                .from(ObjetivoCitaConsejero.class, "occ")
                .join("citaConsejeroAlumno cca", "objetivoTutorial obt")
                .filter("obt.id", plan);

        return all(sql);
    }

    @Override
    public List<ObjetivoCitaConsejero> allByCitas(List<CitaConsejeroAlumno> citas) {
        Octavia sql = new Octavia()
                .from(ObjetivoCitaConsejero.class, "occ")
                .join("citaConsejeroAlumno cca", "objetivoTutorial obt")
                .in("cca.id", citas);

        return all(sql);
    }

    @Override
    public List<ObjetivoCitaConsejero> allByCita(CitaConsejeroAlumno cita) {
        Octavia sql = new Octavia()
                .from(ObjetivoCitaConsejero.class, "occ")
                .join("citaConsejeroAlumno cca", "objetivoTutorial obt")
                .filter("cca.id", cita);

        return all(sql);
    }

}
