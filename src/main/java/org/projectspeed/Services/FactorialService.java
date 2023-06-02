package org.projectspeed.Services;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Service
public class FactorialService {

    /**
     * Calcula o fatorial de um número de forma assíncrona.
     *
     * @param number o número para calcular o fatorial
     * @return um CompletableFuture contendo o valor do fatorial
     */
    public CompletableFuture<BigInteger> calculateFactorial(int number) {
        return CompletableFuture.supplyAsync(() -> {
            BigInteger factorial = BigInteger.ONE;
            for (int i = 1; i <= number; i++) {
                factorial = factorial.multiply(BigInteger.valueOf(i));
            }
            long threadId = Thread.currentThread().getId();
            System.out.println("Thread #" + threadId + " está executando essa tarefa");
            System.out.println("Fatorial de " + number + ": " + factorial);
            return factorial;
        });
    }


    /**
     * Calcula o fatorial de um número de forma síncrona, sem utilizar threads.
     *
     * @param number o número para calcular o fatorial
     * @return um CompletableFuture contendo o valor do fatorial
     */
    public static CompletableFuture<BigInteger> NoThreadcalculateFactorial(int number) {
        BigInteger factorial = BigInteger.ONE;
        for (int i = 1; i <= number; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        System.out.println("Fatorial de " + number + ": " + factorial);
        return CompletableFuture.completedFuture(factorial);
    }

    /**
     * Calcula o fatorial parcial de um número.
     *
     * @param number o número para calcular o fatorial parcial
     * @return o valor do fatorial parcial como um objeto BigInteger
     */
    public static BigInteger calculatePartialFactorial(int number) {
        BigInteger partialFactorial = BigInteger.ONE;
        for (int i = 1; i <= number; i++) {
            partialFactorial = partialFactorial.multiply(BigInteger.valueOf(i));
        }
        return partialFactorial;
    }

}

