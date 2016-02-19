/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.data.v2.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a RODA report.
 * 
 * @author Rui Castro
 */
public class Report implements Serializable {
  private static final long serialVersionUID = 8031045207181692901L;

  /**
   * A plugin report
   */
  public static final String TYPE_PLUGIN_REPORT = "PLUGIN_REPORT";

  private String id = null;
  private String type = null;
  private String title = null;
  private List<Attribute> attributes = new ArrayList<Attribute>();
  private List<ReportItem> items = new ArrayList<ReportItem>();

  /**
   * Constructs a new {@link Report}.
   */
  public Report() {
  }

  /**
   * Constructs a new {@link Report} cloning an existing {@link Report}.
   * 
   * @param report
   *          the {@link Report} to clone.
   */
  public Report(Report report) {
    this(report.getId(), report.getType(), report.getTitle(), report.getAttributes(), report.getItems());
  }

  /**
   * Constructs a new {@link Report} with the given parameters.
   * 
   * @param id
   * @param type
   * @param title
   * @param attributes
   * @param items
   */
  public Report(String id, String type, String title, List<Attribute> attributes, List<ReportItem> items) {
    setId(id);
    setType(type);
    setTitle(title);
    setAttributes(attributes);
    setItems(items);
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Report(id=" + getId() + ", type=" + getType() + ", title=" + getTitle() + ", attributes=" + attributes
      + ", items=" + items + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((items == null) ? 0 : items.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Report)) {
      return false;
    }
    Report other = (Report) obj;
    if (attributes == null) {
      if (other.attributes != null) {
        return false;
      }
    } else if (!attributes.equals(other.attributes)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (items == null) {
      if (other.items != null) {
        return false;
      }
    } else if (!items.equals(other.items)) {
      return false;
    }
    if (title == null) {
      if (other.title != null) {
        return false;
      }
    } else if (!title.equals(other.title)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the attributes
   */
  public List<Attribute> getAttributes() {
    return attributes;
  }

  /**
   * @param attributes
   *          the attributes to set
   */
  public void setAttributes(List<Attribute> attributes) {
    this.attributes.clear();
    if (attributes != null) {
      this.attributes.addAll(attributes);
    }
  }

  /**
   * @param attribute
   * @return
   */
  public Report addAttribute(Attribute attribute) {
    this.attributes.add(attribute);
    return this;
  }

  /**
   * @return the items
   */
  public List<ReportItem> getItems() {
    return items;
  }

  /**
   * @param items
   *          the items to set
   */
  public void setItems(List<ReportItem> items) {
    this.items.clear();
    if (items != null) {
      this.items.addAll(items);
    }
  }

  /**
   * @param item
   */
  public void addItem(ReportItem item) {
    this.items.add(item);
  }

}
