# Java Random Number Generator
Testing tool that implements and compare random number generation methods provided by Java libraries.
The fixed algorithm non "cryptographically secure" used comes from the classes: java.lang.**Math**, java.util.**Random** and java.util.concurrent.**ThreadLocalRandom**.
It compares those algorithm with java.security.**SecureRandom**, as a [CSPRNG](https://en.wikipedia.org/wiki/Cryptographically_secure_pseudorandom_number_generator).
There are a total of 6 [SecureRandom Algorithm available](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecureRandom), the ones you can use are printed, and are dependent on your OS.
 
 As an example, see [differences between Random and SecureRandom](https://www.techiedelight.com/difference-java-util-random-java-security-securerandom/).
 
## Behaviour

For each algorthm you choose, it asks the length (in bits) and the number of attempts. Then computes the [**Runs Test**](https://en.wikipedia.org/wiki/Wald%E2%80%93Wolfowitz_runs_test)
with a simple analisys on the frequence (average, variance, standard deviation) for each algorithm used, aggregating runs of the same length. You can visualize the **longest runs generated**.

**execution time** for each algorithm used is shown. 

Reference values are in [FIPS 140-1](https://tsapps.nist.gov/publication/get_pdf.cfm?pub_id=917970).

Bit streams are generated in two ways:
* **FullFill**, where the selected class is asked to generate in one call the entire byte array.
* **BitByBit**, with the methods nextBool() asking to generate single bits in a loop.

Seeds are never manually configured. 

If (number of bits)*(number of attempts) < 50000 it asks also if you want to visualize the generated numbers as well as the monobit test (rate between zero and ones). 
Limit of 50000 is just to prevent overflow on stdout.
