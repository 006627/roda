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
package config.i18n.client.flags;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Luis Faria
 * @author Vladislav Korecký <vladislav_korecky@gordic.cz>
 */
public interface FlagsBundle extends ClientBundle {

  @Source("pt_PT.png")
  public ImageResource pt_PT();

  @Source("en.png")
  public ImageResource en();

  @Source("cs_CZ.png")
  public ImageResource cs_CZ();

}
