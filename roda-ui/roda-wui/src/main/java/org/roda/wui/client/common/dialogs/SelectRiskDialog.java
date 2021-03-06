/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.client.common.dialogs;

import org.roda.core.data.v2.index.filter.Filter;
import org.roda.core.data.v2.risks.IndexedRisk;
import org.roda.wui.client.common.lists.RiskList;
import org.roda.wui.client.common.lists.utils.AsyncTableCellOptions;
import org.roda.wui.client.common.lists.utils.ListBuilder;

public class SelectRiskDialog extends DefaultSelectDialog<IndexedRisk> {
  public SelectRiskDialog(String title, Filter filter) {
    super(title, new ListBuilder<>(() -> new RiskList(),
      new AsyncTableCellOptions<>(IndexedRisk.class, "SelectRiskDialog_risks").withFilter(filter).withSummary(title)));
  }
}
