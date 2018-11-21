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

package de.mach.tools.neodesigner.core;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class TestModelDiv {
  @Test
  public void testConfiguration() {
    final String adrCheckRes = "bolt\\://127.0.0.1\\:7687";
    final String startLocCheckRes = "E\\:\\Programme\\Neo4J";
    final String pwCheckRes = "test";
    final String usrCheckRes = "MaxMustermann";
    final int lengthCheckRes = 26;
    final Configuration conf = new Configuration();
    conf.init();
    final String adr = conf.getAddrOfDb();
    final String startLoc = conf.getNeoDbStarterLocation();
    final String pw = conf.getPw();
    final String usr = conf.getUser();
    final int length = conf.getWordLength();
    conf.setNeoDbStarterLocation(startLocCheckRes);
    conf.save(adrCheckRes, usrCheckRes, pwCheckRes);
    conf.setWordLength(lengthCheckRes);
    conf.save();
    conf.init();
    final String adrCheck = conf.getAddrOfDb();
    final String startLocCheck = conf.getNeoDbStarterLocation();
    final String pwCheck = conf.getPw();
    final String usrCheck = conf.getUser();
    final int lengthCheck = conf.getWordLength();
    conf.setAddrOfDb(adr);
    conf.setNeoDbStarterLocation(startLoc);
    conf.setPw(pw);
    conf.setUser(usr);
    conf.setWordLength(length);
    conf.save();
    assertTrue(adrCheckRes.contains(adrCheck));
    assertTrue(startLocCheckRes.contains(startLocCheck));
    assertTrue(pwCheckRes.contains(pwCheck));
    assertTrue(usrCheckRes.contains(usrCheck));
    assertTrue(lengthCheckRes == lengthCheck);
  }
}
