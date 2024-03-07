package com.everdro1d.libs.io;

import com.everdro1d.libs.core.ApplicationCore;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Files {
    private Files() {}

    /**
     * Get the abs. path of the jar file.
     * @param clazz the class to trace the jar path from
     * @param debug whether to print debug information
     * @return the path of the jar file as a string
     */
    public static String getJarPath(Class<?> clazz, boolean debug) {
        String jarPath = null;
        try {
            jarPath = Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
            if (debug) System.out.println("Jar Path: " + jarPath);
        } catch (URISyntaxException e) {
            if (debug) e.printStackTrace(System.err);
            System.err.println("[ERROR] Failed to get jar path.");
        }
        return jarPath;
    }

    /**
     * Check if a file is in use.
     * @param filePath the path of the file to check
     * @return boolean
     */
    public static boolean isFileInUse(Path filePath) {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE);
             FileLock lock = channel.tryLock()) {
            // If the lock is null, then the file is already locked
            return lock == null;
        } catch (IOException e) {
            // An exception occurred, which means the file is likely in use
            return true;
        }
    }

    /**
     * Delete a file.
     * @param path the path of the file to delete
     * @param debug whether to print debug information
     */
    public static void deleteFile(String path, boolean debug) {
        java.io.File fileToDelete = new java.io.File(path);
        String name = fileToDelete.getName();
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                if (debug) System.out.println("Deleted file: " + name);
            } else {
                System.err.println("Failed to delete file: " + name);
            }
        }
    }

    /**
     * Get a set of files in a directory.
     * @param inputDirectory the directory to get the files from
     * @param contains the string to match in the file names
     * @param debug whether to print debug information
     * @return set of matching file names, null if no matches are found
     */
    public static Set<String> getMatchingFiles(String inputDirectory, String contains, boolean debug) {
        Set<String> allFiles = getAllFilesInDirectory(inputDirectory);
        if (debug) System.out.println("All Files: \n" + allFiles);

        Set<String> wantedFiles = new HashSet<>();
        for (String file : allFiles) {
            if (file.contains(contains)) {
                wantedFiles.add(file);
            }
        }

        if (wantedFiles.isEmpty()) {
            return null;
        }

        if (debug) System.out.println("Found " + wantedFiles.size() + " files: \n" + wantedFiles);
        return wantedFiles;
    }

    /**
     * Get a set of all files in a directory.
     * @param inputDirectory the directory to get the files from
     * @return set of file names
     */
    public static Set<String> getAllFilesInDirectory(String inputDirectory) {
        return Stream.of(Objects.requireNonNull(new java.io.File(inputDirectory).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(java.io.File::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Open a directory in the file manager, and select the file.
     * @param path the path to the file
     */
    public static void openInFileManager(String path) {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            System.err.println("Desktop not supported. Cannot open in file manager.");
            return;
        } else if (!validateFilePath(path)) {
            System.err.println("Invalid file path. Cannot open in file manager.");
            return;
        }

        String directory;
        if (!new java.io.File(path).isDirectory()) {
            String fileDiv = FileSystems.getDefault().getSeparator();
            String fileName = path.split(fileDiv)[path.split(fileDiv).length - 1];
            directory = path.replace(fileName, "");
        } else {
            directory = path;
        }


        try {
            Desktop.getDesktop().open(new java.io.File(directory));

            if (!new File(path).exists()) {
                System.err.println("File or Directory does not exist. Cannot select in file manager.");
                return;
            } else if (directory.equals(path)) {
                System.err.println("Path is not a file. Cannot select in file manager.");
                return;
            }
            String os = ApplicationCore.detectOS(false);
            if (os.equals("Windows")) {
                new ProcessBuilder("explorer.exe", "/select,", path).start();
            } else if (os.equals("macOS")) {
                new ProcessBuilder("open", "-R", path).start();
            } else {
                System.err.println("Unsupported OS: " + ApplicationCore.detectOS(false) + ". Cannot select in file manager.");
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Check if a file path is valid.
     * @param path the path to check
     * @return true if the path is valid, false otherwise.
     */
    private static boolean validateFilePath(String path) {
        if (path == null || path.isEmpty()) return false;

        String os = ApplicationCore.detectOS(false);
        if (os.equals("Windows")) {
            if (!(path.contains(":") && path.contains("\\"))) return false;
        } else if (os.equals("macOS") || os.equals("Unix")) {
            if (!path.contains("/")) return false;
        } else {
            System.err.println("Unsupported OS: " + os + ". Cannot check file path. Assuming true.");
        }

        if (!(new java.io.File(path).exists() || new java.io.File(path).isDirectory())) {
            return false;
        }

        return true;
    }
}