package org.basex.gui.layout;

import static org.basex.gui.GUIConstants.GRAY;
import static org.basex.gui.GUIConstants.LGRAY;
import static org.basex.gui.GUIConstants.WHITE;
import static org.basex.gui.GUIConstants.color;
import static org.basex.gui.GUIConstants.fontWidths;
import static org.basex.gui.layout.BaseXKeys.DECFONT;
import static org.basex.gui.layout.BaseXKeys.ENTER;
import static org.basex.gui.layout.BaseXKeys.ESCAPE;
import static org.basex.gui.layout.BaseXKeys.GOBACK;
import static org.basex.gui.layout.BaseXKeys.GOFORWARD;
import static org.basex.gui.layout.BaseXKeys.GOHOME;
import static org.basex.gui.layout.BaseXKeys.GOUP;
import static org.basex.gui.layout.BaseXKeys.INCFONT1;
import static org.basex.gui.layout.BaseXKeys.INCFONT2;
import static org.basex.gui.layout.BaseXKeys.INPUT1;
import static org.basex.gui.layout.BaseXKeys.INPUT2;
import static org.basex.gui.layout.BaseXKeys.NORMFONT;
import static org.basex.util.Token.chopNumber;
import static org.basex.util.Token.cl;
import static org.basex.util.Token.cp;
import static org.basex.util.Token.string;
import static org.basex.util.Token.token;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.basex.core.Prop;
import org.basex.gui.GUI;
import org.basex.gui.GUICommands;
import org.basex.gui.GUIProp;
import org.basex.gui.dialog.Dialog;
import org.basex.util.Util;
import org.basex.util.list.ObjList;

