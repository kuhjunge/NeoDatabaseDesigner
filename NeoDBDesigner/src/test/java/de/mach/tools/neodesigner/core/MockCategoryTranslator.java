package de.mach.tools.neodesigner.core;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.Table;

public class MockCategoryTranslator extends CategoryTranslator {

  @Override
  public void load(final Iterable<String> c) {
    for (final String category : getCategories(c)) {
      categoryToName.put(category, category.replace(',', '-'));
    }
  }

  // TODO: test hierfür schreiben
  public List<String> tableToCat(final List<Table> lt) {
    return super.getCategoriesFromTable(lt);
  }

  @Override
  public void save(final Map<String, String> catIdAndCatName) {
    for (final Entry<String, String> c : catIdAndCatName.entrySet()) {
      categoryToName.put(c.getKey(), c.getValue());
    }
  }

}
