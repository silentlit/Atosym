package net.akami.atosym.handler;

import net.akami.atosym.alteration.CalculationCanceller;
import net.akami.atosym.alteration.IOCalculationModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents objects which handle
 */
public interface AlterationHandler<T, R> {

    List<CalculationCanceller<T, R>> getCancellers();
    List<IOCalculationModifier<T>> getModifiers();

    default void addCanceller(CalculationCanceller<T, R> canceller) {
        getCancellers().add(canceller);
    }

    default void addModifier(IOCalculationModifier<T> modifier) {
        getModifiers().add(modifier);
    }

    default void addCancellers(List<CalculationCanceller<T, R>> cancellers) {
        getCancellers().addAll(cancellers);
    }

    default void addModifiers(List<IOCalculationModifier<T>> modifiers) {
        getModifiers().addAll(modifiers);
    }

    default void removeCanceller(CalculationCanceller<T, R> canceller) {
        getCancellers().remove(canceller);
    }

    default void removeCancellers(List<CalculationCanceller<T, R>> cancellers) {
        getCancellers().removeAll(cancellers);
    }

    default void removeModifier(IOCalculationModifier<T> modifier) {
        getModifiers().remove(modifier);
    }

    default void removeModifiers(List<IOCalculationModifier<T>> modifiers) {
        getModifiers().removeAll(modifiers);
    }

    default Optional<CalculationCanceller<T, R>> getSuitableCanceller(T... input) {
        for(CalculationCanceller<T, R> alteration : getCancellers()) {
            if(alteration.appliesTo(input)) return Optional.of(alteration);
        }
        return Optional.empty();
    }

    default List<IOCalculationModifier<T>> getSuitableModifiers(T... input) {
        List<IOCalculationModifier<T>> compatibles = new ArrayList<>();

        for(IOCalculationModifier<T> alteration : getModifiers()) {
            if(alteration.appliesTo(input)) compatibles.add(alteration);
        }
        Collections.sort(compatibles);
        return compatibles;
    }

    default <S extends CalculationCanceller<T, R>> Optional<S> getCanceller(Class<S> clazz) {
        for(CalculationCanceller<T, R> canceller : getCancellers()) {
            // cast is secured
            if(canceller.getClass().equals(clazz)) return (Optional<S>) Optional.of(canceller);
        }
        return Optional.empty();
    }

    default <S extends IOCalculationModifier<T>> Optional<S> getModifier(Class<S> clazz) {
        for(IOCalculationModifier<T> canceller : getModifiers()) {
            // cast is secured
            if(canceller.getClass().equals(clazz)) return (Optional<S>) Optional.of(canceller);
        }
        return Optional.empty();
    }
}
