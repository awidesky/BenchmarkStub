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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

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

@Warmup(iterations = 0) 		// Warmup Iteration = 2
@Measurement(iterations = 1)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 2)
public class MyBenchmark {

	/**
	 * 
	 * Benchmark            (N)  Mode  Cnt       Score         Error  Units
	 * MyBenchmark.adler32    5  avgt    4  190133.821 ±± 1351327.724  ms/op
	 * MyBenchmark.crc32      5  avgt    4  174584.636 ±± 1062065.076  ms/op
	 * */

	@Param({"4096",	"8192",	"16384", "32768"})
	public int bufSize;
	
    private File f = new File("C:\\Users\\Eugene Hong\\Videos\\영화\\존윅_NonDRM_[FHD].mp4");
    		
    @Setup(Level.Trial)
    public void setup() {
        //setup
    }
	 
    @Benchmark
    public void adler32(Blackhole bh) {
		Adler32 ad = new Adler32();
		
		try (FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ)) {
	         ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufSize);

	         while(channel.read(byteBuffer) != -1) {
	        	 byteBuffer.flip();
	        	 while(byteBuffer.hasRemaining())
	        		 ad.update(byteBuffer);
	        	 byteBuffer.clear();
	         }

	      } catch (IOException e) {
	         e.printStackTrace();
	      }

		bh.consume(Long.toString(ad.getValue()));
    }
    
    @Benchmark
    public void crc32(Blackhole bh) {
		CRC32 cr = new CRC32();
		
		try (FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ)) {
	         ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufSize);

	         while(channel.read(byteBuffer) != -1) {
	        	 byteBuffer.flip();
	        	 while(byteBuffer.hasRemaining())
	        		 cr.update(byteBuffer);
	        	 byteBuffer.clear();
	         }

	      } catch (IOException e) {
	         e.printStackTrace();
	      }

		bh.consume(Long.toString(cr.getValue()));
    }

}