/**
 * This class assembles layout and paint methods which are frequently
 * used in the GUI.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public final class BaseXLayout {
  /** Cached images. */
  private static final HashMap<String, ImageIcon> IMAGES =
    new HashMap<String, ImageIcon>();

  /** Private constructor. */
  private BaseXLayout() { }

  /**
   * Sets the help text for the specified component.
   * @param cont input container
   */
  static void focus(final Component cont) {
    final GUI gui = gui(cont);
    if(gui == null) return;
    if(gui.gprop.is(GUIProp.MOUSEFOCUS)) cont.requestFocusInWindow();
  }

  /**
   * Returns the GUI reference of the specified container.
   * @param cont input container
   * @return gui
   */
  private static GUI gui(final Component cont) {
    final Container c = cont.getParent();
    return c == null || c instanceof GUI ? (GUI) c : gui(c);
  }

  /**
   * Sets the component width, adopting the original component height.
   * @param comp component
   * @param w width
   */
  public static void setWidth(final Component comp, final int w) {
    comp.setPreferredSize(new Dimension(w, comp.getPreferredSize().height));
  }

  /**
   * Sets the component height, adopting the original component width.
   * @param comp component
   * @param h height
   */
  public static void setHeight(final Component comp, final int h) {
    comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, h));
  }

  /**
   * Sets the component size.
   * @param comp component
   * @param w width
   * @param h height
   */
  static void setSize(final Component comp, final int w, final int h) {
    comp.setPreferredSize(new Dimension(w, h));
  }

  /**
   * Adds drag and drop functionality.
   * @param comp component
   * @param dnd drag and drop handler
   */
  public static void addDrop(final JComponent comp, final DropHandler dnd) {
    comp.setDropTarget(new DropTarget(comp,
        DnDConstants.ACTION_COPY_OR_MOVE, null, true, null) {
      @Override
      public synchronized void drop(final DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        final Transferable tr = dtde.getTransferable();
        for(final Object o : contents(tr)) dnd.drop(o);
        comp.requestFocusInWindow();
      }
    });
  }

  /**
   * Returns the contents of the specified transferable.
   * @param tr transferable
   * @return contents
   */
  @SuppressWarnings("unchecked")
  public static ObjList<Object> contents(final Transferable tr) {
    final ObjList<Object> list = new ObjList<Object>();
    try {
      if(tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        for(final File fl : (List<File>)
            tr.getTransferData(DataFlavor.javaFileListFlavor)) list.add(fl);
      } else if(tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        list.add(tr.getTransferData(DataFlavor.stringFlavor));
      }
    } catch(final Exception ex) {
      Util.stack(ex);
    }
    return list;
  }

  /**
   * Drag and drop handler.
   * @author BaseX Team 2005-12, BSD License
   * @author Christian Gruen
   */
  public interface DropHandler {
    /**
     * Drops a file.
     * @param obj object to be dropped
     */
    void drop(final Object obj);
  }

  /**
   * Adds default interactions to the specified component.
   * @param comp component
   * @param win parent window
   */
  static void addInteraction(final Component comp, final Window win) {
    comp.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(final MouseEvent e) {
        focus(comp);
      }
    });

    if(win instanceof Dialog) {
      // add default keys
      final Dialog d = (Dialog) win;
      comp.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(final KeyEvent e) {
          final Object s = e.getSource();
          if(s instanceof BaseXCombo && ((BaseXCombo) s).isPopupVisible())
            return;

          // process key events
          if(ENTER.is(e)) {
            if(!(s instanceof BaseXButton)) d.close();
          } else if(ESCAPE.is(e)) {
            d.cancel();
          }
        }
      });
      return;
    }

    if(!(win instanceof GUI)) {
      Util.notexpected("Reference to main window required to add shortcuts.");
      return;
    }
    final GUI gui = (GUI) win;

    // add default keys for main window
    comp.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(final KeyEvent e) {
        // browse back/forward
        if(gui.context.data() != null) {
          if(GOBACK.is(e)) {
            GUICommands.C_GOBACK.execute(gui);
          } else if(GOFORWARD.is(e)) {
            GUICommands.C_GOFORWARD.execute(gui);
          } else if(GOUP.is(e)) {
            GUICommands.C_GOUP.execute(gui);
          } else if(GOHOME.is(e)) {
            GUICommands.C_GOHOME.execute(gui);
          }
        }

        // jump to input bar
        if(INPUT1.is(e) || INPUT2.is(e)) {
          gui.input.requestFocusInWindow();
        }

        // change font size
        final int fs = gui.gprop.num(GUIProp.FONTSIZE);
        int nfs = fs;
        if(INCFONT1.is(e) || INCFONT2.is(e)) {
          nfs = fs + 1;
        } else if(DECFONT.is(e)) {
          nfs = Math.max(1, fs - 1);
        } else if(NORMFONT.is(e)) {
          nfs = 12;
        }
        if(fs != nfs) {
          gui.gprop.set(GUIProp.FONTSIZE, nfs);
          gui.updateLayout();
        }
      }
    });
  }

  /**
   * Returns the specified image as icon.
   * @param name name of icon
   * @return icon
   */
  public static ImageIcon icon(final String name) {
    ImageIcon img = IMAGES.get(name);
    if(img != null) return img;
    img = new ImageIcon(image(name));
    IMAGES.put(name, img);
    return img;
  }

  /**
   * Returns the specified image.
   * @param name name of image
   * @return image
   */
  public static Image image(final String name) {
    return Toolkit.getDefaultToolkit().getImage(imageURL(name));
  }

  /**
   * Returns the image url.
   * @param name name of image
   * @return url
   */
  private static URL imageURL(final String name) {
		// changed Christoph Plutte
		// final String path = "/img/" + name + ".png";
		// final URL url = GUI.class.getResource(path);
		final String path = Prop.HOME + "WORK" + "/img/" + name + ".png";
		File f = new File(path);
		URL url = null;
		try
		{
			url = f.toURI().toURL();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    if(url == null) Util.errln("Not found: " + path);
    return url;
  }

  /**
   * Returns the value of the specified pre value and attribute.
   * @param val value to be evaluated
   * @return value as string
   */
  public static String value(final double val) {
    return string(chopNumber(token(val)));
  }

  /**
   * Fills the specified area with a color gradient.
   * @param gg graphics reference
   * @param c1 first color
   * @param c2 second color
   * @param xs horizontal start position
   * @param ys vertical start position
   * @param xe horizontal end position
   * @param ye vertical end position
   */
  public static void fill(final Graphics gg, final Color c1,
      final Color c2, final int xs, final int ys, final int xe, final int ye) {

    final int w = xe - xs;
    final int h = ye - ys;
    final int r = c1.getRed();
    final int g = c1.getGreen();
    final int b = c1.getBlue();
    final float rf = (c2.getRed() - r) / (float) h;
    final float gf = (c2.getGreen() - g) / (float) h;
    final float bf = (c2.getBlue() - b) / (float) h;

    int nr, ng, nb;
    int cr = 0, cg = 0, cb = 0;
    int hh = 0;
    for(int y = 0; y < h; ++y) {
      nr = r + (int) (rf * y);
      ng = g + (int) (gf * y);
      nb = b + (int) (bf * y);
      if(nr != cr || ng != cg || nb != cb) {
        gg.setColor(new Color(nr, ng, nb));
        gg.fillRect(xs, ys + y - hh, w, hh);
        hh = 0;
      }
      cr = nr;
      cg = ng;
      cb = nb;
      ++hh;
    }
    gg.fillRect(xs, ys + h - hh, w, hh);
  }

  /**
   * Draws a colored cell.
   * @param g graphics reference
   * @param xs horizontal start position
   * @param xe horizontal end position
   * @param ys vertical start position
   * @param ye vertical end position
   * @param focus highlighting flag
   */
  public static void drawCell(final Graphics g, final int xs,
      final int xe, final int ys, final int ye, final boolean focus) {

    g.setColor(GRAY);
    g.drawRect(xs, ys, xe - xs - 1, ye - ys - 1);
    g.setColor(Color.white);
    g.drawRect(xs + 1, ys + 1, xe - xs - 3, ye - ys - 3);

    fill(g, focus ? LGRAY : Color.white, LGRAY,
        xs + 2, ys + 2, xe - 1, ye - 1);
  }

  /**
   * Draws a centered string to the panel.
   * @param g graphics reference
   * @param text text to be painted
   * @param w panel width
   * @param y vertical position
   */
  public static void drawCenter(final Graphics g, final String text,
      final int w, final int y) {
    g.drawString(text, (w - width(g, text)) / 2, y);
  }

  /**
   * Draws a visualization tooltip.
   * @param g graphics reference
   * @param tt tooltip label
   * @param x horizontal position
   * @param y vertical position
   * @param w width
   * @param c color color depth
   */
  public static void drawTooltip(final Graphics g, final String tt,
      final int x, final int y, final int w, final int c) {
    final int tw = width(g, tt);
    final int th = g.getFontMetrics().getHeight();
    final int xx = Math.min(w - tw - 8, x);
    g.setColor(color(c));
    g.fillRect(xx - 1, y - th, tw + 4, th);
    g.setColor(WHITE);
    g.drawString(tt, xx, y - 4);
  }

  /**
   * Returns the width of the specified text.
   * @param g graphics reference
   * @param s string to be evaluated
   * @return string width
   */
  public static int width(final Graphics g, final String s) {
    return g.getFontMetrics().stringWidth(s);
  }

  /**
   * Draws the specified string.
   *
   * @param g graphics reference
   * @param s text
   * @param x x coordinate
   * @param y y coordinate
   * @param w width
   * @param fs font size
   */
  public static void chopString(final Graphics g, final byte[] s,
      final int x, final int y, final int w, final int fs) {

    if(w < 12) return;
    final int[] cw = fontWidths(g.getFont());

    int j = s.length;
    int fw = 0;
    int l = 0;
    try {
      for(int k = 0; k < j; k += l) {
        final int ww = width(g, cw, cp(s, k));
        if(fw + ww >= w - 4) {
          j = Math.max(1, k - l);
          if(k > 1) fw -= width(g, cw, cp(s, k - 1));
          g.drawString("..", x + fw, y + fs);
          break;
        }
        fw += ww;
        l = cl(s, k);
      }
    } catch(final Exception ex) {
      Util.debug(ex);
    }
    g.drawString(string(s, 0, j), x, y + fs);
  }

  /**
   * Returns the width of the specified text.
   * Cached font widths are used to speed up calculation.
   * @param g graphics reference
   * @param s string to be evaluated
   * @return string width
   */
  public static int width(final Graphics g, final byte[] s) {
    final int[] cw = fontWidths(g.getFont());
    final int l = s.length;
    int fw = 0;
    try {
      // ignore faulty character sets
      for(int k = 0; k < l; k += cl(s, k)) fw += width(g, cw, cp(s, k));
    } catch(final Exception ex) {
      Util.debug(ex);
    }
    return fw;
  }

  /**
   * Returns the character width of the specified character.
   * @param g graphics reference
   * @param cw array with character widths
   * @param c character
   * @return character width
   */
  public static int width(final Graphics g, final int[] cw, final int c) {
    return c >= cw.length ? g.getFontMetrics().charWidth(c) : cw[c];
  }
}