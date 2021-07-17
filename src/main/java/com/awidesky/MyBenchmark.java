/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.awidesky;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/*
 * mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=com.awidesky -DartifactId=BenchmarkStub -Dversion=1.0
 * */

@Warmup(iterations = 3) 		// Warmup Iteration = 3
@Measurement(iterations = 5)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 2)
public class MyBenchmark {

    @Param({ "10000000" })
    private int N;

    @Param({ "1000" })
    private int DEMENSION;

    private double[][] a;
    private double[][] b;
    
    double[][] result1 = new double[DEMENSION][DEMENSION];
    double[][] result2 = new double[DEMENSION][DEMENSION];
    
    @Setup(Level.Iteration)
    public void setup() {	
        //setup
    	a = new double[DEMENSION][DEMENSION];
    	b = new double[DEMENSION][DEMENSION];
    	
    	result1 = new double[DEMENSION][DEMENSION];
    	result2 = new double[DEMENSION][DEMENSION];
    	
    	
    	Random r = new Random();
    	
    	for(int i = 0; i < DEMENSION ; i++) {
    		for(int j = 0; j < DEMENSION ; j++) {
    			
    			a[i][j] = r.nextDouble();
    			b[i][j] = r.nextDouble();
    			
    		}
    	}
    }
	 

/* In my laptop
Benchmark          (DEMENSION)        (N)  Mode  Cnt     Score      Error  Units
MyBenchmark.test1         1000  10000000  avgt   10  6920.150 ± 1875.616  ms/op
MyBenchmark.test2         1000  10000000  avgt   10  1005.611 ±  218.944  ms/op
*/
    @Benchmark
    public void test1(Blackhole bh) {
    	
    	
		for (int i = 0; i < DEMENSION; i++)
			for (int j = 0; j < DEMENSION; j++)
				for (int k = 0; k < DEMENSION; k++)
					result1[i][j] += a[i][k] * b[k][j];
		
		bh.consume(result1);
    }
    
    @Benchmark
    public void test2(Blackhole bh) { /** Optimized */
    	
    	
		for (int i = 0; i < DEMENSION; i++)
			for (int j = 0; j < DEMENSION; j++)
				for (int k = 0; k < DEMENSION; k++)
					result1[i][k] += a[i][j] * b[j][k];
		
		bh.consume(result2);
    }

}
