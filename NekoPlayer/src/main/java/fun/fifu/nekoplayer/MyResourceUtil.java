package fun.fifu.nekoplayer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.*;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import java.io.InputStream;

public class MyResourceUtil extends ResourceUtil {
    public static InputStream getStream(String resource) throws NoResourceException {
        return getResourceObj(resource).getStream();
    }

    public static Resource getResourceObj(String path) {
        if (StrUtil.isNotBlank(path)) {
            if (path.startsWith(URLUtil.FILE_URL_PREFIX) || FileUtil.isAbsolutePath(path)) {
                return new FileResource(path);
            }
        }
        return new MyClassPathResource(path, NekoPlayer.nekoPlayer.getClass().getClassLoader());
    }
}

class MyClassPathResource extends ClassPathResource {

    public MyClassPathResource(String path, ClassLoader classLoader) {
        super(path, classLoader);
    }
}
