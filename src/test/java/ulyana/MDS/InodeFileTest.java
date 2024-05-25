package ulyana.MDS;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InodeFileTest {
    private InodeFile file;

    @BeforeEach
    public void init(){
        file = new InodeFile("", "", 0, 0, 0);
    }

    @Test
    public void access(){
        Assert.assertEquals(true, file.access("ulyana"));
        Assert.assertEquals(true, file.access(null));
        file.setBlock("ulyana");
        Assert.assertEquals(true, file.access("ulyana"));
        Assert.assertEquals(false, file.access("noname"));
        Assert.assertEquals(false, file.access(null));
    }
}
