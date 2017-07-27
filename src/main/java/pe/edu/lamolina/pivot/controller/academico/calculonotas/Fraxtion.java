package pe.edu.lamolina.pivot.controller.academico.calculonotas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;

public class Fraxtion {

    BigDecimal dividendo;

    BigDecimal divisor;

    public static Fraxtion ZERO = new Fraxtion("0");

    private final Integer decimalesMinimo = 14;
    private final Integer decimalesMaximo = 16;

    public Fraxtion(Object dividendo, Object divisor) {
        if (dividendo instanceof BigDecimal) {
            this.dividendo = ((BigDecimal) dividendo).add(BigDecimal.ZERO);
        } else {
            this.dividendo = TypesUtil.getBigDecimal(dividendo);
        }

        if (divisor instanceof BigDecimal) {
            this.divisor = ((BigDecimal) divisor).add(BigDecimal.ZERO);
        } else {
            this.divisor = TypesUtil.getBigDecimal(divisor);
        }

        if (this.divisor.compareTo(BigDecimal.ONE) != 0) {
            if (!this.isIndefinido()) {
                this.dividendo = this.getValue();
                this.divisor = BigDecimal.ONE;
            }
        }

        if (this.divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new PhobosException("Error por división de cero");
        }
    }

    public Fraxtion(Object fraxtion) {
        if (fraxtion instanceof String) {
            String fr = (String) fraxtion;
            if (fr.contains("/")) {
                this.dividendo = new BigDecimal(fr.split("/")[0]);
                this.divisor = new BigDecimal(fr.split("/")[1]);

            } else {
                this.dividendo = new BigDecimal(fr);
                this.divisor = BigDecimal.ONE;
            }

        } else if (fraxtion instanceof BigDecimal) {
            this.dividendo = ((BigDecimal) fraxtion).add(BigDecimal.ZERO);
            this.divisor = BigDecimal.ONE;

        } else {
            this.dividendo = TypesUtil.getBigDecimal(fraxtion);
            this.divisor = BigDecimal.ONE;
        }

        if (this.divisor.compareTo(BigDecimal.ONE) != 0) {
            if (!this.isIndefinido(this.dividendo, this.divisor)) {
                this.dividendo = this.getValue();
                this.divisor = BigDecimal.ONE;
            }
        }

        if (this.divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new PhobosException("Error por división de cero");
        }

    }

    public Fraxtion multiply(BigDecimal multiplicador) {
        if (this.divisor.compareTo(BigDecimal.ONE) != 0) {
            if (!this.isIndefinido(multiplicador, this.divisor)) {
                BigDecimal dividendo = this.dividendo.multiply(multiplicador.divide(this.divisor, decimalesMinimo, RoundingMode.HALF_DOWN));
                BigDecimal divisor = BigDecimal.ONE;
                return new Fraxtion(dividendo, divisor);
            }
        }
        return new Fraxtion(this.dividendo.multiply(multiplicador), this.divisor);

    }

    public Fraxtion divide(BigDecimal divisor) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new PhobosException("Error por división de cero");
        }

        if (divisor.compareTo(BigDecimal.ONE) == 0) {
            return new Fraxtion(this.dividendo, this.divisor);
        }

        if (!this.isIndefinido(this.dividendo, divisor)) {
            BigDecimal dividendo = this.dividendo.divide(divisor, decimalesMinimo, RoundingMode.HALF_DOWN);
            return new Fraxtion(dividendo, this.divisor);
        }
        return new Fraxtion(this.dividendo, this.divisor.multiply(divisor));
    }

    public Fraxtion add(BigDecimal sumando) {
        if (this.divisor.compareTo(BigDecimal.ONE) == 0) {
            return new Fraxtion(this.dividendo.add(sumando), this.divisor);
        }

        return new Fraxtion(this.dividendo.add(this.divisor.multiply(sumando)), this.divisor);
    }

    public Fraxtion substract(BigDecimal sustraendo) {
        if (this.divisor.compareTo(BigDecimal.ONE) == 0) {
            return new Fraxtion(this.dividendo.subtract(sustraendo), this.divisor);
        }

        return new Fraxtion(this.dividendo.subtract(this.divisor.multiply(sustraendo)), this.divisor);
    }

    public Fraxtion multiply(Fraxtion multiplicador) {
        return new Fraxtion(this.dividendo.multiply(multiplicador.getDividendo()), this.divisor.multiply(multiplicador.getDivisor()));

    }

    public Fraxtion divide(Fraxtion divisor) {
        if (divisor.getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new PhobosException("Error por división de cero");
        }
        return new Fraxtion(this.dividendo.multiply(divisor.getDivisor()), this.divisor.multiply(divisor.getDividendo()));
    }

    public Fraxtion add(Fraxtion sumando) {
        BigDecimal dividendo = this.dividendo.multiply(sumando.getDivisor()).add(this.divisor.multiply(sumando.getDividendo()));
        BigDecimal divisor = this.divisor.multiply(sumando.getDivisor());
        return new Fraxtion(dividendo, divisor);
    }

    public Fraxtion substract(Fraxtion sustraendo) {
        BigDecimal dividendo = this.dividendo.multiply(sustraendo.getDivisor()).subtract(this.divisor.multiply(sustraendo.getDividendo()));
        BigDecimal divisor = this.divisor.multiply(sustraendo.getDivisor());
        return new Fraxtion(dividendo, divisor);
    }

    private boolean isIndefinido(BigDecimal dividendo, BigDecimal divisor) {
        BigDecimal div1 = dividendo.divide(divisor, decimalesMinimo, RoundingMode.HALF_DOWN);
        BigDecimal div2 = dividendo.divide(divisor, decimalesMaximo, RoundingMode.HALF_DOWN);
        return div1.compareTo(div2) != 0;
    }

    public boolean isIndefinido() {
        return this.isIndefinido(this.dividendo, this.divisor);
    }

    public BigDecimal getValue() {
        return this.dividendo.divide(this.divisor, decimalesMinimo, RoundingMode.HALF_DOWN);
    }

    public BigDecimal getValue(int decimales) {
        return this.dividendo.divide(this.divisor, decimales, RoundingMode.HALF_DOWN);
    }

    public BigDecimal getValue(int decimales, RoundingMode round) {
        return this.dividendo.divide(this.divisor, decimales, round);
    }

    public int sign() {
        return this.getValue().signum();
    }

    public BigDecimal getDividendo() {
        return dividendo;
    }

    public BigDecimal getDivisor() {
        return divisor;
    }

    public boolean isZero() {
        return this.divisor.compareTo(BigDecimal.ONE) == 0 && this.dividendo.compareTo(BigDecimal.ZERO) == 0;
    }

    public int compareTo(Fraxtion fr) {
        BigDecimal one = this.getValue(20);
        BigDecimal two = fr.getValue(21);
        return one.compareTo(two);
    }

    @Override
    public String toString() {
        if (this.divisor.compareTo(BigDecimal.ONE) == 0) {
            return this.dividendo.stripTrailingZeros().toString();
        }
        return this.dividendo.stripTrailingZeros() + "/" + this.divisor.stripTrailingZeros();
    }

    public static class OrdenReverso implements Comparator<Fraxtion> {

        @Override
        public int compare(Fraxtion fr1, Fraxtion fr2) {
            return fr2.getValue().compareTo(fr1.getValue());
        }
    }

}
