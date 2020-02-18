import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class Throttle {
    public static void main(String[] args) {

    }
    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
    }

    public static void startProxy(int devicePortNumber, String newSpeed) throws IOException {
        String newPath = "./runningProfiles/" + newSpeed + devicePortNumber + ".config";
        copyFileUsingChannel(new File("/ /pankaj/tmp/source.avi"), new File("./profiles/" + newSpeed + ".config"));

    }
}
