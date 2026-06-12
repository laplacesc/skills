package sc.laplace.test.hbasequery;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import sc.laplace.test.hbasequery.process.HbaseQueryProcess;
import sc.laplace.test.hbasequery.trigger.CountOrTimeTrigger;

import java.io.IOException;

/**
 * @author jxwu
 */
@Slf4j
public class HbaseQueryJob {

    private final ParameterTool parameterTool;

    public HbaseQueryJob(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    public void run() throws IOException {
        log.info("job start. parameter: {}", parameterTool.toMap());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment()
                .setRuntimeMode(RuntimeExecutionMode.AUTOMATIC)
                .enableCheckpointing(5000);

        env.getConfig().setGlobalJobParameters(parameterTool);

        FileSource<String> source = FileSource.forRecordStreamFormat(
                new TextLineFormat(),
                new Path(parameterTool.get("input"))
        ).build();

        // StreamingFileSink<String> streamingFileSink = StreamingFileSink
        //         .<String>forRowFormat(new Path(parameterTool.get("type")), new SimpleStringEncoder<>())
        //         .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "c2 domain file")
                .windowAll(TumblingProcessingTimeWindows.of(Time.minutes(parameterTool.getLong("windowMinutes"))))
                .trigger(new CountOrTimeTrigger(
                        parameterTool.getLong("triggerCount"),
                        Time.minutes(parameterTool.getLong("triggerMinutes")).toMilliseconds())
                ).process(new HbaseQueryProcess());

        try {
            env.execute(parameterTool.get("jobName"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("job finished.");
    }
}
