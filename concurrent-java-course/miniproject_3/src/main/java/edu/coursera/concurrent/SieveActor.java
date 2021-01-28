package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
    final SieveActorActor sa = new SieveActorActor();
        finish(() -> {
            for (int i = 3; i <= limit; i += 2) {
                sa.send(i);
            }
            sa.send(0);
        });
        int tprimes = 1;
        SieveActorActor curr = sa;
        while (curr != null) {
            tprimes += curr.nump;
            curr = curr.next;
        }
        return tprimes;
    }



    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        static final int MAX_PRIMES = 1000;
        SieveActorActor next = null;
        int[] localp = new int[MAX_PRIMES];
        int nump = 0;
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */

        @Override
        public void process(final Object msg) {
            int cand = (int) msg;
            if (cand <= 0) {
                if (next != null) {
                    next.send(msg);
                }
                return;
            }

            if (!isLocalp(cand)){
                return;
            }

            if (nump < MAX_PRIMES) {
                localp[nump++] = cand;
                return;
            }

            if (next == null) {
                next = new SieveActorActor();
            }
            next.send(msg);
        }
        boolean isLocalp(int cand){
            for(int i = 0; i < nump; i++) {
                if (cand % localp[i] == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
