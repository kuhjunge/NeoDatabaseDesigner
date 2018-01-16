package de.mach.tools.neodesigner.ui;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;

public class PathTest {

  @Test
  public void testMainFxml() {
    checkPath(Strings.FXML_NEDDBDESIGNER);
  }

  @Test
  public void testFkFxml() {
    checkPath(Strings.FXML_FKRELEDITOR);
  }

  @Test
  public void testIndexFxml() {
    checkPath(Strings.FXML_INDEXRELEDITOR);
  }

  private void checkPath(final String p) {
    final URL u = getClass().getResource(p);
    try {
      final File f = new File(u.toURI());
      Assert.assertTrue(f.exists());
    } catch (final URISyntaxException e) {
      e.printStackTrace();
      Assert.fail("IO Exception");
    }
  }
}
