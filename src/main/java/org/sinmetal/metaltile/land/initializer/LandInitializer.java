package org.sinmetal.metaltile.land.initializer;

import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Value;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryOptions;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import java.util.logging.Logger;

/**
 * Created by sinmetal on 2017/09/08.
 */
public class LandInitializer {

    static Logger logger = Logger.getLogger(LandInitializer.class.getSimpleName());

    public interface LandInitializerOptions extends BigQueryOptions {
    }

    static class TableRowToEntityFn extends DoFn<TableRow, Entity> {

        @ProcessElement
        public void processElement(ProcessContext c) {
            Integer row = Integer.parseInt(c.element().get("row").toString());
            Integer col = Integer.parseInt(c.element().get("col").toString());

            String keyName = String.format("row-%03d-col-%03d", row, col);
            logger.info("keyName = " + keyName);

            Key.Builder keyBuilder = Key.newBuilder();
            Key.PathElement pathElement = keyBuilder.addPathBuilder().setKind("world-default20170908-land-home").setName(keyName).build();
            Key key = keyBuilder.setPath(0, pathElement).build();

            Entity.Builder entityBuilder = Entity.newBuilder();
            entityBuilder.setKey(key);
            entityBuilder.putProperties("chip", Value.newBuilder().setIntegerValue(0).build());
            entityBuilder.putProperties("hitPoint", Value.newBuilder().setDoubleValue(1000.0).build());
            c.output(entityBuilder.build());
        }
    }

    public static class BigQueryToDatastore extends PTransform<PCollection<TableRow>, PCollection<Entity>> {
        @Override
        public PCollection<Entity> expand(PCollection<TableRow> rows) {
            return rows.apply(ParDo.of(new TableRowToEntityFn()));
        }
    }

    public static void main(String[] args) {
        LandInitializerOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(LandInitializerOptions.class);
        Pipeline p = Pipeline.create(options);

        p.apply(BigQueryIO.read().from("metal-tile-dev1:world_default.land_home"))
                .apply(new BigQueryToDatastore())
                .apply(DatastoreIO.v1().write().withProjectId(options.getProject()));

        p.run();
    }
}
