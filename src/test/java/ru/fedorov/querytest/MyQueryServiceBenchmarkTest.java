package ru.fedorov.querytest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import ru.fedorov.querytest.entity.MyEntity;
import ru.fedorov.querytest.service.MyQueryService;

@SpringBootTest
public class MyQueryServiceBenchmarkTest {

    public static MyQueryService testQueryService;

    @Autowired
    void setService(MyQueryService testQueryService){
        MyQueryServiceBenchmarkTest.testQueryService = testQueryService;   
    }
   

    @Test
    public void launchBenchmark() throws Exception {

            Options opt = new OptionsBuilder()
                // Specify which benchmarks to run. 
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                // Set the following options as needed
                .mode (Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementTime(TimeValue.seconds(10))
                .measurementIterations(2)
                .threads(1)
                .forks(0)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
                //.addProfiler(WinPerfAsmProfiler.class)
                // .addProfiler(StackProfiler.class)
                .build();

            new Runner(opt).run();
        }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State (Scope.Thread)
    public static class BenchmarkState
    {
        // List<Integer> list;

        static int MAX_COUNT_OF_ROWS = 1000;
        static int PAGE_SIZE = 100;
        static int PAGES_COUNT = (int)(MAX_COUNT_OF_ROWS/PAGE_SIZE)-1;
        

        //   @Setup (Level.Trial) 
        //   public void initialize() {

        //         list = new ArrayList<>(PAGES_COUNT);
        //         for (int i = 0; i < PAGES_COUNT; i++)
        //             list.add (i*PAGE_SIZE);
        //     }
    }

    @Benchmark 
    public void benchmarkGetPageByNumberUsingMapper (BenchmarkState state, Blackhole bh) {

        // List<Integer> list = state.list;

        for (int iPageNumber = 0; iPageNumber < BenchmarkState.PAGES_COUNT; iPageNumber++){

            Pageable pageable = Pageable.ofSize(BenchmarkState.PAGE_SIZE).withPage(iPageNumber);
            bh.consume ( testQueryService.<MyEntity>getPageByNumberUsingMapper(pageable, MyEntity.class) );
        }
    }
}
