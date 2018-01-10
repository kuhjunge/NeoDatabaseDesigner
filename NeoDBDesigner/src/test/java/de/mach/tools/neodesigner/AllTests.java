package de.mach.tools.neodesigner;

import de.mach.tools.neodesigner.core.TestDatamodel;
import de.mach.tools.neodesigner.core.TestExport;
import de.mach.tools.neodesigner.core.TestImport;
import de.mach.tools.neodesigner.core.TestModel;
import de.mach.tools.neodesigner.database.TestDatabase;
import de.mach.tools.neodesigner.database.TestQueryBuilder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestDatamodel.class, TestExport.class, TestModel.class, TestImport.class,
    TestDatabase.class, TestQueryBuilder.class })
public class AllTests {

}
