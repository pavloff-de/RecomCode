import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.plugin.YamlLoader;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class TestYamlLoader {

    private YamlLoader yamlReader = new YamlLoader();

    @Test
    public void testLoad() throws FileNotFoundException {
        yamlReader.clearCodeFragments();
        yamlReader.loadFrom(new FileInputStream(new File(
                "src/test/resources/test-fragments.yml")));
        List<CodeFragment> fragments = yamlReader.getCodeFragments();

        assert fragments.size() == 3;
    }

    @Test
    public void testLoadDF() throws FileNotFoundException {
        yamlReader.clearCodeFragments();
        yamlReader.loadFrom(new FileInputStream(new File(
                "src/main//resources/yaml/fragments-pandas.yml")));
        List<CodeFragment> fragments = yamlReader.getCodeFragments();

        assert fragments.size() == 16;
    }
}
