package fun.fifu.nekoplayer;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        List<String> frames = new ArrayList<>();
        // load frames
        for (int i = 1; i <= 1745; i++) {
            frames.add(IoUtil.read(ResourceUtil.getStream("bad-apple/" + i + ".txt"), StandardCharsets.UTF_8));
        }

        frames.forEach(f -> {
            System.out.println(f);
            /* char mod
            for (char c : f.toCharArray()) {
                switch (c) {
                    case '1':
                        System.out.print('1');
                        break;
                    case ' ':
                        System.out.print('0');
                        break;
                    default:
                        System.out.println();
                        break;
                }
            }*/
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
