package net.akami.mask.utils;

import net.akami.mask.core.MaskContext;
import net.akami.mask.expression.Expression;
import net.akami.mask.expression.Monomial;
import net.akami.mask.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static net.akami.mask.core.MaskContext.DEFAULT;

public class MathUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MathUtils.class);

    public static Expression sum(Expression a, Expression b)      { return sum(a, b, DEFAULT);      }
    public static Expression subtract(Expression a, Expression b) { return subtract(a, b, DEFAULT); }
    public static Expression mult(Expression a, Expression b)     { return mult(a, b, DEFAULT);     }
    public static Expression divide(Expression a, Expression b)   { return divide(a, b, DEFAULT);   }
    public static Expression pow(Expression a, Expression b)      { return pow(a, b, DEFAULT);      }

    public static Expression sum(Expression a, Expression b, MaskContext context)      { return context.binaryCompute(a, b, Adder.class);         }
    public static Expression subtract(Expression a, Expression b, MaskContext context) { return context.binaryCompute(a, b, Subtractor.class);    }
    public static Expression mult(Expression a, Expression b, MaskContext context)     { return context.binaryCompute(a, b, Multiplier.class); }
    public static Expression divide(Expression a, Expression b, MaskContext context)   { return context.binaryCompute(a, b, Divider.class);       }
    public static Expression pow(Expression a, Expression b, MaskContext context)      { return context.binaryCompute(a, b, PowerCalculator.class); }

    public static Expression diffSum(Expression a, Expression altA, Expression b, Expression altB) {
        return sum(altA, altB);
    }
    public static Expression diffSubtract(Expression a, Expression altA, Expression b, Expression altB) {
        return subtract(altA, altB);
    }
    public static Expression diffMult(Expression a, Expression altA, Expression b, Expression altB) {
        return sum(mult(altA, b), mult(altB, a));
    }
    public static Expression diffDivide(Expression a, Expression altA, Expression b, Expression altB) {
        return divide(subtract(mult(altA,b), mult(altB, a)), mult(b, b));
    }
    public static Expression diffPow(Expression a, Expression altA, Expression b, Expression altB) {
        return mult(mult(b, pow(a, subtract(b, Expression.of(1)))), altA);
    }

    @Deprecated
    /**
     * This method does not go along with the new expression system, thus will be replaced soon.
     */
    public static String monomialSum(List<String> monomials) {
        List<Monomial> finalMonomials = new ArrayList<>();
        for(String monomial : monomials) {
            finalMonomials.add(ReducerFactory.reduce(monomial).get(0));
        }
        return new Expression(finalMonomials).toString();
    }

    @Deprecated
    public static String divide(String a, String b) {
        Expression bExp = ReducerFactory.reduce(b);
        List<String> monomials = ExpressionUtils.toMonomials(a);
        List<String> finalResult = new ArrayList<>();

        for(String monomial : monomials) {
            Expression divResult = divide(ReducerFactory.reduce(monomial), bExp);
            finalResult.add(divResult.toString());
        }
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for(String result : finalResult) {
            if(ExpressionUtils.isSigned(result) || i == 0) {
                builder.append(result);
            } else {
                builder.append('+').append(result);
            }
            i++;
        }
        return builder.toString();
    }
    public static String breakNumericalFraction(String self) {
        for(int i = 0; i < self.length(); i++) {
            if(self.charAt(i) == '/') {
                // TODO return divide(self.substring(0, i), self.substring(i+1));
            }
        }
        return self;
    }

    public static String sin(String a) {
        return trigonometryOperation(a, '@', Math::sin);
    }

    public static String cos(String a) {
        return trigonometryOperation(a, '#', Math::cos);
    }

    public static String tan(String a) {
        return trigonometryOperation(a, '§', Math::tan);
    }

    public static String trigonometryOperation(String a, char opChar, UnaryOperation operation) {
        if(ExpressionUtils.isANumber(a)) {
            double result = operation.compute(Double.valueOf(a));
            return String.valueOf(Math.abs(result) > 10E-15 ? result : 0);
        }
        return "(("+a+")"+opChar+")";
    }

    public static String roundPeriodicSeries(String value) {
        if(!value.contains(".")) return value;

        String[] parts = value.split("\\.");
        if(parts[1].matches("[9]+") && parts[1].length() > 15) {
            return String.valueOf(Integer.parseInt(parts[0]) + 1);
        }
        if(parts[1].matches("[0]+"))
            return parts[0];
        return value;
    }

    public static String cutSignificantZero(String self) {
        self = roundPeriodicSeries(self);
        return self.endsWith(".0") ? self.substring(0, self.length() - 2) : self;
    }

    /**
     * The reason why it returns an array of strings instead of an array of integers is that
     * it is easier to deal with strings during the reduction process
     * @param self the number to decompose
     * @return a list of strings being the divided version of the given parameter
     */
    public static List<String> decomposeNumberToString(float self) {
        LOGGER.info("Now decomposing float {}", self);

        List<String> results = new ArrayList<>();
        if (self % 1 != 0) {
            LOGGER.info("Non-integer given, returns it");
            results.add(String.valueOf(self));
            return results;
        }
        if (self < 0) {
            results.add("-1");
            self *= -1;
        }

        List<Integer> dividers = getDividers(self);

        int index = 0;
        while (index < dividers.size()) {
            int divider = dividers.get(index);
            if (self % divider == 0) {
                LOGGER.error("{} is a divider of {}", divider, self);
                results.add(String.valueOf(divider));
                self /= divider;
            } else {
                LOGGER.error("{} is not a divider of {}", divider, self);
                index++;
            }
        }
        LOGGER.error("Result : {}", results);
        return results;
    }

    public static List<Float> decomposeNumber(float self) {
        return decomposeNumber(self, 0);
    }

    public static List<Float> decomposeNumber(float self, float other) {
        LOGGER.info("Now decomposing float {}", self);

        List<Float> results = new ArrayList<>();
        if(other != 0 && self % other == 0) {
            float divResult = self / other;
            self /= divResult;
            results.add(divResult);
        }

        if (self % 1 != 0) {
            LOGGER.info("Non-integer given, returns it");
            results.add(self);
            return results;
        }
        if (self < 0) {
            results.add(-1.0f);
            self *= -1;
        }

        List<Integer> dividers = getDividers(self);

        int index = 0;
        while (index < dividers.size()) {
            int divider = dividers.get(index);
            if (self % divider == 0) {
                LOGGER.debug("{} is a divider of {}", divider, self);
                results.add((float) divider);
                self /= divider;
            } else {
                LOGGER.debug("{} is not a divider of {}", divider, self);
                index++;
            }
        }
        LOGGER.info("Result : {} (decomposition of {})", results, self);
        return results;
    }

    public static List<Integer> getDividers(float self) {
        List<Integer> dividers = new ArrayList<>();
        // Builds the dividers array
        for (int i = 2; i <= self; i++) {
            boolean unique = true;
            for (int j = 2; j < i; j++) {
                if (i % j == 0 && (j != i && i != 2)) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                dividers.add(i);
            }
        }
        return dividers;
    }

    @FunctionalInterface
    private interface UnaryOperation {
        double compute(double d);
    }
}
