package net.akami.atosym.operator;

import net.akami.atosym.core.MaskContext;
import net.akami.atosym.expression.MathObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/*
    To not confuse :

        Mult is a binary operator, which means that he can only computes a*b, not a*b*c*...
        However, it does not mean that the mult operator can only store 2 arguments.

        mult(a, mult(b,c)) can be computed, and will result in mult(a,b,c)

        Same goes for the sum :

        sum(1,2,3) will never be "computed", but sum(1, sum(2,3) will result in sum(1,2,3)
 */
public abstract class BinaryOperator extends MathOperator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BinaryOperator.class);

    public BinaryOperator(MaskContext context, String name) {
        this(context, new String[]{name});
    }

    public BinaryOperator(MaskContext context, String... names) {
        super(Arrays.asList(names), 2, context);
    }

    @Override
    protected MathObject operate(List<MathObject> input) {
        return binaryOperate(input.get(0), input.get(1));
    }

    public abstract MathObject binaryOperate(MathObject a, MathObject b);

    public static Set<BinaryOperator> generateDefaultBinaryOperators(MaskContext context) {
        return new HashSet<>(Arrays.asList(
                new SumOperator(context),
                new SubOperator(context),
                new MultOperator(context),
                new DivisionOperator(context),
                new ExponentiationOperator(context)
        ));
    }
}
