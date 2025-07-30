package Implementation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

/**
 * Utility class for loading resources consistently from both JAR files and the file system.
 * This class provides methods to load images, files, and other resources in a way that works
 * regardless of whether the application is running from a JAR or directly from the file system.
 */
public class ResourceLoader {
    
    /**
     * Loads an image resource from either the JAR or the file system.
     * 
     * @param path The path to the image resource, relative to the workspace root
     * @return An ImageIcon containing the loaded image, or null if loading fails
     */
    public static ImageIcon loadImage(String path) {
        try {
            // Remove Implementation/ prefix if present for JAR loading
            String jarPath = path;
            if (path.startsWith("Implementation/")) {
                jarPath = path.substring("Implementation/".length());
            }
            
            // First try loading from the classpath/JAR using no prefix
            URL resourceUrl = ResourceLoader.class.getResource("/" + jarPath);
            if (resourceUrl != null) {
                ImageIcon icon = new ImageIcon(resourceUrl);
                if (icon.getIconWidth() > 0) {
                    System.out.println("Loaded resource from JAR path: " + jarPath);
                    return icon;
                }
            }
            
            // Try loading from the classpath with the original path (for IDE environment)
            resourceUrl = ResourceLoader.class.getResource("/" + path);
            if (resourceUrl != null) {
                ImageIcon icon = new ImageIcon(resourceUrl);
                if (icon.getIconWidth() > 0) {
                    System.out.println("Loaded resource from classpath path: " + path);
                    return icon;
                }
            }
            
            // If that fails, try loading directly from the file system
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                System.out.println("Loaded resource from file system path: " + path);
                return icon;
            }
            
            // If still no success, log the error
            System.err.println("Failed to load image: " + path);
            return null;
        } catch (Exception e) {
            System.err.println("Error loading image " + path + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads an image resource and resizes it to the specified dimensions.
     * 
     * @param path The path to the image resource, relative to the workspace root
     * @param width The desired width of the resized image
     * @param height The desired height of the resized image
     * @return An ImageIcon containing the resized image, or null if loading fails
     */
    public static ImageIcon loadAndResizeImage(String path, int width, int height) {
        ImageIcon icon = loadImage(path);
        if (icon == null) {
            return null;
        }
        
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    
    /**
     * Opens an input stream to a resource file, from either the JAR or the file system.
     * 
     * @param path The path to the resource, relative to the workspace root
     * @return An InputStream for the resource, or null if opening fails
     */
    public static InputStream getResourceAsStream(String path) {
        try {
            // Remove Implementation/ prefix if present for JAR loading
            String jarPath = path;
            if (path.startsWith("Implementation/")) {
                jarPath = path.substring("Implementation/".length());
            }
            
            // First try loading from the classpath/JAR with no prefix
            InputStream stream = ResourceLoader.class.getResourceAsStream("/" + jarPath);
            if (stream != null) {
                System.out.println("Loaded stream from JAR path: " + jarPath);
                return stream;
            }
            
            // Try with the original path (for IDE environment)
            stream = ResourceLoader.class.getResourceAsStream("/" + path);
            if (stream != null) {
                System.out.println("Loaded stream from classpath path: " + path);
                return stream;
            }
            
            // If that fails, try loading directly from the file system
            File file = new File(path);
            if (file.exists()) {
                System.out.println("Loaded stream from file system path: " + path);
                return new FileInputStream(file);
            }
            
            // If still no success, log the error
            System.err.println("Failed to open resource: " + path);
            return null;
        } catch (Exception e) {
            System.err.println("Error opening resource " + path + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Checks if a resource exists (either in JAR or file system).
     * 
     * @param path The path to the resource, relative to the workspace root
     * @return true if the resource exists, false otherwise
     */
    public static boolean resourceExists(String path) {
        // Remove Implementation/ prefix if present for JAR loading
        String jarPath = path;
        if (path.startsWith("Implementation/")) {
            jarPath = path.substring("Implementation/".length());
        }
        
        // Check in classpath/JAR with no prefix
        if (ResourceLoader.class.getResource("/" + jarPath) != null) {
            return true;
        }
        
        // Check in classpath with original path
        if (ResourceLoader.class.getResource("/" + path) != null) {
            return true;
        }
        
        // Check in file system
        File file = new File(path);
        return file.exists();
    }
} 