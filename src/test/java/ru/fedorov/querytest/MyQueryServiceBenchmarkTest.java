package ru.fedorov.querytest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import ru.fedorov.querytest.entity.MyEntity;
import ru.fedorov.querytest.service.MyQueryService;

@SpringBootTest
@Slf4j
public class MyQueryServiceBenchmarkTest {

    private static volatile ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MyQueryServiceBenchmarkTest.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        // in case when forks > 0 @Autowired will not work
        if(MyQueryServiceBenchmarkTest.applicationContext == null){ 
            synchronized(MyQueryServiceBenchmarkTest.class){
                if(MyQueryServiceBenchmarkTest.applicationContext == null){ 
                    ApplicationContext context = new SpringApplication(MyQueryApplication.class).run();
                    MyQueryServiceBenchmarkTest.applicationContext = context;
                }
            }
        }
        return MyQueryServiceBenchmarkTest.applicationContext;
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
                .warmupTime(TimeValue.seconds(10))
                .warmupIterations(1)
                .measurementTime(TimeValue.seconds(20))
                .measurementIterations(3)
                .threads(10)
                .forks(0)
                // .forks(1)
                // .jvmArgs(  "-Xmx16g"
                //     , "-Xms16g"
                //     //, "-XX:+UseG1GC"
                //     , "-XX:+UseParallelGC"
                //     , "-verbose:gc"
                //     , "-Xloggc:gc_%p_%t.log"
                //     , "-XX:+UseGCLogFileRotation"
                //     , "-XX:NumberOfGCLogFiles=10"
                //     , "-XX:GCLogFileSize=10M"
                //     , "-XX:+PrintGCTimeStamps"
                //     , "-XX:+PrintGCDateStamps"
                //     , "-XX:+PrintGCDetails"
                //     , "-XX:+HeapDumpOnOutOfMemoryError"
                //     , "-XX:ReservedCodeCacheSize=256M"
                //     , "-XX:+UnlockCommercialFeatures"
                //     , "-XX:+FlightRecorder"
                //     , "-XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true"
                //     , "-Djmh.shutdownTimeout=300"
                //                         )
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
        MyQueryService testQueryService;

        @Param( { queryCountOverLimitOffset
                , queryLimitCountOverLimitOffset
                }
        )
        String QUERY_TEXT_TEMPLATE;

        static final String queryCountOverLimitOffset = "queryCountOverLimitOffset";
        static final String queryLimitCountOverLimitOffset = "queryLimitCountOverLimitOffset";
        static int MAX_COUNT_OF_ROWS = 100;
        static int PAGE_SIZE = 20;
        static int PAGES_COUNT = (int)(MAX_COUNT_OF_ROWS/PAGE_SIZE)-1;
        static HashMap<String,String> queriesMap;

        static {
            queriesMap = new HashMap<>();
            queriesMap.put(queryCountOverLimitOffset, "select count(*) over() as total_cnt, t.id, t.name from testentity t order by t.name");
            queriesMap.put(queryLimitCountOverLimitOffset, "with t_limited as (select  tt.id, tt.name from testentity tt order by tt.name LIMIT 10000 ) select  count(*) over() as total_cnt, t.id, t.name from t_limited t");
        }

        public String getQueryTextTemplate(){
            return queriesMap.get(QUERY_TEXT_TEMPLATE);
        }
        
        @Setup(Level.Trial)
        public void initializeBean(){
            testQueryService = MyQueryServiceBenchmarkTest.getApplicationContext().getBean(MyQueryService.class);
            log.info("MyQueryService " + testQueryService.toString() + " is initialized in thread " + Thread.currentThread().toString());
        }
    }

    @Benchmark
    public void benchmarkGetPageByNumberUsingMapper (BenchmarkState state, Blackhole bh) {
        String queryTextTemplate = state.getQueryTextTemplate();
        for (int iPageNumber = 0; iPageNumber < BenchmarkState.PAGES_COUNT; iPageNumber++){
            Pageable pageable = Pageable.ofSize(BenchmarkState.PAGE_SIZE).withPage(iPageNumber);
            bh.consume ( state.testQueryService.<MyEntity>getPageByNumberUsingMapper(queryTextTemplate, pageable, MyEntity.class) );
        }
    }

    @Benchmark
    public void benchmarkGetPageByNumberUsingTransformer (BenchmarkState state, Blackhole bh) {
        String queryTextTemplate = state.getQueryTextTemplate();
        for (int iPageNumber = 0; iPageNumber < BenchmarkState.PAGES_COUNT; iPageNumber++){
            Pageable pageable = Pageable.ofSize(BenchmarkState.PAGE_SIZE).withPage(iPageNumber);
            bh.consume ( state.testQueryService.<MyEntity>getPageByNumberUsingTransformer(queryTextTemplate, pageable, ((Object[] x) -> {
                return new MyEntity( ((Integer) x[1] ).longValue(), (String) x[2]);
            })));
        }
    }
}
