/* Copyright (C) 2018 Chris Deter Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The
 * above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package de.mach.tools.neodesigner.ui;


import java.util.List;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;


/** Util Funktionen der GUI.
 *
 * @author cd */
public class GuiUtil {
  // Static Class Only
  private GuiUtil() {}

  /** Aktiviert den Fokuks für ein ausgewähltes Node.
   *
   * @param node das Node, welches fokussiert werden soll. */
  public static void repeatFocus(final Node node) {
    Platform.runLater(() -> {
      if (!node.isFocused()) {
        node.requestFocus();
        GuiUtil.repeatFocus(node);
      }
    });
  }

  /** Erzeugt die Validierungsanzeige für die Tabelle.
   *
   * @param textfield das Feld in dem die Validierung angezeigt werden soll
   * @param validator2 der Validator für die Tabelle
   * @param string der alte Name der Tabelle */
  public static void validator(final TextField textfield, final de.mach.tools.neodesigner.core.Validator validator2,
                               final String string) {
    final ValidationSupport support = new ValidationSupport();
    final Validator<String> validator = (control, value) -> {
      final boolean condition = value != null && !validator2.validateTableName(value, string);
      return ValidationResult.fromMessageIf(control, validator2.getLastError(), Severity.ERROR, condition);
    };
    support.registerValidator(textfield, false, validator);
  }

  public static AutoCompletionBinding<String> getAutocomplete(TextField tf, List<String> list) {
    return TextFields.bindAutoCompletion(tf, list);
  }

  public static void notification(final String title, final String text) {
    Notifications.create().title(title).text(text).showInformation();
  }

  public static void setValidator(final TextField txtfld,
                                  final de.mach.tools.neodesigner.core.Validator validatorForField) {
    if (validatorForField != null) {
      final ValidationSupport support = new ValidationSupport();
      final Validator<String> validator = (control, value) -> {
        final boolean condition = value != null && validatorForField.isFieldNameInvalid(value);
        return ValidationResult.fromMessageIf(control, validatorForField.getLastError(), Severity.ERROR, condition);
      };
      support.registerValidator(txtfld, false, validator);
    }
  }
}
