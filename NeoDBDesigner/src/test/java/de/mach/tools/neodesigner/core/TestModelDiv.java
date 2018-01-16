package de.mach.tools.neodesigner.core;

import org.junit.Assert;
import org.junit.Test;

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
    Assert.assertTrue(adrCheckRes.contains(adrCheck));
    Assert.assertTrue(startLocCheckRes.contains(startLocCheck));
    Assert.assertTrue(pwCheckRes.contains(pwCheck));
    Assert.assertTrue(usrCheckRes.contains(usrCheck));
    Assert.assertTrue(lengthCheckRes == lengthCheck);
  }
}
