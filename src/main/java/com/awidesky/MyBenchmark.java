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

import java.util.ArrayList;
import java.util.List;
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

@Warmup(iterations = 2) 		// Warmup Iteration = 3
@Measurement(iterations = 5)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 2)
public class MyBenchmark {

    @Param({ "10000" })
    private int N;
    List<User> users = new ArrayList<>();

    @Setup(Level.Trial)
    public void setup() {
    	for(int i = 0; i < N; i++){
    	    User user = new User()
    	            .setId(i)
    	            .setName(i+"name")
    	            .setEmailAddress("test"+i+"gmail.com")
    	            .setNumber(i+10)
    	            .setVerified(i % 2 == 0 ? true : false);
    	    users.add(user);
    	}
    }
	 
    @Benchmark
    public void sequentialCheck(Blackhole bh) {
    	users.stream().parallel()
		.filter(user -> !user.isVerified())
		.forEach(bh::consume);
    }
    
    
    @Benchmark
    public void parallelCheck(Blackhole bh) {
    	users.stream()
    	.filter(user -> !user.isVerified())
    	.forEach(bh::consume);
    }

    /*
    @Benchmark
	public void sequentialToList(Blackhole bh) {
		bh.consume(users.stream().filter(user -> !user.isVerified()).map(user -> user.getNumber())
				.collect(Collectors.toList()));
	}
    
    
    @Benchmark
    public void parallelToList(Blackhole bh) {
    	bh.consume(users.stream().filter(user -> !user.isVerified()).map(user -> user.getNumber())
				.collect(Collectors.toList()));
    }
    */
}

class User {

	private int id;
	private String name;
	private String addr;
	private int num;
	private boolean veri;
	
	public User setId(int i) { id = i; return this; }
	public User setName(String n) { name = n; return this; }
	public User setEmailAddress(String a) { addr = a; return this; }
	public User setNumber(int i) { num = i; return this; }
	public User setVerified(boolean b) { veri = b; return this; }

	public boolean isVerified() { return veri; }
	public int getNumber() { return num; }
	
	@Override
	public String toString() { return id + " " + name + " " + addr + " " + num + " " + veri; }
	
}
