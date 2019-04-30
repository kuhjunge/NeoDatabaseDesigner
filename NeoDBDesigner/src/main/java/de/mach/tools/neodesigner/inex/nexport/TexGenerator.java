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

package de.mach.tools.neodesigner.inex.nexport;


import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.Generator;
import de.mach.tools.neodesigner.inex.nexport.tex.LineProcessor;


public class TexGenerator implements Generator {
  private final VelocityEngine ve = new VelocityEngine();
  private final VelocityContext context = new VelocityContext();
  private final Properties props = new Properties();
  private final Map<String, String> catTransl;

  public TexGenerator(final String title, final String author, final Map<String, String> catTransl) {
    /* first, get and initialize an engine */
    props.put(Strings.PROP_IN_ENCODING, Strings.ENCODINGUTF8);
    props.put(Strings.PROP_OUT_ENCODING, Strings.ENCODINGUTF8);
    props.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
    props.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    replace("title", title);
    replace("author", author);
    this.catTransl = catTransl;
  }

  private LineProcessor createDataModel(final List<Table> lt) {
    final LineProcessor lineProcessor = new LineProcessor(catTransl);
    lineProcessor.generate(lt);
    return lineProcessor;
  }

  @Override
  public String generate(final List<Table> tableList) {
    replace("datamodel", createDataModel(tableList).getDatamodel());
    ve.init(props);
    /* next, get the Template */
    final Template t = ve.getTemplate(Strings.RES_PATH + Strings.DATAMODEL_TEMPLATE_NAME, Strings.ENCODINGUTF8);
    /* now render the template into a StringWriter */
    final StringWriter writer = new StringWriter();
    t.merge(context, writer);
    /* show the World */
    return writer.toString();
  }

  private void replace(final String key, final Object value) {
    /* create a context and add data */
    context.put(key, value);
  }
}
