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

package de.mach.tools.neodesigner.core.nexport.tex.util;


import java.util.Comparator;


public class PageComparator implements Comparator<String> {

  @Override
  public int compare(final String o1, final String o2) {
    final Integer[] a = split(o1);
    final Integer[] b = split(o2);

    if (a[0] > b[0]) {
      return 1;
    }
    else if (a[0] < b[0]) {
      return -1;
    }
    else if (a[1] > b[1]) {
      return 1;
    }
    else if (a[1] < b[1]) {
      return -1;
    }
    else {
      return 0;
    }
  }

  private Integer[] split(final String pageNumber) {
    final String[] strings = pageNumber.split(",");
    return new Integer[] { Integer.valueOf(strings[0]), Integer.valueOf(strings[1]) };
  }
}
