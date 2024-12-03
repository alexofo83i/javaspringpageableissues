package ru.fedorov.querytest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.creation.MockSettingsImpl;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import ru.fedorov.querytest.MyQueryServiceBenchmarkTest.BenchmarkState;
import ru.fedorov.querytest.entity.MyEntity;
import ru.fedorov.querytest.service.MyQueryService;

@SpringBootTest
@Slf4j
public class MyQueryMockBenchmarkTest {

    private static volatile ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MyQueryMockBenchmarkTest.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        // in case when forks > 0 @Autowired will not work
        if(MyQueryMockBenchmarkTest.applicationContext == null){ 
            synchronized(MyQueryServiceBenchmarkTest.class){
                if(MyQueryMockBenchmarkTest.applicationContext == null){ 
                    ApplicationContext context = new SpringApplication(MyQueryApplication.class).run();
                    MyQueryMockBenchmarkTest.applicationContext = context;
                }
            }
        }
        return MyQueryMockBenchmarkTest.applicationContext;
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
    public static class MapperBenchmarkState {
        public ObjectMapper objectMapper;
        public Query query;
        
        @Setup(Level.Trial)
        public void initializeBean(){
            objectMapper = MyQueryMockBenchmarkTest.getApplicationContext().getBean(ObjectMapper.class);
            log.info("objectMapper " + objectMapper.toString() + " is initialized in thread " + Thread.currentThread().toString());

            query = mock(Query.class, Mockito.withSettings().stubOnly());
            List<Map<String, Object>> listStubs = new ArrayList<Map<String,Object>>(100);
            for( int i = 0; i < 100; i++){
                Map<String,Object> map = new HashMap<>();
                map.put("id", Long.valueOf(i));
                map.put("name", UUID.randomUUID().toString());
                map.put("total_cnt", Long.valueOf(1000000L));
                listStubs.add(map);
            }

            when(query.getResultList()).thenReturn(listStubs);
        }
    }

    @SuppressWarnings("unchecked")
    @Benchmark
    public void benchmarkGetPageByNumberUsingMapper (MapperBenchmarkState state, Blackhole bh) {
        bh.consume ( state.query
                    .getResultList()
                    .stream()
                    .map(x-> state.objectMapper
                            .convertValue(x, MyEntity.class))
                    .toList() 
                    );
    }

    @State (Scope.Thread)
    public static class TransformerBenchmarkState {
        public Query query;
        
        @Setup(Level.Trial)
        public void initializeBean(){
            query = mock(Query.class, Mockito.withSettings().stubOnly());
            List<Object[]> listStubs = new ArrayList<Object[]>(100);
            for( int i = 0; i < 100; i++){  
                Object[] tuple = new Object[]{Long.valueOf(1000000L), Long.valueOf(i), UUID.randomUUID().toString()};
                listStubs.add(tuple);
            }

            when(query.getResultList()).thenReturn(listStubs);
        }
    }


    @Benchmark @SuppressWarnings("unchecked")
    public void benchmarkGetPageByNumberUsingTransformer (TransformerBenchmarkState state, Blackhole bh) {
        bh.consume ( ((List<Object[]>)state.query
                    .getResultList())
                    .stream()
                    .map(((Object[] x) -> {
                        return new MyEntity( (Long) x[1], (String) x[2]); 
                    }))
                    .toList()
                    );
    }
}

