package nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @description: 直接内存映射
 * @author: movesan
 * @create: 2020-10-04 09:38
 **/
public class MMapDemo {

    static final int BUFFER_SIZE = 1024;

    /**
     * 使用直接内存映射读取文件
     * @param file
     */
    public static void fileReadWithMmap(File file) {

        long begin = System.currentTimeMillis();
        byte[] b = new byte[BUFFER_SIZE];
        int len = (int) file.length();
        MappedByteBuffer buff;
        try (FileChannel channel = new FileInputStream(file).getChannel()) {
            // 将文件所有字节映射到内存中。返回MappedByteBuffer
            buff = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            for (int offset = 0; offset < len; offset += BUFFER_SIZE) {
                if (len - offset > BUFFER_SIZE) {
                    buff.get(b);
                } else {
                    buff.get(new byte[len - offset]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("time is:" + (end - begin));
    }

    /**
     * HeapByteBuffer读取文件
     * @param file
     */
    public static void fileReadWithByteBuffer(File file) {

        long begin = System.currentTimeMillis();
        try(FileChannel channel = new FileInputStream(file).getChannel();) {
            // 申请HeapByteBuffer
            ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);
            while (channel.read(buff) != -1) {
                buff.flip();
                buff.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("time is:" + (end - begin));
    }
}
