package pe.edu.lamolina.pivot.zelper;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import org.joda.time.DateTime;
import org.joda.time.Days;
import pe.edu.lamolina.model.enums.TipoDocumentoBienestarEnum;
import pe.edu.lamolina.model.enums.TipoMatriculaEnum;

public class Laboratory {

    public static void main2(String[] args) {

        DateTime inicio = new DateTime("2018-02-20");
        DateTime fin = new DateTime("2018-02-21");

        int dias = Days.daysBetween(inicio, fin).getDays();
        System.out.println("dias " + dias);
        
        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode objNode1 = new ObjectNode(JsonNodeFactory.instance);

//        for (TipoMatriculaEnum d : TipoMatriculaEnum.values()) {
//           objNode.put(d.name(),d.getValue());
//        };
                
      

        System.out.println("value: " + objNode );
    }
}
