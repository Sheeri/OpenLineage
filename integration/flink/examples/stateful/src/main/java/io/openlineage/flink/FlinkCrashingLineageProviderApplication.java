/*
/* Copyright 2018-2025 contributors to the OpenLineage project
/* SPDX-License-Identifier: Apache-2.0
*/

package io.openlineage.flink;

import static io.openlineage.flink.StreamEnvironment.setupEnv;

import io.openlineage.client.OpenLineage;
import io.openlineage.flink.api.DatasetFactory;
import io.openlineage.flink.api.LineageProvider;
import io.openlineage.util.OpenLineageFlinkJobListenerBuilder;
import java.util.List;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class FlinkCrashingLineageProviderApplication {

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = setupEnv(args);
    env.addSource(new FakeSource()).addSink(new FakeSink());

    env.registerJobListener(
        OpenLineageFlinkJobListenerBuilder.create()
            .executionEnvironment(env)
            .jobName("flink-crushing-lineage-job")
            .build());
  }

  static class FakeSource implements SourceFunction<Integer> {
    boolean isRunning = true;

    @Override
    public void run(SourceContext<Integer> ctx) throws Exception {
      while (isRunning) {
        synchronized (ctx.getCheckpointLock()) {
          ctx.collect(1);
        }
        Thread.sleep(100);
      }
    }

    @Override
    public void cancel() {
      isRunning = true;
    }
  }

  static class FakeSink
      implements SinkFunction<Integer>, LineageProvider<OpenLineage.OutputDataset> {
    @Override
    public List<OpenLineage.OutputDataset> getDatasets(
        DatasetFactory<OpenLineage.OutputDataset> datasetFactory) {
      throw new RuntimeException("fail");
    }
  }
}
