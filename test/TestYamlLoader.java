import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeParam;
import de.pavloff.pycharm.yaml.YamlLoader;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class TestYamlLoader {

    private YamlLoader yamlReader = new YamlLoader();

    @Test
    public void testLoad() {
        File[] files = {new File("test/ressources/test-fragments.yml")};
        ArrayList<CodeParam> params = yamlReader.getCodeParams(files);
        ArrayList<CodeFragment> fragments = yamlReader.getCodeFragments(files);

        assert params.size() == 2;
        assert fragments.size() == 3;
    }

    @Test
    public void testLoadDF() {
        File[] files = {new File("src/de/pavloff/pycharm/yaml/resources/fragments-dataframe.yml")};
        ArrayList<CodeFragment> fragments = yamlReader.getCodeFragments(files);

        assert fragments.size() == 5;
    }
}
