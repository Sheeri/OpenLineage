/* SPDX-License-Identifier: Apache-2.0 */

package io.openlineage.spark3.agent.lifecycle.plan.catalog;

import io.openlineage.spark.agent.util.DatasetIdentifier;
import lombok.SneakyThrows;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.connector.catalog.Identifier;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.apache.spark.sql.execution.datasources.v2.jdbc.JDBCTableCatalog;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JdbcHandlerTest {

  @Test
  @SneakyThrows
  public void testGetDatasetIdentifier() {
    JdbcHandler handler = new JdbcHandler();

    JDBCTableCatalog tableCatalog = new JDBCTableCatalog();
    JDBCOptions options = mock(JDBCOptions.class);
    when(options.url()).thenReturn("jdbc:postgresql://postgreshost:5432");
    FieldUtils.writeField(tableCatalog, "options", options, true);

    DatasetIdentifier datasetIdentifier =
        handler.getDatasetIdentifier(
            mock(SparkSession.class),
            tableCatalog,
            Identifier.of(new String[] {"database", "schema"}, "table"),
            new HashMap<>());

    assertEquals("database.schema.table", datasetIdentifier.getName());
    assertEquals("postgresql://postgreshost:5432", datasetIdentifier.getNamespace());
  }
}
