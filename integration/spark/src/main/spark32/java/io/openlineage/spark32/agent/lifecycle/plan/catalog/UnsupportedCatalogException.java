/* SPDX-License-Identifier: Apache-2.0 */

package io.openlineage.spark32.agent.lifecycle.plan.catalog;

public class UnsupportedCatalogException extends RuntimeException {
  public UnsupportedCatalogException(String catalog) {
    super(catalog);
  }
}