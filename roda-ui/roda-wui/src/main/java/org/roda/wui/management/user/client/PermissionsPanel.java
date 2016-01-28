/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
/**
 * 
 */
package org.roda.wui.management.user.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Vector;

import org.roda.wui.client.common.UserLogin;
import org.roda.wui.common.client.ClientLogger;
import org.roda.wui.common.client.widgets.LoadingPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import config.i18n.client.UserManagementConstants;

/**
 * @author Luis Faria
 * 
 */
public class PermissionsPanel extends FlowPanel implements HasValueChangeHandlers<String> {

  // private final List<ChangeListener> changelisteners;

  private class Permission extends HorizontalPanel implements HasValueChangeHandlers<Boolean>, Comparable<Permission> {

    // functional attributes
    private final String sortingkeyword;

    private final String role;

    private boolean locked;

    private boolean enabled;

    // UI attributes
    private final CheckBox checkbox;

    private final Label descriptionLabel;

    public Permission(String role, String description, String sortingkeyword) {
      this.role = role;
      this.checkbox = new CheckBox();
      this.descriptionLabel = new Label(description);
      this.sortingkeyword = sortingkeyword;
      this.add(checkbox);
      this.add(descriptionLabel);
      this.locked = false;
      this.enabled = true;

      this.descriptionLabel.addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          if (isEnabled() && !locked) {
            checkbox.setValue(!checkbox.getValue());
            onChange();
          }
        }
      });

      this.checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
          onChange();
        }
      });

      this.addStyleName("permission");
      checkbox.addStyleName("permission-checkbox");
      descriptionLabel.setStylePrimaryName("permission-description");
    }

    protected void onChange() {
      ValueChangeEvent.fire(this, checkbox.getValue());
    }

    public boolean isLocked() {
      return locked;
    }

    public void setLocked(boolean locked) {
      this.locked = locked;
      checkbox.setEnabled(!locked);
    }

    public boolean isChecked() {
      return checkbox.getValue();
    }

    public void setChecked(boolean checked) {
      checkbox.setValue(checked);
    }

    public String getRole() {
      return role;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if (!locked) {
        this.checkbox.setEnabled(enabled);
        if (enabled) {
          this.descriptionLabel.removeStyleDependentName("off");
          this.descriptionLabel.addStyleDependentName("on");
        } else {
          this.descriptionLabel.removeStyleDependentName("on");
          this.descriptionLabel.addStyleDependentName("off");
        }
      }
    }

    public int compareTo(Permission permission0) {
      return sortingkeyword.compareTo(permission0.sortingkeyword);
    }

    @SuppressWarnings("unused")
    public String getSortingkeyword() {
      return sortingkeyword;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
      return addHandler(handler, ValueChangeEvent.getType());
    }
  }

  private static UserManagementConstants constants = (UserManagementConstants) GWT
    .create(UserManagementConstants.class);

  private ClientLogger logger = new ClientLogger(getClass().getName());

  private final List<Permission> permissions;

  private boolean enabled;

  private final LoadingPopup loading;

  /**
   * 
   */
  public PermissionsPanel() {
    this.permissions = new Vector<Permission>();
    loading = new LoadingPopup(this);
    logger.debug("Getting permissions from RODA properties");
    loading.show();

    UserLogin.getRodaProperties(new AsyncCallback<Map<String, String>>() {

      public void onFailure(Throwable caught) {
        loading.hide();
        logger.fatal("Error getting RODA properties", caught);
      }

      public void onSuccess(Map<String, String> rodaProperties) {
        logger.debug("Creating permissions list");
        for (String key : rodaProperties.keySet()) {
          if (key.startsWith("ui.role.")) {
            String role = (String) rodaProperties.get(key);
            String description;
            try {
              description = constants.getString(key.replace('.', '_'));
            } catch (MissingResourceException e) {
              description = role + " (needs translation)";
            }

            Permission permission = new Permission(role, description, (String) key);
            permissions.add(permission);
          }
        }

        logger.debug("Sorting permissions list");
        Collections.sort(permissions);

        logger.debug("Adding permissions to panel");
        for (final Permission permission : permissions) {
          PermissionsPanel.this.add(permission);
          permission.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
              onChange();
            }
          });
        }
        loading.hide();
      }

    });

    this.enabled = true;

    this.addStyleName("permissions");
  }

  /**
   * Set all permissions defined by roles checked and set locked with parameters
   * 
   * @param roles
   *          roles of the permissions to check
   * @param lock
   *          if permissions should also be locked
   */
  public void checkPermissions(Set<String> roles, boolean lock) {
    Iterator<String> it = roles.iterator();

    while (it.hasNext()) {
      String role = it.next();
      boolean foundit = false;
      for (Iterator<Permission> j = permissions.iterator(); j.hasNext() && !foundit;) {
        Permission p = j.next();
        if (p.getRole().equals(role)) {
          foundit = true;
          p.setChecked(true);
          p.setLocked(lock);
        }
      }
    }
  }

  public void clear() {
    for (Permission p : permissions) {
      p.setChecked(false);
      p.setLocked(false);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    for (Permission p : permissions) {
      p.setEnabled(enabled);
    }
  }

  public void updateLockedPermissions(Set<String> memberGroups) {
    if (memberGroups.size() > 0) {
      logger.debug("Getting group permissions");
      this.setEnabled(false);
      loading.show();
      // UserManagementService.Util.getInstance().getGroupsRoles(
      // memberGroups, new AsyncCallback<Set<String>>() {
      //
      // public void onFailure(Throwable caught) {
      // loading.hide();
      // logger.error("Error while getting member"
      // + "groups permissions", caught);
      // }
      //
      // public void onSuccess(Set<String> inheritedRoles) {
      // logger.info("got " + inheritedRoles.size()
      // + " permissions to add");
      //
      // // unlock all
      // for (Permission p : permissions) {
      // p.setLocked(false);
      //
      // }
      // // Lock inherited roles
      // checkPermissions(inheritedRoles, true);
      //
      // PermissionsPanel.this.setEnabled(true);
      // loading.hide();
      // }
      //
      // });
    }

  }

  /**
   * Get roles that are directly defined, i.e. are not inherited
   * 
   * @return
   */
  public Set<String> getDirectRoles() {
    List<Permission> checkedPermissions = new Vector<Permission>();
    for (Permission p : permissions) {
      if (p.isChecked() && !p.isLocked()) {
        checkedPermissions.add(p);
      }
    }
    Set<String> specialRoles = new HashSet<String>();
    for (int i = 0; i < checkedPermissions.size(); i++) {
      specialRoles.add(((Permission) checkedPermissions.get(i)).getRole());
    }
    return specialRoles;
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  protected void onChange() {
    ValueChangeEvent.fire(this, "");
  }
}
