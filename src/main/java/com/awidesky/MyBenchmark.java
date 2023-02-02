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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

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

@Warmup(iterations = 1) 		// Warmup Iteration = 3
@Measurement(iterations = 3)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 2)
public class MyBenchmark {

	@Benchmark
	public static void KB516() {
		downloadCompact(516 * 1024);
	}
	@Benchmark
	public static void M1() {
		downloadCompact(1024 * 1024);
	}
	@Benchmark
	public static void MB2() {
		downloadCompact(2 * 1024 * 1024);
	}
    
	public static void download(int bufsize) {
		if(new File(".\\ffmpeg.zip").exists()) new File(".\\ffmpeg.zip").delete();
		
		try (ReadableByteChannel in = Channels.newChannel(new URL("https://www.gyan.dev/ffmpeg/builds/ffmpeg-release-essentials.zip").openStream());
				FileChannel out = new FileOutputStream(new File(".\\ffmpeg.zip")).getChannel()) {
			ByteBuffer bytebuf = ByteBuffer.allocateDirect(bufsize);
			while (in.read(bytebuf) > 0 || bytebuf.position() > 0) {
				// flip the buffer which set the limit to current position, and position to 0.
				bytebuf.flip();
				out.write(bytebuf); // Write data from ByteBuffer to file
				bytebuf.compact(); // For the next read
			}
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
	}
    
  	public static void downloadCompact(int bufsize) {
  		if(new File(".\\ffmpeg.zip").exists()) new File(".\\ffmpeg.zip").delete();
  		
  		try (ReadableByteChannel in = Channels.newChannel(new URL("https://www.gyan.dev/ffmpeg/builds/ffmpeg-release-essentials.zip").openStream());
  				FileChannel out = new FileOutputStream(new File(".\\ffmpeg.zip")).getChannel()) {
  			ByteBuffer bytebuf = ByteBuffer.allocateDirect(bufsize);
  			
			boolean eof = false;
  			while (true) {
  				while(bytebuf.hasRemaining() && !eof) {
  					eof = in.read(bytebuf) == -1;
  				}
				// flip the buffer which set the limit to current position, and position to 0.
				bytebuf.flip();
				while(bytebuf.hasRemaining()) out.write(bytebuf); // Write data from ByteBuffer to file
				bytebuf.clear();
				if(eof) break;
			}
  		} catch (IOException e) {
  			// TODO
  			e.printStackTrace();
  		}
  	}

	
}
