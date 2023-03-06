package test;

import java.util.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
public class ZPFSDelete {
    public static void main(String [] args) throws Exception {

        /* Define ZIP File System Properies in HashMap */    
        Map<String, String> zip_properties = new HashMap<>(); 
        /* We want to read an existing ZIP File, so we set this to False */
        zip_properties.put("create", "false"); 

        /* Specify the path to the ZIP File that you want to read as a File System */
        URI zip_disk = URI.create("jar:file:/my_zip_file.zip");

        /* Create ZIP file System */
        try (FileSystem zipfs = FileSystems.newFileSystem(zip_disk, zip_properties)) {
            /* Get the Path inside ZIP File to delete the ZIP Entry */
            Path pathInZipfile = zipfs.getPath("lib");
            /* Execute Delete */
            Files.delete(pathInZipfile);
        } 
    }
}
