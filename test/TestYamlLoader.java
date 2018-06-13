import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeParam;
import de.pavloff.pycharm.yaml.YamlLoader;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class TestYamlLoader {

    private String ressourcePath = "test/ressources/test-fragments.yml";
    private YamlLoader yamlReader = new YamlLoader();

    @Test
    public void testLoad() {
        File[] files = {new File(ressourcePath)};
        ArrayList<CodeParam> params = yamlReader.getCodeParams(files);
        ArrayList<CodeFragment> fragments = yamlReader.getCodeFragments(files);

        assert params.size() == 2;
        assert fragments.size() == 3;
    }
}
