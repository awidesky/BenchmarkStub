/*
 * ===================== Original Benchmark stub License =====================
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

import io.github.awidesky.jCipherUtil.cipher.symmetric.aes.AESKeySize;
import io.github.awidesky.jCipherUtil.cipher.symmetric.aes.AES_GCMCipherUtil;
import io.github.awidesky.jCipherUtil.messageInterface.InPut;

/*
 * mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=com.awidesky -DartifactId=BenchmarkStub -Dversion=1.0
 *
 * 
 * Step 1: build Maven project
 *  1. Right click on project,
 *  2. Select Run As,
 *  3. Select Maven Build and specify goals as clean install
 * 
 * Step 2: run tests
 * 1. Right click on project,
 * 2. Select Run As,
 * 3. Select Java Application and choose either Main - org.openjdk.jmh or the main you created
 * 
 * */

@Warmup(iterations = 1)
@Measurement(iterations = 2)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1)
public class MyBenchmark {
    
    @Param({ "128", "256", "512", "1024", "2048", "4096", "8192" })
    private int BUFSIZE;

    private static byte[] plain = new byte[32* 1024 * 1024]; // TODO : in file
    private static byte[] encrypted;
    
    @Setup(Level.Trial)
    public static void genData() {
    	new Random().nextBytes(plain);
    	encrypted = new AES_GCMCipherUtil.Builder("Hello, World!".toCharArray(), AESKeySize.SIZE_256).build().encryptToSingleBuffer(InPut.from(plain));
    }
    
    @Benchmark
    public void encrypt(Blackhole bh) throws Exception {
    	bh.consume(new AES_GCMCipherUtil.Builder("Hello, World!".toCharArray(), AESKeySize.SIZE_256).bufferSize(BUFSIZE).build().encryptToSingleBuffer(InPut.from(plain)));
    }
    @Benchmark
    public void decrypt(Blackhole bh) throws Exception {
    	bh.consume(new AES_GCMCipherUtil.Builder("Hello, World!".toCharArray(), AESKeySize.SIZE_256).bufferSize(BUFSIZE).build().decryptToSingleBuffer(InPut.from(encrypted)));
    }
    
}
/*

i5 8400 (heap data)
Benchmark            (BUFSIZE)  Mode  Cnt     Score   Error  Units
MyBenchmark.decrypt        128  avgt    2  1384.701          ms/op
MyBenchmark.decrypt        256  avgt    2  1404.861          ms/op
MyBenchmark.decrypt        512  avgt    2  1311.097          ms/op
MyBenchmark.decrypt       1024  avgt    2  1325.848          ms/op
MyBenchmark.decrypt       2048  avgt    2  1330.148          ms/op
MyBenchmark.decrypt       4096  avgt    2  1285.562          ms/op
MyBenchmark.decrypt       8192  avgt    2  1236.253          ms/op

MyBenchmark.encrypt        128  avgt    2   802.458          ms/op
MyBenchmark.encrypt        256  avgt    2   809.533          ms/op
MyBenchmark.encrypt        512  avgt    2   824.541          ms/op
MyBenchmark.encrypt       1024  avgt    2   828.644          ms/op
MyBenchmark.encrypt       2048  avgt    2   783.131          ms/op
MyBenchmark.encrypt       4096  avgt    2   776.425          ms/op
MyBenchmark.encrypt       8192  avgt    2   776.080          ms/op

M2 mac (heap data)
Benchmark            (BUFSIZE)  Mode  Cnt     Score   Error  Units
MyBenchmark.decrypt        128  avgt    2  2590.413          ms/op
MyBenchmark.decrypt        256  avgt    2  2879.331          ms/op
MyBenchmark.decrypt        512  avgt    2  2560.454          ms/op
MyBenchmark.decrypt       1024  avgt    2  2514.228          ms/op
MyBenchmark.decrypt       2048  avgt    2  2916.379          ms/op
MyBenchmark.decrypt       4096  avgt    2  2834.329          ms/op
MyBenchmark.decrypt       8192  avgt    2  2883.208          ms/op

MyBenchmark.encrypt        128  avgt    2  2632.838          ms/op
MyBenchmark.encrypt        256  avgt    2  2769.748          ms/op
MyBenchmark.encrypt        512  avgt    2  2638.438          ms/op
MyBenchmark.encrypt       1024  avgt    2  2593.194          ms/op
MyBenchmark.encrypt       2048  avgt    2  2670.498          ms/op
MyBenchmark.encrypt       4096  avgt    2  2575.861          ms/op
MyBenchmark.encrypt       8192  avgt    2  2587.340          ms/op
*/
