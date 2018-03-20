import com.mpatric.mp3agic.*;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by FORESTER on 20.03.18.
 */
public class Main {

    private final static Logger log = getLogger(Main.class);
    public static final String DIR = "./src/file/";
//    public static final String DIR = "/Users/FORESTER/Desktop/MUSIC/";
//    public static final String DIR = "/Users/FORESTER/Desktop/test/";
//    public static final String DIR = "/Volumes/WALKMAN/MUSIC/test/";


    public static void main(String[] args) throws Exception{
        File dir = new File(DIR);
        //TODO skip ._blabla.mp3 files
        for (String fileName : dir.list(new SuffixFileFilter(".mp3"))){
            log.info("Processing track {}",fileName);
            Mp3File musicFile = new Mp3File(DIR+fileName);

            ID3v2 tagV2 = musicFile.getId3v2Tag();

            if (!isTagBad(tagV2)){
                log.info("Track with has V2 tag: {}",fileName);
                continue;
            }

            TrackInfo trackInfo = getTrackInfo(musicFile, fileName);
            if (trackInfo == null){
                return;
            }

            tagV2 = fillTag(tagV2, trackInfo);
            musicFile.setId3v2Tag(tagV2);
            log.info("saving changes...");
            musicFile.save(musicFile.getFilename()+".tmp");
            File outputFile = new File(musicFile.getFilename()+".tmp");
            if (!outputFile.renameTo(new File(musicFile.getFilename()))){
                log.error("Unable to replace file with name {}",fileName);
                outputFile.delete();
            }
        }
    }

    private static TrackInfo getTrackInfo(Mp3File file, String fileName) {
        Pattern pattern = Pattern.compile("^(.*) - (.*).mp3$");
        Matcher matcher = pattern.matcher(fileName);
        try {
            matcher.find();
            return new TrackInfo(matcher.group(1), matcher.group(2));
        } catch (Exception e) {
            log.info("No matches found for track {}",fileName);
            return null;
        }
    }

    private static boolean isTagBad(ID3v1 tag){
        return tag == null || isEmpty(tag.getArtist()) || isEmpty(tag.getTitle());
    }

    private static boolean isEmpty(String str){
        return str==null || str.isEmpty();
    }

    private static ID3v2 fillTag(ID3v2 tag, TrackInfo trackInfo){
        if (tag==null){
            tag = new ID3v22Tag();
        }
        tag.setArtist(trackInfo.getAuthor());
        tag.setTitle(trackInfo.getTitle());
        return tag;
    }
}
