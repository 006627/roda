/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.data.descriptionLevels;

import java.io.Serializable;

import org.roda.core.data.exceptions.RequestNotValidException;

/**
 * 
 * 
 * @author Rui Castro
 * @author Hélder Silva
 * @author Luis Faria <lfaria@keep.pt>
 */
public class DescriptionLevel implements Serializable {
  private static final long serialVersionUID = 9038357012292858570L;

  // description level
  private String level = null;

  /**
   * Constructs an empty (<strong>invalid</strong>) {@link DescriptionLevel}.
   * <p>
   * <strong>This method should not be used. All the possible values for a
   * {@link DescriptionLevel} are already defined as constant values.</strong>
   * </p>
   */
  public DescriptionLevel() {
  }

  /**
   * Constructs a {@link DescriptionLevel} clonning an existing
   * {@link DescriptionLevel}.
   * 
   * @param dLevel
   *          the {@link DescriptionLevel} to clone.
   * 
   * @throws InvalidDescriptionLevel
   *           if the specified level is not one of the allowed levels.
   */
  public DescriptionLevel(DescriptionLevel dLevel) throws RequestNotValidException {
    this(dLevel.getLevel());
  }

  /**
   * Constructs a new {@link DescriptionLevel} of the specified level.
   * 
   * @param level
   *          the level of this {@link DescriptionLevel}.
   * 
   * @throws InvalidDescriptionLevel
   *           if the specified level is not one of the allowed levels.
   */
  public DescriptionLevel(String level) throws RequestNotValidException {
    setLevel(level);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((level == null) ? 0 : level.hashCode());
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
    if (!(obj instanceof DescriptionLevel)) {
      return false;
    }
    DescriptionLevel other = (DescriptionLevel) obj;
    if (level == null) {
      if (other.level != null) {
        return false;
      }
    } else if (!level.equals(other.level)) {
      return false;
    }
    return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return getLevel();
  }

  /**
   * @return the level
   */
  public String getLevel() {
    return level;
  }

  /**
   * Sets the level (it gets trimmed in the process)
   * 
   * @param level
   *          the level to set.
   * @throws InvalidDescriptionLevel
   *           if the specified level is null or empty {@link String}.
   */
  public void setLevel(String level) throws RequestNotValidException {
    if (level != null && !"".equals(level.trim().toLowerCase())) {
      this.level = level.trim().toLowerCase();
    } else {
      throw new RequestNotValidException("Invalid level: '" + level + "'");
    }
  }
}